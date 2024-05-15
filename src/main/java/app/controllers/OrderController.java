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
        app.get("viewAllOrders", ctx -> viewAllOrders(ctx, connectionPool));
        app.post("filterByStatus", ctx -> filterByStatus(ctx, connectionPool));
        app.post("inquiryDetailsPage", ctx -> inquiryDetailsPage(ctx, connectionPool));
        app.post("approveInquiry", ctx -> approveInquiry(ctx, connectionPool));
        app.get("/mine-ordrer", ctx -> getOrdersByUser(ctx, connectionPool));
        app.get("orderPaid",ctx -> setOrderPaid(ctx, connectionPool));
    }

    private static void setOrderPaid(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        OrderMapper.setOrderPaid(connectionPool, user);
        getOrdersByUser(ctx,connectionPool);
    }

    private static void getOrdersByUser(Context ctx, ConnectionPool connectionPool) {

        User user = ctx.sessionAttribute("currentUser");
        List<Order> orderList = OrderMapper.getOrdersByUser(connectionPool, user);
        ctx.attribute("orderList", orderList);
        ctx.render("user/view-orders.html");

    }

    private static void inquiryDetailsPage(Context ctx, ConnectionPool connectionPool) {

        ProductListCalc.clearList();

        int orderID = Integer.parseInt(ctx.formParam("orderID"));


        Order order = OrderMapper.getOrderByID(connectionPool, orderID);
        String svgDrawwing = prepareCarportDrawing(order.getCarportWidth().getWidth(), order.getCarportLength().getLength(), order.isShed());

        List<ProductListItem> productListItems = prepareProductList(order.getCarportWidth().getWidth(), order.getCarportLength().getLength(), order.isShed(), connectionPool);


        if ((ctx.formParam("costPrice")) == null) {

        preparePriceDetails(ctx, order.getTotalPrice());

        }

        ctx.attribute("svgDrawing", svgDrawwing);
        ctx.attribute("productListItems", productListItems);
        ctx.attribute("order", order);
        ctx.render("admin/inquiry-details.html");

        ProductListCalc.clearList();

    }

    private static void updateInquiryPrice (Context ctx, int costPrice, int profitPrice) {




    }

    private static Context preparePriceDetails (Context ctx, int orderPrice) {

        int profitPrice = orderPrice - PROCESSING_FEE;
        int costPrice = (int) (profitPrice - (profitPrice * DEGREE_OF_COVERAGE));

        ctx.attribute("totalPrice", orderPrice);
        ctx.attribute("processFee", PROCESSING_FEE);
        ctx.attribute("profitPrice", profitPrice);
        ctx.attribute("costPrice", costPrice);

        return ctx;
    }

    private static void viewAllOrders(Context ctx, ConnectionPool connectionPool) {

        globalOrderAttributes(ctx, connectionPool, null);

        ctx.render("admin/orders.html");

    }

    private static void filterByStatus(Context ctx, ConnectionPool connectionPool) {

        int statusID = Integer.parseInt(ctx.formParam("filter"));

        globalOrderAttributes(ctx, connectionPool, statusID);

        ctx.render("admin/orders.html");

    }

    private static Context globalOrderAttributes(Context ctx, ConnectionPool connectionPool, Integer statusID) {

        List<Status> statusList = OrderMapper.loadStatusList(connectionPool);
        List<Order> orderList = OrderMapper.getOrders(connectionPool, statusID);

        ctx.attribute("orderList", orderList);
        ctx.attribute("statusList", statusList);

        return ctx;
    }

    private static boolean approveInquiry(Context ctx, ConnectionPool connectionPool) {

        int orderID = Integer.parseInt(ctx.formParam("orderID"));

        if (OrderMapper.ApproveOrder(connectionPool, orderID)) {

            String message = "Ordre Godkendt";

            ctx.attribute("approved", message);
            inquiryDetailsPage(ctx, connectionPool);
            return true;
        }
         return false;

    }

    private static void prepareInquiry(Context ctx, ConnectionPool connectionPool) {
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
                    + "\nLængde: " + carportLength + " cm."
                    + "\nTag: Plastmo Ecolite"
                    + "\nSkur: " + (shed ? "Ja" : "Nej")
                    + "\nBemærkning: " + remark;
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

        List<ProductListItem> productList = prepareProductList(carportWidth, carportLength, shed, connectionPool);
        for (ProductListItem productListItem : productList) {
            estimatedPrice += productListItem.getPrice(); //TODO: Needs the coverage degree added to the price
        }
        estimatedPrice = calculateOrderPrice(estimatedPrice);
        String carportDrawing = prepareCarportDrawing(carportWidth, carportLength, shed);
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
        List<ProductListItem> productList = ctx.sessionAttribute("productList");
        String carportDrawing = ctx.sessionAttribute("carportDrawing");
        int orderPrice = ctx.sessionAttribute("estimatedPrice");

        // If user has not logged in, create an account TODO: If they have already have an account, there is no option currently to simply log in
        if (user == null) {
            String name = ctx.formParam("username");
            String email = ctx.formParam("email");
            String password = ctx.formParam("password");
            try {
                user = UserMapper.createUser(name, email, password, connectionPool);
            } catch (DatabaseException e) {
                String msg = "Kan ikke oprette forespørgsel, da en bruger med denne email allerede eksisterer. Prøv igen.";
                ctx.attribute("inquiryFailed", msg);
            }
        }

        // Create new order/inquiry, and redirect back to the landing page, sending orderID as an attribute to be displayed to the user
        try {
            int orderID = OrderMapper.newOrder(user, carportWidthID, carportLengthID, description, shedChosen, remark, productList, orderPrice, carportDrawing, connectionPool);
            ctx.attribute("orderID", orderID);
            ctx.render("user/index.html");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ProductListItem> prepareProductList(int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {
        ProductListCalc productListCalc = new ProductListCalc(carportWidth, carportLength, shed, connectionPool);
        productListCalc.calculateProductList();
        return productListCalc.getProductList();
    }

    // Will become method to be used with prepareInquiry to receive SVG drawing
    private static String prepareCarportDrawing (int carportWidth, int carportLength, boolean shed) {
        Locale.setDefault(new Locale("US"));
        CarportSvg carportSvg = new CarportSvg(carportLength, carportWidth, shed);

        return carportSvg.toString();
    }

    private static Context prepareOrderAttributes (Context ctx, int carportWidthID, int carportLengthID, String inquiryDescription, boolean shed, String remark, List<ProductListItem> productList, String svgDrawing, int estimatedPrice) {
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

    private static int calculateOrderPrice (int price) {
        int carportPrice = (int) ((price * DEGREE_OF_COVERAGE) + price + PROCESSING_FEE);

        return carportPrice;
    }
}
