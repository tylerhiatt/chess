package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;


public class LoginService {
    public Result login(UserData userData) {
        DataAccess data = DataAccess.getInstance();  // gets correct state

        try {

            // add another try catch block specifically for when the user doesn't exist to pass the tests
            UserData user;
            try {
                user = data.getUser(userData.username());

            } catch (DataAccessException e) {
                if ("User does not exist".equals(e.getMessage())) {
                    return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized"); // 401
                } else {
                    throw e; // if different data access issue, just rethrow it
                }
            }

            if (!user.password().equals(userData.password())) {
                return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized"); // 401
            }

            // Correct password, proceed with auth token creation
            AuthData authData = data.createAuth(userData.username());
            return Result.successRegisterAndLogin(userData.username(), authData.authToken(), user.email()); // 200


        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: server error"); // 500
        }
    }
}