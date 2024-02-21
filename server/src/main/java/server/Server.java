package server;

import com.google.gson.Gson;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        clearEndpoint();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void clearEndpoint() {
        // route for clearing the database
        Spark.delete("/db", (req, res) -> {
            ClearService service = new ClearService();
            Result result = service.clear();

            res.type("application/json");

            return new Gson().toJson(result);
        });
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}