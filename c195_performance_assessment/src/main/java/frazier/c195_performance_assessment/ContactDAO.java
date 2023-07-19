package frazier.c195_performance_assessment;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The ContactDAO class provides data access methods for managing contacts in the system.
 */
public class ContactDAO {



    /**
     * Retrieves a list of appointment strings associated with a specific contact.
     *
     * @param contactID The ID of the contact.
     * @return An ObservableList of appointment strings.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<String> getContactAppts(String contactID) throws SQLException {
        ObservableList<String> apptStr = FXCollections.observableArrayList();
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement(
                "SELECT * FROM appointments WHERE Contact_ID = ?");

        sqlCommand.setString(1, contactID);

        ResultSet results = sqlCommand.executeQuery();

        while ( results.next()) {
            String apptID = results.getString("Appointment_ID");
            String title = results.getString("Title");
            String type = results.getString("Type");
            String start = results.getString("Start");
            String end = results.getString("End");
            String customerID = results.getString("Customer_ID");

            String newLine = "  AppointmentID: " + apptID + "\n";
            newLine += "        Title: " + title + "\n";
            newLine += "        Type: " + type + "\n";
            newLine += "        Start date/time: " + start + " UTC\n";
            newLine += "        End date/time: " + end + " UTC\n";
            newLine += "        CustomerID: " + customerID + "\n";

            apptStr.add(newLine);

        }

        sqlCommand.close();
        return apptStr;

    }

    /**
     * Retrieves a list of all contact names in the system.
     *
     * @return An ObservableList of contact names.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<String> getAllContactName() throws SQLException {
        ObservableList<String> allContactName = FXCollections.observableArrayList();
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("SELECT DISTINCT Contact_Name" +
                " FROM contacts;");
        ResultSet results = sqlCommand.executeQuery();

        while ( results.next() ) {
            allContactName.add(results.getString("Contact_Name"));
        }
        sqlCommand.close();
        return allContactName;
    }


    /**
     * Finds the ID of a contact based on the contact name.
     *
     * @param contactName The name of the contact.
     * @return The ID of the contact, or -1 if not found.
     * @throws SQLException if a database access error occurs.
     */
    public static Integer findContactID(String contactName) throws SQLException {
        Integer contactID = -1;
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("SELECT Contact_ID, Contact_Name " +
                "FROM contacts WHERE Contact_Name = ?");
        sqlCommand.setString(1, contactName);
        ResultSet results = sqlCommand.executeQuery();

        while (results.next()) {
            contactID = results.getInt("Contact_ID");
        }
        sqlCommand.close();
        return contactID;


    }
}
