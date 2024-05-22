package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.*;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Middleware to initialize currentUser session attribute
        app.before(ctx -> {
            if (ctx.sessionAttribute("currentUser") == null) {
                ctx.sessionAttribute("currentUser", null);
            }
        });

        // Routing
        app.get("/", ctx ->  {
            ctx.attribute("isLoggedIn", ctx.sessionAttribute("currentUser") != null);
            ctx.render("user/index.html");
        });

        LoginController.addRoute(app, connectionPool);
        UserController.addRoute(app, connectionPool);
        ProductController.addRoute(app, connectionPool);
        OrderController.addRoute(app, connectionPool);
        ErrorController.addRoute(app, connectionPool);

    }
}