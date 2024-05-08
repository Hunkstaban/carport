package app.services;

import app.entities.Product;
import app.entities.ProductListItem;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;

import java.util.ArrayList;
import java.util.List;

public class ProductListCalc {

    private static final int POSTTYPEID = 7;
    private static final int RAFTERTYPEID = 6;
    private static final int SHEDDIMENSIONS = 1400;
    private static final int CMTOMM = 10;
    private static final int MAXDISTANCEBETWEENPOSTS = 3400;
    private List<ProductListItem> productList = new ArrayList<>();
    private static int carportWidth;
    private static int carportLength;
    private static boolean shed;
    private static ConnectionPool connectionPool;

    public ProductListCalc(int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.shed = shed;
        this.connectionPool = connectionPool;
    }

    public static List<ProductListItem> calculateProductList () {
        List<ProductListItem> productList = null;
        if (shed) {
            carportLength += 1400;
        }
        calcPosts(carportLength, shed);
        calcRafters(carportLength);
        calcBeams(carportWidth, carportLength);
        calcRoof(carportWidth, carportLength);
        return productList;
    }

    private static void calcPosts (int carportLength, boolean shed) {
        // All carports start with 4 posts
        int posts = 4;

        List<Product> postOptions = ProductMapper.getProductsByTypeID(POSTTYPEID, connectionPool);
        int remainingLength = (carportLength * CMTOMM) - 4400; // 1000mm front unsupported + 3400mm until additional posts needed
        int additionalPosts;
        if (MAXDISTANCEBETWEENPOSTS % remainingLength != 0) {
//            additionalPosts = remainingLength / DISTANCEBETWEENPOSTS + 1;
          //  additionalPosts = remainingLength / DISTANCEBETWEENPOSTS + 1;
        }
       // posts += (additionalPosts * 2);
    }

    private static void calcRafters(int carportLength) {

    }

    private static void calcBeams(int carportWidth, int carportLength) {
    }

    private static void calcRoof (int carportWidth, int carportLength) {
        int roofPlates = 0;
    }
}
