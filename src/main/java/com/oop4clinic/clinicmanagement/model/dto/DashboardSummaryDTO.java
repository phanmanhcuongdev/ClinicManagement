package com.oop4clinic.clinicmanagement.model.dto;

import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.entity.Patient;

import java.util.List;
import java.util.Map;

/**
 * Gom toàn bộ dữ liệu dashboard admin để controller lấy 1 lần.
 */
public class DashboardSummaryDTO {

    // ===== KPI top cards =====
    private long totalPatients;
    private long totalDoctors;
    private long activeDoctors;
    private long appointmentsToday;

    private double revenueToday;
    private double revenueThisWeek;
    private double revenueThisMonth;

    // ===== Chart: visits last 7 days =====
    // key = yyyy-MM-dd, value = số lịch hẹn/người khám trong ngày đó
    private Map<String, Long> visitCountsLast7Days;

    // ===== Bảng "Bệnh nhân mới" =====
    private List<Patient> recentPatients;

    // ===== "Lịch hẹn sắp tới" =====
    private List<Appointment> upcomingAppointments;

    // ---------- getters / setters ----------

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public long getActiveDoctors() {
        return activeDoctors;
    }

    public void setActiveDoctors(long activeDoctors) {
        this.activeDoctors = activeDoctors;
    }

    public long getAppointmentsToday() {
        return appointmentsToday;
    }

    public void setAppointmentsToday(long appointmentsToday) {
        this.appointmentsToday = appointmentsToday;
    }

    public double getRevenueToday() {
        return revenueToday;
    }

    public void setRevenueToday(double revenueToday) {
        this.revenueToday = revenueToday;
    }

    public double getRevenueThisWeek() {
        return revenueThisWeek;
    }

    public void setRevenueThisWeek(double revenueThisWeek) {
        this.revenueThisWeek = revenueThisWeek;
    }

    public double getRevenueThisMonth() {
        return revenueThisMonth;
    }

    public void setRevenueThisMonth(double revenueThisMonth) {
        this.revenueThisMonth = revenueThisMonth;
    }

    public Map<String, Long> getVisitCountsLast7Days() {
        return visitCountsLast7Days;
    }

    public void setVisitCountsLast7Days(Map<String, Long> visitCountsLast7Days) {
        this.visitCountsLast7Days = visitCountsLast7Days;
    }

    public List<Patient> getRecentPatients() {
        return recentPatients;
    }

    public void setRecentPatients(List<Patient> recentPatients) {
        this.recentPatients = recentPatients;
    }

    public List<Appointment> getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void setUpcomingAppointments(List<Appointment> upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;
    }
}
