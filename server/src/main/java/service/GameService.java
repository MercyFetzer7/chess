package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
    public List<GameData> listGames(String authToken)
            throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new IllegalStateException("Unauthorized");
        }

        return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName)
            throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new IllegalStateException("Unauthorized");
        }

        if (gameName == null || gameName.isBlank()) {
            throw new IllegalArgumentException("Bad request");
        }

        GameData game = new GameData(
                0,
                null,
                null,
                gameName,
                new chess.ChessGame()
        );

        return gameDAO.createGame(game);
    }
}
