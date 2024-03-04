package server;

import dataAccess.DataAccess;
import dataAccess.MySQLDataAccess;

public class ClearService {

    public Result clear() {
        // DataAccess data = DataAccess.getInstance(); // get correct state
        MySQLDataAccess data = new MySQLDataAccess();

        try {
            data.clear();
            return Result.genericSuccessService("Database cleared successfully");  // 200
        } catch (Exception e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: server error");  // 500
        }

    }
}