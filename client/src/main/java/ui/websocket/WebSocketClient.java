package ui.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;


public class WebSocketClient extends Endpoint {
   private Session session;
   private final Gson serializer = new Gson();

    public WebSocketClient(String url, Consumer<String> messageHandler) throws URISyntaxException, DeploymentException, IOException {

        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    // System.out.println("DEBUG" + message);
                    messageHandler.accept(message);
                }
            });

        } catch (URISyntaxException | DeploymentException | IOException e) {
            System.err.println("Websocket connection error: " + e.getMessage());
            throw e; // rethrow to handle outside
        }
   }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // System.out.println("WebSocket connection opened " + session.getAsyncRemote().toString());
        this.session = session;
    }

    @OnClose
   public void onClose(Session session, CloseReason reason) {
        //System.out.println("WebSocket connection closed: " + reason);
        this.session = null; // clear session
   }

   public void sendJoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor playerColor) {
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

   public void sendLeaveCommand(String authToken, int gameID) {
        UserGameCommand command = new LeaveCommand(authToken, gameID);
        sendCommand(command);
   }

   public void sendResignCommand(String authToken, int gameID) {
        UserGameCommand command = new ResignCommand(authToken, gameID);
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
           System.out.println("WebSocket connection closed");
       }
   }



}
