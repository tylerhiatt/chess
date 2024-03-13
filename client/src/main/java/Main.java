import chess.*;
import ui.Client;
import server.Server;

public class Main {

    private static final Client client = new Client();

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        // start server
        Server server = new Server();
        int port = server.run(8080);
        System.out.println("Server running on port: " + port);

        // interact with server with client UI
        client.clientStart();

    }


}