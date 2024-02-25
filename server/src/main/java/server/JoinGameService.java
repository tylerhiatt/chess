package server;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;


public class JoinGameService {
    public Result joinGame(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        DataAccess data = DataAccess.getInstance();

        try {
            AuthData authData = data.getAuth(authToken);
            if (authData == null) {
                return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized");  // 401
            }

            GameData game = data.getGame(gameID);
            if (game == null) {
                return Result.error(Result.ErrorType.BAD_REQUEST, "Error: bad request"); // 400
            }

            if (playerColor != null) {
                if ((playerColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null) ||
                        (playerColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null)) {
                    return Result.error(Result.ErrorType.ALREADY_TAKEN, "Error: already taken");
                }

                // update gameData with new player's turn
                GameData updatedGame = null;
                if (playerColor == ChessGame.TeamColor.WHITE) {
                    updatedGame = new GameData(game.gameID(), authData.username(), game.blackUsername(), game.gameName(), game.game());
                } else if (playerColor == ChessGame.TeamColor.BLACK) {
                    updatedGame = new GameData(game.gameID(), authData.username(), game.whiteUsername(), game.gameName(), game.game());
                }
                // assert updatedGame != null;
                data.updateGame(updatedGame);
            }

            return new Result(true, null, null, null, null, "Join Game Successful", null, 0, null);

        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: description");
        }


    }
}