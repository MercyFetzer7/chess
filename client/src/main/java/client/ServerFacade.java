package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient httpClient;

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

}
