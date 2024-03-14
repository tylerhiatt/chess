package ui;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class RegisterUI {
    public void registerUI(int port, String username, String password, String email) {
        HttpClient client = HttpClient.newHttpClient();
        Gson serializer = new Gson();

        String registerUrl = "http://localhost:" + port + "/user";

        Map<Object, Object> data = Map.of("username", username, "password", password, "email", email);
        String requestBody = serializer.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(registerUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Registered new user " + username);
            } else {
                System.out.println("Registration failed. Here's the response body: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

}