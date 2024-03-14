package ui;

import chess.ChessGame;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class JoinGameUI {
    public void joinGameUI(String authToken, int gameID, String playerColor) {
        HttpClient client = HttpClient.newHttpClient();
        Gson serializer = new Gson();

        String joinGameUrl = "http://localhost:8080/game";

        // put gameID and playerColor in hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("gameID", gameID);

        if (playerColor == null) {
            data.put("playerColor", null);
        } else {
            data.put("playerColor", playerColor.toUpperCase()); // "WHITE" or "BLACK"
        }
        String requestBody = serializer.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(joinGameUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (playerColor == null) {
                    System.out.println("Joined Game with gameID " + gameID + " as observer");
                } else if (playerColor.isEmpty()) {
                    System.out.println("Joined Game with gameID " + gameID + " as empty player");
                } else {
                    System.out.println("Joined Game with gameID " + gameID + " as " + playerColor + " player");
                }

            } else {
                System.out.println("Failed to join game. Here's the response body: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error joining game: " + e.getMessage());
        }

    }
}