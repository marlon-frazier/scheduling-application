package frazier.c195_performance_assessment;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class for the Scheduling Application.
 */
public class SchedulingApplication extends Application {

    /**
     * Starts the JavaFX application.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an error occurs during the loading of the Login view.
     */

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SchedulingApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 370, 706);
        stage.setTitle("C195 Performance Assessment");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The entry point of the application. Launches the JavaFX application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch();
        JDBC.closeConnection();
    }
}