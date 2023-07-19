package frazier.c195_performance_assessment;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller class for the Reports view.
 */
public class ReportsController {

    @FXML
    private TextArea reportTxtArea;
    Stage stage;
    Parent scene;

    /**
     * Generates a report showing the total number of appointments by type and month.
     *
     * @param event The ActionEvent triggered by the button.
     * @throws SQLException If a SQL error occurs during the retrieval of the report data.
     */
    @FXML
    void onActionAppointmentsByTypeAndMonth(ActionEvent event) throws SQLException {
        reportTxtArea.clear();
        ObservableList<String> reportStrings = AppointmentDAO.reportTotalsByTypeAndMonth();

        for (String str : reportStrings) {
            reportTxtArea.appendText(str);
        }
    }

    /**
     * Handles the action when the user clicks the "Back to Dashboard" button.
     *
     * @param event The ActionEvent triggered by the button.
     * @throws IOException If an error occurs during the loading of the Dashboard view.
     */
    @FXML
    void onActionBackToDashboard(ActionEvent event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Generates a report showing the schedule for each contact.
     *
     * @param event The ActionEvent triggered by the button.
     * @throws SQLException If a SQL error occurs during the retrieval of the report data.
     */
    @FXML
    void onActionContactSchedule(ActionEvent event) throws SQLException{
        reportTxtArea.clear();
        ObservableList<String> contacts = ContactDAO.getAllContactName();

        for (String contact : contacts) {
            String contactID = ContactDAO.findContactID(contact).toString();
            reportTxtArea.appendText("Contact Name: " + contact + " ID: " + contactID + "\n");

            ObservableList<String> appointments = ContactDAO.getContactAppts(contactID);
            if(appointments.isEmpty()) {
                reportTxtArea.appendText("    No appointments for contact \n");
            }
            for (String appt : appointments) {
                reportTxtArea.appendText(appt);
            }

        }
    }

    /**
     * Generates a report showing the total number of customers by country.
     *
     * @param event The ActionEvent triggered by the button.
     * @throws SQLException If a SQL error occurs during the retrieval of the report data.
     */
    @FXML
    void onActionViewCustomersByCountry(ActionEvent event) throws SQLException{
        reportTxtArea.clear();
        ObservableList<String> reportStrings = CustomerDAO.customerCountTotalsByCountry();

        for (String str : reportStrings) {
            reportTxtArea.appendText(str);
        }
    }
}


