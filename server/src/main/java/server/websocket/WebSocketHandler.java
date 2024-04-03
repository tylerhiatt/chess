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
import webSocketMessages.userCommands.MoveCommand;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;

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
        // remove player from connections?
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received: " + message);
        // deserialize message to UserGameCommand, process and respond
        // broadcast updates to players/observers

        try {
            UserGameCommand command = serializer.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case JOIN_PLAYER, JOIN_OBSERVER:
                    handleJoinPlayer(session, command);
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
        MySQLDataAccess data = MySQLDataAccess.getInstance();

        try {
            // make sure auth data is valid
            AuthData authData = data.getAuth(command.getAuthString());
            if (authData == null) {
                sendError(session, "Invalid auth token");
                return;
            }

            // make sure game ID is valid
            GameData game = data.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Invalid game ID");
                return;
            }

            boolean isObserver = command.getPlayerColor() == null;
            Result joinGameResult;

            // observer case
            if (isObserver) {
                joinGameResult = joinGameService.joinGame(command.getAuthString(), command.getGameID(), null);
                if (!joinGameResult.isSuccess()) {
                    // send error message back to client
                    sendError(session, joinGameResult.getMessage());
                    return;
                }
                sendNotification(session, "Joined game as observer");

            } else {
                // normal player case
                joinGameResult = joinGameService.joinGame(command.getAuthString(), command.getGameID(), command.getPlayerColor());
                if (!joinGameResult.isSuccess()) {
                    sendError(session, joinGameResult.getMessage());
                    return;
                }
                sendNotification(session, "Joined the game as: " + command.getPlayerColor());
            }

            ChessGame chessGame = game.game();
            broadcastUpdatedGameState(chessGame, command.getGameID());

        } catch (DataAccessException e) {
            sendError(session, "Failed to join game " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, MoveCommand command) throws IOException{
        MySQLDataAccess data = MySQLDataAccess.getInstance();

        try {
            // make sure auth data is valid
            AuthData authData = data.getAuth(command.getAuthString());
            if (authData == null) {
                sendError(session, "Invalid auth token");
                return;
            }

            // make sure game ID is valid
            GameData game = data.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Invalid game ID");
                return;
            }

            // should handle any errors thrown with invalid moves/not player's turn
            ChessGame chessGame = game.game();
            chessGame.makeMove(command.getMove());

            // if successful, broadcast updated game state
            broadcastUpdatedGameState(chessGame, command.getGameID());

        } catch (DataAccessException e) {
            sendError(session, "Failed to make move: " + e.getMessage());
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }


    //// helper methods to send messages to client
    private void sendError(Session session, String message) throws IOException {
        ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, message);
        session.getRemote().sendString(new Gson().toJson(errorMessage));
    }

    private void sendNotification(Session session, String message) throws IOException {
        ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        session.getRemote().sendString(new Gson().toJson(notificationMessage));
    }

    private void broadcastUpdatedGameState(ChessGame chessGame, int gameID) throws IOException{
        // retrieve the updated game state
        String gameStateJson = serializer.toJson(chessGame.getBoard());
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameStateJson);
        String messageJson = serializer.toJson(loadGameMessage);

        // broadcast updated game state to all players
        for (Session session : connections.sessionsConnectedToGame(gameID)) {
            if (session.isOpen()) {
                session.getRemote().sendString(messageJson);
            }
        }



    }


}