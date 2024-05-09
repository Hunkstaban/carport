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
    private static final int DEFAULT_ROOF_PLATE_WIDTH = 1090;
    private static final int ROOF_PANEL_OVERLAP = 200;
    private static List<ProductListItem> productList = new ArrayList<>();
    private static int carportWidth;
    private static int carportLength;
    private static boolean shed;
    private static int numberOfPosts;
    private static int numberOfBeams;
    private static int numberOfRafters;
    private static int numberOfRoofPlates;
    private static ConnectionPool connectionPool;

    public ProductListCalc(int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {
        // Making sure all cm becomes mm
        this.carportWidth = carportWidth * CM_TO_MM;
        this.carportLength = carportLength * CM_TO_MM;
        this.shed = shed;
        this.connectionPool = connectionPool;
    }

    public void calculateProductList() {
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
        List<Product> postOptions = ProductMapper.getProductsByTypeID(POST_TYPEID, connectionPool); // TODO: Make this use the Map method
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

        int woodWaste = 0;
        for (Integer i : beamMap.keySet()) {
            int beamsNeeded = (int) Math.floor((double) totalCarportLength / beamMap.get(i).getLength());
            if (beamsNeeded % 2 != 0) {
                beamsNeeded += 1;
            }
            int totalBeamLength = beamMap.get(i).getLength() * beamsNeeded;
            int woodWasted = totalBeamLength - totalCarportLength;
            if (woodWasted >= woodWaste) {
                woodWaste = woodWasted;
                beamName = beamMap.get(i).getName();
                beamLength = beamMap.get(i).getLength();
                numberOfBeams = beamsNeeded;
            }
        }

        productList.add(new ProductListItem(beamName, description, beamLength, beamUnit, numberOfBeams));
    }

    private static void calcRafters(int carportLength, int carportWidth) {
        String rafterName = null;
        String description = "Spær - monteres på rem";
        int rafterLength = 0;
        String rafterUnit = "Stk.";
        numberOfRafters = 0;
        List<Product> rafterOptions = ProductMapper.getProductsByTypeID(RAFTER_AND_BEAM_TYPEID, connectionPool); //TODO: Make this use the Map method
        // Determine the rafter length and name based on the carport width
        for (Product rafter : rafterOptions) {
            if (carportWidth <= rafter.getLength()) {
                // Set the default rafter length and name to the first suitable option found
                rafterName = rafter.getName();
                rafterLength = rafter.getLength();
                break;
            }
        }

        numberOfRafters = (int) Math.ceil((double) carportLength / DISTANCE_BETWEEN_RAFTERS);
        productList.add(new ProductListItem(rafterName, description, rafterLength, rafterUnit, numberOfRafters));
    }


    private static void calcRoof(int carportWidth, int carportLength) {
        Map<Integer, Product> roofMap = ProductMapper.getProductMapByTypeID(ROOF_TYPEID, connectionPool);
        String roofName = null;
        String description = "Tagplader - monteres på spær";
        int roofLength = 0;
        String roofUnit = "Stk.";

        // Accounting for overlap
        int amountWidthPlates = (int) Math.floor((double) carportWidth / (DEFAULT_ROOF_PLATE_WIDTH - ROOF_PANEL_OVERLAP)) + 1;

        int roofWaste = 0;
        for (Integer i : roofMap.keySet()) {
            int amountLengthPlates = 0;
            if (carportLength == roofMap.get(i).getLength()) {
                amountLengthPlates = (int) Math.floor((double) carportLength / (roofMap.get(i).getLength()));
            } else {
                amountLengthPlates = (int) Math.floor((double) carportLength / (roofMap.get(i).getLength() - ROOF_PANEL_OVERLAP) + 1);
            }
            int totalPanelLength = roofMap.get(i).getLength() * amountLengthPlates;
            int roofWasted = totalPanelLength - carportLength;
            if (roofWasted <= roofWaste) {
                roofWaste = roofWasted;
                roofName = roofMap.get(i).getName();
                roofLength = roofMap.get(i).getLength();
                numberOfRoofPlates = amountLengthPlates * amountWidthPlates;
            }
        }

        productList.add(new ProductListItem(roofName, description, roofLength, roofUnit, numberOfRoofPlates));
    }

    public static List<ProductListItem> getProductList() {
        return productList;
    }

    public static int getNumberOfPosts() {
        return numberOfPosts;
    }

    public static int getNumberOfBeams() {
        return numberOfBeams;
    }

    public static int getNumberOfRafters() {
        return numberOfRafters;
    }

    public static int getNumberOfRoofPlates() {
        return numberOfRoofPlates;
    }
}
