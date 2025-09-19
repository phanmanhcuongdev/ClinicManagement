module com.oop4clinic.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.oop4clinic.clinicmanagement to javafx.fxml;
    exports com.oop4clinic.clinicmanagement;
}