package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.MySQLDataAccess;
import dataAccess.StartDatabase;
import spark.*;
import model.UserData;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.fail;

public class Server {

    public int run(int desiredPort) {

        // initialize database and create tables
        try {
            DatabaseManager.createDatabase();
            StartDatabase.start();
        } catch (DataAccessException e) {
            fail("Database creation failed");
        }

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // register endpoints
        clearEndpoint();
        registerEndpoint();
        loginEndpoint();
        logoutEndpoint();
        listGameEndpoint();
        createGameEndpoint();
        joinGameEndpoint();

        Spark.awaitInitialization();
        return Spark.port();
    }

    // handler for clearing the database
    private void clearEndpoint() {

        Spark.delete("/db", (req, res) -> {
            var serializer = new Gson();
            ClearService clearService = new ClearService();

            Result result = clearService.clear();
            res.type("application/json");

            if (result.isSuccess()) {
                res.status(200);
                return "";  // return empty body
            } else {
                res.status(500);
                return serializer.toJson(new Result.ErrorResponse(result.getMessage()));
            }

        });
    }

    // handler for registering user
    private void registerEndpoint() {
        Spark.post("/user", (req, res) -> {
            var serializer = new Gson();
            RegisterService registerService = new RegisterService();
            UserData userData = serializer.fromJson(req.body(), UserData.class);

            Result result = registerService.register(userData);
            res.type("application/json");

            // response messages
            if (result.isSuccess()) {
                res.status(200);
                return serializer.toJson(new Result.RegisterSuccessResponse(result.getUsername(), result.getAuthToken(), null));

            } else {
                return handleError(res, result, serializer);
            }

        });
    }

    // handler for logging in user
    private void loginEndpoint() {
        Spark.post("/session", (req, res) -> {
            var serializer = new Gson();
            LoginService loginService = new LoginService();
            UserData userData = serializer.fromJson(req.body(), UserData.class);

            Result result = loginService.login(userData);
            res.type("application/json");

            // response messages
            if (result.isSuccess()) {
                res.status(200);
                return serializer.toJson(new Result.RegisterSuccessResponse(result.getUsername(), result.getAuthToken(), null));

            } else {
                return handleError(res, result, serializer);
            }

        });
    }

    // handler for logging out user
    private void logoutEndpoint() {
        Spark.delete("/session", (req, res) -> {
            var serializer = new Gson();
            String authToken = req.headers("authorization");
            LogoutService logoutService = new LogoutService();

            Result result = logoutService.logout(authToken);
            res.type("application/json");

            if (result.isSuccess()) {
                res.status(200);
                return "";  // return empty body
            } else {
                return handleError(res, result, serializer);
            }

        });
    }

    // handler for listing games
    private void listGameEndpoint() {
        Spark.get("/game", (req, res) -> {
            var serializer = new Gson();
            String authToken = req.headers("authorization");
            ListGameService listGameService = new ListGameService();

            Result result = listGameService.listGame(authToken);
            res.type("application/json");

            if (result.isSuccess()) {
                res.status(200);
                return serializer.toJson(new Result.ListGameSuccessResponse(result.getGames()));
            } else {
                return handleError(res, result, serializer);
            }

        });
    }

    // handler for create game
    private void createGameEndpoint() {
        Spark.post("/game", (req, res) -> {
           var serializer = new Gson();
           String authToken = req.headers("authorization");

           Result.GameCreationRequest request = serializer.fromJson(req.body(), Result.GameCreationRequest.class);
           if (request == null || request.gameName == null || request.gameName.trim().isEmpty()) {
               res.status(400);
               return serializer.toJson(new Result.ErrorResponse("Error: bad request"));  // handle 400 error here instead
           }

           CreateGameService createGameService = new CreateGameService();
           Result result = createGameService.createGame(authToken, request.gameName);
           res.type("application/json");

           if (result.isSuccess()) {
               res.status(200);
               return serializer.toJson(new Result.CreateGameSuccessResponse(result.getGameID()));
           }  else {
               return handleError(res, result, serializer);
           }

        });
    }

    private void joinGameEndpoint() {
        Spark.put("/game", (req, res) -> {
            var serializer = new Gson();
            String authToken = req.headers("authorization");
            Result.JoinGameRequest request = serializer.fromJson(req.body(), Result.JoinGameRequest.class);

            JoinGameService joinGameService = new JoinGameService();
            Result result = joinGameService.joinGame(authToken, request.gameID, request.playerColor);
            res.type("application/json");

            if (result.isSuccess()) {
                res.status(200);
                return "";  // return empty body
            } else {
                return handleError(res, result, serializer);
            }

        });
    }

    private String handleError(Response res, Result result, Gson serializer) {
        switch (result.getErrorType()) {
            case BAD_REQUEST:
                res.status(400);
                break;
            case UNAUTHORIZED:
                res.status(401);
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

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}