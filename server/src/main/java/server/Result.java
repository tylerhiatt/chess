package server;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public class Result {
    private boolean success;
    private String message;
    private String username;
    private String authToken;
    private ErrorType errorType;
    private String email;
    private List<GameData> games;
    private int gameID;
    private ChessGame.TeamColor playerColor;

    //// constructors
    public Result(boolean success, String username, String authToken, String email, ErrorType errorType, String message, List<GameData> games, int gameID, ChessGame.TeamColor playerColor) { // for success
        this.success = success;
        this.authToken = authToken;
        this.username = username;
        this.email = email;
        this.errorType = errorType;
        this.message = message;
        this.games = games;
        this.gameID = gameID;
        this.playerColor = playerColor;

    }

    // success result register and login
    public static Result successRegisterAndLogin(String username, String authToken, String email) {
        return new Result(true, username, authToken, email,null, null, null, 0, null);
    }

    // success result list games
    public static Result successListGames(List<GameData> games) {
        return new Result(true, null, null, null, null, null, games, 0, null);
    }

    // success creating game
    public static Result successCreateGame(int gameID) {
        return new Result(true, null, null, null, null, null, null, gameID, null);
    }

    public static Result genericSuccessService(String message) {
        return new Result(true, null, null, null, null, message, null, 0, null);
    }

    // error result
    public static Result error(ErrorType errorType, String message) {
        return new Result(false, null, null, null, errorType, message, null, 0, null);
    }

    //// Getters
    public boolean isSuccess() {
        return success;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthToken() {
        return authToken;
    }
    public String getEmail() { return email; }

    public ErrorType getErrorType() {
        return errorType;
    }
    public List<GameData> getGames() { return games; }
    public int getGameID() {return gameID; }

    // enum for errors
    public enum ErrorType {
        ALREADY_TAKEN, BAD_REQUEST, SERVER_ERROR, UNAUTHORIZED
    }

    //// static classes for success/error responses for various services
    public static class RegisterSuccessResponse {
        private final String username;
        private final String authToken;
        private final String email;

        public RegisterSuccessResponse(String username, String authToken, String email) {
            this.username = username;
            this.authToken = authToken;
            this.email = email;

        }
    }

    public static class ListGameSuccessResponse {
        private final List<GameData> games;
        public ListGameSuccessResponse(List<GameData> games) {
            this.games = games;
        }
    }

    public static class CreateGameSuccessResponse {
        private final int gameID;

        public CreateGameSuccessResponse(int gameID){
            this.gameID = gameID;
        }

    }

    public static class GameCreationRequest {
        String gameName;
    }

    public static class JoinGameRequest {
        ChessGame.TeamColor playerColor;
        int gameID;
    }

    public static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

    }


}
