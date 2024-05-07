package app.persistence;

import app.entities.Product;
import app.entities.User;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderMapper {




    public static List<Product> calculateProductList (int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {
        List<Product> productList = null;
        int carportSquareMeters = carportWidth * carportLength;
        return productList;
    }

    private static int calculatePosts (int carportLength) {
        // All carports starts with 4 posts
        int posts = 4;
        int remainingLength = (carportLength * 10) - 4300; // 1000mm front unsupported + 3300mm until additional posts needed
        int additionalPosts;
        if (remainingLength % 3400 == 0) {
            additionalPosts = remainingLength / 3400;
        } else {
            additionalPosts = remainingLength / 3400 + 1;
        }
        posts += (additionalPosts * 2);

        return posts;
    }

    private static int calculateRoof (int carportSquareMeters) {
        int roofPlates = 0;
        return roofPlates;
    }

    public static int getWidthByID (int carportWidthID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM carport_widths WHERE carport_width_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, carportWidthID);
            ResultSet rs = ps.executeQuery();


            if (rs.next()) {
                int carportWidth = rs.getInt("width");
                return carportWidth;

            } else {
                throw new DatabaseException("Error no width found");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getLengthByID (int carportLengthID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM carport_lengths WHERE carport_length_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, carportLengthID);
            ResultSet rs = ps.executeQuery();


            if (rs.next()) {
                int carportLength = rs.getInt("length");
                return carportLength;

            } else {
                throw new DatabaseException("Error no length found");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
