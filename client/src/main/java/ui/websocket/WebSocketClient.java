package ui.websocket;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketClient extends Endpoint {
   private final Session session;
   private final Gson serializer = new Gson();

    public WebSocketClient(String url, NotificationHandler notificationHandler) throws URISyntaxException, DeploymentException, IOException {
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
    public void onOpen(Session session, EndpointConfig config) {
       System.out.println("WebSocket connection opened");
   }

   public void sendCommand(UserGameCommand command) {
       if (this.session != null && this.session.isOpen()) {
           try {
               // Convert command to JSON and send
               String commandJson = serializer.toJson(command);
               this.session.getAsyncRemote().sendText(commandJson);
           } catch (Exception ex) {
               System.err.println("Error sending command: " + ex.getMessage());
           }
       } else {
           System.err.println("WebSocket is not connected.");
       }
   }

   public void closeSession() throws IOException {
       if (this.session != null) {
           this.session.close();
       }
   }



}
