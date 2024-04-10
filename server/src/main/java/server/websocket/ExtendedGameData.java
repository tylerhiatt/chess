package server.websocket;

import chess.ChessGame;
import model.GameData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtendedGameData {
    private final GameData gameData;
    private final Map<String, UserRole> userRoles;
    private boolean isResigned;
    private boolean isObserver;

    private ChessGame.TeamColor currentTurn = ChessGame.TeamColor.WHITE;
    private boolean isGameFinished = false;

    public ExtendedGameData(GameData gameData, UserRole userRole) {
        this.gameData = gameData;
        this.userRoles = new ConcurrentHashMap<>();
        this.isObserver = false;
        this.isResigned = false;
    }

    public GameData getGameData() {
        return gameData;
    }

    public void addUserRole(String userID, UserRole role) {
        userRoles.put(userID, role);
    }

    public UserRole getUserRole(String userID) {
        return userRoles.getOrDefault(userID, UserRole.PLAYER);
    }

    public boolean isResigned() {
        return isResigned;
    }

    public boolean isObserver() {
        return isObserver;
    }

    public void setResigned(boolean resigned) {
        this.isResigned = resigned;
    }

    public void setObserver(boolean observer) {
        this.isObserver = observer;
    }

    public ChessGame.TeamColor getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(ChessGame.TeamColor currentTurn) {
        this.currentTurn = currentTurn;
    }

}

