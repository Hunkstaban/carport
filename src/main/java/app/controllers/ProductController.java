package app.controllers;

import app.entities.Product;
import app.persistence.ConnectionPool;
import app.persistence.StorageMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;
import java.util.TreeMap;


public class ProductController {

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
