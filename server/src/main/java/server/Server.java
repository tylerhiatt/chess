package server;

import com.google.gson.Gson;
import spark.*;
import model.UserData;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        clearEndpoint();
        registerEndpoint();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void clearEndpoint() {
        // route for clearing the database
        Spark.delete("/db", (req, res) -> {
            var serializer = new Gson();
            ClearService clearService = new ClearService();
            Result result = clearService.clear();

            res.type("application/json");

            if (result.isSuccess()) {
                res.status(200);
                return serializer.toJson(result.getMessage());  // maybe switch to just return nothing?
            } else {
                res.status(500);
                return serializer.toJson(new Result.ErrorResponse(result.getMessage()));
            }

        });
    }

    private void registerEndpoint() {
        // route for registering user
        Spark.post("/user", (req, res) -> {
            var serializer = new Gson();
            RegisterService registerService = new RegisterService();
            UserData userData = serializer.fromJson(req.body(), UserData.class);

            Result result = registerService.register(userData);
            res.type("application/json");

            // response messages
            if (result.isSuccess()) {
                res.status(200);
                return serializer.toJson(new Result.RegisterSuccessResponse(result.getUsername(), result.getAuthToken()));

            } else {
                // handle error cases
                switch (result.getErrorType()) {
                    case BAD_REQUEST:
                        res.status(400);
                        break;
                    case ALREADY_TAKEN:
                        res.status(403);
                        break;
                    case SERVER_ERROR:
                    default:
                        res.status(500);
                        break;
                }
                return serializer.toJson(new Result.ErrorResponse(result.getMessage()));
            }

        });

    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}