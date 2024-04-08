package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, List<Session>> gameSessions = new ConcurrentHashMap<>();

    // Add user to game
    public void add(int gameID, String playerName, Session session) {
        var connection = new Connection(playerName, session);
        connections.put(playerName, connection);

        gameSessions.compute(gameID, (id, sessions) -> {
            if (sessions == null) {
                sessions = Collections.synchronizedList(new java.util.ArrayList<>());
            }
            sessions.add(session);

            // debugging
            if (playerName.isEmpty()) {
                System.out.println("Added session for Observer to game " + gameID);
            } else {
                System.out.println("Added session for " + playerName + " to game " + gameID);
            }
            return sessions;
        });
    }

    public Connection get(String playerName) {
        return connections.get(playerName);
    }

//    public void remove(int gameID, String playerName) {
//        Connection connection = connections.remove(playerName);
//
//        if (connection != null) {
//            gameSessions.computeIfPresent(gameID, (id, sessions) -> {
//                sessions.remove(connection.session);
//                if (sessions.isEmpty()) {
//                    return null;
//                }
//                return sessions;
//            });
//            System.out.println("Removed session for " + playerName + " from game " + gameID);  // debug
//        }
//    }

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


}