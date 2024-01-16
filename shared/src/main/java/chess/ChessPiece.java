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
                return queenMoves(board, myPosition);
            case BISHOP:
                return bishopMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            case PAWN:
                return pawnMoves(board, myPosition);
        }
        throw new RuntimeException("Piece Type Unknown");
    }


    // implement private functions for each chess piece move

    // generic move function, excluding knight moves and pawn moves
    private Set<ChessMove> genericPieceMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, boolean longerMove) {
        Set<ChessMove> moves = new HashSet<>();

        // loop over each direction that the piece has
        for (int[] direction : directions) {
            // sets to piece's current pos
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();

            while (true) {
                newRow += direction[0];  // 1st number is row direction offset
                newCol += direction[1];  // 2nd number is column direction offset

                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                    break; // break if not in the board boundaries
                }

                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPos = board.getPiece(newPos);  // gets chess piece at new position

                // if there's a piece at the new pos that's of the opposing team, this piece can capture it
                if (pieceAtNewPos != null) {
                    if (pieceAtNewPos.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null)); // capture move
                    }
                    break; // stop if path is blocked
                }

                //
                moves.add(new ChessMove(myPosition, newPos, null));

                if (!longerMove) {
                    break; // for pieces that only move one square in each direction
                }
            }
        }

        return moves;
    }


    private Set<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        // moves for a king
        int[][] directions = {
                {-1, 0},  // up
                {1, 0},   // down
                {0, -1},  // left
                {0, 1},   // right
                {-1, -1}, // up left diagonal
                {-1, 1},  // up right diagonal
                {1, -1},  // down left diagonal
                {1, 1}    // down right diagonal
        };

        return genericPieceMoves(board, myPosition, directions, false);
    }

    private Set<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        // moves for a queen
        int[][] directions = {
                {-1, 0},  // up
                {1, 0},   // down
                {0, -1},  // left
                {0, 1},   // right
                {-1, -1}, // up left diagonal
                {-1, 1},  // up right diagonal
                {1, -1},  // down left diagonal
                {1, 1}    // down right diagonal
        };

        return genericPieceMoves(board, myPosition, directions, true); // like king moves but longerMoves = true
    }

    private Set<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        // diagonal moves for bishop
        int[][] directions = {
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}
        };
        return genericPieceMoves(board, myPosition, directions, true);
    }

    private Set<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> validMoves = new HashSet<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        // moves for a knight
        int[][] knightMoves = {
                {-2, -1}, {-2, 1},  // Upwards L-moves
                {-1, -2}, {-1, 2},  // Left and Right L-moves
                {1, -2},  {1, 2},   // Left and Right L-moves
                {2, -1},  {2, 1}    // Downwards L-moves
        };

        for (int[] move : knightMoves) {
            int newRow = currentRow + move[0];
            int newCol = currentCol + move[1];

            // Check if new position is on the board
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPos = board.getPiece(newPos);

                // Add move if new position is empty or occupied by an opponent's piece
                if (pieceAtNewPos == null || pieceAtNewPos.getTeamColor() != this.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }

        return validMoves;
    }


    private Set<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        // horizontal moves for rook
        int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
        return genericPieceMoves(board, myPosition, directions, true);
    }

    private Set<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented yet");
    }




}




