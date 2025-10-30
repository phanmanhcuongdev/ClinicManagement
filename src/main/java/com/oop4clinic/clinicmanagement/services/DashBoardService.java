package com.oop4clinic.clinicmanagement.services;

import com.oop4clinic.clinicmanagement.dao.AppointmentDAO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.oop4clinic.clinicmanagement.util.ValidationUtils.*;

public class DashBoardService {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public int countPaitent(){
        EntityManager em = EntityManagerProvider.em();
        try{
            return em.createQuery("Select count(p) from Patient p",Long.class)
                    .getSingleResult().intValue();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public int countDoctor(){
        EntityManager em = EntityManagerProvider.em();
        try{
            return em.createQuery("Select count(d) from Doctor d",Long.class)
                    .getSingleResult().intValue();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public int countCompletedAP(){
        EntityManager em = EntityManagerProvider.em();
        try{
            return appointmentDAO.findAllbyStatus(em,AppointmentStatus.CONFIRMED).size();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public int countPendingAP(){
        EntityManager em = EntityManagerProvider.em();
        try{
            return appointmentDAO.findAllbyStatus(em,AppointmentStatus.PENDING).size();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public List<Appointment> getUpcomingAppointments() {
        EntityManager em = EntityManagerProvider.em();
        try {
            List<Appointment> allApptConfirmed = appointmentDAO.findAllbyStatus(em,AppointmentStatus.CONFIRMED);
            LocalDateTime now = LocalDateTime.now();

            List<Appointment> upcommingList = allApptConfirmed.stream()
                    .filter(a -> a.getStartTime().isAfter(now))
                    .collect(Collectors.toList());
            return upcommingList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<Appointment> getWeeklyAppointments(LocalDateTime start, LocalDateTime end) {
        EntityManager em = EntityManagerProvider.em();
        try {
            Query query = em.createNativeQuery("""
                SELECT * FROM appointments
                WHERE status = ?1 
                  AND datetime(start_time) BETWEEN datetime(?2) and datetime(?3) 
                ORDER BY datetime(start_time) ASC
            """, Appointment.class);
            query.setParameter(1, AppointmentStatus.CONFIRMED.name());
            query.setParameter(2, formatTime(start)); // Truyền chuỗi đã định dạng
            query.setParameter(3, formatTime(end));
            List<Appointment> list = query.getResultList();
            return list;
        } finally {
            em.close();
        }
    }


}