package app.persistence;

import app.entities.Product;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductMapper {
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
}
