package ui;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.util.Map;
import com.google.gson.Gson;


public class LoginUI {

    private String authToken = null;

    public String loginUI(int port, String username, String password) {
        HttpClient client = HttpClient.newHttpClient();
        Gson serializer = new Gson();

        String loginUrl = "http://localhost:" + port + "/session";  // prob need to change later idk

        Map<Object, Object> data = Map.of("username", username, "password", password);
        String requestBody = serializer.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // check if login was successful -> need 200 status code
            if (response.statusCode() == 200) {
                System.out.println("Logged in as " + username);

                // need to get auth token and store for future requests
                Map<String, String> responseMap = serializer.fromJson(response.body(), Map.class);
                authToken = responseMap.get("authToken");
                // System.out.println("AuthToken: " + authToken);

            } else {
                System.out.println("Login failed. Here's the response body: " + response.body());
                return null;  // sets authToken to null
            }

        } catch (Exception e) {
            System.out.println("Error logging in: " + e.getMessage());
            return null; // sets authToken to null
        }

        return authToken;
    }
}