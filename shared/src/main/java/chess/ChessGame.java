package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;  // game starts with white team
    private ChessBoard board;

    public ChessGame() {
        // initialize and reset board
        this.board = new ChessBoard();
        board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        Collection<ChessMove> possibleMoves = new HashSet<>();
        Collection<ChessMove> legalMoves = new HashSet<>();

        if (piece != null) {
            possibleMoves = piece.pieceMoves(board, startPosition);
        }

        for (ChessMove move : possibleMoves) {
            ChessBoard temp = copyBoard(board);
            temp.simulateMove(temp, move); // make move on temp board

            if (!tempIsInCheck(temp, piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    private ChessBoard copyBoard(ChessBoard originalBoard) {
        ChessBoard copy = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece originalPiece = originalBoard.getPiece(position);

                copy.addPiece(position, originalPiece); // same piece as og piece
            }
        }
        return copy;
    }

    private ChessPosition findKingPosition(ChessBoard currentBoard, TeamColor teamColor) {
        // need to account for tests where there's just one king on the board -> don't assume purely conventional setup
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = currentBoard.getPiece(position);

                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (piece.getTeamColor() == teamColor) {
                        return position;
                    } else if (kingPosition == null) {  // accounts for test case where there's just one king on opposing team
                        kingPosition = position;
                    }
                }
            }
        }

        return kingPosition;
    }

    private boolean isPositionUnderAttack(ChessPosition position, ChessBoard currentBoard, TeamColor opponentColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPos = new ChessPosition(row, col);
                ChessPiece currentPiece = currentBoard.getPiece(currentPos);

                if (currentPiece != null && currentPiece.getTeamColor() == opponentColor) {
                    Collection<ChessMove> moves = currentPiece.pieceMoves(currentBoard, currentPos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(position)) {
                            return true; // position is under attack by an opponent's piece
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean tempIsInCheck(ChessBoard tempBoard, TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(tempBoard, teamColor);
        if (kingPosition == null) {
            return false;  // deal with test cases where there's no king on board
        }
        // update game turn
        teamColor = updateGameTurn(teamColor);
        return isPositionUnderAttack(kingPosition, tempBoard, teamColor);
    }

    private TeamColor updateGameTurn(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            teamColor = TeamColor.BLACK;
        } else {
            teamColor = TeamColor.WHITE;
        }
        return teamColor;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // check to make sure it's correct team's turn
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());
        if (currentPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Invalid Move -> not your turn yet");
        }

        // check to make sure move is legal
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move -> move not allowed for this piece");
        }

        // make the move here
        board.addPiece(move.getEndPosition(), currentPiece);
        board.addPiece(move.getStartPosition(), null); // get rid of piece at starting pos

        // pawn promotion
        int blackPromoRow = 1;
        int whitePromoRow = 8;
        if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if ((teamTurn == TeamColor.WHITE && move.getEndPosition().getRow() == whitePromoRow) || (teamTurn == TeamColor.BLACK && move.getEndPosition().getRow() == blackPromoRow)) {
                board.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
            }
        }

        // update game turn
        teamTurn = updateGameTurn(teamTurn);

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return tempIsInCheck(this.board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if(!moves.isEmpty()) {
                        return false;  // found legal move to get king out of check
                    }
                }
            }
        }
        teamTurn = updateGameTurn(teamColor);

        return true;  // checkmate
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // if no valid moves if not in checkmate, then stalemate
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    if(!validMoves(pos).isEmpty()) {
                        return false; // found move to get king out of check
                    }


                }
            }
        }
        // update team turn
        teamTurn = updateGameTurn(teamColor);

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
