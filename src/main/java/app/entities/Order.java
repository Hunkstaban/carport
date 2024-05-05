package app.entities;

import java.util.List;

public class Order {
    private int orderID;
    private int userID;
    private int carportLengthID;
    private int carportWidthID;
    private String description;
    private int totalPrice;
    private List<ProductList> productList;
    private int statusID;
    private String date;

    public Order(int orderID, int userID, int carportLengthID, int carportWidthID, String description, int totalPrice, List<ProductList> productList, int statusID, String date) {
        this.orderID = orderID;
        this.userID = userID;
        this.carportLengthID = carportLengthID;
        this.carportWidthID = carportWidthID;
        this.description = description;
        this.totalPrice = totalPrice;
        this.productList = productList;
        this.statusID = statusID;
        this.date = date;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getUserID() {
        return userID;
    }

    public int getCarportLengthID() {
        return carportLengthID;
    }

    public int getCarportWidthID() {
        return carportWidthID;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public List<ProductList> getProductList() {
        return productList;
    }

    public int getStatusID() {
        return statusID;
    }

    public String getDate() {
        return date;
    }

    public void setProductList(List<ProductList> productList) {
        this.productList = productList;
    }
}
