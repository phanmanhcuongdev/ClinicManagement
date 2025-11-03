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

    @Override
    public List<AppointmentDTO> findAppointmentsByPatientId(Integer patientId) {
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

    // hien ds lich hen cua benh nhan dang nhap
    @Override
    public List<AppointmentDTO> searchAppointmentsByPatient(
            Integer patientId,
            String doctorName,
            AppointmentStatus status,
            LocalDate date) {

        EntityManager em = EntityManagerProvider.em();
        try {
            // Gọi phương thức Repository đã được thêm vào
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

    // tao moi lich hen tu booking
    @Override
    public void createAppointment(CreateAppointmentDTO dto) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (dto.getPatientId() == null) throw new IllegalArgumentException("Bệnh nhân rỗng");
            if (dto.getDoctorId() == null) throw new IllegalArgumentException("Bác sĩ rỗng");
            if (dto.getDepartmentId() == null) throw new IllegalArgumentException("Khoa rỗng");
            if (dto.getAppointmentDate() == null) throw new IllegalArgumentException("Ngày hẹn rỗng");
            if (dto.getAppointmentTime() == null) throw new IllegalArgumentException("Giờ hẹn rỗng");

            Patient patient = patientRepo.findById(em, dto.getPatientId())
                    .orElseThrow(() -> new IllegalArgumentException("Bệnh nhân không tồn tại"));

            Doctor doctor = doctorRepo.findById(em, dto.getDoctorId());
            if (doctor == null) {
                throw new IllegalArgumentException("Bác sĩ không tồn tại");
            }

            Department department = deptRepo.findById(em, dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Khoa không tồn tại"));

            // gan du lieu tu DTO vao doi tuong
            Appointment appt = new Appointment();
            appt.setPatient(patient);
            appt.setDoctor(doctor);
            appt.setDepartment(department);
            appt.setReason(dto.getReason());
            appt.setStatus(AppointmentStatus.PENDING);

            LocalDateTime startTime = LocalDateTime.of(dto.getAppointmentDate(), dto.getAppointmentTime());
            appt.setStartTime(startTime);
            appt.setAppointment_date(dto.getAppointmentDate());

            // luu lich hen vao db
            Appointment savedAppt = appointmentRepo.save(em, appt);
            tx.commit();
            em.refresh(savedAppt);
            mapperAp.toDto(savedAppt);

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}