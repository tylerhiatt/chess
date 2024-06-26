package server.websocket;

import chess.ChessBoard;
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

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final JoinGameService joinGameService = new JoinGameService();
    private final Gson serializer = new Gson();
    private final ConcurrentHashMap<Integer, ChessGame> gameStates = new ConcurrentHashMap<>();

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
                    handleLeave(session, command);
                    break;
                case RESIGN:
                    handleResign(session, command);
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

        // get correct game state
        ChessGame chessGame = gameStates.computeIfAbsent(command.getGameID(), id -> new ChessGame());

        // instantiate when player joins game
        ExtendedGameData extendedGameData = connections.getOrCreateExtendedGameData(command.getGameID(), game, UserRole.PLAYER);

        // add user to session and join game
        connections.add(command.getGameID(), command.getAuthString(), session, UserRole.PLAYER);
        Result joinGameResult = joinGameService.joinGame(command.getAuthString(), command.getGameID(), command.getPlayerColor());

        if (!joinGameResult.isSuccess()) {
            sendError(session, joinGameResult.getMessage());
            return;
        }

        // send load game to root client
        sendLoadGame(session, chessGame);

        // send notification to other users
        broadcastNotificationToOthers(command.getGameID(), session, "Joined the game as: " + command.getPlayerColor());
    }

    private void handleJoinObserver(Session session, UserGameCommand command) throws IOException {
        AtomicReference<GameData> gameRef = new AtomicReference<>();
        if (!validate(session, command, gameRef)) return;

        GameData game = gameRef.get();

        ChessGame chessGame = gameStates.computeIfAbsent(command.getGameID(), id -> new ChessGame());;

        connections.add(command.getGameID(), command.getAuthString(), session, UserRole.OBSERVER);
        Result joinGameResult = joinGameService.joinGame(command.getAuthString(), command.getGameID(), null);

        if (!joinGameResult.isSuccess()) {
            sendError(session, joinGameResult.getMessage());
            return;
        }

        // set observer to true
        ExtendedGameData extendedGameData = connections.getOrCreateExtendedGameData(command.getGameID(), game, UserRole.OBSERVER);
        extendedGameData.setObserver(true);

        // send messages
        sendLoadGame(session, chessGame);
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
            ExtendedGameData extendedGameData = connections.getExtendedGameData(command.getGameID());

            if (game == null) {
                sendError(session, "Invalid game ID");
                return;
            }

            // check resignation for make move
            if (extendedGameData.isResigned()) {
                sendError(session, "Can't make move after resignation");
                return;
            }

            ChessGame chessGame = gameStates.getOrDefault(command.getGameID(), new ChessGame());
            if (chessGame.isInCheckmate(chessGame.getTeamTurn()) || chessGame.isInStalemate(chessGame.getTeamTurn())) {
                sendError(session, "Game is over. No more moves can be made.");
                return;
            }

            boolean isPlayerTurn = (chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE && authData.username().equals(game.whiteUsername())) ||
                    (chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK && authData.username().equals(game.blackUsername()));

            // make sure it's correct player's turn
            if (!isPlayerTurn) {
                sendError(session, "Not your turn or you're not a player in this game");
                return;
            }

            // should handle any errors thrown with invalid moves/not player's turn, update turn
            chessGame.makeMove(command.getMove());

            // put updated game state in hash map to get correct turn and game state
            gameStates.put(command.getGameID(), chessGame);


            // if successful send messages
            broadcastLoadGame(session, chessGame, command.getGameID());
            broadcastNotificationToOthers(command.getGameID(), session, authData.username() + " made a move: " + command.getMove().toString());


        } catch (DataAccessException e) {
            sendError(session, "Failed to access data: " + e.getMessage());

        } catch (InvalidMoveException e) {
            sendError(session, "Invalid move " + e.getMessage());
        }

    }

    private void handleResign(Session session, UserGameCommand command) throws IOException {
        MySQLDataAccess data = MySQLDataAccess.getInstance();
        try {
            AuthData authData = data.getAuth(command.getAuthString());

            ExtendedGameData extendedGameData = connections.getExtendedGameData(command.getGameID());

            // check to make sure player isn't observer
            if (Objects.equals(authData.username(), "observer")) {
                sendError(session, "Observer can't resign from game");
                return;
            }

            // make sure resignation hasn't already happened
            if (extendedGameData.isResigned()) {
                sendError(session, "Can't resign after a player has already resigned");
                return;
            }

            broadcastNotification(command.getGameID(), authData.username() + " has resigned from the game");
            extendedGameData.setResigned(true);  // set resigned to true

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException {
        MySQLDataAccess data = MySQLDataAccess.getInstance();
        try {
            AuthData authData = data.getAuth(command.getAuthString());
            String username = authData.username();

            connections.removeSession(session);
            broadcastNotificationToOthers(command.getGameID(), session, username + " has left the game");

        } catch (DataAccessException e) {
            sendError(session, "Unable to leave game");
        }

    }

    //// helper methods to send messages
    private void sendError(Session session, String message) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(message);
        session.getRemote().sendString(serializer.toJson(errorMessage));
    }

    private void sendLoadGame(Session session, ChessGame game) throws IOException {
        ChessBoard board = game.getBoard();
        String chessBoardJson = serializer.toJson(board);
        LoadGameMessage loadGameMessage = new LoadGameMessage(chessBoardJson);
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
        ChessBoard board = game.getBoard();
        // serialize board
        String chessBoardJson = serializer.toJson(board);
        LoadGameMessage loadGameMessage = new LoadGameMessage(chessBoardJson);

        for (Session otherSession : connections.sessionsConnectedToGame(gameID)) {
            if (session.isOpen()) {
                otherSession.getRemote().sendString(serializer.toJson(loadGameMessage));

            }
        }

    }

    private void broadcastNotification(int gameID,String notificationText) throws IOException {
        for (Session otherSession : connections.sessionsConnectedToGame(gameID)) {
            if (otherSession.isOpen()) {
                NotificationMessage notificationMessage = new NotificationMessage(notificationText);
                otherSession.getRemote().sendString(serializer.toJson(notificationMessage));
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