package webSocketMessages.userCommands;

import chess.ChessMove;

public class MoveCommand  extends UserGameCommand {
    private final ChessMove move;

    public MoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.setGameID(gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

}