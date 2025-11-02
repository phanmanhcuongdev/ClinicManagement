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

import java.time.LocalDate;
import java.util.List;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                //getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuPatient.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml")
                 getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/DoctorProfile.fxml")
                //getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/DoctorProfile.fxml")
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
        // Chạy TRƯỚC khi JavaFX Application Thread hiển thị UI
        EntityManagerProvider.init();            // 🔥 khởi động EMF sớm
        // (nếu bạn đang seed ở đây)
        seedDepartmentsOnce();
        seedBasicData();
    }

    public static void seedDepartmentsOnce() {
        DepartmentService service = new DepartmentServiceImpl();

        if (!service.findAll().isEmpty()) {
            System.out.println("🔹 Departments already exist — skip seeding.");
            return;
        }

        record DepSeed(String name, double fee, String desc) {}
        List<DepSeed> seeds = List.of(
            new DepSeed("Khoa Nội Tổng Quát", 180000, "Khám và điều trị các bệnh nội khoa thông thường cho người lớn."),
            new DepSeed("Khoa Nhi", 200000, "Chuyên khám và điều trị cho trẻ em."),
            new DepSeed("Khoa Tim Mạch", 250000, "Chuyên khám và điều trị các bệnh lý về tim và mạch máu."),
            new DepSeed("Khoa Da Liễu", 220000, "Chuyên điều trị các bệnh về da, tóc, móng."),
            new DepSeed("Khoa Thần Kinh", 260000, "Chuyên điều trị các bệnh lý hệ thần kinh."),
            new DepSeed("Khoa Chấn Thương Chỉnh Hình", 240000, "Điều trị các vấn đề về xương khớp, chấn thương."),
            new DepSeed("Khoa Sản", 230000, "Khám và chăm sóc sức khỏe sản khoa."),
            new DepSeed("Khoa Phụ Khoa", 230000, "Chuyên điều trị các bệnh phụ khoa."),
            new DepSeed("Khoa Mắt", 210000, "Chuyên khám và điều trị các bệnh về mắt."),
            new DepSeed("Khoa Nha Khoa", 220000, "Chuyên khám và điều trị các vấn đề răng miệng."),
            new DepSeed("Khoa Tai Mũi Họng", 200000, "Chuyên khám và điều trị tai mũi họng."),
            new DepSeed("Khoa Tâm Thần", 250000, "Chuyên điều trị các bệnh lý tâm thần.")
        );

        System.out.println("🚀 Seeding departments...");
        for (DepSeed s : seeds) {
            DepartmentDTO dto = new DepartmentDTO();
            dto.setName(s.name());
            dto.setBaseFee(s.fee());
            dto.setDescription(s.desc());
            service.create(dto);
        }

        System.out.println("✅ Departments seeded successfully!");
    }




    public static void seedBasicData() {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (em.createQuery("SELECT COUNT(d) FROM Department d", Long.class).getSingleResult() == 0) {
                System.out.println("🚀 Seeding departments...");
                Department noiTongQuat = new Department();
                noiTongQuat.setName("Khoa Nội Tổng Quát");
                noiTongQuat.setBaseFee(180000.0);
                em.persist(noiTongQuat);

                Department timMach = new Department();
                timMach.setName("Khoa Tim Mạch");
                timMach.setBaseFee(250000.0);
                em.persist(timMach);
                System.out.println("✅ Departments seeded.");

                System.out.println("🚀 Seeding doctors...");
                Doctor bsA = new Doctor();
                bsA.setFullName("Bác sĩ Nguyễn Văn An");
                bsA.setGender(Gender.MALE);
                bsA.setDateOfBirth(LocalDate.of(1985, 5, 15));
                bsA.setPhone("0901234567");
                bsA.setEmail("bs.nguyenvana@clinic.com");
                bsA.setDepartment(noiTongQuat);
                bsA.setStatus(DoctorStatus.ACTIVE);
                em.persist(bsA);
                System.out.println("✅ Doctors seeded.");

                System.out.println("✅ Doctors seeded. ID của Bác sĩ A là: " + bsA.getId());

                System.out.println("🚀 Seeding patient 1 (bnX)...");
                Patient bnX = new Patient();
                bnX.setFullName("Trần Thị rừgb");
                bnX.setGender(Gender.FEMALE);
                bnX.setDateOfBirth(LocalDate.of(1990, 10, 20));
                bnX.setPhone("0987654323");
                em.persist(bnX);

                System.out.println("🚀 Seeding patient 2 (bna)...");
                Patient bna = new Patient();
                bna.setFullName("Trần Thị Binhg");
                bna.setGender(Gender.FEMALE);
                bna.setDateOfBirth(LocalDate.of(1990, 10, 20));
                bna.setPhone("0987658221");
                em.persist(bna);

                System.out.println("🚀 Seeding patient 3 (bnn)...");
                Patient bnn = new Patient();
                bnn.setFullName("Trần Thị Ann");
                bnn.setGender(Gender.FEMALE);
                bnn.setDateOfBirth(LocalDate.of(1990, 10, 20));
                bnn.setPhone("0987654201");
                em.persist(bnn);

                System.out.println("✅ All patients seeded.");

                System.out.println("🚀 Seeding appointments...");
                Appointment appt1 = new Appointment();
                appt1.setPatient(bnX);
                appt1.setDoctor(bsA);
                appt1.setDepartment(noiTongQuat);
                appt1.setStartTime(LocalDate.now().atTime(9, 0));
                appt1.setStatus(AppointmentStatus.CONFIRMED);
                appt1.setReason("Đau bụngx");
                em.persist(appt1);

                Appointment appt2 = new Appointment();
                appt2.setPatient(bnn);
                appt2.setDoctor(bsA);
                appt2.setDepartment(noiTongQuat);
                appt2.setStartTime(LocalDate.now().atTime(10, 0));
                appt2.setStatus(AppointmentStatus.CONFIRMED);
                appt2.setReason("Đau bụng n");
                em.persist(appt2);

                Appointment appt3 = new Appointment();
                appt3.setPatient(bna);
                appt3.setDoctor(bsA);
                appt3.setDepartment(noiTongQuat);
                appt3.setStartTime(LocalDate.now().atTime(10, 0));
                appt3.setStatus(AppointmentStatus.CONFIRMED);
                appt3.setReason("Đau bụngd");
                em.persist(appt3);

                System.out.println("✅ Appointments seeded.");

            } else {
                System.out.println("🔹 Database already has data. Skipping seeding.");
            }

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



