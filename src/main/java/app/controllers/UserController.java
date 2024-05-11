package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoute(Javalin app, ConnectionPool connectionPool) {
        app.get("renderLoginPage", ctx -> renderLoginPage(ctx, connectionPool));
        app.get("renderSignupPage", ctx -> renderSignupPage(ctx, connectionPool));
        app.get("renderIndex", ctx -> renderIndex(ctx, connectionPool));
        app.get("renderContactInfo", ctx -> acceptInquiry(ctx, connectionPool));
//        app.get("myOrders", ctx -> myOrders(ctx, connectionPool));
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
