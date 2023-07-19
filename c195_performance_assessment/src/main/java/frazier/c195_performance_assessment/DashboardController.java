package frazier.c195_performance_assessment;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for the Dashboard view.
 */
public class DashboardController implements Initializable {
    @FXML
    private TableColumn<Customer, String> addressCol;

    @FXML
    private TableColumn<Appointment, Integer> appointmentIDCol;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private TableColumn<Customer, String> countryCol;

    @FXML
    private Label currentDate;

    @FXML
    private TableColumn<Appointment, Integer> customerIDCol;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> customerTableIDCol;

    @FXML
    private Button deleteApptBtn;

    @FXML
    private Button deleteCustomerBtn;

    @FXML
    private TableColumn<Appointment, String> descriptionCol;

    @FXML
    private TableColumn<Customer, String> divisionCol;

    @FXML
    private TableColumn<Appointment, Timestamp> endDateCol;

    @FXML
    private ToggleGroup filter;

    @FXML
    private TableColumn<Appointment, String> locationCol;

    @FXML
    private Button logoutBtn;

    @FXML
    private TableColumn<Customer, String> nameCol;

    @FXML
    private Button newApptBtn;

    @FXML
    private Button newCustomerBtn;

    @FXML
    private TableColumn<Customer, String> phoneCol;

    @FXML
    private TableColumn<Customer, String> postalCol;

    @FXML
    private Button reportBtn;

    @FXML
    private TableColumn<Appointment, Timestamp> startDateCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private Button updateApptBtn;

    @FXML
    private Button updateCustomerBtn;
    Stage stage;
    Parent scene;
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final FilteredList<Appointment> filteredAppointments = new FilteredList<>(appointments);

    /**
     * Handles the event when the delete appointment button is clicked.
     *
     * @param event The ActionEvent triggering the event.
     * @throws SQLException If a database access error occurs.
     * @throws IOException  If an I/O error occurs.
     */
    @FXML
    void onActionDeleteAppointment(ActionEvent event) throws SQLException, IOException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            ButtonType clickOK = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert invalidInput = new Alert(Alert.AlertType.WARNING, "No appointment selected", clickOK);
            invalidInput.showAndWait();
            return;
        } else {
            ButtonType clickYes = ButtonType.YES;
            ButtonType clickNo = ButtonType.NO;
            Alert deleteAlert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete appointment: "
                    + selectedAppointment.getId() + " ?", clickYes, clickNo);
            Optional<ButtonType> result = deleteAlert.showAndWait();

            if (result.get() == ButtonType.YES) {
                Boolean success = AppointmentDAO.deleteAppointment(selectedAppointment.getId());
                if (success) {
                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert deletedAppt = new Alert(Alert.AlertType.CONFIRMATION, "Appointment deleted", clickOkay);
                    deletedAppt.showAndWait();
                    appointments.remove(selectedAppointment);
                    appointmentTable.setItems(appointments);
                } else {
                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert deleteAppt = new Alert(Alert.AlertType.WARNING, "Failed to delete Appointment", clickOkay);
                    deleteAppt.showAndWait();

                }


            }
        }
    }

    /**
     * Handles the event when the delete customer button is clicked.
     *
     * @param event The ActionEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If a database access error occurs.
     */
    @FXML
    void onActionDeleteCustomer(ActionEvent event) throws IOException, SQLException{
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.WARNING, "No selected Customer", clickOkay);
            alert.showAndWait();
            return;
        }
        else {
            ButtonType clickYes = ButtonType.YES;
            ButtonType clickNo = ButtonType.NO;
            Alert deleteAlert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete Customer: "
                    + selectedCustomer.getId() + " and all related appointments?", clickYes, clickNo);
            Optional<ButtonType> result = deleteAlert.showAndWait();

            if (result.get() == ButtonType.YES) {
                Boolean customerApptSuccess = AppointmentDAO.deleteCustomersAppointments(selectedCustomer.getId());

                Boolean customerSuccess = CustomerDAO.deleteCustomer(selectedCustomer.getId());


                if (customerSuccess && customerApptSuccess) {
                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert deletedCustomer = new Alert(Alert.AlertType.CONFIRMATION,
                            "Customer + related appointments deleted", clickOkay);
                    deletedCustomer.showAndWait();
                    appointments.remove(selectedCustomer);
                    customers.remove(selectedCustomer);
                    customerTable.setItems(customers);
                }
                else {

                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert deleteAppt = new Alert(Alert.AlertType.WARNING,
                            "Failed to delete Customer or related appointments ", clickOkay);
                    deleteAppt.showAndWait();

                }

            }
            else {
                return;
            }
        }
    }

    /**
     * Handles the event when the log out button is clicked.
     *
     * @param event The ActionEvent triggering the event.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    void onActionLogOut(ActionEvent event) throws IOException{
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the event when the "New Appointment" button is clicked.
     *
     * @param event The ActionEvent triggering the event.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    void onActionNewAppointment(ActionEvent event) throws IOException {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("AddAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the event when the "New Customer" button is clicked.
     *
     * @param event The ActionEvent triggering the event.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    void onActionNewCustomer(ActionEvent event) throws IOException{
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("AddCustomer.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the event when the "Update Appointment" button is clicked.
     * Loads the ModifyAppointment.fxml view and sets the selected appointment data
     * for modification in the ModifyAppointmentController.
     *
     * @param event The ActionEvent triggering the event.
     * @throws IOException If an I/O error occurs.
     * @throws SQLException If a SQL error occurs.
     */
    @FXML
    void onActionUpdateAppointment(ActionEvent event) throws IOException, SQLException{
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyAppointment.fxml"));
            Parent root = loader.load();

            ModifyAppointmentController macontroller = loader.getController();
            macontroller.matchAppointment(appointmentTable.getSelectionModel().getSelectedIndex(), appointmentTable.getSelectionModel().getSelectedItem());

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Select an appointment to modify");
            alert.show();
        }
    }

    /**
     * Handles the event when the "Update Customer" button is clicked.
     * Loads the ModifyCustomer.fxml view and sets the selected customer data
     * for modification in the ModifyCustomerController.
     *
     * @param event The ActionEvent triggering the event.
     * @throws SQLException If a SQL error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    void onActionUpdateCustomer(ActionEvent event) throws SQLException, IOException{
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyCustomer.fxml"));
            Parent root = loader.load();

            ModifyCustomerController mccontroller = loader.getController();
            mccontroller.matchCustomer(customerTable.getSelectionModel().getSelectedIndex(), customerTable.getSelectionModel().getSelectedItem());

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Select a customer to modify");
            alert.show();
        }
    }

    /**
     * Handles the event when the "Show All Appointments" button is clicked.
     * Clears the predicate for filtering appointments and sets the complete
     * appointments list to be displayed in the appointmentTable.
     *
     * @param event The ActionEvent triggering the event.
     */
    @FXML
    void onActionShowAllAppointments(ActionEvent event) {
        filteredAppointments.setPredicate(null);

        appointmentTable.setItems(appointments);
    }

    /**
     * Handles the event when the "Filter Appointments by Month" button is clicked.
     * Filters the appointments to display only those that belong to the current month.
     * Updates the appointmentTable with the filtered appointments.
     *
     * @param event The ActionEvent triggering the event.
     */
    @FXML
    void onActionFilterAppointmentsByMonth(ActionEvent event) {
        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();

        filteredAppointments.setPredicate(appointment -> {
            LocalDate startDate = appointment.getStartDate().toLocalDateTime().toLocalDate();
            Month appointmentMonth = startDate.getMonth();
            return appointmentMonth == currentMonth;
        });

        appointmentTable.setItems(filteredAppointments);
    }

    /**
     * Handles the event when the "Filter Appointments by Week" button is clicked.
     * Filters the appointments to display only those that belong to the current week.
     * Updates the appointmentTable with the filtered appointments.
     *
     * @param event The ActionEvent triggering the event.
     */
    @FXML
    void onActionFilterAppointmentsByWeek(ActionEvent event) {
        LocalDate currentDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());

        filteredAppointments.setPredicate(appointment -> {
            LocalDate startDate = appointment.getStartDate().toLocalDateTime().toLocalDate();
            int appointmentWeekNumber = startDate.get(weekFields.weekOfWeekBasedYear());
            return appointmentWeekNumber == currentWeekNumber;
        });

        appointmentTable.setItems(filteredAppointments);
    }



    /**
     * Handles the event when the "View Reports" button is clicked.
     * Loads the Reports.fxml file and displays it in a new stage.
     *
     * @param event The ActionEvent triggering the event.
     * @throws IOException If an I/O error occurs while loading the Reports.fxml file.
     */
    @FXML
    void onActionViewReports(ActionEvent event) throws IOException{
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("Reports.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Populates the appointmentTable and customerTable with data retrieved from the database.
     * Sets the current date in the currentDate label.
     *
     * @param url      The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb       The resource bundle used to localize the root object, or null if the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle rb){
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = now.format(formatter);
        currentDate.setText(formattedDate);

        JDBC.openConnection();
        try {
            String appointmentQuery = "SELECT appointments.Appointment_ID, appointments.User_ID, appointments.Title, appointments.Description, appointments.Location, contacts.Contact_Name, appointments.Type, appointments.Start, appointments.End, appointments.Create_Date,appointments.Customer_ID, contacts.Contact_ID, appointments.Created_By, appointments.Last_Update, appointments.Last_Updated_By\n" +
                    "FROM appointments\n" +
                    "join contacts on appointments.Contact_ID = contacts.Contact_ID;";
            Statement appointmentStatement = JDBC.connection.createStatement();
            ResultSet appointmentResultSet = appointmentStatement.executeQuery(appointmentQuery);

            while (appointmentResultSet.next()){
                int appointmentID = appointmentResultSet.getInt("Appointment_ID");
                String appointmentTitle = appointmentResultSet.getString("Title");
                String appointmentDescription = appointmentResultSet.getString("Description");
                String appointmentLocation = appointmentResultSet.getString("Location");
                String appointmentType = appointmentResultSet.getString("Type");
                Timestamp appointmentStart = appointmentResultSet.getTimestamp("Start");
                Timestamp appointmentEnd = appointmentResultSet.getTimestamp("End");
                Timestamp createDate = appointmentResultSet.getTimestamp("Create_Date");
                String createdBy = appointmentResultSet.getString("Created_By");
                Timestamp lastUpdate = appointmentResultSet.getTimestamp("Last_Update");
                String lastUpdatedBy = appointmentResultSet.getString("Last_Updated_By");
                int customerID = appointmentResultSet.getInt("Customer_ID");
                int contactID = appointmentResultSet.getInt("Contact_ID");
                String contactName = appointmentResultSet.getString("Contact_Name");
                int userID = appointmentResultSet.getInt("User_ID");

                Appointment appointment = new Appointment(appointmentID,appointmentTitle,appointmentDescription,appointmentLocation,appointmentType,appointmentStart,appointmentEnd,createDate,createdBy,lastUpdate,lastUpdatedBy,customerID,userID,contactID,contactName);
                appointments.add(appointment);
            }
            appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));

            appointmentTable.setItems(appointments);

            String customerQuery = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, first_level_divisions.Division, first_level_divisions.Division_ID, customers.Postal_Code, customers.Phone, countries.Country\n" +
                    "FROM customers\n" +
                    "JOIN first_level_divisions on customers.Division_ID = first_level_divisions.Division_ID\n" +
                    "join countries on first_level_divisions.Country_ID = countries.Country_ID;";
            Statement customerStatement = JDBC.connection.createStatement();
            ResultSet customerResultSet = customerStatement.executeQuery(customerQuery);

            while (customerResultSet.next()){
                int customerID = customerResultSet.getInt("Customer_ID");
                String customerName = customerResultSet.getString("Customer_Name");
                String address = customerResultSet.getString("Address");
                String division = customerResultSet.getString("Division");
                int divisionID = customerResultSet.getInt("Division_ID");
                String postalCode = customerResultSet.getString("Postal_Code");
                String phone = customerResultSet.getString("Phone");
                String country = customerResultSet.getString("Country");

                Customer customer = new Customer(customerID,customerName,address,postalCode,phone,divisionID,division,country);
                customers.add(customer);
            }

            customerTableIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            divisionCol.setCellValueFactory(new PropertyValueFactory<>("division"));
            postalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));


            customerTable.setItems(customers);


        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
