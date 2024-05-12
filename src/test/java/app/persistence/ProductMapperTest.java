package app.persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
//Integrationstest for ProductMapper
class ProductMapperTest {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    // this is running all of them. before all test starts this will run before.
    @BeforeAll
    static void setUpClass()
    {
        try (Connection connection = connectionPool.getConnection())
        {
            // Statement is the same af PreparedStatement but more simple and less secure.
            // but for our ovn test it does not matter
            try (Statement stmt = connection.createStatement())
            {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.carport_lengths");
                stmt.execute("DROP TABLE IF EXISTS test.carport_widths");
                stmt.execute("DROP SEQUENCE IF EXISTS test.carport_lengths_carport_length_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.carport_widths_carport_width_id_seq CASCADE;");
                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test.carport_lengths AS (SELECT * from public.carport_lengths) WITH NO DATA");
                stmt.execute("CREATE TABLE test.carport_widths AS (SELECT * from public.carport_widths) WITH NO DATA");
                // Create sequences for auto generating id's for users and orders
                stmt.execute("CREATE SEQUENCE test.carport_lengths_carport_length_id_seq");
                stmt.execute("ALTER TABLE test.carport_lengths ALTER COLUMN user_id SET DEFAULT nextval('test.carport_lengths_carport_length_id_seq')");
                stmt.execute("CREATE SEQUENCE test.carport_widths_carport_width_id_seq");
                stmt.execute("ALTER TABLE test.carport_widths ALTER COLUMN order_id SET DEFAULT nextval('test.carport_widths_carport_width_id_seq')");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    }

    // this will run before every single test
    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllLength() {
    }

    @Test
    void getAllwidth() {
    }
}