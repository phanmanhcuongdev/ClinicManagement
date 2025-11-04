package com.oop4clinic.clinicmanagement.util;

public final class SessionManager {
    private SessionManager(){}
    private static Integer loggedUser;
    private static String loggedPhoneNumber;

    public static void login(Integer curId, String phoneNumber) {
        loggedUser = curId;
        loggedPhoneNumber = phoneNumber;
    }

    public static int getLoggedUser() {
        return loggedUser;
    }

    public static String getLoggedPhoneNumber() {
        return loggedPhoneNumber;
    }

    public static boolean isLoggedIn() {
        return loggedUser != null;
    }

    public static void logout() {
        loggedUser = null;
        loggedPhoneNumber = null;
    }
}
