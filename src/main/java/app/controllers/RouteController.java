package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class RouteController {

    public static void addRoute(Javalin app, ConnectionPool connectionPool) {

        app.get("renderIndex", ctx -> renderIndex(ctx, connectionPool));
        app.get("renderContactInfo", ctx -> renderContactInfo(ctx, connectionPool));

    }

    private static void renderIndex(Context ctx, ConnectionPool connectionPool) {


        ctx.render("user/index.html");

    }
    private static void renderContactInfo(Context ctx, ConnectionPool connectionPool) {


        ctx.render("accept-inquiry.html");

    }




}
