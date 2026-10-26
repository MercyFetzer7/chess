
package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTest {

    private SQLAuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.configureDatabase();
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    void createAndGetAuth() throws DataAccessException {
        AuthData auth = new AuthData("token123", "alice");

        authDAO.createAuth(auth);

        assertEquals(auth, authDAO.getAuth("token123"));
    }

    @Test
    void getMissingAuth() throws DataAccessException {
        assertNull(authDAO.getAuth("missing"));
    }

    @Test
    void deleteAuth() throws DataAccessException {
        AuthData auth = new AuthData("token123", "alice");
        authDAO.createAuth(auth);

        authDAO.deleteAuth("token123");

        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    void clearRemovesAllAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("token1", "alice"));
        authDAO.createAuth(new AuthData("token2", "bob"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }
}

