package frazier.c195_performance_assessment;

import helper.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;

/**
 * Represents a logon session for a user.
 */
public class LogonSession {
    private static User loggedOnUser;
    private static Locale userLocale;
    private static ZoneId userTimeZone;

    /**
     * Constructs a new LogonSession object.
     */
    public LogonSession() {}


    /**
     * Attempts to log in a user with the provided credentials.
     *
     * @param userNameInput The username input by the user.
     * @param userPassword  The password input by the user.
     * @return True if the logon attempt is successful, false otherwise.
     * @throws SQLException If a SQL error occurs during the logon attempt.
     */
    public static boolean attemptLogon(String userNameInput, String userPassword) throws SQLException {
        Connection conn = JDBC.dbCursor();
        PreparedStatement sqlCommand = conn.prepareStatement("SELECT * FROM users WHERE " +
                "User_Name = ? AND Password = ?");
        sqlCommand.setString(1, userNameInput);
        sqlCommand.setString(2, userPassword);
        System.out.println("Executing query...");
        ResultSet result = sqlCommand.executeQuery();
        if (!result.next()) {
            sqlCommand.close();
            return false;

        }
        else {
            loggedOnUser = new User(result.getString("User_Name"), result.getInt("User_ID"));
            userLocale = Locale.getDefault();
            userTimeZone = ZoneId.systemDefault();
            sqlCommand.close();
            return true;

        }


    }


    /**
     * Retrieves the currently logged-on user.
     *
     * @return The logged-on user.
     */
    public static User getLoggedOnUser() {
        return loggedOnUser;
    }


    /**
     * Retrieves the locale of the logged-on user.
     *
     * @return The locale of the user.
     */
    public static Locale getUserLocale() {
        return userLocale;

    }


    /**
     * Retrieves the time zone of the logged-on user.
     *
     * @return The time zone of the user.
     */
    public static ZoneId getUserTimeZone() {
        return userTimeZone;
    }

    /**
     * Logs off the current user by clearing the logon session data.
     */
    public static void logOff() {
        loggedOnUser = null;
        userLocale = null;
        userTimeZone = null;
    }
}
