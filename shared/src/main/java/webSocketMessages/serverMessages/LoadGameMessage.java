package webSocketMessages.serverMessages;

public class LoadGameMessage extends ServerMessage {
    private final String game;

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public String getChessBoardJson() { return game; }
}