package app.controllers;

import app.entities.Product;
import app.entities.ProductList;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import app.services.ProductListCalc;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("newOrder", ctx -> newOrder(ctx, connectionPool));
    }

    private static void newOrder(Context ctx, ConnectionPool connectionPool) {
        int carportWidthID = Integer.parseInt(ctx.formParam("carportWidth"));
        int carportLengthID = Integer.parseInt(ctx.formParam("carportLength"));
        boolean shed = Boolean.parseBoolean(ctx.formParam("shed"));
        String remark = ctx.formParam("remark");

        try {
            int carportWidth = OrderMapper.getWidthByID(carportWidthID, connectionPool);
            int carportLength = OrderMapper.getLengthByID(carportLengthID, connectionPool);
            List<ProductList> productList = ProductListCalc.calculateProductList(carportWidth, carportLength, shed, connectionPool);
            User user = ctx.sessionAttribute("currentUser");
            // OrderMapper.newOrder(user, productList, remark, connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }


    }
}
