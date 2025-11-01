module com.oop4clinic.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.gluonhq.charm.glisten;
    requires com.gluonhq.attach.lifecycle;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;

    opens com.oop4clinic.clinicmanagement to javafx.fxml;
    exports com.oop4clinic.clinicmanagement;
    exports com.oop4clinic.clinicmanagement.controller;
    opens com.oop4clinic.clinicmanagement.controller to javafx.fxml;
    exports com.oop4clinic.clinicmanagement.model;
    opens com.oop4clinic.clinicmanagement.model to javafx.fxml;
}