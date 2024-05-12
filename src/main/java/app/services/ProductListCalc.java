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
    private static final int INITIAL_POSTS = 4;
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

    // TODO: Handle exceptions and error handling if it can't add a product to the product list
    // TODO: Potentially add a check to see if the chosen product and amount is in stock
    // TODO: Find the option that will waste the least amount of wood/roof AND is the cheapest

    private static void calcPosts(int carportLength, boolean shed) {
        Product optimalPost = null;
        String description = "Stolper - nedgraves 90 cm. i jord";
        String postUnit = "Stk.";
        int price = 0;
        List<Product> postOptions = ProductMapper.getProducts(POST_TYPEID, connectionPool);


        // Currently only one post option exist on the DB, so we populate postName and -Length with a loop
        for (Product postOption : postOptions) {
            optimalPost = postOption;
        }

        // Calculate number of posts needed - all carports start with 4 (INITIAL_POSTS) - if shed is attached, an additional 5 posts gets added
        // An extra set of posts gets added if the remaining length is greater than the MAX_DISTANCE_BETWEEN_POSTS
        numberOfPosts = (int) (INITIAL_POSTS + (2 * Math.floor((double) (carportLength - UNSUPPORTED_SPACE) / MAX_DISTANCE_BETWEEN_POSTS)) + (shed ? 5 : 0));

        price = numberOfPosts * optimalPost.getPrice();

        // Add post details to product list
        if (optimalPost != null) {
            productList.add(new ProductListItem(optimalPost.getProductID(), optimalPost.getName(), description, optimalPost.getLength(), postUnit, numberOfPosts, price));
        }
    }


    private static void calcBeams(int carportLength) {
        List<Product> beamOptions = ProductMapper.getProducts(RAFTER_AND_BEAM_TYPEID, connectionPool);
        int totalCarportLength = 2 * carportLength;
        String description = "Remme i sider - sadles ned i stolper";
        String beamUnit = "Stk.";
        int price = 0;

        // Variables to keep track of and store the optimal beam option
        int leastWoodWaste = Integer.MAX_VALUE;
        Product optimalBeam = null;

        for (Product beam : beamOptions) {
            int beamLength = beam.getLength();
            int beamsNeeded = (int) Math.ceil((double) totalCarportLength / beamLength);
            int totalBeamLength = beamsNeeded * beamLength;
            int woodWaste = totalBeamLength - totalCarportLength;

            // Update optimal beam if it results in less wood waste
            if (woodWaste < leastWoodWaste) {
                leastWoodWaste = woodWaste;
                numberOfBeams = beamsNeeded;
                optimalBeam = beam;
            }
        }

        price = numberOfBeams * optimalBeam.getPrice();

        // Add beam details to product list
        if (optimalBeam != null) {
            productList.add(new ProductListItem(optimalBeam.getProductID(), optimalBeam.getName(), description, optimalBeam.getLength(), beamUnit, numberOfBeams, price));
        }
    }

    private static void calcRafters(int carportWidth, int carportLength) {
        String description = "Spær - monteres på rem";
        String rafterUnit = "Stk.";
        List<Product> rafterOptions = ProductMapper.getProducts(RAFTER_AND_BEAM_TYPEID, connectionPool);
        int price = 0;

        // Variable to keep track of and store the optimal rafter option
        Product optimalRafter = null;

        // Iterate through the list of rafter options to find the suitable option based on carport width
        for (Product rafter : rafterOptions) {
            if (carportWidth <= rafter.getLength()) {
                optimalRafter = rafter;
                break;
            }
        }

        // Calculate the number of rafters needed
        numberOfRafters = (int) Math.ceil((double) carportLength / DISTANCE_BETWEEN_RAFTERS);

        price = numberOfRafters * optimalRafter.getPrice();

        // Add the rafter product to the product list
        if (optimalRafter != null) {
            productList.add(new ProductListItem(optimalRafter.getProductID(), optimalRafter.getName(), description, optimalRafter.getLength(), rafterUnit, numberOfRafters, price));
        }
    }


    private static void calcRoof(int carportWidth, int carportLength) {
        List<Product> roofOptions = ProductMapper.getProducts(ROOF_TYPEID, connectionPool);
        String description = "Tagplader - monteres på spær";
        String roofUnit = "Stk.";
        int price = 0;

        // Calculate the number of roof plates needed for the width, accounting for overlap
        int plateWidthAdjusted = DEFAULT_ROOF_PLATE_WIDTH - ROOF_PANEL_OVERLAP;
        int amountWidthPlates = (int) Math.floor((carportWidth / plateWidthAdjusted));

        // If the overlap accounted roof plate doesn't fit the carport width, add an extra plate
        if (carportWidth % plateWidthAdjusted != 0) {
            amountWidthPlates += 1;
        }

        /*Can be done with a single line of code, however it's a little messy:
        amountWidthPlates = (int)Math.floor((double) carportWidth % plateWidthAdjusted == 0 ? (carportWidth / plateWidthAdjusted) : ((carportWidth / plateWidthAdjusted) + 1));*/

        // Variables to keep track of and store optimal roof plate option
        int leastRoofWaste = Integer.MAX_VALUE;
        Product optimalRoofPlate = null;

        for (Product roofPlate : roofOptions) {
            // Calculate the number of plates needed for the length, accounting for overlap
            int amountLengthPlates = (int) Math.ceil((double) carportLength / (carportLength >= roofPlate.getLength() ? roofPlate.getLength() : roofPlate.getLength() - ROOF_PANEL_OVERLAP));

            // Calculate total panel length and roof waste
            int totalPanelLength = amountLengthPlates * roofPlate.getLength();
            int roofWasted = totalPanelLength - carportLength;

            // Update if this roof plate results in less waste
            if (roofWasted < leastRoofWaste) {
                leastRoofWaste = roofWasted;
                numberOfRoofPlates = amountLengthPlates * amountWidthPlates;
                optimalRoofPlate = roofPlate;
            }
        }

        price = numberOfRoofPlates * optimalRoofPlate.getPrice();

        // Add the optimal roof plate to the productList
        if (optimalRoofPlate != null) {
            productList.add(new ProductListItem(optimalRoofPlate.getProductID(), optimalRoofPlate.getName(), description, optimalRoofPlate.getLength(), roofUnit, numberOfRoofPlates, price));
        }
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
