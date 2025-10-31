package com.oop4clinic.clinicmanagement.model.mapper;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentMapper {

    public AppointmentDTO toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setStartTime(appointment.getStartTime());
        dto.setStatus(appointment.getStatus());
        dto.setReason(appointment.getReason());

        if (appointment.getPatient() != null) {
            dto.setPatientId(appointment.getPatient().getId());
            dto.setPatientName(appointment.getPatient().getFullName());
        }

        if (appointment.getDoctor() != null) {
            dto.setDoctorId(appointment.getDoctor().getId());
            dto.setDoctorName(appointment.getDoctor().getFullName());
        }

        if (appointment.getDepartment() != null) {
            dto.setDepartmentId(appointment.getDepartment().getId());
            dto.setDepartmentName(appointment.getDepartment().getName());
        }

        return dto;
    }

    public List<AppointmentDTO> toDtoList(List<Appointment> appointments) {
        if (appointments == null) {
            return null;
        }
        return appointments.stream()
                           .map(this::toDto)
                           .collect(Collectors.toList());
    }
}
