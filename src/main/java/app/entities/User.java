package app.entities;

public class User {

    private int userID;
    private String name;
    private String email;
    private String password;
    private int roleID;

    public User(int userID, String name, String email, String password, int roleID) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleID = roleID;
    }

    public User(int userID, String name, String email, int roleID) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.roleID = roleID;
    }

    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getRoleID() {
        return roleID;
    }
}
