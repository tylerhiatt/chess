package ui;

import chess.*;

import java.util.Collection;

public class GameplayUI {

    private static final String whiteSquare = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String blackSquare = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String resetBackground = EscapeSequences.RESET_BG_COLOR_DEFAULT;
    //private static ChessBoard chessBoard = new ChessBoard();
    private static ChessBoard chessBoard = ChessBoard.getInstance();

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
                printSquare(piece, isWhiteSquare, false);
            }
            System.out.println(" " + rowNumber);
        }

        printLetters(orientWhite);
        System.out.print(resetBackground);
    }

    private static void printSquare(ChessPiece piece, boolean isWhiteSquare, boolean isHighlighted) {
        String squareColor = isWhiteSquare ? whiteSquare : blackSquare;
        if (isHighlighted) {
            squareColor = EscapeSequences.SET_BG_COLOR_YELLOW;
        }
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

    public static void redrawBoard(boolean isWhite) {
        System.out.flush();
        if (isWhite) {
            printBoardWhiteOrientation(chessBoard);
        } else {
            printBoardBlackOrientation(chessBoard);
        }
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
        // colors reversed for some reason lol
        switch (piece.getPieceType()) {
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;

        }
        return EscapeSequences.EMPTY; // fallback
    }

}