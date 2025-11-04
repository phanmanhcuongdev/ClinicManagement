package com.oop4clinic.clinicmanagement.util;

public class SessionManager {
    private static Integer loggedPatientId;
    private static String loggedPhoneNumber;

    public static void login(Integer patientId, String phoneNumber) {
        loggedPatientId = patientId;
        loggedPhoneNumber = phoneNumber;
    }

    public static Integer getLoggedPatientId() {
        return loggedPatientId;
    }

    public static String getLoggedPhoneNumber() {
        return loggedPhoneNumber;
    }

    public static boolean isLoggedIn() {
        return loggedPatientId != null;
    }

    public static void logout() {
        loggedPatientId = null;
        loggedPhoneNumber = null;
    }
}
