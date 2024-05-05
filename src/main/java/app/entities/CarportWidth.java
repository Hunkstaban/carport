package app.entities;

public class CarportWidth {
    private int carportWidthID;
    private int width;

    public CarportWidth(int carportWidthID, int width) {
        this.carportWidthID = carportWidthID;
        this.width = width;
    }

    public int getCarportWidthID() {
        return carportWidthID;
    }

    public int getWidth() {
        return width;
    }
}
