package frazier.c195_performance_assessment;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) class for User-related database operations.
 */
public class UserDAO {

    /**
     * Retrieves a list of all User IDs from the database.
     *
     * @return An ObservableList containing all User IDs.
     * @throws SQLException If an error occurs while accessing the database.
     */
    public static ObservableList<Integer> getAllUserID() throws SQLException {
        ObservableList<Integer> allUserID = FXCollections.observableArrayList();
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("SELECT DISTINCT User_ID" +
                " FROM users;");
        ResultSet results = sqlCommand.executeQuery();

        while ( results.next() ) {
            allUserID.add(results.getInt("User_ID"));
        }
        sqlCommand.close();
        return allUserID;
    }
}
