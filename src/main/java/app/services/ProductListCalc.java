package app.services;

import app.entities.Product;
import app.entities.ProductListItem;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;

import java.util.ArrayList;
import java.util.List;

public class ProductListCalc {

    private static final int POSTTYPEID = 7;
    private static final int RAFTERANDBEAMTYPEID = 6;
    private static final int ROOFTYPEID = 5;
    private static final int SHEDDIMENSIONS = 1400;
    private static final int CMTOMM = 10;
    private static final int UNSUPPORTEDSPACE = 1300;
    private static final int MAXDISTANCEBETWEENPOSTS = 3400;
    private static List<ProductListItem> productList = new ArrayList<>();
    private static int carportWidth;
    private static int carportLength;
    private static boolean shed;
    private static ConnectionPool connectionPool;

    public ProductListCalc(int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {
        // Making sure all cm becomes mm
        this.carportWidth = carportWidth * CMTOMM;
        this.carportLength = carportLength * CMTOMM;
        this.shed = shed;
        this.connectionPool = connectionPool;
    }

    public static void calculateProductList () {
        if (shed) {
            carportLength += SHEDDIMENSIONS;
        }
        calcPosts(carportLength, shed);
        calcBeams(carportWidth, carportLength);
        calcRafters(carportLength);
        calcRoof(carportWidth, carportLength);
    }

    private static void calcPosts (int carportLength, boolean shed) {
        String postName = null;
        String description = "Stolper - nedgraves 90 cm. i jord";
        int postLength = 0;
        String postUnit = "Stk.";
        List<Product> postOptions = ProductMapper.getProductsByTypeID(POSTTYPEID, connectionPool);
        for (Product postOption : postOptions) {
            postName = postOption.getName();
            postLength = postOption.getLength();
        }
        // All carports start with 4 posts - if shed is attached, an additional 4 posts gets added
        int posts = 4;
        if (shed) {
            posts = 8;
        }
        int remainingLength = carportLength - UNSUPPORTEDSPACE;
        if (remainingLength > MAXDISTANCEBETWEENPOSTS) {
            posts += 2;
        }

        productList.add(new ProductListItem(postName, description, postLength, postUnit, posts));
    }

    private static void calcBeams(int carportWidth, int carportLength) {
        String beamName = null;
        String description = "Remme i sider - sadles ned i stolper";
        int beamLength = 0;
        String beamUnit = "Stk.";
        int beams = 0;
        List<Product> beamOptions = ProductMapper.getProductsByTypeID(RAFTERANDBEAMTYPEID, connectionPool);

        productList.add(new ProductListItem(beamName, description, beamLength, beamUnit, beams));
    }

    private static void calcRafters(int carportLength) {
        String rafterName = null;
        String description = "Spær - monteres på rem";
        int rafterLength = 0;
        String rafterUnit = "Stk.";
        int rafters = 0;
        List<Product> rafterOptions = ProductMapper.getProductsByTypeID(RAFTERANDBEAMTYPEID, connectionPool);


        productList.add(new ProductListItem(rafterName, description, rafterLength, rafterUnit, rafters));
    }


    private static void calcRoof (int carportWidth, int carportLength) {
        int carportSquareArea = carportWidth * carportLength;
        String roofName = null;
        String description = "Tagplader - monteres på spær";
        int roofLength = 0;
        String roofUnit = "Stk.";
        int roofPlates = 0;
        List<Product> roofOptions = ProductMapper.getProductsByTypeID(RAFTERANDBEAMTYPEID, connectionPool);

        productList.add(new ProductListItem(roofName, description, roofLength, roofUnit, roofPlates));
    }
}
