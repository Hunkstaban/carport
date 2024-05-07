package app.controllers;

import app.entities.Product;
import app.entities.Type;
import app.entities.Unit;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

import app.entities.Product;
import app.persistence.ConnectionPool;
import app.persistence.StorageMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;
import java.util.TreeMap;


public class ProductController {


    public static void addRoute(Javalin app, ConnectionPool connectionPool) {

        app.post("storagePage", ctx -> loadProducts(ctx, connectionPool));
        app.post("filterByType", ctx -> filterByType(ctx, connectionPool));
        app.post("updateProduct", ctx -> updateProduct(ctx, connectionPool));

        /*app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
*/

    }

    private static void updateProduct(Context ctx, ConnectionPool connectionPool) {

        int productID = Integer.parseInt(ctx.formParam("productID"));
        String productName = ctx.formParam("productName");
        int typeID = Integer.parseInt(ctx.formParam("typeID"));
        int width = Integer.parseInt(ctx.formParam("width"));
        int height = Integer.parseInt(ctx.formParam("height"));
        int length = Integer.parseInt(ctx.formParam("length"));
        int unitID = Integer.parseInt(ctx.formParam("unitID"));
        int price = Integer.parseInt(ctx.formParam("price"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        Type type = new Type(typeID);
        Unit unit = new Unit(unitID);

//        Product product = new Product(productID, productName, type, unit,)


        ProductMapper.updateProduct(connectionPool, productID);

    }

    private static void filterByType(Context ctx, ConnectionPool connectionPool) {

        int typeID = Integer.parseInt(ctx.formParam("filter"));

        List<Product> productList = ProductMapper.filterByType(connectionPool, typeID);
        List<Type> filtersList = ProductMapper.loadFilters(connectionPool);

        ctx.attribute("productList", productList);
        ctx.attribute("filtersList", filtersList);

        ctx.render("admin-storage");

    }


    private static void loadProducts(Context ctx, ConnectionPool connectionPool) {

        List<Product> productList = ProductMapper.loadProducts(connectionPool);

        ctx.attribute("productList", productList);

        globalStorageAttributes(ctx, connectionPool);

        ctx.render("admin-storage");

    }



    private static Context globalStorageAttributes(Context ctx, ConnectionPool connectionPool) {

        List<Type> filtersList = ProductMapper.loadFilters(connectionPool);

        ctx.attribute("filtersList", filtersList);
        return ctx;
    }


    public static Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void addRoute(Javalin app, ConnectionPool connectionPool){
        app.post("addProduct", ctx -> addProduct(ctx, connectionPool));
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
