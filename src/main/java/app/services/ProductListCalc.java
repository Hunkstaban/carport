package app.services;

import app.entities.Product;
import app.entities.ProductListItem;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;

import javax.xml.crypto.Data;
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

    private List<ProductListItem> productList = new ArrayList<>();
    private int carportWidth;
    private int carportLength;
    private boolean shed;
    private int numberOfPosts;
    private int numberOfPostsWOShed;
    private int numberOfBeams;
    private int numberOfRafters;
    private int numberOfRoofPanels;
    private ConnectionPool connectionPool;

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

    // TODO: Find the option that will waste the least amount of wood/roof AND will be the cheapest
    // TODO: Handle exceptions and error handling if it can't add a product to the product list
    // TODO: Potentially add a check to see if the chosen product and amount is in stock

    void calcPosts(int carportLength) {
        Product optimalPost = null;
        String description = "Stolper - nedgraves 90 cm. i jord";
        String postUnit = "Stk.";
        int costPrice;
        List<Product> postOptions = null;
        try {
            postOptions = ProductMapper.getProducts(POST_TYPEID, connectionPool);
        } catch (DatabaseException e) {
            new DatabaseException("Failed to retrieve product with post type_id from database", e.getMessage());
        }


        // Currently only one post option exist on the DB, so we populate postName and -Length with a loop
        for (Product postOption : postOptions) {
            optimalPost = postOption;
        }

        // Calculate number of posts needed - all carports start with 4 (INITIAL_POSTS) - if shed is attached, an additional 5 posts gets added
        // An extra set of posts gets added if the remaining length is greater than the MAX_DISTANCE_BETWEEN_POSTS
        numberOfPosts = (int) (INITIAL_POSTS + (2 * Math.floor((double) (carportLength - UNSUPPORTED_SPACE) / MAX_DISTANCE_BETWEEN_POSTS)) + (shed ? 5 : 0));

        costPrice = numberOfPosts * optimalPost.getCostPrice();

        // Add post details to product list
        if (optimalPost != null) {
            productList.add(new ProductListItem(optimalPost.getProductID(), optimalPost.getName(), description, optimalPost.getLength(), postUnit, numberOfPosts, costPrice));
        }
    }


    void calcBeams(int carportLength) {
        List<Product> beamList = null;
        try {
            beamList = ProductMapper.getProducts(RAFTER_AND_BEAM_TYPEID, connectionPool);
        } catch (DatabaseException e) {
            new DatabaseException("Failed to retrieve products with beams type_id from database", e.getMessage());
        }
        int totalCarportLength = 2 * carportLength;
        String description = "Remme i sider - sadles ned i stolper";
        String beamUnit = "Stk.";
        int costPrice;
        int beamsNeeded;

        // Calculating the distance inbetween the posts supporting the carport
        numberOfPostsWOShed = numberOfPosts - (shed ? 5 : 0);
        int spaceBetweenPosts = (carportLength - UNSUPPORTED_SPACE) / (numberOfPostsWOShed / 2 - 1) + UNSUPPORTED_SPACE;

        // Variables to keep track of and store woode waste and the optimal beam option
        int leastWoodWaste = Integer.MAX_VALUE;
        Product optimalBeam = null;


        // Looping through each beam, finding the optimal length that waste the least wood
        for (Product beam : beamList) {

            if (
                (numberOfPostsWOShed == 4 && beam.getLength() > carportLength) ||
                (numberOfPostsWOShed > 4 && beam.getLength() == carportLength || beam.getLength() >= spaceBetweenPosts)
            ) {

                //
                double beamsNeededDouble = (double) carportLength / beam.getLength();

                //
                if (beamsNeededDouble % 1 == 0) {
                    beamsNeeded = (int) beamsNeededDouble * 2;
                } else {
                    beamsNeeded = ((int) Math.floor(beamsNeededDouble) + 1) * 2;
                }

                int totalBeamLength = beam.getLength() * beamsNeeded;
                int woodWasted = totalBeamLength - totalCarportLength;

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

    void calcRafters(int carportWidth, int carportLength) {
        String description = "Spær - monteres på rem";
        String rafterUnit = "Stk.";
        List<Product> rafterOptions = null;
        try {
            rafterOptions = ProductMapper.getProducts(RAFTER_AND_BEAM_TYPEID, connectionPool);
        } catch (DatabaseException e) {
            new DatabaseException("Failed to retrieve products with rafter type_id from database", e.getMessage());
        }
        int costPrice;

        // Variables to keep track of and store the optimal rafter option
        int leastWoodWaste = Integer.MAX_VALUE;
        Product optimalRafter = null;

        // Looping through each rafter, finding the optimal length that wastes the least wood
        for (Product rafter : rafterOptions) {
            if (rafter.getLength() >= carportWidth) {
                int woodWasted = rafter.getLength() - carportWidth;
                if (woodWasted < leastWoodWaste) {
                    leastWoodWaste = woodWasted;
                    optimalRafter = rafter;
                }
            }
        }

        if (optimalRafter != null) {
            numberOfRafters = (int) Math.ceil((double) carportLength / DISTANCE_BETWEEN_RAFTERS);
            costPrice = numberOfRafters * optimalRafter.getCostPrice();

            // Add the rafter product to the product list
            productList.add(new ProductListItem(optimalRafter.getProductID(), optimalRafter.getName(), description, optimalRafter.getLength(), rafterUnit, numberOfRafters, costPrice));
        }
    }


    void calcRoof(int carportWidth, int carportLength) {
        List<Product> roofList = null;
        try {
            roofList = ProductMapper.getProducts(ROOF_TYPEID, connectionPool);
        } catch (DatabaseException e) {
            new DatabaseException("Failed to retrieve products with roof type_id from database", e.getMessage());
        }
        String description = "Tagplader - monteres på spær";
        String roofUnit = "Stk.";
        int price;

        // Calculate the number of roof panels needed for the width, accounting for overlap
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

        // Looping through each roof panel, finding the optimal length that waste the least amount of material
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

        // Add the optimal roof panel to the productList
        if (optimalRoofPanel != null) {
            productList.add(new ProductListItem(optimalRoofPanel.getProductID(), optimalRoofPanel.getName(), description, optimalRoofPanel.getLength(), roofUnit, numberOfRoofPanels, price));
        }
    }


    void shedFacadeCalc() {

    }

    void calcScrews() {

    }

    void fittingsCalc() {

    }

    public void clearList() {
        productList.clear();
    }

    public List<ProductListItem> getProductList() {
        return productList;
    }

    public int getNumberOfPosts() {
        return numberOfPosts;
    }

    public int getNumberOfPostsWOShed() {
        return numberOfPostsWOShed;
    }

    public int getNumberOfBeams() {
        return numberOfBeams;
    }

    public int getNumberOfRafters() {
        return numberOfRafters;
    }

    public int getNumberOfRoofPanels() {
        return numberOfRoofPanels;
    }
}
