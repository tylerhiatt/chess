package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;


public class LoginService {
    public Result login(UserData userData) {
        DataAccess data = DataAccess.getInstance();  // gets correct state

        try {
            UserData user = data.getUser(userData.username());

            if (user != null && user.password().equals(userData.password())) {
                // create new token for user
                AuthData authData = data.createAuth(userData.username());
                return Result.success(userData.username(), authData.authToken(), null);  // 200
            } else {
                return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized"); // 401
            }

        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: description"); // 500
        }
    }
}