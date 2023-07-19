module frazier.c195_performance_assessment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens frazier.c195_performance_assessment to javafx.fxml;
    exports frazier.c195_performance_assessment;
}