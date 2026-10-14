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
}
