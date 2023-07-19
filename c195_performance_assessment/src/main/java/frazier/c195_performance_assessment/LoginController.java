package frazier.c195_performance_assessment;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * The controller class for the Login.fxml view.
 */
public class LoginController {
    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button exitBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField password;

    @FXML
    private TextField userName;

    @FXML
    private Label zoneIDLabel;
    private ResourceBundle resourceBundle;
    Stage stage;
    Parent scene;

    /**
     * Initializes the controller. Sets up the UI components and localization.
     */
    public void initialize(){
        Locale locale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("translations", locale);

        userName.setPromptText(resourceBundle.getString("prompt.username"));
        password.setPromptText(resourceBundle.getString("prompt.password"));
        errorMessageLabel.setText(resourceBundle.getString("error.invalidCredentials"));
        exitBtn.setText(resourceBundle.getString("button.exit"));
        loginBtn.setText(resourceBundle.getString("button.login"));
        zoneIDLabel.setText(resourceBundle.getString("label.zoneID") + ": " + TimeZone.getDefault().getID());

        //lambda expression to allow users to press enter instead of clicking login button
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Node source = (Node) event.getSource();
                Scene scene = source.getScene();
                Button loginButton = (Button) scene.lookup("#loginBtn");
                if (loginButton != null) {
                    ActionEvent actionEvent = new ActionEvent(source, loginButton);
                    loginButton.fireEvent(actionEvent);
                }
            }
        });
    }

    /**
     * Handles the action event for the exit button.
     *
     * @param event The action event.
     */
    @FXML
    void onActionExit(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Handles the action event for the login button.
     *
     * @param event The action event.
     * @throws IOException  If an I/O error occurs while loading the next scene.
     * @throws SQLException If a SQL error occurs while attempting to log in.
     */
    @FXML
    void onActionLogin(ActionEvent event) throws IOException, SQLException {

        String enteredUsername = userName.getText();
        String enteredPassword = password.getText();

        boolean logon = LogonSession.attemptLogon(enteredUsername, enteredPassword);

        writeLoginActivity(enteredUsername, logon);

        if (logon) {


            ObservableList<Appointment> upcomingAppts = AppointmentDAO.getAppointmentsIn15Mins();

            if (!upcomingAppts.isEmpty()) {
                for (Appointment upcoming : upcomingAppts) {

                    String message = "Upcoming appointmentID: " + upcoming.getId() + " Start: " +
                            upcoming.getStartDate().toString();
                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert invalidInput = new Alert(Alert.AlertType.WARNING, message, clickOkay);
                    invalidInput.showAndWait();
                }

            }

            else {
                ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                Alert invalidInput = new Alert(Alert.AlertType.CONFIRMATION, "No upcoming Appointments", clickOkay);
                invalidInput.showAndWait();
            }

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        else {
            Locale locale = Locale.getDefault();
            resourceBundle = ResourceBundle.getBundle("translations", locale);
            ButtonType clickOkay = new ButtonType(resourceBundle.getString("okayButton"), ButtonBar.ButtonData.OK_DONE);
            Alert failedLogon = new Alert(Alert.AlertType.WARNING, resourceBundle.getString("error.invalidCredentials"),
                    clickOkay);
            errorMessageLabel.setText(resourceBundle.getString("error.invalidCredentials"));
            errorMessageLabel.setVisible(true);
            failedLogon.showAndWait();
        }
    }

    /**
     * Writes the login activity to a log file.
     *
     * @param username The username used for logging in.
     * @param success  Indicates whether the login attempt was successful or not.
     */
    private void writeLoginActivity(String username, boolean success) {
        String fileName = "login_activity.txt";
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String status = success ? "SUCCESS" : "FAILURE";
        String log = String.format("%s | Username: %s | Status: %s%n", dateTime, username, status);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

