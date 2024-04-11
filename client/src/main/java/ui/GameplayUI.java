package ui;

import chess.ChessBoard;

public class GameplayUI {

    private static final String whiteSquare = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String blackSquare = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String resetBackground = EscapeSequences.RESET_BG_COLOR_DEFAULT;


    private static final String[][] initialBoard = {
        {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK},
        {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},
        {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
        {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
        {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
        {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
        {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},
        {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK}
    };

    private static void printBoard(String[][] board, boolean orientWhite) {
        // print col letters at the top
        printLetters(orientWhite);

        for (int i = 0; i < board.length; i++) {
            // adjust row nums based on orientation
            int rowNumber = orientWhite ? (8 - i) : (i + 1);

            // print row nums before each row
            System.out.print(rowNumber + " ");

            for (int j = 0; j < board[i].length; j++) {
                // alternate colors
                boolean isWhiteSquare = (i + j) % 2 == 0;
                printSquare(board[i][j], isWhiteSquare);
            }

            // close last grid line and print row num
            System.out.println(" " + rowNumber);

        }
        // print col letters at the bottom
        printLetters(orientWhite);
        System.out.print(resetBackground);

    }

    private static void printSquare(String piece, boolean isWhiteSquare) {
        String squareColor = isWhiteSquare ? whiteSquare : blackSquare;
        System.out.print(squareColor + piece + resetBackground);
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

    public static void updateBoard(ChessBoard newBoardState) {
        // figure out how to implement this

    }

    public static void redrawBoard() {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.flush();

        printBoardBlackOrientation();
        System.out.println();
        printBoardWhiteOrientation();
    }


    public static void printBoardWhiteOrientation() {
        printBoard(initialBoard, true);
    }

    public static void printBoardBlackOrientation() {
        String[][] reversedBoard = new String[8][8];

        for (int i = 0; i < initialBoard.length; i++) {
            for (int j = 0; j < initialBoard[i].length; j++) {
                // reverse order of elements in each row for the black perspective
                reversedBoard[i][j] = initialBoard[initialBoard.length - 1 - i][initialBoard[i].length - 1 - j];
            }
        }
        printBoard(reversedBoard, false);
    }

    // for testing:
    public static void main(String[] args) {
        printBoardBlackOrientation();
        System.out.println();
        printBoardWhiteOrientation();
    }

}