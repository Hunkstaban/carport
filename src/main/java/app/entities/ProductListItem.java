package app.entities;

public class ProductListItem {

    private int productID;
    private String productName;
    private String productDescription;
    private int length;
    private String unit;
    private int quantity;
    private int price;

    public ProductListItem(int productID, String productName, String productDescription, int length, String unit, int quantity, int price) {
        this.productID = productID;
        this.productName = productName;
        this.productDescription = productDescription;
        this.length = length;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public int getLength() {
        return length;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }
}
