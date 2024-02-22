package server;

import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;
import dataAccess.DataAccess;

public class RegisterService {

    public Result register(UserData userData) {
        DataAccess data = DataAccess.getInstance();  // get correct state

        try {
            // try to get user, if no exception is thrown then the user already exists
            try {
                data.getUser(userData.username());
                return Result.error(Result.ErrorType.ALREADY_TAKEN, "Username is already taken");  // 403

            } catch (DataAccessException e) {
                // register user | he doesn't exist yet
                data.createUser(userData);
                AuthData authData = data.createAuth(userData.username());
                return Result.success(userData.username(), authData.authToken());
            }

            // add 400 errors here

        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Registration failed due to a server error"); // 500
        }
    }

}