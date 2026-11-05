package client;

import com.google.gson.Gson;
import model.AuthData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
        httpClient = HttpClient.newHttpClient();
    }

    private HttpResponse<String> sendRequest(
            String method,
            String path,
            String body,
            String authToken
    ) throws IOException, InterruptedException {

        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path));

        if (authToken != null) {
            request.header("authorization", authToken);
        }

        if (body != null) {
            request.header("Content-Type", "application/json");
        }

        request.method(
                method,
                body == null
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body)
        );

        return httpClient.send(
                request.build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    public AuthData login(String username, String password)
            throws IOException, InterruptedException {

        String body = gson.toJson(Map.of(
                "username", username,
                "password", password
        ));

        HttpResponse<String> response =
                sendRequest("POST", "/session", body, null);

        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());
        }

        return gson.fromJson(response.body(), AuthData.class);
    }
    public AuthData register(String username, String password, String email)
            throws IOException, InterruptedException {

        String body = gson.toJson(Map.of(
                "username", username,
                "password", password,
                "email", email
        ));

        HttpResponse<String> response =
                sendRequest("POST", "/user", body, null);

        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());
        }

        return gson.fromJson(response.body(), AuthData.class);
    }

}
