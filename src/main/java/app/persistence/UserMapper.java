package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserMapper {

//    public static User changePassword (String password, ConnectionPool connectionPool) {
//
////        ---- we need mail server for this one. og else everyone could change the password.
////        ---- with just the users email. so for security reason. we wont be doing this functionality
//
//    }

    public static User createUser(String name, String email, String password, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO users (name,email,password) VALUES(?,?,?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

            name = name.toLowerCase();
            email = email.toLowerCase();

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            preparedStatement.executeUpdate();
            return login(email, password, connectionPool);

        } catch (SQLException e) {
            String msg = "Something went wrong. try again;
            if (e.getMessage().startsWith("ERROR: duplicate key value")) {
                msg = "email allready exist";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT * FROM users WHERE email = ? AND password = ? ";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

//            preparedStatement.setString(1, "name");
            preparedStatement.setString(1, email.toLowerCase());
            preparedStatement.setString(2, password.toLowerCase());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                String name = resultSet.getString("name");
                int userID = resultSet.getInt("user_id");
                int roleID = resultSet.getInt("role_id");

                return new User(userID, name, email, password, roleID);
            } else throw new DatabaseException("could not get user from database");

        } catch (SQLException e) {
            throw new DatabaseException("Something went wrong with the database." + e.getMessage());
        }

    }




}
