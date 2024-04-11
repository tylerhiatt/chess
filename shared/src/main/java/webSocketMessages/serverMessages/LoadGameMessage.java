package webSocketMessages.serverMessages;

import java.util.List;


public class LoadGameMessage extends ServerMessage {
    //private final Object game;
    private final String game;

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME);
        //this.game = game;
        this.game = game;
    }

    //public Object getGame() {
//        return game;
//    }
    public String getChessBoardJson() { return game; }
}