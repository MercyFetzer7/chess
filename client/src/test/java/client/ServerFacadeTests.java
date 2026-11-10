package client;

import org.junit.jupiter.api.*;
import server.Server;
import model.AuthData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/db"))
                .DELETE()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void registerSuccess() throws Exception {
        AuthData result = facade.register(
                "testUser", "password123", "test@email.com"
        );

        Assertions.assertEquals("testUser", result.username());
        Assertions.assertNotNull(result.authToken());
    }
    @Test
    public void registerDuplicateUser() throws Exception {
        facade.register("testUser", "password123", "test@email.com");

        Assertions.assertThrows(RuntimeException.class, () ->
                facade.register("testUser", "password123", "test@email.com")
        );
    }

    @Test
    public void loginSuccess() throws Exception {
        facade.register("testUser", "password123", "test@email.com");

        AuthData result = facade.login("testUser", "password123");

        Assertions.assertEquals("testUser", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void loginWrongPassword() throws Exception {
        facade.register("testUser", "password123", "test@email.com");

        Assertions.assertThrows(RuntimeException.class, () ->
                facade.login("testUser", "wrongPassword")
        );
    }

    @Test
    public void logoutSuccess() throws Exception {
        AuthData auth = facade.register(
                "testUser", "password123", "test@email.com"
        );

        Assertions.assertDoesNotThrow(() ->
                facade.logout(auth.authToken())
        );
    }

    @Test
    public void logoutInvalidToken() {
        Assertions.assertThrows(RuntimeException.class, () ->
                facade.logout("invalidToken")
        );
    }

    @Test
    public void listGamesSuccess() throws Exception {
        AuthData auth = facade.register(
                "testUser", "password123", "test@email.com"
        );

        var games = facade.listGames(auth.authToken());

        Assertions.assertNotNull(games);
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void listGamesUnauthorized() {
        Assertions.assertThrows(RuntimeException.class, () ->
                facade.listGames("invalidToken")
        );
    }

    @Test
    public void createGameSuccess() throws Exception {
        AuthData auth = facade.register(
                "testUser", "password123", "test@email.com"
        );

        int gameID = facade.createGame(auth.authToken(), "Test Game");

        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameUnauthorized() {
        Assertions.assertThrows(RuntimeException.class, () ->
                facade.createGame("invalidToken", "Test Game")
        );
    }

    @Test
    public void joinGameSuccess() throws Exception {
        AuthData auth = facade.register(
                "testUser", "password123", "test@email.com"
        );

        int gameID = facade.createGame(auth.authToken(), "Test Game");

        Assertions.assertDoesNotThrow(() ->
                facade.joinGame(auth.authToken(), gameID, "WHITE")
        );
    }

    @Test
    public void joinGameUnauthorized() {
        Assertions.assertThrows(RuntimeException.class, () ->
                facade.joinGame("invalidToken", 1, "WHITE")
        );
    }

}
