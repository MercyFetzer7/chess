
package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = """
            INSERT INTO game (whiteUsername, blackUsername, gameName, game)
            VALUES (?, ?, ?, ?)
            """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(
                     sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));

            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new DataAccessException("Failed to create game");
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error creating game", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setInt(1, gameID);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return readGame(result);
                }
                return null;
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error getting game", ex);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM game";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql);
             var result = statement.executeQuery()) {

            while (result.next()) {
                games.add(readGame(result));
            }

            return games;

        } catch (SQLException ex) {
            throw new DataAccessException("Error listing games", ex);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
            UPDATE game
            SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
            WHERE gameID = ?
            """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));
            statement.setInt(5, game.gameID());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DataAccessException("Game does not exist");
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error updating game", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.createStatement()) {

            statement.executeUpdate("DELETE FROM game");
            statement.executeUpdate("ALTER TABLE game AUTO_INCREMENT = 1");

        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing games", ex);
        }
    }

    private GameData readGame(java.sql.ResultSet result) throws SQLException {
        String gameJson = result.getString("game");

        return new GameData(
                result.getInt("gameID"),
                result.getString("whiteUsername"),
                result.getString("blackUsername"),
                result.getString("gameName"),
                gson.fromJson(gameJson, chess.ChessGame.class)
        );
    }
}
