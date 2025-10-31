package com.oop4clinic.clinicmanagement.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ValidationUtils {
    private ValidationUtils() {}

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public static boolean isValidPhone(String p) {
        return p != null && p.matches("\\d{10,11}");
    }

    public static boolean isValidEmail(String e) {
        return e != null && e.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isValidDob(LocalDate dob) {
        return dob != null && !dob.isAfter(LocalDate.now());
    }

    /** null hoặc số >= 0; ném NumberFormatException nếu không hợp lệ */
    public static Double parseFee(String s) {
        if (s == null || s.isBlank()) return null;
        double v = Double.parseDouble(s.trim());
        if (v < 0) throw new NumberFormatException("negative");
        return v;
    }

    public static boolean isPositiveNumber(String s){
        if (s == null) return false;
        try {
            return Double.parseDouble(s.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatTime(LocalDateTime T){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return T.format(formatter);
    }
}
