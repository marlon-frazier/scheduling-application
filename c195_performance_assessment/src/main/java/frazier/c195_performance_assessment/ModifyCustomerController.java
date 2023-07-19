package frazier.c195_performance_assessment;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Controller class for the Modify Customer view.
 */
public class ModifyCustomerController {
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
    private int currentIndex = 0;

    /**
     * Handles the action when the user clicks the "Cancel" button.
     *
     * @param event The ActionEvent triggered by the button.
     * @throws IOException If an error occurs during the loading of the Dashboard view.
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the action when the user clicks the "Submit" button.
     *
     * @param event The ActionEvent triggered by the button.
     * @throws IOException  If an error occurs during the loading of the Dashboard view.
     * @throws SQLException If a SQL error occurs during the customer update.
     */
    @FXML
    void onActionSubmit(ActionEvent event) throws IOException, SQLException{
        String country = countryCB.getValue();
        String division = stateCB.getValue();
        String name = fullNameTxt.getText();
        String address = addressTxt.getText();
        String postalCode = postalTxt.getText();
        String phone = phoneTxt.getText();
        Integer customerID = Integer.parseInt(idTxt.getText());

        if (country == null || division == null || name.isBlank() || address.isBlank() || postalCode.isBlank() ||
                phone.isBlank()) {

            ButtonType clickOK = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert emptyValue = new Alert(Alert.AlertType.WARNING, "All fields need to be completed prior to submitting!",
                    clickOK);
            emptyValue.showAndWait();
            return;

        }

        Boolean success = CustomerDAO.updateCustomer(division, name, address, postalCode, phone, customerID);

        if (success) {
            ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer updated successfully!", clickOkay);
            alert.showAndWait();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        else {
            ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert invalidInput = new Alert(Alert.AlertType.WARNING, "Failed to update customer", clickOkay);
            invalidInput.showAndWait();
        }
    }

    /**
     * Matches the customer data to the UI fields for editing.
     *
     * @param selectedIndex The index of the selected customer.
     * @param customer      The Customer object to be edited.
     * @throws SQLException If a SQL error occurs during the retrieval of customer data.
     */
    public void matchCustomer(int selectedIndex, Customer customer) throws SQLException {
        currentIndex = selectedIndex;

        countryCB.setItems(FXCollections.observableArrayList(CustomerDAO.getAllCountries()));
        countryCB.getSelectionModel().select(customer.getCountry());
        stateCB.setItems(FXCollections.observableArrayList(CustomerDAO.getFilteredDivisions(customer.getCountry())));
        stateCB.getSelectionModel().select(customer.getDivision());
        idTxt.setText(String.valueOf(customer.getId()));
        fullNameTxt.setText(String.valueOf(customer.getName()));
        addressTxt.setText(String.valueOf(customer.getAddress()));
        postalTxt.setText(String.valueOf(customer.getPostalCode()));
        phoneTxt.setText(String.valueOf(customer.getPhone()));
    }
}

