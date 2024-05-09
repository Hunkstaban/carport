package app.entities;

import java.util.List;

public class Order {
    private int orderID;
    private int userID;
    private User user;
    private CarportLength carportLength;
    private CarportWidth carportWidth;
    private String description;
    private int totalPrice;

    private List<ProductListItem> productList;
    private String productListRaw;
    private Status status;
    private String date;
    private boolean shed;
    private String userRemarks;

    public Order(int orderID, int userID, CarportLength carportLength, CarportWidth carportWidth, String description, int totalPrice, String productListRaw, Status status, String date, boolean shed) {
        this.orderID = orderID;
        this.userID = userID;
        this.carportLength = carportLength;
        this.carportWidth = carportWidth;
        this.description = description;
        this.totalPrice = totalPrice;
        this.productListRaw = productListRaw;
        this.status = status;
        this.date = date;
        this.shed = shed;
    }

    public Order(int orderID, int userID, CarportLength carportLength, CarportWidth carportWidth, String description, int totalPrice, String productListRaw, Status status, String date, boolean shed, String userRemarks) {
        this.orderID = orderID;
        this.userID = userID;
        this.carportLength = carportLength;
        this.carportWidth = carportWidth;
        this.description = description;
        this.totalPrice = totalPrice;
        this.productListRaw = productListRaw;
        this.status = status;
        this.date = date;
        this.shed = shed;
        this.userRemarks = userRemarks;
    }

    public Order(int orderID, User user, CarportLength carportLength, CarportWidth carportWidth, String description, int totalPrice, String productListRaw, Status status, String date, boolean shed, String userRemarks) {
        this.orderID = orderID;
        this.user = user;
        this.carportLength = carportLength;
        this.carportWidth = carportWidth;
        this.description = description;
        this.totalPrice = totalPrice;
        this.productListRaw = productListRaw;
        this.status = status;
        this.date = date;
        this.shed = shed;
        this.userRemarks = userRemarks;
    }

    public Order(int orderID, User user, CarportLength carportLength, CarportWidth carportWidth, String description, int totalPrice, String productListRaw, Status status, String date, boolean shed) {
        this.orderID = orderID;
        this.user = user;
        this.carportLength = carportLength;
        this.carportWidth = carportWidth;
        this.description = description;
        this.totalPrice = totalPrice;
        this.productListRaw = productListRaw;
        this.status = status;
        this.date = date;
        this.shed = shed;
    }

    public User getUser() {
        return user;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getUserID() {
        return userID;
    }

    public CarportLength getCarportLength() {
        return carportLength;
    }

    public CarportWidth getCarportWidth() {
        return carportWidth;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public List<ProductListItem> getProductList() {
        return productList;
    }

    public String getProductListRaw() {
        return productListRaw;
    }

    public Status getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public boolean isShed() {
        return shed;
    }

    public String getUserRemarks() {
        return userRemarks;
    }

    public void setProductList (List <ProductListItem> productList) {
        this.productList = productList;
    }
}
