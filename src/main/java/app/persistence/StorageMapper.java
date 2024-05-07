package app.persistence;

import app.entities.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class StorageMapper {

    public static void setNullableInt(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
        if (value != null) {
            ps.setInt(parameterIndex, value);
        } else {
            ps.setNull(parameterIndex, java.sql.Types.INTEGER);
        }
    }

    public static void addProduct(ConnectionPool connectionPool, String name, String description, Integer height, Integer width, Integer length, Integer unitID, Integer typeID, Integer price, Integer costPrice, Integer quantity) {

        String sql = "INSERT INTO products (name," +
                "description," +
                "height," +
                "width," +
                "length," +
                "unit_id," +
                "type_id," +
                "price," +
                "cost_price," +
                "quantity) VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {

//            ps.setString(1,product.getName());
//            ps.setString(2,product.getDescription());
//            ps.setInt(3, product.getHeight());
//            ps.setInt(4, product.getWidth());
//            ps.setInt(5, product.getLength());
//            ps.setInt(6, product.getUnitID());
//            ps.setInt(7, product.getTypeID());
//            ps.setInt(8, product.getPrice());
//            ps.setInt(9, product.getCostPrice());
//            ps.setInt(10, product.getQuantity());

            ps.setString(1,name);
            ps.setString(2,description);
            setNullableInt(ps, 3, height);
            setNullableInt(ps, 4, width);
            setNullableInt(ps, 5, length);
            setNullableInt(ps, 6, unitID);
            setNullableInt(ps, 7, typeID);
            setNullableInt(ps, 8, price);
            setNullableInt(ps, 9, costPrice);
            ps.setInt(10,quantity);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
