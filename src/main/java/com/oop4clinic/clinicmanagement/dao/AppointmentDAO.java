package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentDAO {
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

    public List<Appointment> findAll(EntityManager em){
        String jpql = """
            SELECT a FROM Appointment a 
            JOIN FETCH a.patient        
            JOIN FETCH a.doctor
            JOIN FETCH a.department        
        """;
        TypedQuery<Appointment> query = em.createQuery(jpql, Appointment.class);
        System.out.printf("Co tung nay : %d",query.getResultList().size());
        return query.getResultList();
    }
}


