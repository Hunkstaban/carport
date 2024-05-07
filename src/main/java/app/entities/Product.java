package app.entities;

public class Product {
    private int productID;
    private String name;
    private String description;
    private int height;
    private int width;
    private int length;
    private int unitID;
    private int typeID;
    private int price;
    private int costPrice;
    private int quantity;

    public Product(int productID, String name, String description, int height, int width, int length, int unitID, int typeID, int price, int costPrice) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.height = height;
        this.width = width;
        this.length = length;
        this.unitID = unitID;
        this.typeID = typeID;
        this.price = price;
        this.costPrice = costPrice;
    }

    // Height, width, and length is allowed to be null
    public Product(int productID, String name, String description, int unitID, int typeID, int price, int costPrice) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.unitID = unitID;
        this.typeID = typeID;
        this.price = price;
        this.costPrice = costPrice;
    }

    public Product(String name, String description, int height, int width, int length, int unitID, int typeID, int price, int costPrice, int quantity) {
        this.name = name;
        this.description = description;
        this.height = height;
        this.width = width;
        this.length = length;
        this.unitID = unitID;
        this.typeID = typeID;
        this.price = price;
        this.costPrice = costPrice;
        this.quantity = quantity;
    }

    public int getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getUnitID() {
        return unitID;
    }

    public int getTypeID() {
        return typeID;
    }

    public int getPrice() {
        return price;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
