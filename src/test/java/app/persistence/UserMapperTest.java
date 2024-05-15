package app.persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    // the setup before start testing
    @BeforeAll
    static void setUpClass() {
        try (Connection connection = connectionPool.getConnection()) {
            // Statement is the same af PreparedStatement but more simple and less secure.
            // but for our ovn test it does not matter
            try (Statement stmt = connection.createStatement()) {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.users");

                stmt.execute("DROP SEQUENCE IF EXISTS test.users_user_id_seq CASCADE;");

                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test.users AS (SELECT * from public.users) WITH NO DATA");

                // Create sequences for auto generating id's for users and orders
                stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");


            } catch (SQLException e) {
                e.printStackTrace();
                fail("Database connection failed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        // this will run before every single test. and will delete all rows and put new data into them.
        // so we allways know we have 2 rows in carport_lenghts and 3 rows in carport_widths. we do this to insure we know
        // the result. so we can pinpoint what is correct or wrong with our tests
        @BeforeEach
       void setUp () {
            try (Connection connection = connectionPool.getConnection()) {
                try (Statement stmt = connection.createStatement()) {
                    // Remove all rows from all tables
                    // remember the first line should be the tabel with the Foreign Key.
                    // because you cannot delete the one with PK before deleting the FK
                    stmt.execute("DELETE FROM test.orders");


                    stmt.execute("INSERT INTO test.users (user_id, name, email, password, role_id) " +
                            "VALUES  (1, emil, emil@emil.dk, 1234, 1) , (2, toby, toby@toby.dk, 1234, 2)");


                    // Set sequence to continue from the largest member_id
                    stmt.execute("SELECT setval('test.users_user_id_seq', COALESCE((SELECT MAX(user_id) + 1 FROM test.users), 1), false)");

                } catch (SQLException e) {
                    e.printStackTrace();
                    fail("Database connection failed");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }


//        @Test
//        void createUser () {
////   TODO: creater en user . giver dig en user tilbage some passer med det man creater. = tjatjing
//            //    TODO: create allready existing user. = should give exception
//        }
//
//
//        @Test
//        void login () {
//            //TODO: loginer ind med forkerte creditials = exeption
//            //TODO: loginer ind med det rigtige creditias = tatjing
//        }
//
    }
}