package com.oop4clinic.clinicmanagement;

import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuPatient.fxml")
                 getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml")
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

    public static void main(String[] args) {
        launch(args);
    }

}



