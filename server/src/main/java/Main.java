import chess.*;

import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        // run server to see if I can hit my endpoints with curl
        Server server = new Server();

        int curPort = server.run(8080);
        System.out.println("Server running on port: " + curPort);
    }
}