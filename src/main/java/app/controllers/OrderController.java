package app.controllers;

import app.entities.ProductListItem;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.CarportSvg;
import app.services.ProductListCalc;
import app.services.Svg;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Locale;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("newOrder", ctx -> newOrder(ctx, connectionPool));
        app.get("showSVG", ctx -> showSVG(ctx, connectionPool));
    }

    private static void showSVG(Context ctx, ConnectionPool connectionPool) {
        Locale.setDefault(new Locale("US"));

//        int carportWidthID = Integer.parseInt(ctx.formParam("carportWidth"));
//        int carportLengthID = Integer.parseInt(ctx.formParam("carportLength"));
        int carportWidth = 600;
        int carportLength = 780;
        boolean shed = true;

//        try {
//            carportWidth = OrderMapper.getWidthByID(carportWidthID, connectionPool);
//            carportLength = OrderMapper.getLengthByID(carportLengthID, connectionPool);
//        } catch (DatabaseException e) {
//            throw new RuntimeException(e);
//        }


        CarportSvg carportSvg = new CarportSvg(carportLength, carportWidth, false);
//        Svg carportSvg = new Svg(0,0,"0 0 855 690", "100%");
//        carportSvg.addRectangle(0,0,600,780,"stroke-width:1px; stroke:#000000; fill:#ffffff");


        ctx.attribute("svg", carportSvg.toString());
        ctx.render("testSVG.html");
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

            //List<ProductListItem> productList = ProductListCalc.calculateProductList(carportWidth, carportLength, shed, connectionPool);
            User user = ctx.sessionAttribute("currentUser");
            // OrderMapper.newOrder(user, productList, remark, connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }


    }
}
