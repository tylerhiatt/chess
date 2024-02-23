package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccessInterface {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    GameData createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    List<UserData> listUsers() throws DataAccessException;
    List<AuthData> listAuth() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}