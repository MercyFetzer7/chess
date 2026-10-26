package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = """
            INSERT INTO auth (authToken, username)
            VALUES (?, ?)
            """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Error creating auth token", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new AuthData(
                            result.getString("authToken"),
                            result.getString("username")
                    );
                }
                return null;
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error getting auth token", ex);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Error deleting auth token", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing auth tokens", ex);
        }
    }
}
