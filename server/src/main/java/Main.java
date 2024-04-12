import chess.*;

import server.Server;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Server: ");

        Server server = new Server();

        int curPort = server.run(8080);
        System.out.println("Server running on port: " + curPort);
    }
}