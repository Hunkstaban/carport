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
    private static final int SCREW_TYPEID = 3;
    private static final int SHED_DIMENSIONS = 1400;
    private static final int CM_TO_MM = 10;
    private static final int UNSUPPORTED_SPACE = 1300;
    private static final int MAX_DISTANCE_BETWEEN_POSTS = 3400;
    private static final int DISTANCE_BETWEEN_RAFTERS = 550;
    private static final int DEFAULT_ROOF_PANEL_WIDTH = 1090;
    private static final int ROOF_PANEL_OVERLAP = 200;
    private static final int INITIAL_POSTS = 4;
    private static List<ProductListItem> productList = new ArrayList<>();
    private static int carportWidth;
    private static int carportLength;
    private static boolean shed;
    private static int numberOfPosts;
    private static int numberOfPostsWOShed;
    private static int numberOfBeams;
    private static int numberOfRafters;
    private static int numberOfRoofPanels;
    private static ConnectionPool connectionPool;

    public ProductListCalc(int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {
        // Making sure all cm becomes mm for easier calculations
        this.carportWidth = carportWidth * CM_TO_MM;
        this.carportLength = carportLength * CM_TO_MM;
        this.shed = shed;
        this.connectionPool = connectionPool;
    }

    public void calculateProductList() {
        // Add the extra length if shed is true
        if (shed) {
            carportLength += SHED_DIMENSIONS;
        }
        calcPosts(carportLength);
        calcBeams(carportLength);
        calcRafters(carportWidth, carportLength);
        calcRoof(carportWidth, carportLength);
    }

    public void calculatePosts() {
        calcPosts(carportLength, shed);
    }

    // TODO: Find the option that will waste the least amount of wood/roof AND will be the cheapest
    // TODO: Handle exceptions and error handling if it can't add a product to the product list
    // TODO: Potentially add a check to see if the chosen product and amount is in stock

    private static void calcPosts(int carportLength) {
        Product optimalPost = null;
        String description = "Stolper - nedgraves 90 cm. i jord";
        String postUnit = "Stk.";
        int costPrice;
        List<Product> postOptions = ProductMapper.getProducts(POST_TYPEID, connectionPool);


        // Currently only one post option exist on the DB, so we populate postName and -Length with a loop
        for (Product postOption : postOptions) {
            optimalPost = postOption;
        }

        // Calculate number of posts needed - all carports start with 4 (INITIAL_POSTS) - if shed is attached, an additional 5 posts gets added
        // An extra set of posts gets added if the remaining length is greater than the MAX_DISTANCE_BETWEEN_POSTS
        numberOfPosts = (int) (INITIAL_POSTS + (2 * Math.floor((double) (carportLength - UNSUPPORTED_SPACE) / MAX_DISTANCE_BETWEEN_POSTS)) + (shed ? 5 : 0));

        costPrice = numberOfPosts * optimalPost.getCostPrice();

        /* Alternative way to calculate it/Not on one line
                int remainingLength = carportLength - UNSUPPORTED_SPACE;
                int ratio = (int) Math.floor((double)remainingLength / MAX_DISTANCE_BETWEEN_POSTS);
                beamPosts += ratio * 2;
                numberOfPosts += ratio * 2 + (shed ? 5 : 0);*/

        // Add post details to product list
        if (optimalPost != null) {
            productList.add(new ProductListItem(optimalPost.getProductID(), optimalPost.getName(), description, optimalPost.getLength(), postUnit, numberOfPosts, costPrice));
        }
    }

    /*private static void calcBeams(int carportLength) {
        List<Product> beamOptions = ProductMapper.getProducts(RAFTER_AND_BEAM_TYPEID, connectionPool);
        int totalCarportLength = 2 * carportLength;
        String description = "Remme i sider - sadles ned i stolper";
        String beamUnit = "Stk.";
        int costPrice;

        // Variables to keep track of and store the optimal beam option
        int leastWoodWaste = Integer.MAX_VALUE;
        Product optimalBeam = null;

        for (Product beam : beamOptions) {
            int beamLength = beam.getLength();
            int beamsNeeded = (int) Math.ceil((double) totalCarportLength / beamLength);
            int totalBeamLength = beamsNeeded * beamLength;
            int woodWaste = totalBeamLength - totalCarportLength;

            // Update optimal beam if it results in less wood waste
            if (woodWaste < leastWoodWaste && numberOfPosts == 4) {
                leastWoodWaste = woodWaste;
                numberOfBeams = beamsNeeded;
                optimalBeam = beam;
            } else if (woodWaste < leastWoodWaste && beamLength > MAX_DISTANCE_BETWEEN_POSTS + UNSUPPORTED_SPACE && beamLength < 6000) {
                leastWoodWaste = woodWaste;
                numberOfBeams = beamsNeeded;
                optimalBeam = beam;
            }
        }

        costPrice = numberOfBeams * optimalBeam.getCostPrice();

        // Add beam details to product list
        if (optimalBeam != null) {
            productList.add(new ProductListItem(optimalBeam.getProductID(), optimalBeam.getName(), description, optimalBeam.getLength(), beamUnit, numberOfBeams, costPrice));
        }
    }*/

    /*private static void calcRafters(int carportWidth, int carportLength) {
        String description = "Spær - monteres på rem";
        String rafterUnit = "Stk.";
        List<Product> rafterOptions = ProductMapper.getProducts(RAFTER_AND_BEAM_TYPEID, connectionPool);
        int costPrice;

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

        costPrice = numberOfRafters * optimalRafter.getCostPrice();

        // Add the rafter product to the product list
        if (optimalRafter != null) {
            productList.add(new ProductListItem(optimalRafter.getProductID(), optimalRafter.getName(), description, optimalRafter.getLength(), rafterUnit, numberOfRafters, costPrice));
        }
    }*/

    /*private static void calcRoof(int carportWidth, int carportLength) {
        List<Product> roofOptions = ProductMapper.getProducts(ROOF_TYPEID, connectionPool);
        String description = "Tagplader - monteres på spær";
        String roofUnit = "Stk.";
        int costPrice;

        // Calculate the number of roof plates needed for the width, accounting for overlap
        int plateWidthAdjusted = DEFAULT_ROOF_PLATE_WIDTH - ROOF_PANEL_OVERLAP;
        int amountWidthPlates = (int) Math.ceil(((double) carportWidth / plateWidthAdjusted));

        // If the overlap accounted roof plate doesn't fit the carport width, add an extra plate
        if (carportWidth % plateWidthAdjusted == 0) {
            amountWidthPlates += 1;
        }



    Can be done with a single line of code, however it's a little messy:


       amountWidthPlates = (int)Math.floor((double) carportWidth % plateWidthAdjusted == 0 ? (carportWidth / plateWidthAdjusted) : ((carportWidth / plateWidthAdjusted) + 1));



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

        costPrice = numberOfRoofPlates * optimalRoofPlate.getCostPrice();

        // Add the optimal roof plate to the productList
        if (optimalRoofPlate != null) {
            productList.add(new ProductListItem(optimalRoofPlate.getProductID(), optimalRoofPlate.getName(), description, optimalRoofPlate.getLength(), roofUnit, numberOfRoofPlates, costPrice));
        }
    }*/


    private static void calcBeams(int carportLength) {
        List<Product> beamList = ProductMapper.getProducts(RAFTER_AND_BEAM_TYPEID, connectionPool);
        int totalCarportLength = 2 * carportLength;
        String description = "Remme i sider - sadles ned i stolper";
        String beamUnit = "Stk.";
        int costPrice;
        int beamsNeeded;
        numberOfPostsWOShed = numberOfPosts - (shed ? 5 : 0);
        // Variables to keep track of and store the optimal beam option
        int leastWoodWaste = Integer.MAX_VALUE;
        Product optimalBeam = null;

        //  Go through each wood.
        for (Product beam : beamList) {


            if (
                (numberOfPostsWOShed == 4 && beam.getLength() > carportLength) ||
                (numberOfPostsWOShed > 4 && beam.getLength() == carportLength) ||
                (numberOfPostsWOShed > 4 && beam.getLength() >= (carportLength - UNSUPPORTED_SPACE) / (numberOfPostsWOShed / 2 - 1) + UNSUPPORTED_SPACE)
            ) {

                double beamsNeededDouble = (double) carportLength / beam.getLength(); // Calculate beam needed in decimal

                if (beamsNeededDouble % 1 == 0) {
                    beamsNeeded = (int) beamsNeededDouble * 2;
                } else {
                    beamsNeeded = ((int) Math.floor(beamsNeededDouble) + 1) * 2; // Calculate beam needed
                }

                int totalBeamLength = beam.getLength() * beamsNeeded; // Calculate total beam length
                int woodWasted = totalBeamLength - totalCarportLength; // Calculate beam wasted with this wood

                if (woodWasted < leastWoodWaste) {
                    leastWoodWaste = woodWasted;
                    optimalBeam = beam;
                    numberOfBeams = beamsNeeded;
                }
            }
        }

        costPrice = numberOfBeams * optimalBeam.getCostPrice();

        // Add beam details to product list
        if (optimalBeam != null) {
            productList.add(new ProductListItem(optimalBeam.getProductID(), optimalBeam.getName(), description, optimalBeam.getLength(), beamUnit, numberOfBeams, costPrice));
        }
    }

    private static void calcRafters(int carportWidth, int carportLength) {
        String description = "Spær - monteres på rem";
        String rafterUnit = "Stk.";
        List<Product> rafterOptions = ProductMapper.getProducts(RAFTER_AND_BEAM_TYPEID, connectionPool);
        int costPrice;

        // Variable to keep track of and store the optimal rafter option
        int leastWoodWaste = Integer.MAX_VALUE;
        Product optimalRafter = null;

        for (Product rafter : rafterOptions) {
            if (rafter.getLength() >= carportWidth) {
                int woodWasted = rafter.getLength() - carportWidth;
                if (woodWasted < leastWoodWaste) {
                    optimalRafter = rafter;
                }
            }
        }

        numberOfRafters = (int) Math.ceil((double) carportLength / DISTANCE_BETWEEN_RAFTERS);
        costPrice = numberOfRafters * optimalRafter.getCostPrice();

        // Add the rafter product to the product list
        if (optimalRafter != null) {
            productList.add(new ProductListItem(optimalRafter.getProductID(), optimalRafter.getName(), description, optimalRafter.getLength(), rafterUnit, numberOfRafters, costPrice));
        }
    }

    private static void calcRoof(int carportWidth, int carportLength) {
        List<Product> roofList = ProductMapper.getProducts(ROOF_TYPEID, connectionPool);
        String description = "Tagplader - monteres på spær";
        String roofUnit = "Stk.";
        int price;

        // Calculate the number of width plates accounting for overlap'
        int amountWidthPlates = 0;
        int panelWidthAdjusted = DEFAULT_ROOF_PANEL_WIDTH - ROOF_PANEL_OVERLAP;
        double amountWidthPanelsDouble = ((double) carportWidth - ROOF_PANEL_OVERLAP) / panelWidthAdjusted;

        if (amountWidthPanelsDouble % 1 == 0) {
            amountWidthPlates = (int) amountWidthPanelsDouble;
        } else {
            amountWidthPlates = (int) Math.ceil(amountWidthPanelsDouble);
        }


        // Variables to keep track of and store optimal roof plate option
        int leastRoofWaste = Integer.MAX_VALUE;
        Product optimalRoofPanel = null;

        for (Product roofPanel : roofList) {

            int amountLengthPanels = 0;
            int panelLengthAdjusted = roofPanel.getLength() - ROOF_PANEL_OVERLAP;
            double amountLengthPanelsDouble = ((double) carportLength - ROOF_PANEL_OVERLAP) / panelLengthAdjusted;

            if (amountLengthPanelsDouble % 1 == 0) {
                amountLengthPanels = (int) amountLengthPanelsDouble;
            } else {
                amountLengthPanels = (int) Math.ceil(amountLengthPanelsDouble);
            }

            int totalPanelLength = roofPanel.getLength() * amountLengthPanels;
            int roofWasted = totalPanelLength - carportLength;

            if (roofWasted < leastRoofWaste) {
                leastRoofWaste = roofWasted;
                numberOfRoofPanels = amountLengthPanels * amountWidthPlates;
                optimalRoofPanel = roofPanel;
            }
        }

        price = numberOfRoofPanels * optimalRoofPanel.getPrice();

        // Add the optimal roof plate to the productList
        if (optimalRoofPanel != null) {
            productList.add(new ProductListItem(optimalRoofPanel.getProductID(), optimalRoofPanel.getName(), description, optimalRoofPanel.getLength(), roofUnit, numberOfRoofPanels, price));
        }
    }


    private static void shedFacadeCalc() {

    }

    private static void calcScrews() {

    }

    private static void fittingsCalc() {

    }


    public static List<ProductListItem> getProductList() {
        return productList;
    }

    public static void clearList() {

        productList.clear();
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

    public static int getNumberOfRoofPanels() {
        return numberOfRoofPanels;
    }
}
