package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(String username, String password, String email)
        throws DataAccessException {
        if (username == null || username.isBlank() ||
                password == null || password.isBlank() ||
                email == null || email.isBlank()) {
            throw new IllegalArgumentException("Bad request");
        }
        if (userDAO.getUser(username) != null) {
            throw new IllegalStateException("Username already taken");
        }

        UserData user = new UserData(username, password, email);
        userDAO.createUser(user);

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);

        return auth;
    }

    public AuthData login(String username, String password)
            throws DataAccessException {

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalArgumentException("Bad request");
        }

        UserData user = userDAO.getUser(username);

        if (user == null || !user.password().equals(password)) {
            throw new IllegalStateException("Unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);

        return auth;
    }

    public void logout(String authToken) throws DataAccessException {

        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new IllegalStateException("Unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}
