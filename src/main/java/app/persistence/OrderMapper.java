package app.persistence;

import app.entities.*;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static List<Order> getOrders(ConnectionPool connectionPool, Integer statusID) {

        String sql;

        List<Order> orderList = new ArrayList<>();

        if (statusID != null) {

            sql = "SELECT * FROM public.view_all_orders WHERE status_id = ?";
        } else {

            sql = "SELECT * FROM public.view_all_orders";
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (statusID != null) {

                ps.setInt(1, statusID);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int orderID = rs.getInt("order_id");
                int userID = rs.getInt("user_id");
                String userName = rs.getString("user_name");
                String userEmail = rs.getString("user_email");
                int roleID = rs.getInt("role_id");
                int carportLengthID = rs.getInt("carport_length_id");
                int carportLength = rs.getInt("carport_length");
                int carportWidthID = rs.getInt("carport_width_id");
                int carportWidth = rs.getInt("carport_width");
                String description = rs.getString("description");
                int totalPrice = rs.getInt("total_price");
                String productListRaw = rs.getString("product_list");
                int statusID1 = rs.getInt("status_id");
                String status = rs.getString("status");
                String date = rs.getString("date");
                boolean shed = rs.getBoolean("shed");
                String userRemarks = rs.getString("user_remarks");

                User user = new User(userID, userName, userEmail, roleID);
                Status status1 = new Status(statusID1, status);
                CarportLength carportLength1 = new CarportLength(carportLengthID, carportLength);
                CarportWidth carportWidth1 = new CarportWidth(carportWidthID, carportWidth);

                orderList.add(new Order(orderID, user, carportLength1, carportWidth1, description,
                        totalPrice, productListRaw, status1, date, shed, userRemarks));

            }
            return orderList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Status> loadStatusList(ConnectionPool connectionPool) {

        String sql = "SELECT * FROM status";
        List<Status> statusList = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int statusID = rs.getInt("status_id");
                String name = rs.getString("name");

                statusList.add(new Status(statusID, name));
            }

            return statusList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
