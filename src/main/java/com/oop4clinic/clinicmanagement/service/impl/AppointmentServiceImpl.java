package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.impl.AppointmentRepositoryImpl;
import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.mapper.AppointmentMapper;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import com.oop4clinic.clinicmanagement.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;

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
}
