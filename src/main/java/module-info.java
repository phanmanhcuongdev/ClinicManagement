module com.oop4clinic.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core; // <- thêm dòng này
    requires java.sql;
    requires java.desktop;


    opens com.oop4clinic.clinicmanagement to javafx.fxml;
    exports com.oop4clinic.clinicmanagement;

    opens com.oop4clinic.clinicmanagement.model.entity to org.hibernate.orm.core;
}