package frazier.c195_performance_assessment;


import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The AppointmentDAO class provides data access methods for managing appointments in the system.
 */
public class AppointmentDAO {

    /**
     * Retrieves a list of report strings containing the total number of appointments by type and month.
     *
     * @return An ObservableList of report strings.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<String> reportTotalsByTypeAndMonth() throws SQLException {
        ObservableList<String> reportStrings = FXCollections.observableArrayList();

        reportStrings.add("Total Number of Appointments by type and month:\n");

        // Prepare SQL
        PreparedStatement typeSqlCommand = JDBC.dbCursor().prepareStatement(
                "SELECT Type, COUNT(Type) as \"Total\" FROM appointments GROUP BY Type");

        PreparedStatement monthSqlCommand = JDBC.dbCursor().prepareStatement(
                "SELECT MONTHNAME(Start) as \"Month\", COUNT(MONTH(Start)) as \"Total\" from appointments GROUP BY Month");

        ResultSet typeResults = typeSqlCommand.executeQuery();
        ResultSet monthResults = monthSqlCommand.executeQuery();

        while (typeResults.next()) {
            String typeStr = "Type: " + typeResults.getString("Type") + " Count: " +
                    typeResults.getString("Total") + "\n";
            reportStrings.add(typeStr);

        }

        while (monthResults.next()) {
            String monthStr = "Month: " + monthResults.getString("Month") + " Count: " +
                    monthResults.getString("Total") + "\n";
            reportStrings.add(monthStr);

        }

        monthSqlCommand.close();
        typeSqlCommand.close();

        return reportStrings;

    }

    /**
     * Retrieves a filtered list of appointments for a specific customer on a given date.
     *
     * @param apptDate       The appointment date.
     * @param inputCustomerID The ID of the customer.
     * @return An ObservableList of filtered appointments.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<Appointment> getCustomerFilteredAppointments(
            LocalDate apptDate, Integer inputCustomerID) throws SQLException {
        // Prepare SQL statement
        ObservableList<Appointment> filteredAppts = FXCollections.observableArrayList();
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement(
                "SELECT * FROM appointments as a LEFT OUTER JOIN contacts as c " +
                        "ON a.Contact_ID = c.Contact_ID WHERE datediff(a.Start, ?) = 0 AND Customer_ID = ?;"
        );

        sqlCommand.setInt(2, inputCustomerID);

        sqlCommand.setString(1, apptDate.toString());

        ResultSet results = sqlCommand.executeQuery();

        while( results.next() ) {
            Integer appointmentID = results.getInt("Appointment_ID");
            String title = results.getString("Title");
            String description = results.getString("Description");
            String location = results.getString("Location");
            String type = results.getString("Type");
            Timestamp startDateTime = results.getTimestamp("Start");
            Timestamp endDateTime = results.getTimestamp("End");
            Timestamp createdDate = results.getTimestamp("Create_Date");
            String createdBy = results.getString("Created_by");
            Timestamp lastUpdateDateTime = results.getTimestamp("Last_Update");
            String lastUpdatedBy = results.getString("Last_Updated_By");
            Integer customerID = results.getInt("Customer_ID");
            Integer userID = results.getInt("User_ID");
            Integer contactID = results.getInt("Contact_ID");
            String contactName = results.getString("Contact_Name");

            Appointment newAppt = new Appointment(
                    appointmentID, title, description, location, type, startDateTime, endDateTime, createdDate,
                    createdBy, lastUpdateDateTime, lastUpdatedBy, customerID, userID, contactID, contactName
            );
            filteredAppts.add(newAppt);
        }

        sqlCommand.close();
        return filteredAppts;

    }

    /**
     * Updates an existing appointment with new information.
     *
     * @param inputApptID      The ID of the appointment to update.
     * @param inputTitle       The new title of the appointment.
     * @param inputDescription The new description of the appointment.
     * @param inputLocation    The new location of the appointment.
     * @param inputType        The new type of the appointment.
     * @param inputStart       The new start date and time of the appointment.
     * @param inputEnd         The new end date and time of the appointment.
     * @param inputLastUpdateBy The user who updated the appointment.
     * @param inputCustomerID  The ID of the customer associated with the appointment.
     * @param inputUserID      The ID of the user associated with the appointment.
     * @param inputContactID   The ID of the contact associated with the appointment.
     * @return true if the appointment was successfully updated, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static Boolean updateAppointment(Integer inputApptID, String inputTitle, String inputDescription,
                                            String inputLocation, String inputType, ZonedDateTime inputStart,
                                            ZonedDateTime inputEnd, String inputLastUpdateBy, Integer inputCustomerID,
                                            Integer inputUserID, Integer inputContactID) throws SQLException {
        //check for overlapping appointment
        if (hasOverlappingAppointments(inputStart, inputEnd, inputCustomerID)) {
            System.out.println("Error: Overlapping appointments detected.");
            return false;
        }

        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("UPDATE appointments "
                + "SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=?,Last_Updated_By=?, " +
                "Customer_ID=?, User_ID=?, Contact_ID=? WHERE Appointment_ID = ?");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inputStartString = inputStart.format(formatter).toString();
        String inputEndString = inputEnd.format(formatter).toString();

        sqlCommand.setString(1,inputTitle);
        sqlCommand.setString(2, inputDescription);
        sqlCommand.setString(3, inputLocation);
        sqlCommand.setString(4, inputType);
        sqlCommand.setString(5, inputStartString);
        sqlCommand.setString(6, inputEndString);
        sqlCommand.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        sqlCommand.setString(8, inputLastUpdateBy);
        sqlCommand.setInt(9, inputCustomerID);
        sqlCommand.setInt(10, inputUserID);
        sqlCommand.setInt(11, inputContactID);
        sqlCommand.setInt(12, inputApptID);

        try {
            sqlCommand.executeUpdate();
            sqlCommand.close();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            sqlCommand.close();
            return false;
        }

    }

    /**
     * Adds a new appointment to the database.
     *
     * @param inputTitle        The title of the new appointment.
     * @param inputDescription  The description of the new appointment.
     * @param inputLocation     The location of the new appointment.
     * @param inputType         The type of the new appointment.
     * @param inputStart        The start date and time of the new appointment.
     * @param inputEnd          The end date and time of the new appointment.
     * @param inputCreatedBy    The user who created the new appointment.
     * @param inputLastUpdateBy The user who last updated the new appointment.
     * @param inputCustomerID   The ID of the customer associated with the new appointment.
     * @param inputUserID       The ID of the user associated with the new appointment.
     * @param inputContactID    The ID of the contact associated with the new appointment.
     * @return true if the appointment was successfully added, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static Boolean addAppointment(String inputTitle, String inputDescription,
                                         String inputLocation, String inputType, ZonedDateTime inputStart,
                                         ZonedDateTime inputEnd, String inputCreatedBy,
                                         String inputLastUpdateBy, Integer inputCustomerID,
                                         Integer inputUserID, Integer inputContactID) throws SQLException {
        //check for overlapping appointments
        if (hasOverlappingAppointments(inputStart, inputEnd, inputCustomerID)) {
            System.out.println("Error: Overlapping appointments detected.");
            return false;
        }

        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("INSERT INTO appointments " +
                "(Title, Description, Location, Type, Start, End, Create_date, \n" +
                "Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        // Format inputStart and inputEnd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inputStartString = inputStart.format(formatter).toString();
        String inputEndString = inputEnd.format(formatter).toString();

        // Set Params
        sqlCommand.setString(1, inputTitle);
        sqlCommand.setString(2, inputDescription);
        sqlCommand.setString(3, inputLocation);
        sqlCommand.setString(4, inputType);
        sqlCommand.setString(5, inputStartString);
        sqlCommand.setString(6, inputEndString);
        sqlCommand.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        sqlCommand.setString(8, inputCreatedBy);
        sqlCommand.setString(9, ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        sqlCommand.setString(10, inputLastUpdateBy);
        sqlCommand.setInt(11, inputCustomerID);
        sqlCommand.setInt(12, inputUserID);
        sqlCommand.setInt(13, inputContactID);

        // Execute query
        try {
            sqlCommand.executeUpdate();
            sqlCommand.close();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Deletes an appointment from the database.
     *
     * @param inputApptID The ID of the appointment to delete.
     * @return true if the appointment was successfully deleted, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static Boolean deleteAppointment(Integer inputApptID) throws SQLException {

        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("DELETE FROM appointments " +
                "WHERE Appointment_ID = ?");

        sqlCommand.setInt(1, inputApptID);

        try {
            sqlCommand.executeUpdate();
            sqlCommand.close();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Deletes all appointments associated with a specific customer.
     *
     * @param customerID The ID of the customer whose appointments should be deleted.
     * @return true if the customer's appointments were successfully deleted, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static Boolean deleteCustomersAppointments(Integer customerID) throws SQLException {

        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("DELETE FROM appointments " +
                "WHERE Customer_ID = ?");

        sqlCommand.setInt(1, customerID);

        try {
            sqlCommand.executeUpdate();
            sqlCommand.close();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Retrieves a list of appointments within the next 15 minutes.
     *
     * @return An ObservableList of appointments.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<Appointment> getAppointmentsIn15Mins() throws SQLException{

        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime userTZnow = now.atZone(LogonSession.getUserTimeZone());
        ZonedDateTime nowUTC = userTZnow.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime utcPlus15 = nowUTC.plusMinutes(15);

        String rangeStart = nowUTC.format(formatter).toString();
        String rangeEnd = utcPlus15.format(formatter).toString();
        Integer logonUserID = LogonSession.getLoggedOnUser().getUserID();


        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("SELECT * FROM appointments as a " +
                "LEFT OUTER JOIN contacts as c ON a.Contact_ID = c.Contact_ID WHERE " +
                "Start BETWEEN ? AND ? AND User_ID = ? ");

        sqlCommand.setString(1, rangeStart);
        sqlCommand.setString(2, rangeEnd);
        sqlCommand.setInt(3, logonUserID);

        ResultSet results = sqlCommand.executeQuery();

        while( results.next() ) {
            Integer appointmentID = results.getInt("Appointment_ID");
            String title = results.getString("Title");
            String description = results.getString("Description");
            String location = results.getString("Location");
            String type = results.getString("Type");
            Timestamp startDateTime = results.getTimestamp("Start");
            Timestamp endDateTime = results.getTimestamp("End");
            Timestamp createdDate = results.getTimestamp("Create_Date");
            String createdBy = results.getString("Created_by");
            Timestamp lastUpdateDateTime = results.getTimestamp("Last_Update");
            String lastUpdatedBy = results.getString("Last_Updated_By");
            Integer customerID = results.getInt("Customer_ID");
            Integer userID = results.getInt("User_ID");
            Integer contactID = results.getInt("Contact_ID");
            String contactName = results.getString("Contact_Name");

            Appointment newAppt = new Appointment(
                    appointmentID, title, description, location, type, startDateTime, endDateTime, createdDate,
                    createdBy, lastUpdateDateTime, lastUpdatedBy, customerID, userID, contactID, contactName
            );

            allAppointments.add(newAppt);

        }
        return allAppointments;

    }

    private static boolean hasOverlappingAppointments(ZonedDateTime start, ZonedDateTime end, Integer appointmentID) throws SQLException {
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement(
                "SELECT * FROM appointments WHERE " +
                        "((Start >= ? AND Start < ?) OR (End > ? AND End <= ?) OR " +
                        "(Start <= ? AND End >= ?)) AND " +
                        "Appointment_ID != ?");

        sqlCommand.setString(1, start.toString());
        sqlCommand.setString(2, end.toString());
        sqlCommand.setString(3, start.toString());
        sqlCommand.setString(4, end.toString());
        sqlCommand.setString(5, start.toString());
        sqlCommand.setString(6, end.toString());
        sqlCommand.setInt(7, appointmentID);

        ResultSet results = sqlCommand.executeQuery();

        boolean hasOverlapping = results.next();

        sqlCommand.close();
        results.close();

        return hasOverlapping;
    }



}
