package com.oop4clinic.clinicmanagement.services;

import com.oop4clinic.clinicmanagement.dao.AppointmentDAO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AppointmentService {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public List<Appointment> getAll(){
        EntityManager em = EntityManagerProvider.em();
        try {
            return appointmentDAO.findAll(em);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            em.close();
        }
    }
}
