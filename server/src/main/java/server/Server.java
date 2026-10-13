package server;

import dataaccess.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", ctx -> {
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
        });

        javalin.post("/user", ctx -> ctx.result("{}"));

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private record ErrorMessage(String message) {}
}
