package main.java.user.model;

/**
 * A model class used to store user details required to perform various database operations.
 */
public class AppUser {
    private String userName;
    private String email;
    private String hashedPassword;

    public AppUser() {

    }

    public AppUser(String name, String email, String md_output) {
        this.userName = name;
        this.email = email;
        this.hashedPassword = md_output;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}