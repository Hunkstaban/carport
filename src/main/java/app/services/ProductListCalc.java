package app.services;

import app.entities.ProductList;
import app.persistence.ConnectionPool;

import java.util.List;

public class ProductListCalc {

    public static List<ProductList> calculateProductList (int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {
        List<ProductList> productList = null;
        int carportSquareMeters = carportWidth * carportLength;
        return productList;
    }

    private static int calculatePosts (int carportLength) {
        // All carports starts with 4 posts
        int posts = 4;
        int remainingLength = (carportLength * 10) - 4300; // 1000mm front unsupported + 3300mm until additional posts needed
        int additionalPosts;
        if (remainingLength % 3400 == 0) {
            additionalPosts = remainingLength / 3400;
        } else {
            additionalPosts = remainingLength / 3400 + 1;
        }
        posts += (additionalPosts * 2);

        return posts;
    }

    private static int calculateRoof (int carportSquareMeters) {
        int roofPlates = 0;
        return roofPlates;
    }
}
