package ui;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class CreateGameUI {

    public void createGameUI(String authToken, String gameName) {
        HttpClient client = HttpClient.newHttpClient();
        Gson serializer = new Gson();

        String createGameUrl = "http://localhost:8080/game";

        Map<String, String> data = Map.of("gameName", gameName);
        String requestBody = serializer.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(createGameUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // grab gameID needed for other commands
                Map<String, Object> responseMap = serializer.fromJson(response.body(), Map.class);
                Number gameIDNum = (Number) responseMap.get("gameID");
                int gameID = gameIDNum.intValue();

                if (gameID > -1) {
                    System.out.println("Game " + gameName + " created successfully with gameID " + gameID);
                } else {
                    System.out.println("GameID " + gameID + "invalid for some reason lol. Game creation failed");
                }

            } else {
                System.out.println("Game creation failed. Here's the response body: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error creating game: " + e.getMessage());
        }
    }
}