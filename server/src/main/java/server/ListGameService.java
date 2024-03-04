package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.GameData;

import java.util.List;

public class ListGameService {
    public Result listGame(String authToken) {
        // DataAccess data = DataAccess.getInstance(); // gets correct state
        MySQLDataAccess data = new MySQLDataAccess();

        try {
            AuthData authData = data.getAuth(authToken);
            if (authData == null) {
                return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized");
            }

            List<GameData> games = data.listGames();
            return Result.successListGames(games);

        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: server error");
        }
    }
}