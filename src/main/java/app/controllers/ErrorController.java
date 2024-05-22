package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ErrorController {

    public static void addRoute(Javalin app, ConnectionPool connectionPool) {
        app.error(403, ctx -> html403(ctx, connectionPool));
        app.error(404, ctx -> html404(ctx, connectionPool));
    }

    private static void html403 (Context ctx, ConnectionPool connectionPool) {
        String msg = "403: ADGANG NÆGTET";
        ctx.attribute("message", msg);
        ctx.render("errors");
    }

    private static void html404 (Context ctx, ConnectionPool connectionPool) {
        String msg = "404: SIDEN IKKE FUNDET";
        ctx.attribute("message", msg);
        ctx.render("errors");
    }
}
