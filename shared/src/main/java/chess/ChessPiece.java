package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    // class attributes
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    // override equals, hashcode and to string methods here
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }


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
        throw new RuntimeException("Problem with move instruction");
    }

    // generic move function, excluding knight moves and pawn moves
    private Set<ChessMove> genericPieceMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, boolean longerMove) {
        Set<ChessMove> moves = new HashSet<>();

        // loop over each direction that the piece has
        for (int[] direction : directions) {
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();

            while (true) {
                newRow += direction[0];  // row direction offset
                newCol += direction[1];  // col direction offset

                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                    break; // break if outside boundaries
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
        // special moves -> L shape, same capture logic

        Set<ChessMove> moves = new HashSet<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        // L moves for a knight
        int[][] knightMoves = {
                {-2, -1}, {-2, 1},  // upwards L
                {-1, -2}, {-1, 2},  // left and right L
                {1, -2},  {1, 2},   // left and right L
                {2, -1},  {2, 1}    // downwards L
        };

        for (int[] move : knightMoves) {
            int newRow = currentRow + move[0];
            int newCol = currentCol + move[1];

            // check if new position is on the board
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPos = board.getPiece(newPos);

                // add move if new position is empty or occupied by an opponent's piece
                if (pieceAtNewPos == null || pieceAtNewPos.getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }

        return moves;
    }


    private Set<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        // horizontal/vertical moves for rook
        int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
        return genericPieceMoves(board, myPosition, directions, true);
    }

    private Set<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        // todo: special logic for pawn -> promotion pieces, two-step move from start, forward movement no capture, only diagonal capture
        Set<ChessMove> moves = new HashSet<>();

        // initial move is white
        int direction = 0;
        int startRow = 0;
        int promotionRow = 0;
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            promotionRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            promotionRow = 1;
        }

        ChessPosition moveOne = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        // if the space is empty, move one step forward
        if (board.getPiece(moveOne) == null) {
            if (moveOne.getRow() == promotionRow) {
                helperPromotion(myPosition, moveOne, moves); // become a promotion piece
            } else {
                moves.add(new ChessMove(myPosition, moveOne, null));
            }

            // if the pawn is on the starting row, move two if that space is empty
            ChessPosition moveTwo = new ChessPosition(myPosition.getRow() + (2*direction), myPosition.getColumn());
            if (myPosition.getRow() == startRow) {
                if (board.getPiece(moveTwo) == null) {
                    moves.add(new ChessMove(myPosition, moveTwo, null));
                }
            }
        }

        // handle diagonal move & capture opponent
        int[] diagDirections = {-1, 1};
        for (int direc : diagDirections) {
            ChessPosition capturePos = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + direc);
            if (board.getPiece(capturePos) != null && board.getPiece(capturePos).getTeamColor() != pieceColor) {
                if (capturePos.getRow() == promotionRow) {
                    helperPromotion(myPosition, capturePos, moves);  // promotion moves for capture
                } else {
                    moves.add(new ChessMove(myPosition, capturePos, null));  // regular capture
                }
            }

        }

        return moves;
    }

    private void helperPromotion(ChessPosition start, ChessPosition end, Set<ChessMove> moves) {
        // promotions -> pawn can become either a queen, rook, knight or bishop
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
    }

}




