package app.persistence;


import app.controllers.ProductController;
import app.entities.Order;
import app.entities.Product;
import app.entities.Type;
import app.entities.Unit;
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


    public static List<Type> loadFilters(ConnectionPool connectionPool) {

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
            throw new RuntimeException(e);
        }
    }


    public static List<Product> getProductsByTypeID(int typeID, ConnectionPool connectionPool) {
        String sql = "SELECT * FROM products WHERE type_id = ?";
        List<Product> products = new ArrayList<>();


        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, typeID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int productID = rs.getInt("product_id");
                String productName = rs.getString("name");
                int productLength = rs.getInt("length");
                products.add(new Product(productID, productName, productLength));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }


    public static List<Product> getProducts(ConnectionPool connectionPool, Integer typeID) {

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
            throw new RuntimeException(e);
        }

        return productList;
    }

    public static Map<Integer, Product> getProductMapByTypeID(int typeID, ConnectionPool connectionPool) {
        String sql = "SELECT * FROM products WHERE type_id = ?";
        Map<Integer, Product> productMap = new TreeMap<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, typeID);
            ResultSet rs = ps.executeQuery();

            int key = 1;
            while (rs.next()) {

                int productID = rs.getInt("product_id");
                String productName = rs.getString("name");
                int productLength = rs.getInt("length");
                productMap.put(key, new Product(productID, productName, productLength));
                key++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productMap;

    }

    public static void updateProduct(ConnectionPool connectionPool, Product product) {

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
            throw new RuntimeException(e);
        }

    }
}
