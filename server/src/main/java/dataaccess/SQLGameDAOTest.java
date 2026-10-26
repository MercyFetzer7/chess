
package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {

    private SQLGameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.configureDatabase();
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    void createAndGetGame() throws DataAccessException {
        GameData game = new GameData(
                0, null, null, "Test Game", new ChessGame()
        );

        int gameID = gameDAO.createGame(game);
        GameData saved = gameDAO.getGame(gameID);

        assertNotNull(saved);
        assertEquals(gameID, saved.gameID());
        assertEquals("Test Game", saved.gameName());
        assertNotNull(saved.game());
    }

    @Test
    void getMissingGame() throws DataAccessException {
        assertNull(gameDAO.getGame(999));
    }

    @Test
    void listGames() throws DataAccessException {
        gameDAO.createGame(new GameData(
                0, null, null, "Game One", new ChessGame()
        ));
        gameDAO.createGame(new GameData(
                0, null, null, "Game Two", new ChessGame()
        ));

        assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    void updateGame() throws DataAccessException {
        int gameID = gameDAO.createGame(new GameData(
                0, null, null, "Original", new ChessGame()
        ));

        gameDAO.updateGame(new GameData(
                gameID, "alice", null, "Original", new ChessGame()
        ));

        assertEquals("alice", gameDAO.getGame(gameID).whiteUsername());
    }

    @Test
    void updateMissingGameThrows() {
        GameData missing = new GameData(
                999, null, null, "Missing", new ChessGame()
        );

        assertThrows(
                DataAccessException.class,
                () -> gameDAO.updateGame(missing)
        );
    }

    @Test
    void clearRemovesGames() throws DataAccessException {
        gameDAO.createGame(new GameData(
                0, null, null, "Test Game", new ChessGame()
        ));

        gameDAO.clear();

        assertTrue(gameDAO.listGames().isEmpty());
    }
}
