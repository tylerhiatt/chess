package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    // class attributes
    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        // initialize attributes in constructor
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (getPieceType()) {
            case KING:
                return kingMoves(board, myPosition);
            case QUEEN:
                return null;
            case BISHOP:
                return null;
            case KNIGHT:
                return null;
            case ROOK:
                return null;
            case PAWN:
                return null;
        }
        throw new RuntimeException("Piece Type Unknown");
        // throw new RuntimeException("Not implemented");
    }


    // implement private functions for each chess piece move
    private Set<ChessMove> genericPieceMoves(ChessBoard board, ChessPosition myPosition, int[][] allPossibleMoves) {
        Set<ChessMove> moves = new HashSet<>();

        for (int[] move : allPossibleMoves) {
            int newRow = move[0];  // gets the row
            int newCol = move[1];  // gets the col

            // make sure new pos is in board boundaries
            // define local vars so java doesn't get mad at me
            ChessPiece pieceNewPos = null;
            ChessPosition newPos = null;
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                newPos = new ChessPosition(newRow, newCol);
                pieceNewPos = board.getPiece(newPos);
            }

            // make sure new pos doesn't take over teammates space
            if (pieceNewPos == null || pieceNewPos.getTeamColor() != this.getTeamColor()) {
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }

        return moves;
    }

    private Set<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // all possible moves
        int[][] kingMoves = {
                {row - 1, col},  // up
                {row + 1, col},  // down
                {row, col - 1}, // left
                {row, col + 1}, // right
                {row - 1, col - 1}, // up left diagonal
                {row - 1, col + 1}, // up right diagonal
                {row + 1, col - 1}, // down left diagonal
                {row + 1, col + 1}  // down right diagonal
        };

        return genericPieceMoves(board, myPosition, kingMoves);
    }

}


