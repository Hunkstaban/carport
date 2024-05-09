package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class TestController {


    public static void addRoute(Javalin app, ConnectionPool connectionPool) {

        app.get("loginPage", ctx -> loginPage(ctx, connectionPool));
        app.get("myOrders", ctx -> myOrders(ctx, connectionPool));

        /*app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
*/

    }


    private static void loginPage(Context ctx, ConnectionPool connectionPool) {

        ctx.render("login.html");

    }

    private static void myOrders(Context ctx, ConnectionPool connectionPool) {

        ctx.render("view-orders.html");

    }

    private static void createAccount(Context ctx, ConnectionPool connectionPool) {

        ctx.render("signup.html");

    }


//    private static void renderIndex(Context ctx, ConnectionPool connectionPool) {
//
//
//        ctx.render("index.html");
//
//    }
}
