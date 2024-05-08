package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper {






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
