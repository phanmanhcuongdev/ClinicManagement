package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.AppointmentRepository;
import com.oop4clinic.clinicmanagement.dao.DepartmentRepository;
import com.oop4clinic.clinicmanagement.dao.DoctorRepository;
import com.oop4clinic.clinicmanagement.dao.PatientRepository;
import com.oop4clinic.clinicmanagement.dao.impl.AppointmentRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.DepartmentRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.PatientRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.CreateAppointmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.model.mapper.AppointmentMapper;
import com.oop4clinic.clinicmanagement.service.AppointmentService;

// MỚI: Thêm các import này
import com.oop4clinic.clinicmanagement.dao.InvoiceRepository;
import com.oop4clinic.clinicmanagement.dao.impl.InvoiceRepositoryImpl;
import com.oop4clinic.clinicmanagement.model.entity.Invoice;
import com.oop4clinic.clinicmanagement.model.enums.InvoiceStatus;

// XÓA: Import InvoiceService
// import com.oop4clinic.clinicmanagement.service.InvoiceService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepo = new AppointmentRepositoryImpl();
    private final PatientRepository patientRepo = new PatientRepositoryImpl();
    private final DoctorRepository doctorRepo = new DoctorRepositoryImpl();
    private final DepartmentRepository deptRepo = new DepartmentRepositoryImpl();
    private final AppointmentMapper mapperAp = new AppointmentMapper();

    // SỬA: Thay InvoiceService bằng InvoiceRepository
    // XÓA: private final InvoiceService invoiceService = new InvoiceServiceImpl();
    private final InvoiceRepository invoiceRepo = new InvoiceRepositoryImpl(); // MỚI

    // ... (Các hàm find, search, updateStatus, getAll giữ nguyên) ...
    @Override
    public List<AppointmentDTO> findAppointmentsByPatientId(Integer patientId) {
        // ... (Giữ nguyên code của bạn)
        EntityManager em = EntityManagerProvider.em();
        try {
            List<Appointment> appointments = appointmentRepo.findByPatientId(em, patientId);
            return mapperAp.toDtoList(appointments);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }
    @Override
    public List<AppointmentDTO> searchAppointmentsByPatient(
            Integer patientId,
            String doctorName,
            AppointmentStatus status,
            LocalDate date) {
        // ... (Giữ nguyên code của bạn)
        EntityManager em = EntityManagerProvider.em();
        try {
            List<Appointment> appointments = appointmentRepo.searchByPatient(
                    em, patientId, doctorName, status, date
            );
            return mapperAp.toDtoList(appointments);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }
    @Override
    public AppointmentDTO updateStatus(Integer appointmentId, AppointmentStatus newStatus) {
        // ... (Giữ nguyên code của bạn)
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Appointment appt = em.find(Appointment.class, appointmentId);
            if (appt == null) {
                throw new IllegalArgumentException("Lịch hẹn không tồn tại");
            }
            appt.setStatus(newStatus);
            Appointment updatedAppt = appointmentRepo.save(em, appt);
            tx.commit();
            em.refresh(updatedAppt);
            return mapperAp.toDto(updatedAppt);
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    @Override
    public List<AppointmentDTO> getAll(){
        // ... (Giữ nguyên code của bạn)
        EntityManager em = EntityManagerProvider.em();
        try {
            return mapperAp.toDtoList(appointmentRepo.findAll(em));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            em.close();
        }
    }

    // ===== HÀM ĐÃ ĐƯỢC SỬA LỖI =====
    @Override
    public void createAppointment(CreateAppointmentDTO dto) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin(); // Bắt đầu Giao dịch TỔNG

            // === 1. VALIDATE DỮ LIỆU ===
            if (dto.getPatientId() == null) throw new IllegalArgumentException("Bệnh nhân rỗng");
            if (dto.getDoctorId() == null) throw new IllegalArgumentException("Bác sĩ rỗng");
            // ... (các kiểm tra khác)

            Patient patient = patientRepo.findById(em, dto.getPatientId())
                    .orElseThrow(() -> new IllegalArgumentException("Bệnh nhân không tồn tại"));

            Doctor doctor = doctorRepo.findById(em, dto.getDoctorId());
            if (doctor == null) throw new IllegalArgumentException("Bác sĩ không tồn tại");

            Department department = deptRepo.findById(em, dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Khoa không tồn tại"));

            // === 2. TẠO VÀ LƯU APPOINTMENT ===
            Appointment appt = new Appointment();
            appt.setPatient(patient);
            appt.setDoctor(doctor);
            appt.setDepartment(department);
            appt.setReason(dto.getReason());
            appt.setStatus(AppointmentStatus.PENDING);
            LocalDateTime startTime = LocalDateTime.of(dto.getAppointmentDate(), dto.getAppointmentTime());
            appt.setStartTime(startTime);
            appt.setAppointment_date(dto.getAppointmentDate());

            // Lưu Appointment VÀO GIAO DỊCH
            Appointment savedAppt = appointmentRepo.save(em, appt);

            // === 3. TẠO VÀ LƯU INVOICE (TRONG CÙNG GIAO DỊCH) ===
            // XÓA DÒNG LỖI: invoiceService.createInvoiceForAppointment(savedAppt.getId());

            // THAY BẰNG:
            Invoice invoice = new Invoice();
            invoice.setAppointment(savedAppt); // Dùng đối tượng vừa lưu
            invoice.setPatient(patient);     // Dùng đối tượng đã tìm
            invoice.setTotal(doctor.getConsultationFee()); // Dùng đối tượng đã tìm
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoice.setDetails("Phí đặt lịch hẹn trước");
            // @CreationTimestamp sẽ tự lo 'createdAt'

            // Lưu Invoice VÀO CÙNG GIAO DỊCH
            invoiceRepo.save(em, invoice);

            // === 4. COMMIT ===
            // Chỉ khi cả 2 (Appt và Invoice) lưu OK thì mới commit
            tx.commit();

            em.refresh(savedAppt);
            mapperAp.toDto(savedAppt);

        } catch (RuntimeException e) {
            // Nếu 1 trong 2 thất bại, rollback tất cả
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            // Giữ lại catch block của bạn
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}