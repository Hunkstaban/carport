package app.entities;

public class CarportLength {
    private int carportLengthID;
    private int length;

    public CarportLength(int carportLengthID, int length) {
        this.carportLengthID = carportLengthID;
        this.length = length;
    }

    public int getCarportLengthID() {
        return carportLengthID;
    }

    public int getLength() {
        return length;
    }
}
