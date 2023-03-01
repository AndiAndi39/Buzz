module com.example.reteasocializare {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.reteasocializare to javafx.fxml;
    exports com.example.reteasocializare;
}