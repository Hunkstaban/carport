package app.entities;

import java.util.Objects;

public class ProductListItem {

    private int productID;
    private String productName;
    private String productDescription;
    private int length;
    private String unit;
    private int quantity;
    private int costPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductListItem that = (ProductListItem) o;
        return productID == that.productID && length == that.length && quantity == that.quantity && costPrice == that.costPrice && Objects.equals(productName, that.productName) && Objects.equals(productDescription, that.productDescription) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID, productName, productDescription, length, unit, quantity, costPrice);
    }

    public ProductListItem(int productID, String productName, String productDescription, int length, String unit, int quantity, int costPrice) {
        this.productID = productID;
        this.productName = productName;
        this.productDescription = productDescription;
        this.length = length;
        this.unit = unit;
        this.quantity = quantity;
        this.costPrice = costPrice;
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

    public int getCostPrice() {
        return costPrice;
    }

    @Override
    public String toString() {
        return "Vare nr.: " + productID + " ||" +
                " Navn: " + productName + " ||" +
                " Beskrivelse: " + productDescription + " ||" +
                " Længde: " + length + " mm. ||" +
                " Mængde: " + quantity + " " + unit +
                "<br>";
    }
}
