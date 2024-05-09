package app.controllers;

import app.entities.Order;
import app.entities.Status;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;


import java.util.List;

public class OrderController {


    public static void addRoute(Javalin app, ConnectionPool connectionPool) {

        app.post("viewAllOrders", ctx -> viewAllOrders(ctx, connectionPool));
        app.post("filterByStatus", ctx -> filterByStatus(ctx, connectionPool));
        app.post("inquiryDetailsPage", ctx -> inquiryDetailsPage(ctx, connectionPool));


    }

    private static void inquiryDetailsPage(Context ctx, ConnectionPool connectionPool) {




        ctx.render("inquiry-details");
    }

    private static void viewAllOrders(Context ctx, ConnectionPool connectionPool) {

        globalOrderAttributes(ctx, connectionPool, null);

        ctx.render("admin-orders");

    }

    private static void filterByStatus(Context ctx, ConnectionPool connectionPool) {

        int statusID = Integer.parseInt(ctx.formParam("filter"));

        globalOrderAttributes(ctx, connectionPool, statusID);

        ctx.render("admin-orders");

    }

    private static Context globalOrderAttributes(Context ctx, ConnectionPool connectionPool, Integer statusID) {

        List<Status> statusList = OrderMapper.loadStatusList(connectionPool);
        List<Order> orderList = OrderMapper.getOrders(connectionPool, statusID);

        ctx.attribute("orderList", orderList);
        ctx.attribute("statusList", statusList);

        return ctx;
    }

}
