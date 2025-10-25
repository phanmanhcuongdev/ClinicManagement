package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.*;
import com.oop4clinic.clinicmanagement.dao.impl.*;
import com.oop4clinic.clinicmanagement.model.dto.DashboardSummaryDTO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.service.AdminDashboardService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final PatientRepository patientRepo          = new PatientRepositoryImpl();
    private final DoctorRepository doctorRepo            = new DoctorRepositoryImpl();
    private final AppointmentRepository appointmentRepo  = new AppointmentRepositoryImpl();
    private final InvoiceRepository invoiceRepo          = new InvoiceRepositoryImpl();

    /**
     * TODO: cập nhật cho đúng util của bạn để lấy EntityManager.
     * Ví dụ:
     *
     * private EntityManager getEntityManager() {
     *     return JpaUtil.getEntityManager();
     * }
     */
    private EntityManager getEntityManager() {
        return null; // <-- CHỈ TẠM, bạn phải thay
    }

    @Override
    public DashboardSummaryDTO loadDashboardData() {

        EntityManager em = getEntityManager();
        if (em == null) {
            // để tránh NPE nếu bạn quên set JpaUtil
            return new DashboardSummaryDTO();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            if (!tx.isActive()) {
                tx.begin();
            }

            DashboardSummaryDTO dto = new DashboardSummaryDTO();

            // ====== 1. KPI cơ bản ======
            long totalPatients = patientRepo.countAll(em);
            long totalDoctors  = doctorRepo.countAll(em);
            long activeDoctors = doctorRepo.countByStatus(em, DoctorStatus.ACTIVE);

            LocalDate today = LocalDate.now();
            LocalDateTime todayStart = today.atStartOfDay();
            LocalDateTime todayEnd   = today.plusDays(1).atStartOfDay().minusNanos(1);

            long appointmentsToday = appointmentRepo.countInRange(em, todayStart, todayEnd);

            // ====== 2. Doanh thu ======
            double revenueToday = invoiceRepo.sumByCreatedAtBetween(em, todayStart, todayEnd);

            // tuần này (Monday -> Sunday)
            LocalDate monday = today.minusDays((today.getDayOfWeek().getValue() + 6) % 7);
            LocalDate sunday = monday.plusDays(6);

            LocalDateTime weekStart = monday.atStartOfDay();
            LocalDateTime weekEnd   = sunday.plusDays(1).atStartOfDay().minusNanos(1);

            double revenueWeek = invoiceRepo.sumByCreatedAtBetween(em, weekStart, weekEnd);

            // tháng này
            LocalDate firstDayOfMonth = today.withDayOfMonth(1);
            LocalDate lastDayOfMonth  = today.with(TemporalAdjusters.lastDayOfMonth());

            LocalDateTime monthStart = firstDayOfMonth.atStartOfDay();
            LocalDateTime monthEnd   = lastDayOfMonth.plusDays(1).atStartOfDay().minusNanos(1);

            double revenueMonth = invoiceRepo.sumByCreatedAtBetween(em, monthStart, monthEnd);

            // ====== 3. Lượt khám 7 ngày gần nhất (bar chart) ======
            Map<String, Long> visitsLast7 = new LinkedHashMap<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate day = today.minusDays(i);
                LocalDateTime dayStart = day.atStartOfDay();
                LocalDateTime dayEnd   = day.plusDays(1).atStartOfDay().minusNanos(1);

                long cnt = appointmentRepo.countInRange(em, dayStart, dayEnd);
                visitsLast7.put(day.toString(), cnt);
            }

            // ====== 4. Bệnh nhân mới nhất ======
            List<Patient> recentPatients = patientRepo.findNewest(em, 5);

            // ====== 5. Lịch hẹn sắp tới ======
            List<Appointment> upcomingAppointments =
                    appointmentRepo.findUpcoming(em, LocalDateTime.now(), 5);

            // ====== 6. Đổ vào DTO ======
            dto.setTotalPatients(totalPatients);
            dto.setTotalDoctors(totalDoctors);
            dto.setActiveDoctors(activeDoctors);
            dto.setAppointmentsToday(appointmentsToday);

            dto.setRevenueToday(revenueToday);
            dto.setRevenueThisWeek(revenueWeek);
            dto.setRevenueThisMonth(revenueMonth);

            dto.setVisitCountsLast7Days(visitsLast7);
            dto.setRecentPatients(recentPatients);
            dto.setUpcomingAppointments(upcomingAppointments);

            // done
            if (tx.isActive()) tx.commit();
            return dto;

        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
