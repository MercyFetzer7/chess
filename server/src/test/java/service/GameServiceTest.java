package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Test
    void listGamesSuccess() throws Exception {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO, authDAO);

        authDAO.createAuth(new AuthData("valid-token", "mercy"));

        gameDAO.createGame(new GameData(
                0, null, null, "Game One", new ChessGame()));
        gameDAO.createGame(new GameData(
                0, null, null, "Game Two", new ChessGame()));

        List<GameData> games = service.listGames("valid-token");

        assertEquals(2, games.size());
    }

    @Test
    void listGamesUnauthorized() {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO, authDAO);

        assertThrows(IllegalStateException.class,
                () -> service.listGames("invalid-token"));
    }

    @Test
    void createGameSuccess() throws Exception {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO, authDAO);

        authDAO.createAuth(new AuthData("valid-token", "mercy"));

        int gameID = service.createGame("valid-token", "My Chess Game");

        assertTrue(gameID > 0);

        GameData game = gameDAO.getGame(gameID);
        assertNotNull(game);
        assertEquals("My Chess Game", game.gameName());
    }

    @Test
    void createGameUnauthorized() {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO, authDAO);

        assertThrows(IllegalStateException.class,
                () -> service.createGame("invalid-token", "My Chess Game"));
    }

    @Test
    void createGameBadRequest() throws Exception {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO, authDAO);

        authDAO.createAuth(new AuthData("valid-token", "mercy"));

        assertThrows(IllegalArgumentException.class,
                () -> service.createGame("valid-token", ""));
    }
}
