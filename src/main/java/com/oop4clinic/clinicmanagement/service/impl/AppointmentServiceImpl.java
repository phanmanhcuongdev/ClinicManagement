package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.impl.AppointmentRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.model.mapper.AppointmentMapper;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentRepositoryImpl appointmentDAO = new AppointmentRepositoryImpl();
    private AppointmentMapper mapperAp = new AppointmentMapper();

    @Override
    public List<AppointmentDTO> getAll(){
        EntityManager em = EntityManagerProvider.em();
        try {
            return mapperAp.toDtoList(appointmentDAO.findAll(em));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
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
            List<Appointment> entities = appointmentDAO.findAllByDoctorId(em, doctorId);
            return mapperAp.toDtoList(entities);
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
