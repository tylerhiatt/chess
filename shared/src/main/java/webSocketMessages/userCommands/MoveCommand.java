package webSocketMessages.userCommands;

import chess.ChessMove;

public class MoveCommand  extends UserGameCommand {
    private int gameID;
    private ChessMove move;

    public MoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }




}