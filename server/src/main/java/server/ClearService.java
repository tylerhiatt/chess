package server;

import dataAccess.DataAccess;

public class ClearService {

    public Result clear() {
        DataAccess data = DataAccess.getInstance(); // get correct state
        try {
            data.clear();
            return new Result(true, null, null, null, null, "Database cleared successfully", null);  // 200
        } catch (Exception e) {
            return Result.error(Result.ErrorType.SERVER_ERROR, "Error: description");  // 500
        }

    }
}