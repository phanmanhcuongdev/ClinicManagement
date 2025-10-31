package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;

import java.util.List;

public interface AppointmentService {
    List<AppointmentDTO> getAll();
}
