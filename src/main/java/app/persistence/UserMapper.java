package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UserMapper {

    public static User createUser(String name, String email, String password, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO users (name,email,password) VALUES(?,?,?)";

        try {

            Connection connection = connectionPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "email findes allerede. Prøv igen";
            }
            throw new DatabaseException(msg, e.getMessage());


        }

        return login(email, password, connectionPool);

    }

    private static User login(String email, String password, ConnectionPool connectionPool) {

        return null;
    }

}
