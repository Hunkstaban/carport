package app.services;

import app.entities.ProductListItem;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
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
        ProductListCalc productListCalc = new ProductListCalc(carportWidth, carportLength,  shed, connectionPool);
        productListCalc.calculateProductList();
        List<ProductListItem> productList = productListCalc.getProductList();

        // We add posts, beams, rafters and roof plates objects, so we expect the product list has 4 objects
        assertEquals(4, productList.size());

        // With the chosen dimensions, we expect the following:
        // Number of posts should be 6
        assertEquals(6, productListCalc.getNumberOfPosts());

        // Number of beams should be 4, and the length should be 4600 mm.
        assertEquals(4, productListCalc.getNumberOfBeams());
        assertEquals(4600, productList.get(1).getLength());

        // Number of rafters should be 15, and the length should be 6000 mm.
        assertEquals(15, productListCalc.getNumberOfRafters());
        assertEquals(6000, productList.get(2).getLength());

        // Number of roof panels should be 21, and the length should be 3000 mm.
        assertEquals(21, productListCalc.getNumberOfRoofPanels());
        assertEquals(3000, productList.get(3).getLength());

        /*for (ProductListItem productListItem : productList) {
            System.out.println("Vare nr.: " + productListItem.getProductID());
            System.out.println("Navn: " + productListItem.getProductName());
            System.out.println("Beskrivelse: " + productListItem.getProductDescription());
            System.out.println("Længde: " + productListItem.getLength() + " mm.");
            System.out.println("Mængde: " + productListItem.getQuantity() + " " + productListItem.getUnit());
            System.out.println("Pris: " + productListItem.getCostPrice() + " kr.\n");

            totalPrice += productListItem.getCostPrice();
        }

        System.out.println("Carport pris: " + totalPrice);
        System.out.println(productList);*/
    }
}