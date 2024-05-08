package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import static app.controllers.TestController.addRoute;

public class UserController {

    public static void addRoute(Javalin app, ConnectionPool connectionPool) {

        app.post("createUser", ctx -> createUser(ctx, connectionPool));
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
        app.get("renderLoginPage", ctx -> renderLoginPage(ctx, connectionPool));
        app.get("renderSignupPage", ctx -> renderSignupPage(ctx, connectionPool));

    }

    private static void renderLoginPage(Context ctx, ConnectionPool connectionPool) {

        ctx.render("login.html");
    }

    private static void renderSignupPage (Context ctx, ConnectionPool connectionPool) {

        ctx.render("signup.html");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {

        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(email, password, connectionPool);

            if (user.getRoleID() == 1) {
                userLogin(ctx, connectionPool, user);
            }
            if (user.getRoleID() == 2) {
                adminLogin(ctx, connectionPool, user);
            }
        } catch (DatabaseException e) {
            String msg = "forkert email eller kodeord. Prøv igen";
            ctx.attribute("loginError", msg);
            ctx.render("login.html");
        }
    }

    private static void logout(Context ctx) {

        ctx.req().getSession().invalidate();
        ctx.render("index.html");

    }

    private static void adminLogin(Context ctx, ConnectionPool connectionPool, User user) {

        ctx.sessionAttribute("currentUser", user);
        ctx.render("admin-orders.html");

    }

    private static void createUser(Context ctx, ConnectionPool connectionPool) {

        String name = ctx.formParam("username");
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User newUser = UserMapper.createUser(name, email, password, connectionPool);

            userLogin(ctx, connectionPool, newUser);
        } catch (DatabaseException e) {
            String msg = "Bruger med denne email eksisterer allerede.";
            ctx.attribute("alreadyExist", msg);
            ctx.render("signup.html");

        }

    }

    private static void userLogin(Context ctx, ConnectionPool connectionPool, User user) {

        ctx.sessionAttribute("currentUser", user);
        ctx.render("index.html");

    }
    private static void renderIndex(Context ctx, ConnectionPool connectionPool) {

        ctx.render("index.html");
    }

    private  static  void changePassword(Context ctx, ConnectionPool connectionPool) {


    }
}
