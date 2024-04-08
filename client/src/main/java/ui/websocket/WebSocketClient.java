package ui.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.MoveCommand;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketClient extends Endpoint {
   private Session session;
   private final Gson serializer = new Gson();
   private NotificationHandler notificationHandler;

    public WebSocketClient(String url, NotificationHandler notificationHandler) throws URISyntaxException, DeploymentException, IOException {
        this.notificationHandler = notificationHandler;

        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = serializer.fromJson(message, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
            });

        } catch (URISyntaxException | DeploymentException | IOException e) {
            System.err.println("Websocket connection error: " + e.getMessage());
            throw e; // rethrow to handle outside
        }
   }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection opened");
        this.session = session;
    }

    @OnClose
   public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket connection closed: " + reason);
        this.session = null; // clear session
   }

   public void sendJoinPlayerCommand(int gameID, ChessGame.TeamColor playerColor, String authToken) {
        UserGameCommand command = new JoinPlayerCommand(authToken, gameID, playerColor);
        sendCommand(command);
   }

   public void sendJoinObserverCommand(String authToken, int gameID) {
        UserGameCommand command = new JoinObserverCommand(authToken, gameID);
        sendCommand(command);
   }

   public void sendMakeMoveCommand(String authToken, int gameID, ChessMove move) {
        UserGameCommand command = new MoveCommand(authToken, gameID, move);
        sendCommand(command);
   }

   public void sendCommand(UserGameCommand command) {
       if (session != null && session.isOpen()) {
           try {
               // convert command to JSON and send
               String commandJson = serializer.toJson(command);
               session.getAsyncRemote().sendText(commandJson);
           } catch (Exception ex) {
               System.err.println("Error sending command: " + ex.getMessage());
           }
       } else {
           System.err.println("WebSocket is not connected.");
       }
   }

   public void closeSession() throws IOException {
       if (session != null) {
           session.close();
       }
   }



}
