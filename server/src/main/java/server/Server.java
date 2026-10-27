package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import io.javalin.Javalin;
import model.AuthData;
import service.UserService;
import service.GameService;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public Server() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new io.javalin.json.JavalinGson());
        });
        // Register endpoints
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }

    // DELETE /db
    private void clear(io.javalin.http.Context ctx) {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    // POST /user
    private void register(io.javalin.http.Context ctx) {
        try {
            RegisterRequest request =
                    gson.fromJson(ctx.body(), RegisterRequest.class);

            if (request == null) {
                ctx.status(400);
                ctx.json(new ErrorMessage("Error: bad request"));
                return;
            }

            AuthData auth = userService.register(
                    request.username(),
                    request.password(),
                    request.email()
            );

            ctx.status(200);
            ctx.json(new RegisterResult(
                    auth.username(),
                    auth.authToken()
            ));

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.json(new ErrorMessage("Error: bad request"));

        } catch (IllegalStateException e) {
            ctx.status(403);
            ctx.json(new ErrorMessage("Error: already taken"));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    // POST /session
    private void login(io.javalin.http.Context ctx) {
        try {
            LoginRequest request =
                    gson.fromJson(ctx.body(), LoginRequest.class);

            if (request == null) {
                ctx.status(400);
                ctx.json(new ErrorMessage("Error: bad request"));
                return;
            }

            AuthData auth = userService.login(
                    request.username(),
                    request.password()
            );

            ctx.status(200);
            ctx.json(new RegisterResult(
                    auth.username(),
                    auth.authToken()
            ));

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.json(new ErrorMessage("Error: bad request"));

        } catch (IllegalStateException e) {
            ctx.status(401);
            ctx.json(new ErrorMessage("Error: unauthorized"));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    // DELETE /session
    private void logout(io.javalin.http.Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            userService.logout(authToken);

            ctx.status(200);
            ctx.result("{}");

        } catch (IllegalStateException e) {
            ctx.status(401);
            ctx.json(new ErrorMessage("Error: unauthorized"));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    // GET /game
    private void listGames(io.javalin.http.Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            var games = gameService.listGames(authToken);

            ctx.status(200);
            ctx.json(new ListGamesResult(games));

        } catch (IllegalStateException e) {
            ctx.status(401);
            ctx.json(new ErrorMessage("Error: unauthorized"));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    // POST /game
    private void createGame(io.javalin.http.Context ctx) {
        try {
            CreateGameRequest request =
                    gson.fromJson(ctx.body(), CreateGameRequest.class);

            if (request == null) {
                ctx.status(400);
                ctx.json(new ErrorMessage("Error: bad request"));
                return;
            }

            String authToken = ctx.header("authorization");

            int gameID = gameService.createGame(
                    authToken, request.gameName());

            ctx.status(200);
            ctx.json(new CreateGameResult(gameID));

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.json(new ErrorMessage("Error: bad request"));

        } catch (IllegalStateException e) {
            ctx.status(401);
            ctx.json(new ErrorMessage("Error: unauthorized"));

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    // PUT /game
    private void joinGame(io.javalin.http.Context ctx) {
        try {
            JoinGameRequest request =
                    gson.fromJson(ctx.body(), JoinGameRequest.class);

            if (request == null || request.playerColor() == null) {
                ctx.status(400);
                ctx.json(new ErrorMessage("Error: bad request"));
                return;
            }

            String authToken = ctx.header("authorization");

            gameService.joinGame(
                    authToken,
                    request.gameID(),
                    request.playerColor()
            );

            ctx.status(200);
            ctx.json(new EmptyResult());

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.json(new ErrorMessage("Error: bad request"));
        } catch (IllegalStateException e) {
            if ("Unauthorized".equals(e.getMessage())) {
                ctx.status(401);
                ctx.json(new ErrorMessage("Error: unauthorized"));
            } else {
                ctx.status(403);
                ctx.json(new ErrorMessage("Error: already taken"));
            }
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private record RegisterRequest(
            String username,
            String password,
            String email
    ) {}

    private record RegisterResult(
            String username,
            String authToken
    ) {}

    private record ErrorMessage(
            String message
    ) {}

    private record LoginRequest(
            String username,
            String password
    ) {}

    private record ListGamesResult(
            java.util.List<model.GameData> games
    ) {}
    private record CreateGameRequest(
            String gameName
    ) {}

    private record CreateGameResult(
            int gameID
    ) {}

    private record JoinGameRequest(
            int gameID,
            String playerColor
    ) {}

    private record EmptyResult() {}
}
