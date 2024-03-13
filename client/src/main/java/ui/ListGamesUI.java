package ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ListGamesUI {
    public void listGamesUI(String authToken) {
        HttpClient client = HttpClient.newHttpClient();
        Gson serializer = new Gson();

        String listGameUrl = "http://localhost:8080/game";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(listGameUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // parse response body into a list of games

                // use type token to keep track of types to get what response types should be
                Map<String, List<Map<String, Object>>> responseMap = serializer.fromJson(response.body(),
                        new TypeToken<Map<String, List<Map<String, Object>>>>(){}.getType());

                List<Map<String, Object>> gamesList = responseMap.get("games");

                System.out.println("List of games:");
                for (Map<String, Object> game : gamesList) {
                    int gameID = ((Number) game.get("gameID")).intValue();
                    String gameName = (String) game.get("gameName");
                    String whiteUsername = (String) game.get("whiteUsername");
                    String blackUsername = (String) game.get("blackUsername");

                    System.out.println("Game ID: " + gameID + ", Game Name: " + gameName + ", White Player: " + whiteUsername + ", Black Player: " + blackUsername);
                }
            } else {
                System.out.println("List games failed. Here's the response body: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error listing games: " + e.getMessage());
        }
    }
}