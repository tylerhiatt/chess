import chess.*;

import server.Server;

public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);

        Server server = new Server();
        int port = 8080;
        if (args.length > 0 && args[0].equals("test")) {
            port = 0; // if running tests, run on port 0
        }

        int curPort = server.run(port);
        System.out.println("Server running on port: " + curPort);
    }
}