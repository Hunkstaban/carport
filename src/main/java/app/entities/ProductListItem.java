package app.entities;

public class ProductListItem {

    private int productID;
    private String productName;
    private String productDescription;
    private int length;
    private String unit;
    private int quantity;
    private double price;

    public ProductListItem(int productID, String productName, String productDescription, int length, String unit, int quantity, double price) {
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

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Vare nr.: " + productID + " ||" +
                " Navn: " + productName + " ||" +
                " Beskrivelse: " + productDescription + " ||" +
                " Længde: " + length + " mm. ||" +
                " Mængde: " + quantity + " " + unit +
                "\n";
    }
}
