package app.persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    // the setup before start testing
    @BeforeAll
    static void setUpClass() {
        try (Connection connection = connectionPool.getConnection()) {
            // Statement is the same af PreparedStatement but more simple and less secure.
            // but for our ovn test it does not matter
            try (Statement stmt = connection.createStatement()) {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.orders");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");
                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");

                // Create sequences for auto generating id's for users and orders
                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");

            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }


    // this will run before every single test. and will delete all rows and put new data into them.
    // so we allways know we have 2 rows in carport_lenghts and 3 rows in carport_widths. we do this to insure we know
    // the result. so we can pinpoint what is correct or wrong with our tests
    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Remove all rows from all tables
                // remember the first line should be the tabel with the Foreign Key.
                // because you cannot delete the one with PK before deleting the FK
                stmt.execute("DELETE FROM test.orders");


                    stmt.execute("INSERT INTO test.orders (order_id, user_id,carport_length_id,carport_width_id,description,total_price,product_list,status_id,date,shed,user_remark,carport_drawing) " +
                            "VALUES  (1, 1, 400, 500, 'en lækker carport', 10000, 'produktliste', 1, CURRENT_DATE , true, 'henter den på tirs', 'tegning her'), (2, 2, 200, 200, 'lille carport', 15000, 'produktliste', 2, CURRENT_DATE, false, 'henter den på tors', 'tegning her')");


                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test.orders_order_id_seq', COALESCE((SELECT MAX(order_id) + 1 FROM test.orders), 1), false)");

            } catch (SQLException e) {
                e.printStackTrace();
                fail("Database connection failed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

//    @Test
//    void getOrders() {
//
//    }
}