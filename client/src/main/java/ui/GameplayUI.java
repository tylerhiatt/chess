package ui;

public class GameplayUI {

    private static final String[][] INITIAL_BOARD = {
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
                System.out.print("|" + board[i][j]); // add grid line between pieces
            }

            // close last grid line and print row num
            System.out.println("| " + rowNumber);

        }
        // print col letters at the bottom
        printLetters(orientWhite);

    }

    private static void printLetters(boolean orientWhite) {
        // take into account orientation
        System.out.print("   ");
        for (int c = 0; c < 8; c++) {
            char letter = (char) ('a' + (orientWhite ? c : 7 - c));
            System.out.print(" " + letter + "  ");
        }
        System.out.println();
    }


    public static void printBoardWhiteOrientation() {
        printBoard(INITIAL_BOARD, true);
    }

    public static void printBoardBlackOrientation() {
        String[][] reversedBoard = new String[8][8];

        for (int i = 0; i < INITIAL_BOARD.length; i++) {
            for (int j = 0; j < INITIAL_BOARD[i].length; j++) {
                // reverse order of elements in each row for the black perspective
                reversedBoard[i][j] = INITIAL_BOARD[INITIAL_BOARD.length - 1 - i][INITIAL_BOARD[i].length - 1 - j];
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