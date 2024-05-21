package app.services;

import app.entities.ProductListItem;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductListCalcTest {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";
    private ProductListCalc productListCalc;
    private List<ProductListItem> productList;
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeEach
    void setUp() {
        int carportWidth = 600;
        int carportLength = 780;
        boolean shed = false; // adding a shed will result in posts increasing by 5
        productListCalc = new ProductListCalc(carportWidth, carportLength,  shed, connectionPool);
        productListCalc.calculateProductList();
        productList = productListCalc.getProductList();
    }

    @Test
    void testProductList() {
        // We add posts, beams, rafters and roof plates objects, so we expect the product list has 4 objects

        // Expected
        int expectedPListSize = 4;

        // Actual
        int actualPListSize = productList.size();

        // Assert
        assertEquals(expectedPListSize, actualPListSize);
    }


    @Test
    void calcPosts() {
        // Arrange
        productListCalc.calcPosts(7800);

        // Expected
        int expectedQuantity = 6;

        // Actual
        int actualQuantity = productListCalc.getNumberOfPosts();

        // Assert
        assertEquals(expectedQuantity, actualQuantity);
    }

    @Test
    void calcBeams() {
        // Arrange
        productListCalc.calcBeams(7800);

        // Expected
        int expectedQuantity = 4;
        int expectedLength = 4600;

        // Actual
        int actualQuantity = productListCalc.getNumberOfBeams();
        int actualLength = productList.get(1).getLength();

        // Assert
        assertEquals(expectedQuantity, actualQuantity);
        assertEquals(expectedLength, actualLength);
    }

    @Test
    void calcRafters() {
        // Arrange
        productListCalc.calcRafters(6000, 7800);

        // Expected
        int expectedQuantity = 15;
        int expectedLength = 6000;

        // Actual
        int actualQuantity = productListCalc.getNumberOfRafters();
        int actualLength = productList.get(2).getLength();

        // Assert
        assertEquals(expectedQuantity, actualQuantity);
        assertEquals(expectedLength, actualLength);
    }


    @Test
    void calcRoof() {
        // Arrange
        productListCalc.calcRoof(6000, 7800);

        // Expected
        int expectedQuantity = 21;
        int expectedLength = 3000;

        // Actual
        int actualQuantity = productListCalc.getNumberOfRoofPanels();
        int actualLength = productList.get(3).getLength();

        // Assert
        assertEquals(expectedQuantity, actualQuantity);
        assertEquals(expectedLength, actualLength);
    }



   /* @Test
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
    }*/
}