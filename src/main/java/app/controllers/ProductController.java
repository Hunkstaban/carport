package app.controllers;

import app.entities.Product;
import app.entities.Type;
import app.entities.Unit;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

import app.persistence.StorageMapper;



public class ProductController {

    // To push

    public static void addRoute(Javalin app, ConnectionPool connectionPool) {

        app.get("/lager", ctx -> loadProducts(ctx, connectionPool));
        app.post("filterByType", ctx -> filterByType(ctx, connectionPool));
        app.post("updateProduct", ctx -> updateProduct(ctx, connectionPool));
        app.post("addProduct", ctx -> addProduct(ctx, connectionPool));

        /*app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
*/

    }

    // controller to take data inputs from the front end and update the database through the ProductMapper.updateProduct mapper method
    private static void updateProduct(Context ctx, ConnectionPool connectionPool) {

        // method gets all the formparams from the frontend and created a 'product' object to send as a variable to the mapper
        int productID = Integer.parseInt(ctx.formParam("productID"));
        String name = ctx.formParam("name");
        String description = ctx.formParam("description");
        int typeID = Integer.parseInt(ctx.formParam("typeID"));
        int width = Integer.parseInt(ctx.formParam("width"));
        int height = Integer.parseInt(ctx.formParam("height"));
        int length = Integer.parseInt(ctx.formParam("length"));
        int unitID = Integer.parseInt(ctx.formParam("unitID"));
        int price = Integer.parseInt(ctx.formParam("price"));
        int costPrice = Integer.parseInt(ctx.formParam("costPrice"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        Type type = new Type(typeID);
        Unit unit = new Unit(unitID);
        Product product = new Product(productID, name, description, height, width, length, unit, type, price, costPrice,quantity);

        ProductMapper.updateProduct(product, connectionPool);

        loadProducts(ctx, connectionPool);

    }

    // takes input from the frontend to get the 'filter' parameter to dertemine which type to load from the database
    private static void filterByType(Context ctx, ConnectionPool connectionPool) {

        int typeID = Integer.parseInt(ctx.formParam("filter"));

        // loads the different types from the database to filter the productList
        globalStorageAttributes(ctx, connectionPool, typeID);

        ctx.render("admin/storage.html");

    }

    // loads all products from the database and sends an attribute to the frontend for the admin
    private static void loadProducts(Context ctx, ConnectionPool connectionPool) {

        // TODO: make a check if user is admin before rendering the admin page

        globalStorageAttributes(ctx, connectionPool, null);

        ctx.render("admin/storage.html");

    }



    private static Context globalStorageAttributes(Context ctx, ConnectionPool connectionPool, Integer typeID) {

        List<Type> filtersList = ProductMapper.loadFilters(connectionPool);
        List<Product> productList = ProductMapper.getProducts(typeID, connectionPool);

        ctx.attribute("filtersList", filtersList);
        ctx.attribute("productList", productList);

        return ctx;
    }


    public static Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }


    public static void addProduct(Context ctx, ConnectionPool connectionPool){
        String name = ctx.formParam("product-name");
        String description = ctx.formParam("description");
        Integer height = tryParseInt(ctx.formParam("height"));
        Integer width = tryParseInt(ctx.formParam("width"));
        Integer length = tryParseInt(ctx.formParam("length"));
        Integer unitID = tryParseInt(ctx.formParam("unit-id"));
        Integer typeID = tryParseInt(ctx.formParam("type-id"));
        Integer price = tryParseInt(ctx.formParam("price"));
        Integer costPrice = tryParseInt(ctx.formParam("cost-price"));
        Integer quantity = tryParseInt(ctx.formParam("amount"));

//        Map productMap = new TreeMap();
//        productMap.put("name",name);
//        productMap.put("description",description);
//        productMap.put("height",width);
//        productMap.put("length",length);
//        productMap.put("unitID",unitID);
//        productMap.put("typeID",typeID);
//        productMap.put("price",price);
//        productMap.put("costPrice",costPrice);
//        productMap.put("quantity",quantity);
//
//        for (Object value : productMap.values()) {
//            if (value == null) {
//                value = "NULL";
//            }
//        }

//        Product product = new Product(name, description, height, width, length, unitID, typeID, price, costPrice, quantity);


        try {
            StorageMapper.addProduct(connectionPool, name, description, height, width, length, unitID, typeID, price, costPrice, quantity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
