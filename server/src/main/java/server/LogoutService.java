package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;

import javax.xml.crypto.Data;

public class LogoutService {
    public Result logout(String authToken) {
        DataAccess data = DataAccess.getInstance();  // gets correct state

        try {
            AuthData authData = data.getAuth(authToken);
            if (authData != null) {
                data.deleteAuth(authToken);
                return Result.genericSuccessService("Logout Successful");  // 200
            } else {
                return Result.error(Result.ErrorType.UNAUTHORIZED, "Error: unauthorized");
            }

        } catch (DataAccessException e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: server error");
        }
    }
}