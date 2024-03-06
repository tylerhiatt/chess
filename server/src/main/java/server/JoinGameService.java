package server;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.GameData;


public class JoinGameService {
    public Result joinGame(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        // DataAccess data = DataAccess.getInstance();
        MySQLDataAccess data = MySQLDataAccess.getInstance();

        try {
            AuthData authData = data.getAuth(authToken);
            if (authData == null) {
                return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized");  // 401
            }

            GameData game = data.getGame(gameID);
            if (game == null) {
                return Result.error(Result.ErrorType.BAD_REQUEST, "Error: bad request"); // 400
            }

            // ability to join game as a watcher
            if (playerColor == null) {
                return Result.genericSuccessService("Joined as a watcher");  // 200
            }

            // if player already assigned
            if ((playerColor == ChessGame.TeamColor.WHITE && authData.username().equals(game.whiteUsername())) ||
                    (playerColor == ChessGame.TeamColor.BLACK && authData.username().equals(game.blackUsername()))) {

                return Result.genericSuccessService("Join Game successful");  // 200
            }

            // if color is already taken
            if ((playerColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null) ||
                    (playerColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null)) {
                return Result.error(Result.ErrorType.ALREADY_TAKEN, "Error: already taken");  // 403
            }

            // Assign player to the game
            GameData updatedGame;
            if (playerColor == ChessGame.TeamColor.WHITE) {
                updatedGame = new GameData(game.gameID(), authData.username(), game.blackUsername(), game.gameName(), game.game());
            } else {
                updatedGame = new GameData(game.gameID(), game.whiteUsername(), authData.username(), game.gameName(), game.game());
            }

            data.updateGame(updatedGame);

            return Result.genericSuccessService("Join Game Successful");  // 200

        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: server error"); // 500
        }


    }
}