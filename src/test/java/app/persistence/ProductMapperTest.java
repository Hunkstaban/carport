package app.persistence;

import app.entities.CarportLength;
import app.entities.CarportWidth;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//Integrationstest for ProductMapper
class ProductMapperTest {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    // the setup before start testing
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
                stmt.execute("ALTER TABLE test.carport_lengths ALTER COLUMN carport_length_id SET DEFAULT nextval('test.carport_lengths_carport_length_id_seq')");
                stmt.execute("CREATE SEQUENCE test.carport_widths_carport_width_id_seq");
                stmt.execute("ALTER TABLE test.carport_widths ALTER COLUMN carport_width_id SET DEFAULT nextval('test.carport_widths_carport_width_id_seq')");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }



    // this will run before every single test. and will delete all rows and put new data into them.
    // so we allways know we have 2 rows in carport_lenghts and 3 rows in carport_widths. we do this to insure we know
    // the result. so we can pinpoint what is correct or wrong with our tests
    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                // Remove all rows from all tables
                // remember the first line should be the tabel with the Foreign Key.
                // because you cannot delete the one with PK before deleting the FK
                stmt.execute("DELETE FROM test.carport_widths");
                stmt.execute("DELETE FROM test.carport_lengths");

                stmt.execute("INSERT INTO test.carport_lengths (carport_length_id, length) " +
                        "VALUES  (1, 333), (2, 450)");

                stmt.execute("INSERT INTO test.carport_widths (carport_width_id, width) " +
                        "VALUES (1, 600), (2, 540), (3, 480)") ;
                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test.carport_widths_carport_width_id_seq', COALESCE((SELECT MAX(carport_width_id) + 1 FROM test.carport_widths), 1), false)");
                stmt.execute("SELECT setval('test.carport_lengths_carport_length_id_seq', COALESCE((SELECT MAX(carport_length_id) + 1 FROM test.carport_lengths), 1), false)");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @Test
    void getAllLength() {
        try
        {
            int expected = 2;
            List<CarportLength> actualLenghts = ProductMapper.getAllLength(connectionPool);
            assertEquals(expected, actualLenghts.size());
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void getAllwidth() {
        try
        {
            int expected = 3;
            List<CarportWidth> actualWidths = ProductMapper.getAllwidth(connectionPool);
            assertEquals(expected, actualWidths.size());
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }

    }
}