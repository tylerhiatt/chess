package server;

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

    //// constructors
    public Result(boolean success, String username, String authToken, String email, ErrorType errorType, String message, List<GameData> games) { // for success
        this.success = success;
        this.authToken = authToken;
        this.username = username;
        this.email = email;
        this.errorType = errorType;
        this.message = message;
        this.games = games;
    }

    // success result method
    public static Result success(String username, String authToken, String email) {
        return new Result(true, username, authToken, email,null, null, null);
    }

    // error result method
    public static Result error(ErrorType errorType, String message) {
        return new Result(false, null, null, null, errorType, message, null);
    }

    // success result list games
    public static Result successListGames(List<GameData> games) {
        return new Result(true, null, null, null, null, null, games);
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

    public static class LoginSuccessResponse {
        private final List<GameData> games;
        public LoginSuccessResponse(List<GameData> games) {
            this.games = games;
        }
    }

    public static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

    }


}
