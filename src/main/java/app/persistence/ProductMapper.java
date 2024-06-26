package app.persistence;


import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ProductMapper {


    public static List<Type> loadFilters(ConnectionPool connectionPool) throws DatabaseException {

        // gets all the types from the DB and returns a List which is used in the filter controller
        // to filter by the chosen type on the frontend.

        String sql = "SELECT * FROM types";
        List<Type> filters = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int typeID = rs.getInt("type_id");
                String name = rs.getString("name");
                filters.add(new Type(typeID, name));
            }

            return filters;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to load filter types.", e.getMessage());
        }
    }

    public static List<Unit> loadUnits(ConnectionPool connectionPool) throws DatabaseException {

        // gets all the types from the DB and returns a List which is used in the filter controller
        // to filter by the chosen type on the frontend.

        String sql = "SELECT * FROM units";
        List<Unit> units = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int typeID = rs.getInt("unit_id");
                String name = rs.getString("name");
                units.add(new Unit(typeID, name));
            }

            return units;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to load Units. ", e.getMessage());
        }
    }


    // gets products from the database, is dynamic and can either retrieve all products
    // or if given a type id only the specific product types.
    public static List<Product> getProducts(Integer typeID, ConnectionPool connectionPool) throws DatabaseException {

        String sql;

        List<Product> productList = new ArrayList<>();

        if (typeID != null) {

            sql = "SELECT * FROM public.view_all_products WHERE type_id = ?";
        } else {

            sql = "SELECT * FROM public.view_all_products";
        }
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (typeID != null) {

                ps.setInt(1, typeID);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int productID = rs.getInt("product_id");
                String name = rs.getString("product_name");
                String description = rs.getString("description");
                int height = rs.getInt("height");
                int width = rs.getInt("width");
                int length = rs.getInt("length");
                int unitID = rs.getInt("unit_id");
                String unitName = rs.getString("unit_name");
                int typeID1 = rs.getInt("type_id");
                String typeName = rs.getString("type_name");
                int price = rs.getInt("price");
                int costPrice = rs.getInt("cost_price");
                int quantity = rs.getInt("quantity");

                Unit unit = new Unit(unitID, unitName);
                Type type = new Type(typeID1, typeName);

                productList.add(new Product(productID, name, description, height, width, length, unit, type, price, costPrice, quantity));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get products", e.getMessage());
        }

        return productList;
    }

    public static void updateProduct(Product product, ConnectionPool connectionPool) throws DatabaseException {

        // recieves a Product object with all the values from the frontend and updates the database with the new values.

        String sql = "UPDATE public.products\n" +
                "\tSET name=?, description=?, height=?, width=?, unit_id=?, type_id=?, price=?, cost_price=?, quantity=?, length=?\n" +
                "\tWHERE product_id = ?;";


        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setInt(3, product.getHeight());
            ps.setInt(4, product.getWidth());
            ps.setInt(5, product.getUnit().getUnitID());
            ps.setInt(6, product.getType().getTypeID());
            ps.setInt(7, product.getPrice());
            ps.setInt(8, product.getCostPrice());
            ps.setInt(9, product.getQuantity());
            ps.setInt(10, product.getLength());
            ps.setInt(11, product.getProductID());


            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl i opdatering af bestillingsoplysninger");
            }

        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Error updating product.", e.getMessage());
        }

    }

    public static void deleteProduct(ConnectionPool connectionPool, int productID) throws DatabaseException {
        String sql = "DELETE FROM public.products WHERE product_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {

            ps.setInt(1,productID);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1) {
                throw new DatabaseException("Error in deleteting product.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error in deleteting product.", e.getMessage());
        }
    }

    public static List<CarportLength> getAllLength(ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT * FROM carport_lengths order by length ASC";
        List<CarportLength> getAllLengthList = new ArrayList<>();


        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                int getLengthID = resultSet.getInt("carport_length_id");
                int getLengthCm = resultSet.getInt("length");
                getAllLengthList.add(new CarportLength(getLengthID, getLengthCm));
            }


        } catch (SQLException e) {
            throw new DatabaseException("Something went wrong with the database" + e.getMessage());
        }

        return getAllLengthList;
    }

    public static List<CarportWidth> getAllwidth(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM carport_widths order by width ASC";

        List<CarportWidth> getAllWidthList = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ) {


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int getWidthID = resultSet.getInt("carport_width_id");
                int getWidth = resultSet.getInt("width");
                getAllWidthList.add(new CarportWidth(getWidthID, getWidth));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Something went wrong with the database" + e.getMessage());

        }
        return getAllWidthList;

    }
}
