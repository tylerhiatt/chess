package server;

public class Result {
    private boolean success;
    private String message;
    private String username;
    private String authToken;
    private ErrorType errorType;
    private String email;

    //// constructors
    public Result(boolean success, String username, String authToken, String email, ErrorType errorType, String message) { // for success
        this.success = success;
        this.authToken = authToken;
        this.username = username;
        this.email = email;
        this.errorType = errorType;
        this.message = message;
    }

    // methods for success or error result
    public static Result success(String username, String authToken, String email) {
        return new Result(true, username, authToken, email,null, null);
    }

    // Method to create an error result
    public static Result error(ErrorType errorType, String message) {
        return new Result(false, null, null, null, errorType, message);
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

    // enum for errors
    public enum ErrorType {
        ALREADY_TAKEN, BAD_REQUEST, SERVER_ERROR, UNAUTHORIZED
    }

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

    public static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

    }


}
