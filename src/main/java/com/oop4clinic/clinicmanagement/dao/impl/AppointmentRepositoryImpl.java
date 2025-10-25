package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.AppointmentRepository;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import jakarta.persistence.EntityManager;

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

    @Override
    public List<Appointment> findByStartTimeRange(EntityManager em,
                                                  LocalDateTime from,
                                                  LocalDateTime to) {

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
    public List<Appointment> findUpcoming(EntityManager em,
                                          LocalDateTime after,
                                          int limit) {

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
    public long countInRange(EntityManager em,
                             LocalDateTime from,
                             LocalDateTime to) {

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
}
