package app.persistence;


import app.entities.*;
import app.exceptions.DatabaseException;
import app.services.CarportSvg;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {


    public static Order getOrderByID(ConnectionPool connectionPool, int orderID) throws DatabaseException {

        String sql = "SELECT * FROM public.view_all_orders WHERE order_id = ?";
        Order order = null;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderID);

            ResultSet rs = ps.executeQuery();


            if (rs.next()) {

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

                order = new Order(orderID, user, carportLength1, carportWidth1, description,
                        totalPrice, productListRaw, status1, date, shed, userRemarks);

            }
            return order;

        } catch (SQLException e) {
            throw new DatabaseException("Error: Failed to get order by id. " + e.getMessage());
        }
    }

    public static List<Order> getOrders(ConnectionPool connectionPool, Integer statusID) throws DatabaseException {

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
            throw new DatabaseException("Error: Failed to get all orders or by type. " + e.getMessage());
        }
    }


    public static int getWidthByID(int carportWidthID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM carport_widths WHERE carport_width_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carportWidthID);
            ResultSet rs = ps.executeQuery();


            if (rs.next()) {
                return rs.getInt("width");

            } else {
                throw new DatabaseException("Error: no width found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<Status> loadStatusList(ConnectionPool connectionPool) throws DatabaseException {

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
            throw new DatabaseException("Error: Failed to load status list from Database" + e.getMessage());
        }
    }

    public static int getLengthByID(int carportLengthID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM carport_lengths WHERE carport_length_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carportLengthID);
            ResultSet rs = ps.executeQuery();


            if (rs.next()) {
                return rs.getInt("length");

            } else {
                throw new DatabaseException("Error no length found");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int newOrder(User user, int carportWidthID, int carportLengthID, String description, boolean shed, String remark, List<ProductListItem> productList, int totalPrice, String carportDrawing, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (user_id, carport_width_id, carport_length_id, description, total_price, product_list, shed, user_remark, carport_drawing) VALUES (?,?,?,?,?,?,?,?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, user.getUserID());
            ps.setInt(2, carportWidthID);
            ps.setInt(3, carportLengthID);
            ps.setString(4, description);
            ps.setInt(5, totalPrice);
            ps.setString(6, productList.toString());
            ps.setBoolean(7, shed);
            ps.setString(8, remark);
            ps.setString(9, carportDrawing);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1) {
                throw new DatabaseException("Could not add the order to the database");
            }

            // Retrieve the auto-generated keys
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                // Retrieve the generated order_id
                return generatedKeys.getInt(1);
            } else {
                throw new DatabaseException("Could not retrieve the generated ID");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean ApproveOrder(ConnectionPool connectionPool, int orderID, int totalPrice) throws DatabaseException {

        String sql = "UPDATE public.orders SET status_id = ?, total_price = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, 2);
            ps.setInt(2, totalPrice);
            ps.setInt(3, orderID);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1) {

                throw new DatabaseException("Fejl i godkend ordre");
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<Order> getOrdersByUser(ConnectionPool connectionPool, User user) throws DatabaseException {

        String sql = "SELECT * FROM view_all_orders WHERE user_id = ? ORDER BY order_id DESC";
        List<Order> orderList = new ArrayList<>();
//        Map<Integer, Order> orderMap = new TreeMap<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, user.getUserID());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderID = rs.getInt("order_id");
                int statusID = rs.getInt("status_id");
                String status = rs.getString("status");
                String date = String.valueOf(rs.getDate("date"));
                int totalPrice = rs.getInt("total_price");
                String totalPriceRaw = totalPrice + " kr inkl. moms";
                String productListRaw = rs.getString("product_list");

                // Details
                int carportLength = rs.getInt("carport_length");
                int carportLengthID = rs.getInt("carport_length_id");
                int carportWidth = rs.getInt("carport_width");
                int carportWidthID = rs.getInt("carport_width_id");
                boolean shed = rs.getBoolean("shed");
                String userRemarks = rs.getString("user_remarks");
                String description = rs.getString("description");
                String svg = rs.getString("carport_drawing");

//                String drawing = rs.getString("drawing");
//                String title = -----------------------------;

                orderList.add(new Order(
                        orderID,
                        new CarportLength(carportLengthID, carportLength),
                        new CarportWidth(carportWidthID, carportWidth),
                        description,
                        totalPrice,
                        totalPriceRaw,
                        productListRaw,
                        new Status(statusID, status),
                        date,
                        shed,
                        userRemarks,
                        svg));
            }
            return orderList;

        } catch (SQLException e) {
            throw new DatabaseException("Error: Failed to get users orders. " + e.getMessage());
        }
    }

    public static void setOrderPaid(ConnectionPool connectionPool, User user, int orderID) throws DatabaseException {
        int statusPaid = 3;
        String sql = "UPDATE orders SET status_id = ? WHERE user_id = ? AND order_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {

            ps.setInt(1, statusPaid);
            ps.setInt(2, user.getUserID());
            ps.setInt(3, orderID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Error: Failed to set order status to paid.  " + e.getMessage());
        }
    }

    public static boolean cancelOrder(ConnectionPool connectionPool, int orderID, User user) throws DatabaseException {

        int userRole = 1;
        int adminRole = 2;
        String sql = null;
        int cancelStatusID = 5;

        if (user.getRoleID() == userRole) {
            sql = "UPDATE public.orders SET status_id = ? WHERE order_id = ? AND user_id = ?";
        } else if (user.getRoleID() == adminRole) {
            sql = "UPDATE public.orders SET status_id = ? WHERE order_id = ?";
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, cancelStatusID);
            ps.setInt(2, orderID);

            if (user.getRoleID() == userRole) {
                ps.setInt(3, user.getUserID());
            }


            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1) {

                throw new DatabaseException("Fejl i annuller ordre");
            }
            return true;


        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Error: Failed to set order status to cancelled. " + e.getMessage());
        }
    }
}