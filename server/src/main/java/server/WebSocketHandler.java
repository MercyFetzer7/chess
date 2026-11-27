package server;

import com.google.gson.Gson;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {

    private final Gson gson = new Gson();

    public void onConnect(WsConnectContext ctx) {
        // Connection handling
    }

    public void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            // Handle CONNECT in the next step.
        }
    }
}