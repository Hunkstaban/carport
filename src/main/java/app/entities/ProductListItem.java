package app.entities;

public class ProductListItem {
    private String productName;
    private String productDescription;
    private int length;
    private String unit;
    private int quantity;

    public ProductListItem(String productName, String productDescription, int length, String unit, int quantity) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.length = length;
        this.unit = unit;
        this.quantity = quantity;
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
}
