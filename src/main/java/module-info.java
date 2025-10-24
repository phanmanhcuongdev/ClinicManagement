module com.oop4clinic.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core; // <- thêm dòng này
    requires java.sql;
    requires java.desktop;
    requires javafx.base;



    exports com.oop4clinic.clinicmanagement;

    opens com.oop4clinic.clinicmanagement to javafx.fxml;

    opens com.oop4clinic.clinicmanagement.model.entity to org.hibernate.orm.core;

    opens com.oop4clinic.clinicmanagement.controller to javafx.fxml,javafx.base;

    opens com.oop4clinic.clinicmanagement.model.dto to javafx.base;
}