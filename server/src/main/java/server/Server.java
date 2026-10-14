package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import model.AuthData;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserService userService;
    private final Gson gson = new Gson();

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userService = new UserService(userDAO, authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register endpoints
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
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

    private record ErrorMessage(String message) {}
}
