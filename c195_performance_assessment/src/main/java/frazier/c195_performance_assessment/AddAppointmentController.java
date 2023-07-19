package frazier.c195_performance_assessment;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.SQLException;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/** This class handles the add appointment functionality of the application. */
public class AddAppointmentController implements Initializable {
    @FXML
    private TextField appointmentIDTxt;

    @FXML
    private Button cancelBtn;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private ComboBox<Integer> customerComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea descriptionTxt;

    @FXML
    private TextField endTimeTxt;

    @FXML
    private TextField locationTxt;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField startTimeTxt;

    @FXML
    private Label timeZoneLbl;

    @FXML
    private TextField titleTxt;

    @FXML
    private TextField typeTxt;

    @FXML
    private ComboBox<Integer> userIDComboBox;
    Stage stage;
    Parent scene;

    /** This method redirects the user to the dashboard */
    @FXML
    void onActionBackToDashboard(ActionEvent event) throws IOException {
        redirectToDashboard(event);
    }

    /**
     * Handles the action event for saving an appointment.
     *
     * @param event The action event triggered by the save button.
     * @throws SQLException If a database error occurs.
     * @throws IOException  If an I/O error occurs.
     */
    @FXML
    void onActionSaveAppointment(ActionEvent event) throws SQLException, IOException {
        String errorMessage = validateInputFields();
        if (!errorMessage.isEmpty()) {
            showValidationError(errorMessage);
            return;
        }

        LocalDateTime startDateTime = parseLocalDateTime(datePicker.getValue(), startTimeTxt.getText());
        LocalDateTime endDateTime = parseLocalDateTime(datePicker.getValue(), endTimeTxt.getText());

        if (!validateDateTimeFormat(startDateTime) || !validateDateTimeFormat(endDateTime)) {
            showValidationError("Invalid date or time format. Please ensure proper format HH:MM, including leading 0's.");
            return;
        }

        LocalDate apptDate = datePicker.getValue();
        Integer customerID = customerComboBox.getValue();
        Boolean validBusinessHours = validateBusinessHours(startDateTime, endDateTime, apptDate);
        Boolean validOverlap = validateCustomerOverlap(customerID, startDateTime, endDateTime, apptDate);

        if (!validBusinessHours) {
            showValidationError("Invalid Business Hours. (8am to 10pm EST)");
            return;
        }
        if (!validOverlap) {
            showValidationError("Invalid Customer Overlap. Cannot double book customers.");
            return;
        }

        ZonedDateTime zonedStartDateTime = toZonedDateTime(startDateTime);
        ZonedDateTime zonedEndDateTime = toZonedDateTime(endDateTime);
        String loggedOnUserName = LogonSession.getLoggedOnUser().getUserName();

        Boolean success = AppointmentDAO.addAppointment(titleTxt.getText(), descriptionTxt.getText(), locationTxt.getText(),
                typeTxt.getText(), zonedStartDateTime, zonedEndDateTime, loggedOnUserName, loggedOnUserName,
                customerID, userIDComboBox.getValue(), ContactDAO.findContactID(contactComboBox.getValue()));

        if (success) {
            showConfirmationDialog("Appointment added successfully!");
            redirectToDashboard(event);
        } else {
            showErrorDialog("Failed to add appointment.  Overlapping appointments.");
        }
    }

    /**
     * Validates the input fields for the appointment form.
     *
     * @return A string containing error messages for any invalid input fields.
     */
    private String validateInputFields() {
        StringBuilder errorMessage = new StringBuilder();
        if (titleTxt.getText().isBlank() || descriptionTxt.getText().isBlank() || locationTxt.getText().isBlank()
                || contactComboBox.getValue() == null || typeTxt.getText().isBlank()
                || customerComboBox.getValue() == null || userIDComboBox.getValue() == null
                || datePicker.getValue() == null || endTimeTxt.getText().isBlank() || startTimeTxt.getText().isBlank()) {
            errorMessage.append("Please ensure a value has been entered in all fields.\n");
        }
        return errorMessage.toString();
    }

    /**
     * Displays a validation error dialog with the provided error message.
     *
     * @param errorMessage The error message to display in the dialog.
     */
    private void showValidationError(String errorMessage) {
        ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        Alert invalidInput = new Alert(Alert.AlertType.WARNING, errorMessage, clickOkay);
        invalidInput.showAndWait();
    }

    /**
     * Parses the provided time string and combines it with the given date to create a LocalDateTime object.
     *
     * @param date The date part of the LocalDateTime.
     * @param time The time part of the LocalDateTime in the format "HH:mm".
     * @return A LocalDateTime object representing the combination of the date and time.
     */
    private LocalDateTime parseLocalDateTime(LocalDate date, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalDateTime.of(date, LocalTime.parse(time, formatter));
    }

    /**
     * Validates the format of the provided LocalDateTime object by comparing it with the start time and end time
     * text fields.
     *
     * @param dateTime The LocalDateTime object to validate.
     * @return true if the format of the LocalDateTime matches the format of the start time or end time text fields,
     *         false otherwise.
     */
    private boolean validateDateTimeFormat(LocalDateTime dateTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedTime = dateTime.toLocalTime().format(formatter);
            return formattedTime.equals(startTimeTxt.getText()) || formattedTime.equals(endTimeTxt.getText());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates the format of the provided LocalDateTime object by comparing it with the start time and end time
     * text fields.
     *
     * @param dateTime The LocalDateTime object to validate.
     * @return true if the format of the LocalDateTime matches the format of the start time or end time text fields,
     *         false otherwise.
     */
    private ZonedDateTime toZonedDateTime(LocalDateTime dateTime) {
        return ZonedDateTime.of(dateTime, LogonSession.getUserTimeZone())
                .withZoneSameInstant(ZoneOffset.UTC);
    }

    private void showConfirmationDialog(String message) {
        ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, clickOkay);
        alert.showAndWait();
    }

    /**
     * Displays an error dialog with the provided message.
     *
     * @param message The error message to display in the dialog.
     */
    private void showErrorDialog(String message) {
        ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.WARNING, message, clickOkay);
        alert.showAndWait();
    }

    /**
     * Redirects the user to the dashboard view.
     *
     * @param event The action event that triggered the redirection.
     * @throws IOException If an I/O exception occurs during the redirection process.
     */
    private void redirectToDashboard(ActionEvent event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    /**
     * Validates if the appointment's start and end times fall within the business hours.
     *
     * @param startDateTime The start date and time of the appointment.
     * @param endDateTime The end date and time of the appointment.
     * @param apptDate The date of the appointment.
     * @return {@code true} if the appointment is within the business hours, {@code false} otherwise.
     */
    public Boolean validateBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate apptDate) {

        ZonedDateTime startZonedDateTime = ZonedDateTime.of(startDateTime, LogonSession.getUserTimeZone());
        ZonedDateTime endZonedDateTime = ZonedDateTime.of(endDateTime, LogonSession.getUserTimeZone());

        ZonedDateTime startBusinessHours = ZonedDateTime.of(apptDate, LocalTime.of(8,0),
                ZoneId.of("America/New_York"));
        ZonedDateTime endBusinessHours = ZonedDateTime.of(apptDate, LocalTime.of(22, 0),
                ZoneId.of("America/New_York"));


        if (startZonedDateTime.isBefore(startBusinessHours) | startZonedDateTime.isAfter(endBusinessHours) |
                endZonedDateTime.isBefore(startBusinessHours) | endZonedDateTime.isAfter(endBusinessHours) |
                startZonedDateTime.isAfter(endZonedDateTime)) {
            return false;

        }
        else {
            return true;
        }

    }

    /**
     * Validates if there is any overlap between the specified customer's existing appointments and the given appointment.
     *
     * @param inputCustomerID The ID of the customer.
     * @param startDateTime The start date and time of the appointment to be checked for overlap.
     * @param endDateTime The end date and time of the appointment to be checked for overlap.
     * @param apptDate The date of the appointment to be checked for overlap.
     * @return {@code true} if there is no overlap with the customer's existing appointments, {@code false} otherwise.
     * @throws SQLException If an SQL exception occurs while retrieving customer's appointments.
     */
    public Boolean validateCustomerOverlap(Integer inputCustomerID, LocalDateTime startDateTime,
                                           LocalDateTime endDateTime, LocalDate apptDate) throws SQLException {

        ObservableList<Appointment> possibleConflicts = AppointmentDAO.getCustomerFilteredAppointments(apptDate,
                inputCustomerID);

        if (possibleConflicts.isEmpty()) {
            return true;
        }
        else {
            for (Appointment conflictAppt : possibleConflicts) {

                LocalDateTime conflictStart = conflictAppt.getStartDate().toLocalDateTime();
                LocalDateTime conflictEnd = conflictAppt.getEndDate().toLocalDateTime();

                if( conflictStart.isBefore(startDateTime) & conflictEnd.isAfter(endDateTime)) {
                    return false;
                }
                if (conflictStart.isBefore(endDateTime) & conflictStart.isAfter(startDateTime)) {
                    return false;
                }
                if (conflictEnd.isBefore(endDateTime) & conflictEnd.isAfter(startDateTime)) {
                    return false;
                }
                else {
                    return true;
                }

            }
        }
        return true;

    }

    /**
     * Initializes the appointment form with default values and sets up event listeners.
     *
     * @param url The location used to resolve relative paths for the root object, or {@code null} if the location is not known.
     * @param rb The resources used to localize the root object, or {@code null} if the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle rb){
        timeZoneLbl.setText("Time Zone: " + LogonSession.getUserTimeZone());

        //Lambda Expression - Disable users from picking dates in the past or weekend.

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate apptDatePicker, boolean empty) {
                super.updateItem(apptDatePicker, empty);
                setDisable(
                        empty || apptDatePicker.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                apptDatePicker.getDayOfWeek() == DayOfWeek.SUNDAY || apptDatePicker.isBefore(LocalDate.now()));
            }
        });

        try {
            customerComboBox.setItems(CustomerDAO.getAllCustomerID());
            userIDComboBox.setItems(UserDAO.getAllUserID());
            contactComboBox.setItems(ContactDAO.getAllContactName());
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}

