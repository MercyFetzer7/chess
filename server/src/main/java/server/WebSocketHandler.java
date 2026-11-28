package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.UserGameCommand;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import websocket.messages.ServerMessage;

public class WebSocketHandler {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private final Gson gson = new Gson();

    public void onConnect(WsConnectContext ctx) {
        // Connection handling
    }

    public void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            try {
                AuthData auth = authDAO.getAuth(command.getAuthToken());

                if (auth == null) {
                    sendError(ctx, "Error: invalid authentication token");
                    return;
                }

                GameData game = gameDAO.getGame(command.getGameID());

                if (game == null) {
                    sendError(ctx, "Error: game not found");
                    return;
                }

                ServerMessage response = new ServerMessage(
                        ServerMessage.ServerMessageType.LOAD_GAME,
                        game.game()
                );
                ctx.send(gson.toJson(response));

            } catch (DataAccessException e) {
                sendError(ctx, "Error: unable to connect to game");
            }
        }
    }
    private void sendError(WsMessageContext ctx, String message) {
        ServerMessage response = new ServerMessage(
                ServerMessage.ServerMessageType.ERROR,
                message,
                true
        );
        ctx.send(gson.toJson(response));
    }
}