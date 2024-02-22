package server;

import dataAccess.DataAccess;

public class ClearService {

    public Result clear() {
        DataAccess data = DataAccess.getInstance(); // get correct state
        try {
            data.clear();
            return new Result(true, null, null, null, "Database cleared successfully");
        } catch (Exception e) {
            return new Result(false, null, null, Result.ErrorType.SERVER_ERROR, "Database clearing failed");
        }

    }
}