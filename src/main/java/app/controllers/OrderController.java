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
        app.get("getOrdersByUser", ctx -> getOrdersByUser(ctx, connectionPool));

    }

    private static void getOrdersByUser(Context ctx, ConnectionPool connectionPool) {

        User user = ctx.sessionAttribute("currentUser");
        List<Order> orderList = OrderMapper.getOrdersByUser(connectionPool, user);
        ctx.sessionAttribute("ordersByUser", orderList);
        ctx.render("view-order.html");

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

    private static void newOrder(Context ctx, ConnectionPool connectionPool) {
        int carportWidthID = Integer.parseInt(ctx.formParam("carportWidth"));
        int carportLengthID = Integer.parseInt(ctx.formParam("carportLength"));
        boolean shed = Boolean.parseBoolean(ctx.formParam("shed"));
        String remark = ctx.formParam("remark");

        try {
            int carportWidth = OrderMapper.getWidthByID(carportWidthID, connectionPool);
            int carportLength = OrderMapper.getLengthByID(carportLengthID, connectionPool);
            ProductListCalc productListCalc = new ProductListCalc(carportWidth, carportLength, shed, connectionPool);
            productListCalc.calculateProductList();
            List<ProductListItem> productList = productListCalc.getProductList();
            User user = ctx.sessionAttribute("currentUser");
            // OrderMapper.newOrder(user, productList, remark, connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }


    }
}
