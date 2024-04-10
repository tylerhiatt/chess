package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
    public ResignCommand(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.setGameID(gameID);
    }
}
