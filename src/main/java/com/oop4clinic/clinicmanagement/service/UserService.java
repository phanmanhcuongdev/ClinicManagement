package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.UserDTO;
import com.oop4clinic.clinicmanagement.model.entity.User;

public interface UserService {
    User login(String username, String password) throws Exception;
    boolean register(String username, String pass, String confirmpass) throws Exception;
    UserDTO createOrResetDoctorAccount(Integer doctorId) throws Exception;
}
