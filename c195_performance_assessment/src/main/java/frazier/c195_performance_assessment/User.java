package frazier.c195_performance_assessment;

/**
 * Represents a User in the system.
 */
public class User {
    private String userName;
    private int userID;


    /**
     * Constructs a User object with the specified user name and user ID.
     *
     * @param userName The user name.
     * @param userID   The user ID.
     */
    public User(String userName, int userID) {
        this.userName = userName;
        this.userID = userID;
    }

    /**
     * Retrieves the user name.
     *
     * @return The user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user name.
     *
     * @param userName The user name to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Retrieves the user ID.
     *
     * @return The user ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user ID.
     *
     * @param userID The user ID to set.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }
}
