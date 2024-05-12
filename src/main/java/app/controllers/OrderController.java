package app.controllers;


import app.entities.Order;
import app.entities.Status;
import app.entities.ProductListItem;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.ProductListCalc;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;

public class OrderController {

    public static void addRoute(Javalin app, ConnectionPool connectionPool) {
        app.post("newOrder", ctx -> newOrder(ctx, connectionPool));
        app.post("viewAllOrders", ctx -> viewAllOrders(ctx, connectionPool));
        app.post("filterByStatus", ctx -> filterByStatus(ctx, connectionPool));
        app.post("inquiryDetailsPage", ctx -> inquiryDetailsPage(ctx, connectionPool));


    }

    private static void inquiryDetailsPage(Context ctx, ConnectionPool connectionPool) {

        ctx.render("admin/inquiry-details.html");
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

    private static void prepareInquiry(Context ctx, ConnectionPool connectionPool) {
        int carportWidthID = Integer.parseInt(ctx.formParam("carportWidth"));
        int carportLengthID = Integer.parseInt(ctx.formParam("carportLength"));
        boolean shed = Boolean.parseBoolean(ctx.formParam("shed"));
        String remark = ctx.formParam("remark");
        int carportWidth = 0;
        int carportLength = 0;

        try {
            carportWidth = OrderMapper.getWidthByID(carportWidthID, connectionPool);
            carportLength = OrderMapper.getLengthByID(carportLengthID, connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

        List<ProductListItem> productList = prepareProductList(carportWidth, carportLength, shed, connectionPool);
        String carportDrawing = prepareCarportDrawing(carportWidth, carportLength, shed);
        prepareOrderAttributes(ctx, carportWidthID, carportLengthID, shed, remark, productList, carportDrawing);

        ctx.render("user/accept-inquiry.html");
    }

    private static void newOrder(Context ctx, ConnectionPool connectionPool) {

    }

    private static List<ProductListItem> prepareProductList (int carportWidth, int carportLength, boolean shed, ConnectionPool connectionPool) {

        ProductListCalc productListCalc = new ProductListCalc(carportWidth, carportLength, shed, connectionPool);
        productListCalc.calculateProductList();
        return productListCalc.getProductList();
    }

    // Will become method to be used with prepareInquiry
    private static String prepareCarportDrawing (int carportWidth, int carportLength, boolean shed) {
        return "test";
    }

    private static Context prepareOrderAttributes (Context ctx, int carportWidthID, int carportLengthID, boolean shed, String remark, List<ProductListItem> productList, String svgDrawing) {
        ctx.attribute("carportWidthID", carportWidthID);
        ctx.attribute("carportLengthID", carportLengthID);
        ctx.attribute("orderRemark", remark);
        ctx.attribute("shed", shed);
        ctx.attribute("productList", productList);
        ctx.attribute("carportDrawing", svgDrawing);
        return ctx;
    }
}
