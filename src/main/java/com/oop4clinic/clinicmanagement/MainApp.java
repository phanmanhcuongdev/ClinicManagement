package com.oop4clinic.clinicmanagement;

import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuPatient.fxml")
                //getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml")
                //getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml")
        );
        init();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Clinic OOP4");
        stage.show();
    }

    @Override
    public void stop()
    {
        com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider.close();
    }

    @Override
    public void init() throws Exception {
        EntityManagerProvider.init();
        seedAppointmentsForDoctor4();

    }

    public static void seedAppointmentsForDoctor4() {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Find doctor by ID 4
            Doctor bacSiHuy = em.find(Doctor.class, 4);
            if (bacSiHuy == null) {
                System.err.println("❗ Bác sĩ với ID 4 không tồn tại. Hủy seed.");
                tx.rollback();
                return;
            }

            System.out.println("🚀 Đang seed bệnh nhân và lịch hẹn cho Bác sĩ Lê Quang Huy...");

            // Seed new patient: Nguyễn Thị C
            Patient benhNhanC = new Patient();
            benhNhanC.setFullName("Nguyễn Thị C");
            benhNhanC.setGender(Gender.FEMALE);
            benhNhanC.setPhone("0900000011");
            benhNhanC.setEmail("thib77@gmail.com");
            benhNhanC.setAddress("Hà Đông - Hà Nội");
            benhNhanC.setCccd("011111111199");
            benhNhanC.setInsuranceCode("BH0999");
            em.persist(benhNhanC);

            // Seed appointment
            Appointment appointment = new Appointment();
            appointment.setPatient(benhNhanC);
            appointment.setDoctor(bacSiHuy);
            appointment.setDepartment(bacSiHuy.getDepartment()); // Use doctor's existing department
            appointment.setReason("Mọc răng khôn");
            appointment.setStartTime(
                    Instant.ofEpochMilli(1762241400000L)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );
            appointment.setStatus(AppointmentStatus.PENDING);
            em.persist(appointment);

            System.out.println("✅ Patient and appointment seeded successfully for Doctor ID 4.");

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }





}



