package app.entities;

public class Product {
    private int productID;
    private String name;
    private String description;
    private int height;
    private int width;
    private int length;
    private Unit unit;
    private Type type;
    private int price;
    private int costPrice;
    private int quantity;

    public Product(int productID, String name, String description, int height, int width, int length, Unit unit, Type type, int price, int costPrice, int quantity) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.height = height;
        this.width = width;
        this.length = length;
        this.unit = unit;
        this.type = type;
        this.price = price;
        this.costPrice = costPrice;
        this.quantity = quantity;
    }

    // Height, width, and length is allowed to be null
    public Product(int productID, String name, String description, Unit unit, Type type, int price, int costPrice, int quantity) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.type = type;
        this.price = price;
        this.costPrice = costPrice;
        this.quantity = quantity;
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

    public Unit getUnit() {
        return unit;
    }

    public Type getType() {
        return type;
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
