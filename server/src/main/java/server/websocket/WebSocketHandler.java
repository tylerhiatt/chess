package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import com.google.gson.Gson;
import server.JoinGameService;
import server.Result;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.MoveCommand;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final JoinGameService joinGameService = new JoinGameService();
    private final Gson serializer = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New connection opened: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        // remove player from connection
//        connections.removeSession(session);
//        System.out.println("Connection closed: " + session.getRemoteAddress().getAddress() + ", Reason: " + reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received: " + message);
        // deserialize message to UserGameCommand, process and respond
        // broadcast updates to players/observers

        try {
            UserGameCommand command = serializer.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case JOIN_PLAYER:
                    handleJoinPlayer(session, command);
                    break;
                case JOIN_OBSERVER:
                    handleJoinObserver(session, command);
                    break;
                case MAKE_MOVE:
                    MoveCommand moveCommand = serializer.fromJson(message, MoveCommand.class);
                    handleMakeMove(session, moveCommand);
                    break;
                case LEAVE:
                    break;
                case RESIGN:
                    break;

                default:
                    System.err.println("Unknown command type: " + command.getCommandType());
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getRemote().sendString("Error processing command: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //// handler methods for different command cases

    private void handleJoinPlayer(Session session, UserGameCommand command) throws IOException {
        // use atomic ref to condense code
        AtomicReference<GameData> gameRef = new AtomicReference<>();
        if (!validate(session, command, gameRef)) return;

        // make sure game can be joined
        GameData game = gameRef.get();
        if (game.whiteUsername() == null && game.blackUsername() == null) {
            sendError(session, "Game hasn't been started yet, not joinable");
            return;
        }

        // add user to session and join game
        connections.add(command.getGameID(), command.getAuthString(), session);
        Result joinGameResult = joinGameService.joinGame(command.getAuthString(), command.getGameID(), command.getPlayerColor());

        if (!joinGameResult.isSuccess()) {
            sendError(session, joinGameResult.getMessage());
            return;
        }

        // send load game to root client
        sendLoadGame(session, game.game());

        // send notification to other users
        broadcastNotificationToOthers(command.getGameID(), session, "Joined the game as: " + command.getPlayerColor());
    }

    private void handleJoinObserver(Session session, UserGameCommand command) throws IOException {
        AtomicReference<GameData> gameRef = new AtomicReference<>();
        if (!validate(session, command, gameRef)) return;

        GameData game = gameRef.get();
        connections.add(command.getGameID(), command.getAuthString(), session);
        Result joinGameResult = joinGameService.joinGame(command.getAuthString(), command.getGameID(), null);

        if (!joinGameResult.isSuccess()) {
            sendError(session, joinGameResult.getMessage());
            return;
        }

        sendLoadGame(session, game.game());
        broadcastNotificationToOthers(command.getGameID(), session, "Joined the game as an observer");
    }

    private void handleMakeMove(Session session, MoveCommand command) throws IOException{
        MySQLDataAccess data = MySQLDataAccess.getInstance();

        try {
            AuthData authData = data.getAuth(command.getAuthString());
            if (authData == null) {
                sendError(session, "Invalid auth token");
                return;
            }

            GameData game = data.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Invalid game ID");
                return;
            }

            ChessGame chessGame = game.game();

            boolean isPlayerTurn = (chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE && authData.username().equals(game.whiteUsername())) ||
                    (chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK && authData.username().equals(game.blackUsername()));

            if (!isPlayerTurn) {
                sendError(session, "Not your turn or you're not a player in this game");
                return;
            }

            // should handle any errors thrown with invalid moves/not player's turn
            chessGame.makeMove(command.getMove());

            // if successful send messages
            broadcastLoadGame(session, chessGame, game.gameID());
            broadcastNotificationToOthers(command.getGameID(), session, command.getPlayerColor() + " made a move: " + command.getMove().toString());

        } catch (DataAccessException e) {
            sendError(session, "Failed to access data: " + e.getMessage());

        } catch (InvalidMoveException e) {
            sendError(session, "Invalid move " + e.getMessage());
        }

    }


    //// helper methods to send messages
    private void sendError(Session session, String message) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(message);
        session.getRemote().sendString(serializer.toJson(errorMessage));
    }

    private void sendLoadGame(Session session, ChessGame game) throws IOException {
        String gameStateJson = serializer.toJson(game);
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameStateJson);
        session.getRemote().sendString(serializer.toJson(loadGameMessage));
    }

    private void broadcastNotificationToOthers(int gameID, Session excludeSession, String notificationText) throws IOException {
        for (Session otherSession : connections.sessionsConnectedToGame(gameID)) {
            // exclude root client
            if (!otherSession.equals(excludeSession) && otherSession.isOpen()) {
                // notification message
                NotificationMessage notificationMessage = new NotificationMessage(notificationText);
                otherSession.getRemote().sendString(serializer.toJson(notificationMessage));

            }
        }
    }

    private void broadcastLoadGame(Session session, ChessGame game, int gameID) throws IOException {
        for (Session otherSession : connections.sessionsConnectedToGame(gameID)) {
            if (session.isOpen()) {

                LoadGameMessage loadGameMessage = new LoadGameMessage(serializer.toJson(game));
                otherSession.getRemote().sendString(serializer.toJson(loadGameMessage));

            }
        }

    }

    //// helper method to clean up code
    private boolean validate(Session session, UserGameCommand command, AtomicReference<GameData> gameRef) throws IOException {
        MySQLDataAccess data = MySQLDataAccess.getInstance();
        try {
            AuthData authData = data.getAuth(command.getAuthString());
            if (authData == null) {
                sendError(session, "Invalid auth token");
                return false;
            }

            GameData game = data.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Invalid game ID");
                return false;
            }

            gameRef.set(game);
            return true;

        } catch (DataAccessException e) {
            sendError(session, "Failed to access data: " + e.getMessage());
            return false;
        }
    }




}