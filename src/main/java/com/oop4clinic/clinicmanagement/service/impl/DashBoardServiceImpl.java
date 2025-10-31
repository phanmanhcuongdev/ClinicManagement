package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.impl.AppointmentRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.PatientRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.model.mapper.AppointmentMapper;
import com.oop4clinic.clinicmanagement.service.DashBoradService;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DashBoardServiceImpl implements DashBoradService {
    private AppointmentRepositoryImpl appointmentDAO = new AppointmentRepositoryImpl();
    private AppointmentMapper mapper = new AppointmentMapper();
    private DoctorRepositoryImpl doctorDAO = new DoctorRepositoryImpl();
    private PatientRepositoryImpl patientDAO = new PatientRepositoryImpl();

    public long countPaitent(){
        EntityManager em = EntityManagerProvider.em();
        try{
            return patientDAO.countAll(em);
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public long countDoctor(){
        EntityManager em = EntityManagerProvider.em();
        try{
            return doctorDAO.countAll(em);
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public long countCompletedAP(){
        EntityManager em = EntityManagerProvider.em();
        try{
            return appointmentDAO.findAllbyStatus(em,AppointmentStatus.COMPLETED).size();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public long countPendingAP(){
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

    public List<AppointmentDTO> getUpcomingAppointments() {
        EntityManager em = EntityManagerProvider.em();
        try {
            List<Appointment> allApptConfirmed = appointmentDAO.findAllbyStatus(em,AppointmentStatus.CONFIRMED);
            LocalDateTime now = LocalDateTime.now();

            List<Appointment> upcommingList = allApptConfirmed.stream()
                    .filter(a -> a.getStartTime().isAfter(now))
                    .collect(Collectors.toList());
            return mapper.toDtoList(upcommingList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<AppointmentDTO> getWeeklyAppointments(LocalDateTime start, LocalDateTime end) {
        EntityManager em = EntityManagerProvider.em();
        try {
            return mapper.toDtoList(appointmentDAO.findByStartTimeRange(em,start,end));
        } finally {
            em.close();
        }
    }
}