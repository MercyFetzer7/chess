package dataaccess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {

    @Test
    void connectionWorks() throws Exception {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT 1 + 1");
             var result = statement.executeQuery()) {

            assertTrue(result.next());
            assertEquals(2, result.getInt(1));
        }
    }

    @Test
    void configureDatabaseWorks() throws Exception {
        DatabaseManager.configureDatabase();

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.createStatement();
             var result = statement.executeQuery("SHOW TABLES")) {

            assertTrue(result.next());
        }
    }
}
