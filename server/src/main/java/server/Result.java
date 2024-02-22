package server;

public class Result {
    private boolean success;
    private String message;
    private String username;
    private String authToken;
    private ErrorType errorType;

    //// constructors
    public Result(boolean success, String authToken, String username, ErrorType errorType, String message) { // for success
        this.success = success;
        this.authToken = authToken;
        this.username = username;
        this.errorType = errorType;
        this.message = message;
    }

    // methods for success or error result
    public static Result success(String username, String authToken) {
        return new Result(true, authToken, username, null, null);
    }

    // Method to create an error result
    public static Result error(ErrorType errorType, String message) {
        return new Result(false, null, null, errorType, message);
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

    public ErrorType getErrorType() {
        return errorType;
    }

    // enum for errors
    public enum ErrorType {
        ALREADY_TAKEN, BAD_REQUEST, SERVER_ERROR
    }

    public static class RegisterSuccessResponse {
        private final String username;
        private final String authToken;

        public RegisterSuccessResponse(String username, String authToken) {
            this.username = username;
            this.authToken = authToken;

        }
    }

    public static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

    }


}
