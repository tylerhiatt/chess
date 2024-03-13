package ui;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LogoutUI {
    public void logoutUI(String authToken) {
        HttpClient client = HttpClient.newHttpClient();
        String logoutUrl = "http://localhost:8080/session";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(logoutUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // System.out.println("Logged out user with authToken " + authToken);
                System.out.println("Logged out user");
            } else {
                System.out.println("Logout failed. Here's the response body: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error logging out user: " + e.getMessage());
        }
    }
}