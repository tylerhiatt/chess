import chess.*;
import ui.Client;
 import server.Server;

public class Main {

    private static final Client client = new Client();

    public static void main(String[] args) {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        System.out.println("♕ 240 Chess Client");

        // start server
        Server server = new Server();
        server.run(port);
        System.out.println("Server running on port: " + port);

        // interact with server with client UI
        client.clientStart(port);

    }


}