module com.example.lab19 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.lab19 to javafx.fxml;
    exports com.example.lab19;
}