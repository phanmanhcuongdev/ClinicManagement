package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.AppointmentRepository;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentRepositoryImpl implements AppointmentRepository {

    @Override
    public Appointment save(EntityManager em, Appointment appt) {
        if (appt.getId() == null) {
            em.persist(appt);
            return appt;
        }
        return em.merge(appt);
    }

    // hien lich hen cua nguoi dung dang nhap
    @Override
    public List<Appointment> findByPatientId(EntityManager em, Integer patientId) {
        return em.createQuery(
                        """
                        select a
                        from Appointment a
                        join fetch a.patient p
                        left join fetch a.doctor d
                        left join fetch a.department dept
                        where p.id = :patientId
                        order by a.startTime asc
                        """,
                        Appointment.class
                )
                .setParameter("patientId", patientId)
                .getResultList();
    }

    // tim kiem bac si hien lich hen
    @Override
    public List<Appointment> searchByPatient(EntityManager em, Integer patientId, String doctorName, AppointmentStatus status, LocalDate date) {

        StringBuilder jpql = new StringBuilder("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient p
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH a.department dept
            WHERE p.id = :patientId
        """);

        if (doctorName != null && !doctorName.isEmpty()) {
            jpql.append(" AND LOWER(d.fullName) LIKE LOWER(:doctorName)");
        }
        if (status != null) {
            jpql.append(" AND a.status = :status");
        }
        if (date != null) {
            jpql.append(" AND a.appointment_date = :date");
        }
        jpql.append(" ORDER BY a.startTime ASC");
        TypedQuery<Appointment> query = em.createQuery(jpql.toString(), Appointment.class);
        query.setParameter("patientId", patientId);

        if (doctorName != null && !doctorName.isEmpty()) {
            query.setParameter("doctorName", "%" + doctorName + "%");
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        if (date != null) {
            query.setParameter("date", date);
        }
        return query.getResultList();
    }

    @Override
    public List<Appointment> findAllByDoctorId(EntityManager em, int doctorId) {
        String jpql = """
                SELECT a FROM Appointment a 
                JOIN FETCH a.patient        
                WHERE a.doctor.id = :doctorId   
                ORDER BY a.startTime DESC    
            """;
        TypedQuery<Appointment> query = em.createQuery(jpql, Appointment.class);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    // tim kiem gio trong cua bac si
    @Override
    public List<Appointment> findByStartTimeRange(EntityManager em, LocalDateTime from, LocalDateTime to) {

        return em.createQuery(
                """
                select a
                from Appointment a
                join fetch a.patient p
                left join fetch a.doctor d
                left join fetch a.department dept
                where a.startTime between :from and :to
                order by a.startTime asc
                """,
                Appointment.class
        )
        .setParameter("from", from)
        .setParameter("to",   to)
        .getResultList();
    }

    @Override
    public List<Appointment> findUpcoming(EntityManager em, LocalDateTime after, int limit) {
        return em.createQuery(
                """
                select a
                from Appointment a
                join fetch a.patient p
                left join fetch a.doctor d
                left join fetch a.department dept
                where a.startTime > :after
                order by a.startTime asc
                """,
                Appointment.class
        )
        .setParameter("after", after)
        .setMaxResults(limit)
        .getResultList();
    }

    @Override
    public long countInRange(EntityManager em, LocalDateTime from, LocalDateTime to) {

        return em.createQuery(
                """
                select count(a)
                from Appointment a
                where a.startTime between :from and :to
                """,
                Long.class
        )
        .setParameter("from", from)
        .setParameter("to",   to)
        .getSingleResult();
    }

    // hien tat ca trang thai
    @Override
    public List<Appointment> findAllbyStatus(EntityManager em, AppointmentStatus status){
        String jpql = """
                SELECT a FROM Appointment a 
                JOIN FETCH a.patient        
                JOIN FETCH a.doctor         
                WHERE a.status = :status   
                ORDER BY a.startTime ASC    
            """;
        TypedQuery<Appointment> query = em.createQuery(jpql, Appointment.class);
        query.setParameter("status", status);
        List<Appointment> list = query.getResultList();
        return list;
    }

    @Override
    public List<Appointment> findAll(EntityManager em){
        String jpql = """
                SELECT a FROM Appointment a 
                JOIN FETCH a.patient        
                JOIN FETCH a.doctor
                JOIN FETCH a.department
                ORDER BY a.startTime ASC        
            """;
        TypedQuery<Appointment> query = em.createQuery(jpql, Appointment.class);
        return query.getResultList();
    }

}
