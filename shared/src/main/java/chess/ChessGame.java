package chess;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
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
        ChessPiece piece = getBoard().getPiece(startPosition);

        if (piece.getTeamColor() != getTeamTurn()) {
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(getBoard(), startPosition);
        Collection<ChessMove> legalMoves = new HashSet<>();

        for (ChessMove move : possibleMoves) {
            ChessBoard temp = copyBoard(getBoard());
            temp.simulateMove(temp, move); // make move on temp board

            if (!tempIsInCheck(temp, piece.getTeamColor())) {
                legalMoves.add(move);
            }

        }

        return legalMoves;
    }

    private ChessBoard copyBoard(ChessBoard ogBoard) {
        ChessBoard copy = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece ogPiece = ogBoard.getPiece(position);
                copy.addPiece(position, ogPiece); // same piece as og piece
                // copy.board[row][col] = ogBoard.board[row][col];

            }
        }
        return copy;
    }

    private ChessPosition findKingPosition(ChessBoard myBoard, TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = myBoard.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }

        throw new IllegalStateException("King not found"); // shouldn't happen lol
    }

    private boolean isPositionUnderAttack(ChessPosition position, ChessBoard myBoard, TeamColor teamColor) {
        TeamColor opponentColor = TeamColor.BLACK;
        if (teamColor == TeamColor.BLACK) {
            opponentColor = TeamColor.WHITE;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPos = new ChessPosition(row, col);
                ChessPiece currentPiece = myBoard.getPiece(currentPos);

                if (currentPiece != null && currentPiece.getTeamColor() == opponentColor) {
                    Collection<ChessMove> moves = currentPiece.pieceMoves(myBoard, currentPos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(position)) {
                            return true; // position is under attack by an opponent's piece
                        }
                    }
                }
            }
        }
        return false; // No opponent's piece can capture at that position
    }

    private boolean tempIsInCheck(ChessBoard tempBoard, TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(tempBoard, teamColor);
        return isPositionUnderAttack(kingPosition, tempBoard, teamColor);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece currentPiece = getBoard().getPiece(move.getStartPosition());
        if (currentPiece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Invalid Move -> not your turn yet");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move -> move not allowed for this piece");
        }

        ChessBoard temp = copyBoard(getBoard());
        temp.simulateMove(temp, move);
        if(tempIsInCheck(temp, getTeamTurn())) {
            throw new InvalidMoveException("Invalid Move -> can't put king in check");
        }

        // make the move here
        getBoard().addPiece(move.getEndPosition(), currentPiece);
        getBoard().addPiece(move.getStartPosition(), null); // get rid of piece at starting pos

        // update game turn maybe here?
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKingPosition(getBoard(), teamColor);
        return isPositionUnderAttack(kingPos, getBoard(), teamColor);
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
                ChessPiece piece = getBoard().getPiece(pos);

                if (piece != null && piece.getTeamColor() == getTeamTurn()) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if(!moves.isEmpty()) {
                        return false;  // found legal move to get king out of check
                    }
                }
            }
        }
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
        // means nobody one, games over
        if (!isInCheck(teamColor)) {
            return false;
        }

        if (!isInCheckmate(teamColor)) {
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition pos = new ChessPosition(row, col);
                    ChessPiece piece = getBoard().getPiece(pos);

                    if (piece != null && piece.getTeamColor() == getTeamTurn()) {
                        Collection<ChessMove> moves = validMoves(pos);
                        if(!moves.isEmpty()) {
                            return false;  // found legal move to get king out of check
                        }
                    }
                }
            }
        }

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
