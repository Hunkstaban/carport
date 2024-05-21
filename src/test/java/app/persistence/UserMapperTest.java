package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
//                stmt.execute("CREATE TABLE test.roles AS (SELECT * from public.roles)");

                // Create sequences for auto generating id's for users and orders
                stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");
                stmt.execute("ALTER TABLE test.users ADD CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES test.roles(role_id)");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN role_id SET DEFAULT 1");
                stmt.execute("ALTER TABLE test.users ADD CONSTRAINT email_unique UNIQUE (email)");


            } catch (SQLException e) {
                e.printStackTrace();
                fail("Database connection failed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                stmt.execute("DELETE FROM test.users");


                stmt.execute("INSERT INTO test.users (user_id, name, email, password, role_id) " +
                        "VALUES  (1, 'emil', 'emil@emil.dk', '1234', 1) , (2, 'toby', 'toby@toby.dk', '1234', 1)");


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


    //  AN INTERGRATION-TEST ON CREATING A USER
    @Test
    void createUser() throws DatabaseException {
        int userIDExpected = 3;
        String nameExpected = "per";
        String emailExpected = "per@per.dk";
        String passwordExpected = "1234";
        int roleIDExpected = 1;

        User user = UserMapper.createUser("per", "per@per.dk", "1234", connectionPool);

        assertEquals(userIDExpected, user.getUserID());
        assertEquals(nameExpected, user.getName());
        assertEquals(emailExpected, user.getEmail());
        assertEquals(passwordExpected, user.getPassword());
        assertEquals(roleIDExpected, user.getRoleID());

    }

    @Test
    void createUserWithUpperCase() throws DatabaseException {


        String nameExpected = "bob";
        String emailExpected = "bob@bob.dk";
        String passwordExpected = "1234";


        User user = UserMapper.createUser("BOB", "BOB@BOB.dk", "1234", connectionPool);

        assertEquals(nameExpected, user.getName());
        assertEquals(emailExpected, user.getEmail());

    }


    // A INTEGRATION-TEST ON CREATING A USER THAT ALLREADY EXIST. THAT SHOULD THROWS AN DATABASE EXCEPTION
    @Test
    void createUserException() {

        assertThrows(DatabaseException.class, () -> {
            User user = UserMapper.createUser("toby", "toby@toby.dk", "1234", connectionPool);

        });

    }
    // AN INTEGRATION-TEST ON LOGGIN AN EXISTING USER IN
    @Test
    void login() throws DatabaseException {
        int userIDExpected = 1;
        String nameExpected = "emil";
        String emailExpected = "emil@emil.dk";
        String passwordExpected = "1234";
        int roleIDExpected = 1;

        User user = UserMapper.login("emil@emil.dk", "1234", connectionPool);


        assertEquals(userIDExpected, user.getUserID());
        assertEquals(nameExpected, user.getName());
        assertEquals(emailExpected, user.getEmail());
        assertEquals(passwordExpected, user.getPassword());
        assertEquals(roleIDExpected, user.getRoleID());

    }

    // A INTEGRATION-TEST ON LOGGIN IN WITH USER THAT DOESN'T EXIST. THAT SHOULD THROW AN DATABASE EXCEPTION
    @Test
    void loginUserException() {

        assertThrows(DatabaseException.class, () -> {
            User user = UserMapper.login("bob", "bob@bob.dk", connectionPool);

        });

    }
}
