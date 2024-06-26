package app.entities;

import app.services.CarportSvg;

import java.util.List;

public class Order {
    private int orderID;
    private int userID;
    private User user;
    private CarportLength carportLength;
    private CarportWidth carportWidth;
    private String description;
    private int totalPrice;
    private String totalPriceRaw;

    private List<ProductListItem> productList;
    private String productListRaw;
    private Status status;
    private String date;
    private boolean shed;
    private String userRemarks;
    private String svg;

    // UserID instead of user and not user remark
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

    // userID instead of user
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

    // User instead of userID
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

    // User instead of UserID and not user remark
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

    // no user/userID and no productList list. Only productListRaw
    public Order(int orderID, CarportLength carportLength, CarportWidth carportWidth, String description, int totalPrice, String totalPriceRaw, String productListRaw, Status status, String date, boolean shed, String userRemarks, String svg) {
        this.orderID = orderID;
//        this.user = user;
        this.carportLength = carportLength;
        this.carportWidth = carportWidth;
        this.description = description;
        this.totalPrice = totalPrice;
        this.totalPriceRaw = totalPriceRaw;
        this.productListRaw = productListRaw;
        this.status = status;
        this.date = date;
        this.shed = shed;
        this.userRemarks = userRemarks;
        this.svg = svg;
    }

    public Order(int orderID, String totalPriceRaw, String productListRaw, Status status, String date, CarportLength carportLength, CarportWidth carportWidth, boolean shed, String userRemarks, String description) {
        this.orderID = orderID;
        this.totalPriceRaw = totalPriceRaw;
        this.productListRaw = productListRaw;
        this.status = status;
        this.date = date;
        this.carportLength = carportLength;
        this.carportWidth = carportWidth;
        this.shed = shed;
        this.userRemarks = userRemarks;
        this.description = description;
    }

    public String getTotalPriceRaw() {
        return totalPriceRaw;
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

    public String getSvg() {
        return svg;
    }

    public void setProductList (List <ProductListItem> productList) {
        this.productList = productList;
    }
}
