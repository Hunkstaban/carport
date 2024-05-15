package app.services;

import app.entities.ProductListItem;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductListCalcTest {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @Test
    void calculateProductList() {
        int carportWidth = 480;
        int carportLength = 600;
        ProductListCalc productListCalc = new ProductListCalc(carportWidth,carportLength,false, connectionPool);
        productListCalc.calculateProductList();
        List<ProductListItem> productList = productListCalc.getProductList();
        int totalPrice = 0;

        for (ProductListItem productListItem : productList) {
            System.out.println("Vare nr.: " + productListItem.getProductID());
            System.out.println("Navn: " + productListItem.getProductName());
            System.out.println("Beskrivelse: " + productListItem.getProductDescription());
            System.out.println("Længde: " + productListItem.getLength() + " mm.");
            System.out.println("Mængde: " + productListItem.getQuantity() + " " + productListItem.getUnit());
            System.out.println("Pris: " + productListItem.getPrice() + " kr.\n");

            totalPrice += productListItem.getPrice();
        }

        System.out.println("Carport pris: " + totalPrice);
        System.out.println(productList);
    }
}