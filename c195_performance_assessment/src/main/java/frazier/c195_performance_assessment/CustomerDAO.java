package frazier.c195_performance_assessment;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The CustomerDAO class provides data access methods for managing customers in the system.
 */
public class CustomerDAO {

    /**
     * Updates the details of a customer.
     *
     * @param division    The division associated with the customer.
     * @param name        The name of the customer.
     * @param address     The address of the customer.
     * @param postalCode  The postal code of the customer.
     * @param phoneNum    The phone number of the customer.
     * @param customerID  The ID of the customer to update.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static Boolean updateCustomer( String division, String name, String address,
                                          String postalCode, String phoneNum, Integer customerID) throws SQLException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("UPDATE customers "
                + "SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Last_Update=?," +
                " Last_Updated_By=?, Division_ID=? WHERE Customer_ID = ?");

        sqlCommand.setString(1, name);
        sqlCommand.setString(2, address);
        sqlCommand.setString(3, postalCode);
        sqlCommand.setString(4, phoneNum);
        sqlCommand.setString(5, ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        sqlCommand.setString(6, LogonSession.getLoggedOnUser().getUserName());
        sqlCommand.setInt(7, CustomerDAO.getSpecificDivisionID(division));
        sqlCommand.setInt(8, customerID);

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
     * Deletes a customer from the system.
     *
     * @param customerID The ID of the customer to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static Boolean deleteCustomer(Integer customerID) throws SQLException {
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("DELETE FROM customers " +
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
     * Adds a new customer to the system.
     *
     * @param country     The country associated with the customer.
     * @param division    The division associated with the customer.
     * @param name        The name of the customer.
     * @param address     The address of the customer.
     * @param postalCode  The postal code of the customer.
     * @param phoneNum    The phone number of the customer.
     * @param divisionID  The ID of the division associated with the customer.
     * @return true if the addition was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static Boolean addCustomer(String country, String division, String name, String address, String postalCode,
                                      String phoneNum, Integer divisionID) throws SQLException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement(
                "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, " +
                        "Last_Update, Last_Updated_By, Division_ID) \n" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);");

        sqlCommand.setString(1, name);
        sqlCommand.setString(2, address);
        sqlCommand.setString(3, postalCode);
        sqlCommand.setString(4, phoneNum);
        sqlCommand.setString(5, ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        sqlCommand.setString(6, LogonSession.getLoggedOnUser().getUserName());
        sqlCommand.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        sqlCommand.setString(8, LogonSession.getLoggedOnUser().getUserName());
        sqlCommand.setInt(9, divisionID);

        try {
            sqlCommand.executeUpdate();
            sqlCommand.close();
            return true;
        }
        catch (SQLException e) {
            //TODO- log error
            e.printStackTrace();
            sqlCommand.close();
            return false;
        }

    }

    /**
     * Retrieves the ID of a specific division.
     *
     * @param division The name of the division.
     * @return The ID of the division.
     * @throws SQLException if a database access error occurs.
     */
    public static Integer getSpecificDivisionID(String division) throws SQLException {
        Integer divID = 0;
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("SELECT Division, Division_ID FROM " +
                "first_level_divisions WHERE Division = ?");

        sqlCommand.setString(1, division);

        ResultSet result = sqlCommand.executeQuery();

        while ( result.next() ) {
            divID = result.getInt("Division_ID");
        }

        sqlCommand.close();
        return divID;

    }

    /**
     * Retrieves a list of all customer IDs in the system.
     *
     * @return An ObservableList of customer IDs.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<Integer> getAllCustomerID() throws SQLException {

        ObservableList<Integer> allCustomerID = FXCollections.observableArrayList();
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("SELECT DISTINCT Customer_ID" +
                " FROM customers;");
        ResultSet results = sqlCommand.executeQuery();

        while ( results.next() ) {
            allCustomerID.add(results.getInt("Customer_ID"));
        }
        sqlCommand.close();
        return allCustomerID;
    }

    /**
     * Retrieves a list of filtered divisions based on the country.
     *
     * @param inputCountry The name of the country.
     * @return An ObservableList of filtered divisions.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<String> getFilteredDivisions(String inputCountry) throws SQLException {

        ObservableList<String> filteredDivs = FXCollections.observableArrayList();
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement(
                "SELECT c.Country, c.Country_ID,  d.Division_ID, d.Division FROM countries as c RIGHT OUTER JOIN " +
                        "first_level_divisions AS d ON c.Country_ID = d.Country_ID WHERE c.Country = ?");

        sqlCommand.setString(1, inputCountry);
        ResultSet results = sqlCommand.executeQuery();

        while (results.next()) {
            filteredDivs.add(results.getString("Division"));
        }

        sqlCommand.close();
        return filteredDivs;

    }

    /**
     * Retrieves a list of all countries in the system.
     *
     * @return An ObservableList of all countries.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<String> getAllCountries() throws SQLException {

        ObservableList<String> allCountries = FXCollections.observableArrayList();
        PreparedStatement sqlCommand = JDBC.dbCursor().prepareStatement("SELECT DISTINCT Country FROM countries");
        ResultSet results = sqlCommand.executeQuery();

        while (results.next()) {
            allCountries.add(results.getString("Country"));
        }
        sqlCommand.close();
        return allCountries;

    }


    /**
     * Generates a report of the total number of customers by country.
     *
     * @return An ObservableList of report strings.
     * @throws SQLException if a database access error occurs.
     */
    public static ObservableList<String> customerCountTotalsByCountry() throws SQLException{
        ObservableList<String> reportStrings = FXCollections.observableArrayList();

        reportStrings.add("Total number of customers by country:\n");

        PreparedStatement countSqlCommand = JDBC.dbCursor().prepareStatement(
                "SELECT countries.Country, COUNT(customers.Customer_ID) AS \"TotalCustomers\"" +
                        " FROM customers" +
                        " JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID" +
                        " JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID" +
                        " GROUP BY countries.Country;"
        );
        ResultSet countResults = countSqlCommand.executeQuery();
        while (countResults.next()){
            String countStr = "Country: " + countResults.getString("Country") + "\tNumber of customers: " + countResults.getString("TotalCustomers") + "\n";
            reportStrings.add(countStr);
        }
        return reportStrings;
    }
}


