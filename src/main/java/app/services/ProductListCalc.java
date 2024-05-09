package app.services;

import app.entities.Product;
import app.entities.ProductListItem;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductListCalc {

    private static final int POST_TYPEID = 7;
    private static final int RAFTER_AND_BEAM_TYPEID = 6;
    private static final int ROOF_TYPEID = 5;
    private static final int SHED_DIMENSIONS = 1400;
    private static final int CM_TO_MM = 10;
    private static final int UNSUPPORTED_SPACE = 1300;
    private static final int MAX_DISTANCE_BETWEEN_POSTS = 3400;
    private static final int DISTANCE_BETWEEN_RAFTERS = 550;
    private static final int DEFAULT_ROOF_WIDTH = 1090;
    private static List<ProductListItem> productList = new ArrayList<>();
    private static int carportWidth;
    private static int carportLength;
    private static boolean shed;
    private static int numberOfPosts;
    private static ConnectionPool connectionPool;

    public ProductListCalc(int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {
        // Making sure all cm becomes mm
        this.carportWidth = carportWidth * CM_TO_MM;
        this.carportLength = carportLength * CM_TO_MM;
        this.shed = shed;
        this.connectionPool = connectionPool;
    }

    public static void calculateProductList() {
        if (shed) {
            carportLength += SHED_DIMENSIONS;
        }
        calcPosts(carportLength, shed);
        calcBeams(carportLength);
        calcRafters(carportWidth, carportLength);
        calcRoof(carportWidth, carportLength);
    }

    private static void calcPosts(int carportLength, boolean shed) {
        String postName = null;
        String description = "Stolper - nedgraves 90 cm. i jord";
        int postLength = 0;
        String postUnit = "Stk."; // TODO: find correct unit based on product unitID
        List<Product> postOptions = ProductMapper.getProductsByTypeID(POST_TYPEID, connectionPool);
        for (Product postOption : postOptions) {
            postName = postOption.getName();
            postLength = postOption.getLength();
        }
        // Calculate number of posts needed - if shed is attached, an additional 5 posts gets added
        numberOfPosts = 2 * (1 + (carportLength - UNSUPPORTED_SPACE) / MAX_DISTANCE_BETWEEN_POSTS) + (shed ? 5 : 0);


        productList.add(new ProductListItem(postName, description, postLength, postUnit, numberOfPosts));
    }

    private static void calcBeams(int carportLength) {
        Map<Integer, Product> beamMap = ProductMapper.getProductMapByTypeID(RAFTER_AND_BEAM_TYPEID, connectionPool);
        int totalCarportLength = 2 * carportLength;
        String beamName = null;
        String description = "Remme i sider - sadles ned i stolper";
        int beamLength = 0;
        String beamUnit = "Stk.";
        int beams = 0;

        int woodWaste = 0;
        if (numberOfPosts == 4) {
            for (Integer i : beamMap.keySet()) {
                int beamsNeeded = (int)Math.floor((double) totalCarportLength / beamMap.get(i).getLength()) + 1;
                int totalBeamLength = beamMap.get(i).getLength() * beamsNeeded;
                int woodWasted = totalBeamLength - totalBeamLength;
                if(woodWasted >= woodWaste) {
                    woodWaste = woodWasted;
                    beamName = beamMap.get(i).getName();
                    beamLength = beamMap.get(i).getLength();
                    beams = beamsNeeded;
                }
            }

            productList.add(new ProductListItem(beamName, description, beamLength, beamUnit, beams));
        }
    }

    private static void calcRafters(int carportLength, int carportWidth) {
        String rafterName = null;
        String description = "Spær - monteres på rem";
        int rafterLength = 0;
        String rafterUnit = "Stk.";
        int rafters = 0;
        List<Product> rafterOptions = ProductMapper.getProductsByTypeID(RAFTER_AND_BEAM_TYPEID, connectionPool);
        // Determine the rafter length and name based on the carport width
        for (Product rafter : rafterOptions) {
            if (carportWidth <= rafter.getLength()) {
                // Set the default rafter length and name to the first suitable option found
                rafterName = rafter.getName();
                rafterLength = rafter.getLength();
                break;
            }
        }
        rafters = (int) Math.ceil((double) carportLength / DISTANCE_BETWEEN_RAFTERS);


        productList.add(new ProductListItem(rafterName, description, rafterLength, rafterUnit, rafters));
    }


    private static void calcRoof(int carportWidth, int carportLength) {
        int carportSquareArea = carportWidth * carportLength;
        String roofName = null;
        String description = "Tagplader - monteres på spær";
        int roofLength = 0;
        String roofUnit = "Stk.";
        int roofPlates = 0;
        List<Product> roofOptions = ProductMapper.getProductsByTypeID(ROOF_TYPEID, connectionPool);

        productList.add(new ProductListItem(roofName, description, roofLength, roofUnit, roofPlates));
    }

    public static List<ProductListItem> getProductList() {
        return productList;
    }
}
