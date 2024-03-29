package dataAccess;

import model.UserData;
import model.GameData;
import model.AuthData;

import java.util.*;

public class DataAccess implements DataAccessInterface {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private int nextGameId = 1;
    private static DataAccess instance = null;

    // keep instance the same across classes
    public static DataAccess getInstance() {
        if (instance == null) {
            instance = new DataAccess();
        }
        return instance;
    }

    @Override
    public void clear() {
        users.clear();
        games.clear();
        authTokens.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (users.containsKey(userData.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(userData.username(), userData); // puts new user in hashmap
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User does not exist");
        }
        return users.get(username);
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        int newGameId = nextGameId++;
        GameData newGame = new GameData(newGameId, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(newGameId, newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);  // allowed to return null, exception handled in joinGameService
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Cannot update game");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();  // generates random string for token

        AuthData newAuth = new AuthData(authToken, username);
        authTokens.put(authToken, newAuth);

        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken); // allowed to return null
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Cannot delete auth Token");
        }
        authTokens.remove(authToken);
    }


}