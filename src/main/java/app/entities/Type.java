package app.entities;

public class Type {
    private int typeID;
    private String name;

    public Type(int typeID, String name) {
        this.typeID = typeID;
        this.name = name;
    }

    public Type(int typeID) {
        this.typeID = typeID;
    }

    public int getTypeID() {
        return typeID;
    }

    public String getName() {
        return name;
    }
}
