module com.oop4clinic.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;



    exports com.oop4clinic.clinicmanagement;

    opens com.oop4clinic.clinicmanagement to javafx.fxml;

    opens com.oop4clinic.clinicmanagement.model.entity to org.hibernate.orm.core;

    opens com.oop4clinic.clinicmanagement.controller to javafx.fxml,javafx.base;

    opens com.oop4clinic.clinicmanagement.model.dto to javafx.base;

    opens com.oop4clinic.clinicmanagement.util to org.hibernate.orm.core;
    opens com.oop4clinic.clinicmanagement.controller.admin to javafx.base, javafx.fxml;
    opens com.oop4clinic.clinicmanagement.controller.doctor to javafx.base, javafx.fxml;
    opens com.oop4clinic.clinicmanagement.controller.patient to javafx.base, javafx.fxml;
}