package app.controllers;

import app.entities.Product;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class ProductController {


    public static void addRoute(Javalin app, ConnectionPool connectionPool) {

        app.post("storagePage", ctx -> loadProducts(ctx, connectionPool));
        app.post("filterByType", ctx -> filterByType(ctx, connectionPool));

        /*app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
*/

    }

    private static void filterByType(Context ctx, ConnectionPool connectionPool) {

        int typeID = Integer.parseInt(ctx.formParam("filter"));

        List<Product> productList = ProductMapper.filterByType(connectionPool, typeID);

        ctx.attribute("productList", productList);

        ctx.render("admin-storage");

    }


    private static void loadProducts(Context ctx, ConnectionPool connectionPool) {

        List<Product> productList = ProductMapper.loadProducts(connectionPool);

        ctx.attribute("productList", productList);

        ctx.render("admin-storage");

    }


}
