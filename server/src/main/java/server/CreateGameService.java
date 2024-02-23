package server;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class CreateGameService {
    public Result createGame(String authToken, String gameName) {
        DataAccess data = DataAccess.getInstance();

        try {
            AuthData authData = data.getAuth(authToken);
            if (authData == null) {
                return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized"); // 401
            }

            GameData game = data.createGame(new GameData(0, authData.username(), null, gameName, new ChessGame()));
            return Result.successCreateGame(game.gameID()); // 200

        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: description");  // 500
        }
    }
}