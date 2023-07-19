package frazier.c195_performance_assessment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Controller class for the Modify Appointment view.
 */
public class ModifyAppointmentController{
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
    private int currentIndex = 0;

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
     * Handles the action when the user clicks the "Save Appointment" button.
     *
     * @param event The ActionEvent triggered by the button.
     * @throws SQLException If a SQL error occurs during the appointment update.
     * @throws IOException  If an error occurs during the loading of the Dashboard view.
     */
    @FXML
    void onActionSaveAppointment(ActionEvent event) throws SQLException, IOException{

            Boolean validStartDateTime = true;
            Boolean validEndDateTime = true;
            Boolean validOverlap = true;
            Boolean validBusinessHours = true;
            String errorMessage = "";

            Integer apptID = Integer.parseInt(appointmentIDTxt.getText());
            String title = titleTxt.getText();
            String description = descriptionTxt.getText();
            String location = locationTxt.getText();
            String contactName = contactComboBox.getValue();
            String type = typeTxt.getText();
            Integer customerID = customerComboBox.getValue();
            Integer userID = userIDComboBox.getValue();
            LocalDate apptDate = datePicker.getValue();
            LocalDateTime endDateTime = null;
            LocalDateTime startDateTime = null;
            ZonedDateTime zonedEndDateTime = null;
            ZonedDateTime zonedStartDateTime = null;

            Integer contactID = ContactDAO.findContactID(contactName);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");


            try {
                startDateTime = LocalDateTime.of(datePicker.getValue(),
                        LocalTime.parse(startTimeTxt.getText(), formatter));
                validStartDateTime = true;
            }
            catch(DateTimeParseException error) {
                validStartDateTime = false;
                errorMessage += "Invalid Start time. Please ensure proper format HH:MM, including leading 0's.\n";
            }

            try {
                endDateTime = LocalDateTime.of(datePicker.getValue(),
                        LocalTime.parse(endTimeTxt.getText(), formatter));
                validEndDateTime = true;
            }
            catch(DateTimeParseException error) {
                validEndDateTime = false;
                errorMessage += "Invalid End time. Please ensure proper format HH:MM, including leading 0's.\n";
            }

            if (title.isBlank() || description.isBlank() || location.isBlank() || contactName == null || type.isBlank() ||
                    customerID == null || userID == null || apptDate == null || endDateTime == null ||
                    startDateTime == null) {

                errorMessage += "Please ensure a value has been entered in all fields.\n";
                ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                Alert invalidInput = new Alert(Alert.AlertType.WARNING, errorMessage, clickOkay);
                invalidInput.showAndWait();
                return;

            }

            validBusinessHours = validateBusinessHours(startDateTime, endDateTime, apptDate);
            validOverlap = validateCustomerOverlap(customerID, startDateTime, endDateTime, apptDate);

            if (!validBusinessHours) {
                errorMessage += "Invalid Business Hours.(8am to 10pm EST)\n";
            }
            if (!validOverlap) {
                errorMessage += "Invalid Customer Overlap. Cannot double book customers.\n";
            }


            if (!validOverlap || !validBusinessHours || !validEndDateTime || !validStartDateTime) {
                ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                Alert invalidInput = new Alert(Alert.AlertType.WARNING, errorMessage, clickOkay);
                invalidInput.showAndWait();
                return;

            }
            else {
                zonedStartDateTime = ZonedDateTime.of(startDateTime, LogonSession.getUserTimeZone());
                zonedEndDateTime = ZonedDateTime.of(endDateTime, LogonSession.getUserTimeZone());
                String loggedOnUserName = LogonSession.getLoggedOnUser().getUserName();

                zonedStartDateTime = zonedStartDateTime.withZoneSameInstant(ZoneOffset.UTC);
                zonedEndDateTime = zonedEndDateTime.withZoneSameInstant(ZoneOffset.UTC);

                Boolean success = AppointmentDAO.updateAppointment(apptID, title, description, location, type, zonedStartDateTime,
                        zonedEndDateTime, loggedOnUserName, customerID, userID, contactID );

                if (success) {
                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert invalidInput = new Alert(Alert.AlertType.CONFIRMATION, "Appointment updated successfully!", clickOkay);
                    invalidInput.showAndWait();
                    stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();
                }
                else {
                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert invalidInput = new Alert(Alert.AlertType.WARNING, "failed to Update appointment", clickOkay);
                    invalidInput.showAndWait();
                }

            }

        }


    /**
     * Matches the appointment data to the UI fields for editing.
     *
     * @param selectedIndex The index of the selected appointment.
     * @param appointment   The Appointment object to be edited.
     * @throws SQLException If a SQL error occurs during the retrieval of appointment data.
     */
    public void matchAppointment(int selectedIndex, Appointment appointment) throws SQLException{
        currentIndex = selectedIndex;
        timeZoneLbl.setText("Time Zone: " + LogonSession.getUserTimeZone());

        //lambda function that disables weekends in the date picker
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                DayOfWeek dayOfWeek = date.getDayOfWeek();

                // Disable weekends (Saturday and Sunday)
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: #dddddd;");
                }
            }
        });

        ZonedDateTime startDateTimeUTC = appointment.getStartDate().toInstant().atZone(ZoneOffset.UTC);
        ZonedDateTime endDateTimeUTC = appointment.getEndDate().toInstant().atZone(ZoneOffset.UTC);

        ZonedDateTime localStartDateTime = startDateTimeUTC.withZoneSameInstant(LogonSession.getUserTimeZone());
        ZonedDateTime localEndDateTime = endDateTimeUTC.withZoneSameInstant(LogonSession.getUserTimeZone());

        String startTime = String.valueOf(appointment.getStartDate());
        String endTime = String.valueOf(appointment.getEndDate());

        appointmentIDTxt.setText(String.valueOf(appointment.getId()));
        titleTxt.setText(String.valueOf(appointment.getTitle()));
        descriptionTxt.setText(String.valueOf(appointment.getDescription()));
        locationTxt.setText(String.valueOf(appointment.getLocation()));
        contactComboBox.setItems(FXCollections.observableArrayList(ContactDAO.getAllContactName()));
        contactComboBox.getSelectionModel().select(appointment.getContactName());
        typeTxt.setText(String.valueOf(appointment.getType()));
        customerComboBox.setItems(FXCollections.observableArrayList(CustomerDAO.getAllCustomerID()));
        customerComboBox.getSelectionModel().select(appointment.getCustomerID());
        userIDComboBox.setItems(FXCollections.observableArrayList(UserDAO.getAllUserID()));
        userIDComboBox.getSelectionModel().select(appointment.getUserID());
        datePicker.setValue(appointment.getStartDate().toLocalDateTime().toLocalDate());
        startTimeTxt.setText(startTime.substring(startTime.length() - 10, startTime.length() - 5));
        endTimeTxt.setText(endTime.substring(endTime.length() - 10, endTime.length() - 5));
    }

    /**
     * Validates if the appointment falls within the business hours.
     *
     * @param startDateTime The start date and time of the appointment.
     * @param endDateTime   The end date and time of the appointment.
     * @param apptDate      The date of the appointment.
     * @return True if the appointment is within business hours, false otherwise.
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
     * Validates if there are any overlapping appointments for the same customer.
     *
     * @param inputCustomerID The ID of the customer.
     * @param startDateTime   The start date and time of the appointment.
     * @param endDateTime     The end date and time of the appointment.
     * @param apptDate        The date of the appointment.
     * @return True if no overlapping appointments exist, false otherwise.
     * @throws SQLException If a SQL error occurs during the retrieval of appointment data.
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


}
