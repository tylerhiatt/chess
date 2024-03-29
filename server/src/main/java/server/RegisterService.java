package server;

import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.UserData;
import model.AuthData;
import dataAccess.DataAccess;

public class RegisterService {

    public Result register(UserData userData) {
        // DataAccess data = DataAccess.getInstance();  // get correct state
        MySQLDataAccess data = MySQLDataAccess.getInstance();

        try {
            // Check for invalid input
            if (userData.username() == null || userData.username().isEmpty() ||
                    userData.password() == null || userData.password().isEmpty() ||
                    userData.email() == null || userData.email().isEmpty() ) {
                return Result.error(Result.ErrorType.BAD_REQUEST, "Error: bad request"); // 400
            }

            // check if user already exists
            UserData existingUser = data.getUser(userData.username());
            if (existingUser != null) {
                return Result.error(Result.ErrorType.ALREADY_TAKEN, "Error: already taken");  // 403
            }

            // register user given he doesn't exist yet
            data.createUser(userData);
            AuthData authData = data.createAuth(userData.username());
            return Result.successRegisterAndLogin(userData.username(), authData.authToken(), userData.email());  // 200

        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: server error"); // 500
        }
    }

}