
package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {

    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.configureDatabase();
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    void createAndGetUser() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@example.com");

        userDAO.createUser(user);

        assertEquals(user, userDAO.getUser("alice"));
    }

    @Test
    void getMissingUser() throws DataAccessException {
        assertNull(userDAO.getUser("missing"));
    }

    @Test
    void duplicateUserThrows() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@example.com");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    void clearRemovesUsers() throws DataAccessException {
        userDAO.createUser(new UserData("alice", "password123", "alice@example.com"));

        userDAO.clear();

        assertNull(userDAO.getUser("alice"));
    }
}
