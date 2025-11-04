package com.oop4clinic.clinicmanagement.util;

import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.entity.User;

// quản lý thông tin người dùng đăng nhập
public class UserSession {
    private static User currentUser;
    private static PatientDTO currentPatient;

    private static Integer currentId;
    private static String currentPhone;

    // người dùng là admin, doctor
    public static void setCurrentUser(User user) {
        currentUser = user;
        currentPatient = null;
        currentId = (user != null) ? user.getId() : null;
        currentPhone = (user != null) ? user.getUsername() : null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    // người dùng đăng nhập là bệnh nhân
    public static void setCurrentPatient(PatientDTO patient) {
        currentPatient = patient;
        currentUser = null;
        currentId = (patient != null) ? patient.getId() : null;
        currentPhone = (patient != null) ? patient.getPhone() : null;
    }

    public static PatientDTO getCurrentPatient() {
        return currentPatient;
    }

    // phần thông tin chung cho admin, patient (sdt, id)
    public static Integer getCurrentId() {
        return currentId;
    }

    public static String getCurrentPhone() {
        return currentPhone;
    }

    public static boolean isLoggedIn() {
        return currentUser != null || currentPatient != null;
    }

    public static void clear() {
        currentUser = null;
        currentPatient = null;
        currentId = null;
        currentPhone = null;
    }
}
