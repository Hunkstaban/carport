package app.entities;

public class Status {
    private int statusID;
    private String name;

    public Status(int statusID, String name) {
        this.statusID = statusID;
        this.name = name;
    }

    public int getStatusID() {
        return statusID;
    }

    public String getName() {
        return name;
    }
}
