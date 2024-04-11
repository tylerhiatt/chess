package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

public class GameplayUI {

    private static final String whiteSquare = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String blackSquare = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String resetBackground = EscapeSequences.RESET_BG_COLOR_DEFAULT;
    private static ChessBoard chessBoard = new ChessBoard();

    static {
        chessBoard.resetBoard();  // gets initial board state
    }

public static void printBoard(ChessBoard chessBoard, boolean orientWhite) {
    ChessPiece[][] board = chessBoard.getCurrentBoard();
    printLetters(orientWhite);

    for (int i = 0; i < 8; i++) {
        int rowNumber = orientWhite ? 8 - i : i + 1;
        System.out.print(rowNumber + " ");
        for (int j = 0; j < 8; j++) {
            int col = orientWhite ? j : 7 - j;
            ChessPiece piece = board[orientWhite ? i : 7 - i][col];
            boolean isWhiteSquare = (i + j) % 2 == 0;
            printSquare(piece, isWhiteSquare);
        }
        System.out.println(" " + rowNumber);
    }

    printLetters(orientWhite);
    System.out.print(resetBackground);
}

    private static void printSquare(ChessPiece piece, boolean isWhiteSquare) {
        String squareColor = isWhiteSquare ? whiteSquare : blackSquare;
        String pieceRep = pieceToEscapeSequence(piece);
        System.out.print(squareColor + pieceRep + resetBackground);
    }

    private static void printLetters(boolean orientWhite) {
        // take into account orientation
        System.out.print("  ");
        for (int c = 0; c < 8; c++) {
            char letter = (char) ('a' + (orientWhite ? c : 7 - c));
            System.out.print(" " + letter + " ");
        }
        System.out.println();
    }

    public static void updateBoard(ChessBoard chessBoard, boolean isWhite) {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.flush();
        if (isWhite) {
            printBoardWhiteOrientation(chessBoard);
        } else {
            printBoardBlackOrientation(chessBoard);
        }
    }

    public static void redrawBoard() {

    }

    public static void printBoardWhiteOrientation(ChessBoard chessBoard) {
        printBoard(chessBoard, true);
    }

    public static void printBoardBlackOrientation(ChessBoard chessBoard) {
        printBoard(chessBoard, false);
    }

    public static String pieceToEscapeSequence(ChessPiece piece) {
        // map out pieces to escape sequences
        if (piece == null) return EscapeSequences.EMPTY; // empty space
        switch (piece.getPieceType()) {
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;

        }
        return EscapeSequences.EMPTY; // fallback
    }

    // for testing:
    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();

        System.out.println("White's perspective:");
        printBoardWhiteOrientation(chessBoard);

        System.out.println("\nBlack's perspective:");
        printBoardBlackOrientation(chessBoard);
    }

}