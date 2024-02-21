package server;

import dataAccess.DataAccess;

public class ClearService {
    private final DataAccess dataAccess = new DataAccess();

    public Result clear() {
        dataAccess.clear();
        return new Result(true, "Database cleared successfully"); // should throw exception if not
    }
}