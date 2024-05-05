package app.entities;

public class Unit {
    private int unitID;
    private String name;

    public Unit(int unitID, String name) {
        this.unitID = unitID;
        this.name = name;
    }

    public int getUnitID() {
        return unitID;
    }

    public String getName() {
        return name;
    }
}
