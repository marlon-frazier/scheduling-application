package frazier.c195_performance_assessment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/** This class handles the add customer functionality of the application. */
public class AddCustomerController implements Initializable {
    @FXML
    private TextField addressTxt;

    @FXML
    private Button cancelBtn;

    @FXML
    private ComboBox<String> countryCB;

    @FXML
    private TextField fullNameTxt;

    @FXML
    private TextField idTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField postalTxt;

    @FXML
    private ComboBox<String> stateCB;

    @FXML
    private Button submitBtn;
    Stage stage;
    Parent scene;

    /** This method redirects the user to the dashboard */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        redirectToDashboard(event);
    }

    /**
     * Handles the submission of a customer form.
     *
     * @param event The event triggered by the submit action.
     * @throws SQLException If an SQL exception occurs during customer addition.
     * @throws IOException If an I/O exception occurs during the redirection to the dashboard.
     */
    @FXML
    void onActionSubmit(ActionEvent event) throws SQLException, IOException {
        String name = fullNameTxt.getText();
        String country = countryCB.getValue();
        String division = stateCB.getValue();
        String address = addressTxt.getText();
        String postalCode = postalTxt.getText();
        String phone = phoneTxt.getText();

        if (!validateInputFields(name, country, division, address, postalCode, phone)) {
            return;
        }

        Boolean success = CustomerDAO.addCustomer(country, division, name, address, postalCode, phone,
                CustomerDAO.getSpecificDivisionID(division));

        if (success) {
            showConfirmationDialog("Customer added successfully!");
            redirectToDashboard(event);
        } else {
            showErrorDialog("Failed to add customer");
        }
    }

    /**
     * Validates the input fields of the customer form.
     *
     * @param name The customer name.
     * @param country The customer's country.
     * @param division The customer's division.
     * @param address The customer's address.
     * @param postalCode The customer's postal code.
     * @param phone The customer's phone number.
     * @return {@code true} if all fields are valid, {@code false} otherwise.
     */
    private boolean validateInputFields(String name, String country, String division, String address,
                                        String postalCode, String phone) {
        if (country == null || division == null || name.isBlank() || address.isBlank() || postalCode.isBlank()
                || phone.isBlank()) {
            showValidationError("Please ensure all fields are completed.");
            return false;
        }
        return true;
    }

    /**
     * Displays a validation error alert dialog with the provided error message.
     *
     * @param errorMessage The error message to be displayed in the alert dialog.
     */
    private void showValidationError(String errorMessage) {
        ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        Alert emptyValue = new Alert(Alert.AlertType.WARNING, errorMessage, clickOkay);
        emptyValue.showAndWait();
    }

    /**
     * Displays a confirmation dialog with the provided message.
     *
     * @param message The message to be displayed in the confirmation dialog.
     */
    private void showConfirmationDialog(String message) {
        ButtonType clickOK = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, clickOK);
        alert.showAndWait();
    }

    /**
     * Displays an error dialog with the provided message.
     *
     * @param message The error message to be displayed in the dialog.
     */
    private void showErrorDialog(String message) {
        ButtonType clickOK = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.WARNING, message, clickOK);
        alert.showAndWait();
    }

    /**
     * Redirects the user to the dashboard view.
     *
     * @param event The event triggered by the redirect action.
     * @throws IOException If an I/O exception occurs during the redirection process.
     */
    private void redirectToDashboard(ActionEvent event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    /**
     * Initializes the customer form with default values and sets up event listeners.
     *
     * @param url The location used to resolve relative paths for the root object, or {@code null} if the location is not known.
     * @param rb The resources used to localize the root object, or {@code null} if the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle rb) {
        try {
            countryCB.setItems(CustomerDAO.getAllCountries());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        countryCB.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                stateCB.getItems().clear();
                stateCB.setDisable(true);
            } else {
                stateCB.setDisable(false);
                try {
                    stateCB.setItems(CustomerDAO.getFilteredDivisions(countryCB.getValue()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}


