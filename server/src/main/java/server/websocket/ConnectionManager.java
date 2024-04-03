package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, List<Session>> gameSessions = new ConcurrentHashMap<>();

    // add user to game
    public void add(int gameID, String playerName, Session session) {
        var connection = new Connection(playerName, session);
        connections.put(playerName, connection);

        gameSessions.compute(gameID, (id, sessions) -> {
            if (sessions == null) {
                sessions = new ArrayList<>();
            }
            sessions.add(session);
            return sessions;
        });
    }

    public Connection get(String playerName) {
        return connections.get(playerName);
    }

    public void remove(int gameID, String playerName) {
        Connection connection = connections.remove(playerName);

        if (connection != null) {
            gameSessions.computeIfPresent(gameID, (id, sessions) -> {
                sessions.remove(connection.session);
                return sessions.isEmpty() ? null : sessions;
            });
        }

    }

    public List<Session> sessionsConnectedToGame(int gameID) {
        return gameSessions.getOrDefault(gameID, new ArrayList<>());
    }

//    public void broadcast(String excludePlayerName, String message) throws  IOException {
//        var removeList = new ArrayList<Connection>();
//        for (var connection : connections.values()) {
//            if (connection.session.isOpen()) {
//                if (!connection.playerName.equals(excludePlayerName)) {
//                    connection.send(message);
//                }
//            } else {
//                removeList.add(connection);
//            }
//        }
//        // clean up
//        for (var connection : removeList) {
//            connections.remove(connection.playerName);
//        }
//    }
}