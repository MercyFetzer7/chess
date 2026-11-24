package websocket.messages;

import java.util.Objects;

import chess.ChessGame;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    ChessGame game;
    String message;
    String errorMessage;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessage(ServerMessageType type, ChessGame game) {
        this.serverMessageType = type;
        this.game = game;
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        this.message = message;
    }

    public ServerMessage(ServerMessageType type, String errorMessage, boolean isError) {
        this.serverMessageType = type;
        this.errorMessage = errorMessage;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }
    public ChessGame getGame() { return game; }
    public String getMessage() { return message; }
    public String getErrorMessage() { return errorMessage; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
