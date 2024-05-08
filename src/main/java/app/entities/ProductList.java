package app.entities;

public class ProductList {
    private String productName;
    private String productDescription;
    private int length;
    private String unit;
    private int quantity;

    public ProductList(String productName, String productDescription, int length, String unit, int quantity) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.length = length;
        this.unit = unit;
        this.quantity = quantity;
    }


}
