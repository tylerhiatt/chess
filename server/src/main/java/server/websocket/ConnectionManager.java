package server.websocket;

import chess.ChessGame;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, List<Session>> gameSessions = new ConcurrentHashMap<>();
    private final Map<Integer, ExtendedGameData> extendedGameDatas = new ConcurrentHashMap<>();
    private final Map<Integer, Set<String>> players = new ConcurrentHashMap<>();
    private final Map<Integer, Set<String>> observers = new ConcurrentHashMap<>();

    // storing extended game data instances
    public ExtendedGameData getExtendedGameData(int gameId) {
        return extendedGameDatas.get(gameId);
    }

    public ExtendedGameData getOrCreateExtendedGameData(int gameId, GameData gameData, UserRole userRole) {
        return extendedGameDatas.computeIfAbsent(gameId, x -> new ExtendedGameData(gameData, userRole));
    }

    // Add user to game
    public void add(int gameID, String playerName, Session session, UserRole userRole) {
        var connection = new Connection(playerName, session);
        connections.put(playerName, connection);

        gameSessions.compute(gameID, (id, sessions) -> {
            if (sessions == null) {
                sessions = Collections.synchronizedList(new java.util.ArrayList<>());
            }
            sessions.add(session);

            // debugging
            if (userRole != UserRole.PLAYER) {
                addObserver(gameID, playerName);
                //System.out.println("Added session for Observer to game " + gameID);
            } else {
                addPlayer(gameID, playerName);
                //System.out.println("Added session for " + playerName + " to game " + gameID);
            }
            return sessions;
        });
    }

    public Connection get(String playerName) {
        return connections.get(playerName);
    }

    public void removeSession(Session session) {
        // find the playerName associated with the session to be removed
        String playerNameToRemove = connections.entrySet().stream()
                .filter(entry -> entry.getValue().session.equals(session))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (playerNameToRemove != null) {
            // remove the connection based on playerName
            connections.remove(playerNameToRemove);

            // remove the session from all game sessions if needed
            gameSessions.values().forEach(sessions -> sessions.removeIf(s -> s.equals(session)));

            System.out.println("Session for " + playerNameToRemove + " removed.");
        }
    }


    public List<Session> sessionsConnectedToGame(int gameID) {
        return gameSessions.getOrDefault(gameID, Collections.emptyList());
    }

    private void addPlayer(int gameID, String userId) {
        players.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    private void addObserver(int gameID, String userId) {
        observers.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }


}