package server;

import dataAccess.DataAccess;

public class ClearService {

    public Result clear() {
        DataAccess data = DataAccess.getInstance(); // get correct state
        try {
            data.clear();
            return Result.genericSuccessService("Database cleared successfully");  // 200
        } catch (Exception e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: description");  // 500
        }

    }
}