package app.controllers;


import app.entities.Order;
import app.entities.Status;
import app.entities.ProductListItem;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import app.services.CarportSvg;
import app.services.ProductListCalc;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Locale;

public class OrderController {
    private static final double DEGREE_OF_COVERAGE = 0.40;
    private static final int PROCESSING_FEE = 2000;

    public static void addRoute(Javalin app, ConnectionPool connectionPool) {
        app.post("/godkend-forespoergsel", ctx -> prepareInquiry(ctx, connectionPool));
        app.post("/ny-ordre", ctx -> newOrder(ctx, connectionPool));
        app.get("/alle-ordrer", ctx -> viewAllOrders(ctx, connectionPool));
        app.post("/alle-ordrer-filter", ctx -> filterByStatus(ctx, connectionPool));
        app.post("/forespoergelses-detaljer", ctx -> inquiryDetailsPage(ctx, connectionPool));
        app.post("godkend", ctx -> approveInquiry(ctx, connectionPool));
        app.get("/mine-ordrer", ctx -> getOrdersByUser(ctx, connectionPool));
        app.post("orderPaid", ctx -> setOrderPaid(ctx, connectionPool));
        app.post("annuller", ctx -> cancelOrderByID(ctx, connectionPool));
    }

    private static boolean verifyAdmin(Context ctx) {

        User user = ctx.sessionAttribute("currentUser");

        if (user != null && user.getRoleID() == 2) {

            return true;
        }
        return false;
    }

    private static void cancelOrderByID(Context ctx, ConnectionPool connectionPool) {

        User user = ctx.sessionAttribute("currentUser");
        int orderID = Integer.parseInt(ctx.formParam("orderID"));
        int customer = 1;
        int admin = 2;

        try {


            if (OrderMapper.cancelOrder(connectionPool, orderID, user)) {

                ctx.attribute("orderID", orderID);
                if (user.getRoleID() == customer) {
                    ctx.redirect("/mine-ordrer");
                } else if (user.getRoleID() == admin) {
                    inquiryDetailsPage(ctx, connectionPool);
                }
            } else {
                String msg = "Kan ikke annnullere ordre";
                ctx.attribute("cancelError", msg);
            }
        } catch (DatabaseException e) {

            ctx.status(500);
            System.err.println("DatabaseException: " + e.getMessage());
        }
    }

    private static void setOrderPaid(Context ctx, ConnectionPool connectionPool) {

        try {

            User user = ctx.sessionAttribute("currentUser");
            int orderID = Integer.parseInt(ctx.formParam("orderID"));
            OrderMapper.setOrderPaid(connectionPool, user, orderID);
            getOrdersByUser(ctx, connectionPool);

        } catch (DatabaseException e) {

            ctx.status(500);
            System.err.println("DatabaseException: " + e.getMessage());
        }

    }

    private static void getOrdersByUser(Context ctx, ConnectionPool connectionPool) {

        try {

            User user = ctx.sessionAttribute("currentUser");
            List<Order> orderList = OrderMapper.getOrdersByUser(connectionPool, user);
            ctx.attribute("orderList", orderList);
            ctx.render("user/view-orders.html");
        } catch (DatabaseException e) {

            ctx.status(500);
            System.err.println("DatabaseException: " + e.getMessage());
        }

    }

    private static void inquiryDetailsPage(Context ctx, ConnectionPool connectionPool) {

        int orderID = Integer.parseInt(ctx.formParam("orderID"));
        int costPrice = 0;

        try {

            Order order = OrderMapper.getOrderByID(connectionPool, orderID);
            String svgDrawing = prepareCarportDrawing(order.getCarportWidth().getWidth(), order.getCarportLength().getLength(), order.isShed(), connectionPool);

            List<ProductListItem> productList = prepareProductList(order.getCarportWidth().getWidth(), order.getCarportLength().getLength(), order.isShed(), connectionPool);

            if ((ctx.formParam("costPrice")) == null) {

                preparePriceDetails(ctx, order.getTotalPrice(), productList);

            } else {
                int totalPrice = Integer.parseInt(ctx.formParam("totalPrice"));
                costPrice = Integer.parseInt(ctx.formParam("costPrice"));

                updateInquiryPrice(ctx, order, totalPrice, costPrice);
            }

            ctx.attribute("svgDrawing", svgDrawing);
            ctx.attribute("productListItems", productList);
            ctx.attribute("order", order);
            ctx.render("admin/inquiry-details.html");

        } catch (DatabaseException e) {

            ctx.status(500);
            System.err.println("DatabaseException: " + e.getMessage());
        }
    }


    private static Context preparePriceDetails(Context ctx, int orderPrice, List <ProductListItem> productList) {
        int costPrice = 0;

        for (ProductListItem productListItem : productList) {
            costPrice += productListItem.getCostPrice();
        }
        int profitPrice = orderPrice - PROCESSING_FEE;
        double degreeOfCoverage = (((double) profitPrice / costPrice) - 1) * 100;
        degreeOfCoverage = Math.round(degreeOfCoverage * 100.0) / 100.0;

        if (degreeOfCoverage >= 39.9) {
            degreeOfCoverage = DEGREE_OF_COVERAGE * 100;
        }

        ctx.attribute("degreeOfCoverage", degreeOfCoverage);
        ctx.attribute("totalPrice", orderPrice);
        ctx.attribute("processFee", PROCESSING_FEE);
        ctx.attribute("profitPrice", profitPrice);
        ctx.attribute("costPrice", costPrice);

        return ctx;
    }

    private static Context updateInquiryPrice(Context ctx, Order order, int totalPrice, int costPrice) {
        int profitPrice = totalPrice - PROCESSING_FEE;
        double newDegreeOfCoverage = (((double) profitPrice / costPrice) - 1) * 100;

        if (newDegreeOfCoverage < 5.0 || newDegreeOfCoverage > 40.0) {
            String msg = "Dækningsgrad bliver under 5%/over 40% ved denne pris - vælg en anden pris";
            ctx.attribute("totalPrice", order.getTotalPrice());
            ctx.attribute("degreeOfCoverage", DEGREE_OF_COVERAGE * 100);
            ctx.attribute("profitPrice", order.getTotalPrice() - PROCESSING_FEE);
            ctx.attribute("wrongPrice", msg);
        } else {
            String formattedDegreeOfCoverage = String.format("%.2f", newDegreeOfCoverage);

            ctx.attribute("totalPrice", totalPrice);
            ctx.attribute("degreeOfCoverage", formattedDegreeOfCoverage);
            ctx.attribute("profitPrice", profitPrice);
        }

        ctx.attribute("processFee", PROCESSING_FEE);
        ctx.attribute("costPrice", costPrice);

        return ctx;
    }

    private static void viewAllOrders(Context ctx, ConnectionPool connectionPool) {

        if (verifyAdmin(ctx)) {

            globalOrderAttributes(ctx, connectionPool, null);

            ctx.render("admin/orders.html");
        } else {

            ctx.status(403);
        }
    }

    private static void filterByStatus(Context ctx, ConnectionPool connectionPool) {

        if (verifyAdmin(ctx)) {

            int statusID = Integer.parseInt(ctx.formParam("filter"));

            globalOrderAttributes(ctx, connectionPool, statusID);

            ctx.render("admin/orders.html");
        } else {
            ctx.status(403);

        }
    }

    private static Context globalOrderAttributes(Context ctx, ConnectionPool connectionPool, Integer statusID) {

        try {

            List<Status> statusList = OrderMapper.loadStatusList(connectionPool);
            List<Order> orderList = OrderMapper.getOrders(connectionPool, statusID);

            ctx.attribute("orderList", orderList);
            ctx.attribute("statusList", statusList);

        } catch (DatabaseException e) {

            ctx.status(500);
            new DatabaseException("Failed to retrieve global order attributes from database", e.getMessage());
        }
        return ctx;
    }

    private static boolean approveInquiry(Context ctx, ConnectionPool connectionPool) {

        int orderID = Integer.parseInt(ctx.formParam("orderID"));
        int totalPrice = Integer.parseInt(ctx.formParam("totalPrice"));

        try {


            if (OrderMapper.ApproveOrder(connectionPool, orderID, totalPrice)) {

                String message = "Ordre Godkendt";

                ctx.attribute("approved", message);
                inquiryDetailsPage(ctx, connectionPool);
                return true;
            }
        } catch (DatabaseException e) {

            ctx.status(500);
            System.err.println("DatabaseException: " + e.getMessage());
        }
        return false;

    }

    private static void prepareInquiry(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int carportWidthID = Integer.parseInt(ctx.formParam("carportWidth"));
        int carportLengthID = Integer.parseInt(ctx.formParam("carportLength"));
        boolean shed = Boolean.parseBoolean(ctx.formParam("shed"));
        String remark = ctx.formParam("remark");
        int carportWidth;
        int carportLength;
        int estimatedPrice = 0;
        String inquiryDescription = "";

        try {
            carportWidth = OrderMapper.getWidthByID(carportWidthID, connectionPool);
            carportLength = OrderMapper.getLengthByID(carportLengthID, connectionPool);
            inquiryDescription = "Bredde: " + carportWidth + " cm."
                    + "<br>Længde: " + carportLength + " cm."
                    + "<br>Tag: Plastmo Ecolite"
                    + "<br>Skur: " + (shed ? "Ja" : "Nej")
                    + "<br>Bemærkning: " + remark;
        } catch (DatabaseException e) {
            throw new DatabaseException("Error preparing inquiry", e.getMessage());
        }

        List<ProductListItem> productList = prepareProductList(carportWidth, carportLength, shed, connectionPool);
        estimatedPrice = calculateOrderPrice(productList);
        String carportDrawing = prepareCarportDrawing(carportWidth, carportLength, shed, connectionPool);
        prepareOrderAttributes(ctx, carportWidthID, carportLengthID, inquiryDescription, shed, remark, productList, carportDrawing, estimatedPrice);

        ctx.render("user/accept-inquiry.html");
    }

    private static void newOrder(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        int carportWidthID = ctx.sessionAttribute("carportWidthID");
        int carportLengthID = ctx.sessionAttribute("carportLengthID");
        String description = ctx.sessionAttribute("description");
        boolean shedChosen = ctx.sessionAttribute("shed");
        String remark = ctx.sessionAttribute("orderRemark");
        remark = remark.replace("<br>", " ");
        List<ProductListItem> productList = ctx.sessionAttribute("productList");
        String carportDrawing = ctx.sessionAttribute("carportDrawing");
        int orderPrice = ctx.sessionAttribute("estimatedPrice");

        // If user has not logged in, create an account TODO: If they have already have an account, there is no option currently to simply log in
        if (user == null) {
            String fname = ctx.formParam("fname");
            String lname = ctx.formParam("lname");
            String name = fname + " " + lname;
            String email = ctx.formParam("email");
            String password = ctx.formParam("password");

            try {
                // Attempt to login
                user = UserMapper.login(email.toLowerCase(), password, connectionPool);

                // If login fails, DatabaseException should be thrown.
                // If login succeeds, proceed to set the session attribute.
                ctx.sessionAttribute("currentUser", user);
            } catch (DatabaseException e) {
                // If login fails, try to create the user.
                try {
                    user = UserMapper.createUser(name.toLowerCase(), email.toLowerCase(), password, connectionPool);
                    ctx.sessionAttribute("currentUser", user);
                } catch (DatabaseException ex) {
                    // Handle the case where user creation fails (e.g., email already exists).
                    String msg = "Kan ikke oprette forespørgsel, da en bruger med denne email allerede eksisterer.<br>Prøv igen.";
                    ctx.attribute("inquiryFailed", msg);
                    ctx.render("user/accept-inquiry.html");
                    return; // Stop execution of the method if user creation fails
                }
            }

        }

        // Create new order/inquiry, and redirect back to the landing page, sending orderID as an attribute to be displayed to the user
        try {
            int orderID = OrderMapper.newOrder(user, carportWidthID, carportLengthID, description, shedChosen, remark, productList, orderPrice, carportDrawing, connectionPool);
            ctx.attribute("orderID", orderID);
            ctx.redirect("/mine-ordrer");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ProductListItem> prepareProductList(int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) throws DatabaseException {

        ProductListCalc productListCalc = new ProductListCalc(carportWidth, carportLength, shed, connectionPool);
        productListCalc.calculateProductList();
        return productListCalc.getProductList();
    }

    // Will become method to be used with prepareInquiry to receive SVG drawing

    private static String prepareCarportDrawing(int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) throws DatabaseException {
        Locale.setDefault(new Locale("US"));
        CarportSvg carportSvg = new CarportSvg(carportLength, carportWidth, shed, connectionPool);

        return carportSvg.toString();
    }

    private static Context prepareOrderAttributes(Context ctx, int carportWidthID, int carportLengthID, String inquiryDescription, boolean shed, String remark, List<ProductListItem> productList, String svgDrawing, int estimatedPrice) {
        ctx.sessionAttribute("carportWidthID", carportWidthID);
        ctx.sessionAttribute("carportLengthID", carportLengthID);
        ctx.sessionAttribute("description", inquiryDescription);
        ctx.sessionAttribute("orderRemark", remark);
        ctx.sessionAttribute("shed", shed);
        ctx.sessionAttribute("productList", productList);
        ctx.sessionAttribute("carportDrawing", svgDrawing);
        ctx.sessionAttribute("estimatedPrice", estimatedPrice);
        return ctx;
    }

    private static int calculateOrderPrice(List<ProductListItem> productList) {
        int costPrice = 0;
        int carportPrice;
        for (ProductListItem productListItem : productList) {
             costPrice += productListItem.getCostPrice();
        }
        carportPrice = (int) (((costPrice * DEGREE_OF_COVERAGE) + costPrice) + PROCESSING_FEE);

        return carportPrice;
    }
}
