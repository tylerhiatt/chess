package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class LoginService {
    public Result login(UserData userData) {
        // DataAccess data = DataAccess.getInstance();  // gets correct state
        MySQLDataAccess data = MySQLDataAccess.getInstance();

        try {

            // add another try catch block specifically for when the user doesn't exist to pass the tests
            UserData user;
            try {
                user = data.getUser(userData.username());
                if (user == null) {
                    return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized"); // 401
                }

                // add password matching for password encoder
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                if (!encoder.matches(userData.password(), user.password())) {
                    return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized"); // 401
                }

            } catch (DataAccessException e) {
                if ("User does not exist".equals(e.getMessage())) {
                    return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized"); // 401
                } else {
                    throw e; // if different data access issue, just rethrow it
                }

            }

            // Correct password, proceed with auth token creation
            AuthData authData = data.createAuth(userData.username());
            return Result.successRegisterAndLogin(userData.username(), authData.authToken(), user.email()); // 200


        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: server error"); // 500
        }
    }
}