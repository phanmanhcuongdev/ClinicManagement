package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.*;
import com.oop4clinic.clinicmanagement.dao.impl.*;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.CreateAppointmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.*;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.model.enums.InvoiceStatus;
import com.oop4clinic.clinicmanagement.model.mapper.AppointmentMapper;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepo = new AppointmentRepositoryImpl();
    private final PatientRepository patientRepo = new PatientRepositoryImpl();
    private final DoctorRepository doctorRepo = new DoctorRepositoryImpl();
    private final DepartmentRepository deptRepo = new DepartmentRepositoryImpl();
    private final InvoiceRepository invoiceRepo = new InvoiceRepositoryImpl();

    @Override
    public List<AppointmentDTO> findAppointmentsByPatientId(Integer patientId) {
        EntityManager em = EntityManagerProvider.em();
        try {
            List<Appointment> appointments = appointmentRepo.findByPatientId(em, patientId);
            return AppointmentMapper.toDtoList(appointments);
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
        EntityManager em = EntityManagerProvider.em();
        try {
            List<Appointment> appointments = appointmentRepo.searchByPatient(
                    em, patientId, doctorName, status, date
            );
            return AppointmentMapper.toDtoList(appointments);
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
            return AppointmentMapper.toDto(updatedAppt);
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
            return AppointmentMapper.toDtoList(appointmentRepo.findAll(em));
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
            Patient patient = patientRepo.findById(em, dto.getPatientId())
                    .orElseThrow(() -> new IllegalArgumentException("Bệnh nhân không tồn tại"));

            Doctor doctor = doctorRepo.findById(em, dto.getDoctorId());
            if (doctor == null) throw new IllegalArgumentException("Bác sĩ không tồn tại");

            Department department = deptRepo.findById(em, dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Khoa không tồn tại"));

            Appointment appt = new Appointment();
            appt.setPatient(patient);
            appt.setDoctor(doctor);
            appt.setDepartment(department);
            appt.setReason(dto.getReason());
            appt.setStatus(AppointmentStatus.CONFIRMED);
            LocalDateTime startTime = LocalDateTime.of(dto.getAppointmentDate(), dto.getAppointmentTime());
            appt.setStartTime(startTime);
            appt.setAppointment_date(dto.getAppointmentDate());

            Appointment savedAppt = appointmentRepo.save(em, appt);

            Invoice invoice = new Invoice();
            invoice.setAppointment(savedAppt);
            invoice.setPatient(patient);
            invoice.setTotal(doctor.getConsultationFee());
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoice.setDetails("Phí đặt lịch hẹn trước");

            invoiceRepo.save(em, invoice);
            tx.commit();

            em.refresh(savedAppt);
            AppointmentMapper.toDto(savedAppt);

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<AppointmentDTO> getAppointmentsForToday(int doctorId) {
        EntityManager em = EntityManagerProvider.em();
        try {
//            thoi gian tu 0-23h
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(23, 59, 59);

            // 2. Tạo TypedQuery
            TypedQuery<Appointment> query = em.createQuery(
                    "SELECT a FROM Appointment a " +
                            "WHERE a.doctor.id = :doctorId " +
                            "AND a.startTime >= :startOfDay " +
                            "AND a.startTime <= :endOfDay " +
                            "ORDER BY a.startTime ASC",
                    Appointment.class
            );
            query.setParameter("doctorId", doctorId);
            query.setParameter("startOfDay", startOfDay);
            query.setParameter("endOfDay", endOfDay);

            List<Appointment> entities = query.getResultList();
            System.out.println("DEBUG: Found " + entities.size() + " appointments for doctor " + doctorId + " today.");
            return entities.stream().map(AppointmentMapper :: toDto).toList();
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            throw ex;
        }
        finally {
            em.close();
        }
    }
    @Override
    public List<AppointmentDTO> getAppointmentsForDoctorByDate(int doctorId, LocalDate date) {
        EntityManager em = EntityManagerProvider.em();
        try {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            TypedQuery<Appointment> query = em.createQuery(
                    "SELECT a FROM Appointment a " +
                            "WHERE a.doctor.id = :doctorId " +
                            "AND a.startTime >= :startOfDay " +
                            "AND a.startTime <= :endOfDay " +
                            "ORDER BY a.startTime ASC",
                    Appointment.class
            );
            query.setParameter("doctorId", doctorId);
            query.setParameter("startOfDay", startOfDay);
            query.setParameter("endOfDay", endOfDay);

            List<Appointment> entities = query.getResultList();
            System.out.println("DEBUG: Found " + entities.size() + " appointments for doctor " + doctorId + " on " + date);
            return entities.stream().map(AppointmentMapper :: toDto).toList();
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            throw ex;
        }
        finally {
            em.close();
        }
    }
    @Override
    public List<AppointmentDTO> getAllAppointmentsForDoctor(int doctorId) {
        EntityManager em = EntityManagerProvider.em();
        try {
            List<Appointment> entities = appointmentRepo.findAllByDoctorId(em, doctorId);
            return AppointmentMapper.toDtoList(entities);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    @Override
    public void completeAppointment(int appointmentId) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Appointment appt = em.find(Appointment.class, appointmentId);
            if (appt != null) {
                if(appt.getStatus() != AppointmentStatus.COMPLETED) {
                    appt.setStatus(AppointmentStatus.COMPLETED);
                    em.merge(appt);
                }
            } else {
                throw new IllegalArgumentException("Không tìm thấy lịch hẹn để hoàn tất.");
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}