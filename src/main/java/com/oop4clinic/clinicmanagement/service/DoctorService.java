package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import java.util.*;

public interface DoctorService {
    DoctorDTO create(DoctorDTO dto);
    List<DoctorDTO> findAll();
    DoctorDTO findById(int id);
    DoctorDTO update(DoctorDTO dto);
}
