package app.controllers;

import app.entities.CarportLength;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class UserController {

    public static void addRoute(Javalin app, ConnectionPool connectionPool) {
        app.get("renderLoginPage", ctx -> renderLoginPage(ctx, connectionPool));
        app.get("/opret-bruger", ctx -> renderSignupPage(ctx, connectionPool));
        app.get("/renderIndex", ctx -> renderIndex(ctx, connectionPool));
        app.get("/acceptInquiry", ctx -> acceptInquiry(ctx, connectionPool));
        app.get("myOrders", ctx -> myOrders(ctx, connectionPool));
        app.get("/createInquiry", ctx -> createInquiry(ctx, connectionPool));
    }


    private static void createInquiry(Context ctx, ConnectionPool connectionPool) throws DatabaseException {

        List<CarportLength> getAllLengthList = ProductMapper.getAllLength(connectionPool);
        ctx.attribute("getAllLength", getAllLengthList);
        ctx.render("user/create-inquiry.html");

    }


    private static void renderIndex(Context ctx, ConnectionPool connectionPool) {

        ctx.render("user/index.html");

    }

    private static void myOrders(Context ctx, ConnectionPool connectionPool) {

        ctx.render("user/view-orders.html");

    }
    private static void acceptInquiry(Context ctx, ConnectionPool connectionPool) {

        ctx.render("user/accept-inquiry.html");

    }

    private static void renderLoginPage(Context ctx, ConnectionPool connectionPool) {

        ctx.render("login.html");
    }

    private static void renderSignupPage (Context ctx, ConnectionPool connectionPool) {

        ctx.render("user/signup.html");
    }



    private  static  void changePassword(Context ctx, ConnectionPool connectionPool) {


    }
}
