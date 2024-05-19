package app.services;

import app.entities.ProductListItem;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductListCalcTest {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @Test
    void calculateProductList() {
        int carportWidth = 600;
        int carportLength = 780;
        boolean shed = false;
        ProductListCalc productListCalc = new ProductListCalc(carportWidth,carportLength,shed, connectionPool);
        productListCalc.calculateProductList();
        List<ProductListItem> productList = productListCalc.getProductList();
        int totalPrice = 0;

        assertEquals(4, productList.size());



        for (ProductListItem productListItem : productList) {
            System.out.println("Vare nr.: " + productListItem.getProductID());
            System.out.println("Navn: " + productListItem.getProductName());
            System.out.println("Beskrivelse: " + productListItem.getProductDescription());
            System.out.println("Længde: " + productListItem.getLength() + " mm.");
            System.out.println("Mængde: " + productListItem.getQuantity() + " " + productListItem.getUnit());
            System.out.println("Pris: " + productListItem.getCostPrice() + " kr.\n");

            totalPrice += productListItem.getCostPrice();
        }

        System.out.println("Carport pris: " + totalPrice);
        System.out.println(productList);
    }
}