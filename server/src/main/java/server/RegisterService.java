package server;

import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;
import dataAccess.DataAccess;

public class RegisterService {

    public Result register(UserData userData) {
        DataAccess data = DataAccess.getInstance();  // get correct state

        try {
            if (data.getUser(userData.username()) != null) {
                return Result.error(Result.ErrorType.ALREADY_TAKEN, "Username is already taken"); // 403
            }

            // add checks possibly for invalid email or password format
            // return Result.error(Result.ErrorType.BAD_REQUEST, "invalid input or whateva");  400

            // create new user with authToken
            data.createUser(userData);
            AuthData authData = data.createAuth(userData.username());

            return Result.success(userData.username(), authData.authToken());

        } catch (DataAccessException e){
            return Result.error(Result.ErrorType.SERVER_ERROR, "Registration failed to a server error");  // 500
        }
    }

}