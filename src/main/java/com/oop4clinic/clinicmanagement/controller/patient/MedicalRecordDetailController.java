package com.oop4clinic.clinicmanagement.controller.patient;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO; // Import DTO
import com.oop4clinic.clinicmanagement.service.MedicalRecordService; // Import Service
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl; // Import Service Impl
import com.oop4clinic.clinicmanagement.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.time.format.DateTimeFormatter; // Để định dạng LocalDateTime

public class MedicalRecordDetailController {
    @FXML private TextField dateRecord;
    @FXML private TextField doctorRecord;
    @FXML private TextField departmentRecord;
    @FXML private TextArea diagnosisRecord;
    @FXML private TextArea symptomsRecord;
    @FXML private TextArea prescriptionRecord;
    @FXML private TextArea notesRecord;
    @FXML private Button homeButton;

    private final MedicalRecordService medicalRecordService = new MedicalRecordServiceImpl();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


    public void loadRecordDetails(int recordId) {
        MedicalRecordDTO record = medicalRecordService.getById(recordId);

        if (record != null) {
            dateRecord.setText(record.getCreatedAt() != null
                    ? record.getCreatedAt().format(DATE_FORMATTER)
                    : "N/A");

            doctorRecord.setText(record.getDoctorName());
            departmentRecord.setText(record.getDepartmentName());
            diagnosisRecord.setText(record.getDiagnosis());
            symptomsRecord.setText(record.getSymptoms());
            prescriptionRecord.setText(record.getPrescription());
            notesRecord.setText(record.getNotes());

            setFieldsEditable(false);
        } else {
            diagnosisRecord.setText("Không tìm thấy thông tin cho hồ sơ với ID: " + recordId);
            setFieldsEditable(false);
        }
    }

    private void setFieldsEditable(boolean isEditable) {
        dateRecord.setEditable(isEditable);
        doctorRecord.setEditable(isEditable);
        departmentRecord.setEditable(isEditable);
        diagnosisRecord.setEditable(isEditable);
        symptomsRecord.setEditable(isEditable);
        prescriptionRecord.setEditable(isEditable);
        notesRecord.setEditable(isEditable);
    }

    // chuyen trang
    @FXML void handleInfo(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InfoPatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleRecord(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MedicalRecord.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleBill(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InvoicePatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleAppointment(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/AppointmentPatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml"));
        UserSession.clear();
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
}