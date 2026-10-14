package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerUserSuccessfully() throws Exception {
        UserService service = new UserService(
                new MemoryUserDAO(),
                new MemoryAuthDAO()
        );

        AuthData result = service.register(
                "mercy",
                "password123",
                "mercy@email.com"
        );

        assertEquals("mercy", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerDuplicateUser() throws Exception {
        UserService service = new UserService(
                new MemoryUserDAO(),
                new MemoryAuthDAO()
        );

        service.register("mercy", "password123", "mercy@email.com");

        assertThrows(IllegalStateException.class, () ->
                service.register("mercy", "anotherPassword", "other@email.com")
        );
    }

    @Test
    void registerWithMissingUsername() {
        UserService service = new UserService(
                new MemoryUserDAO(),
                new MemoryAuthDAO()
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.register(null, "password123", "mercy@email.com")
        );
    }

    @Test
    void loginSuccessfully() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        service.register("mercy", "password123", "mercy@email.com");

        AuthData result = service.login("mercy", "password123");

        assertEquals("mercy", result.username());
        assertNotNull(result.authToken());
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    void loginWithWrongPassword() throws Exception {
        UserService service = new UserService(
                new MemoryUserDAO(),
                new MemoryAuthDAO()
        );

        service.register("mercy", "password123", "mercy@email.com");

        assertThrows(IllegalStateException.class, () ->
                service.login("mercy", "wrongpassword")
        );
    }

    @Test
    void loginWithNonexistentUser() {
        UserService service = new UserService(
                new MemoryUserDAO(),
                new MemoryAuthDAO()
        );

        assertThrows(IllegalStateException.class, () ->
                service.login("unknown", "password123")
        );
    }

    @Test
    void logoutSuccessfully() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        AuthData auth = service.register(
                "mercy", "password123", "mercy@email.com"
        );

        service.logout(auth.authToken());

        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void logoutWithInvalidToken() {
        UserService service = new UserService(
                new MemoryUserDAO(),
                new MemoryAuthDAO()
        );

        assertThrows(IllegalStateException.class, () ->
                service.logout("invalid-token")
        );
    }
}
