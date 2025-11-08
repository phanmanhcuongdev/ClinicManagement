package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.DoctorRepository;
import com.oop4clinic.clinicmanagement.dao.DoctorScheduleRepository;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorScheduleRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DoctorScheduleDTO;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.entity.DoctorSchedule;
import com.oop4clinic.clinicmanagement.model.enums.DoctorScheduleStatus;
import com.oop4clinic.clinicmanagement.model.mapper.DoctorScheduleMapper;
import com.oop4clinic.clinicmanagement.service.DoctorScheduleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate; // <-- THÊM IMPORT
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // <-- THÊM IMPORT
import java.util.List;
import java.util.stream.Collectors;

public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepo = new DoctorScheduleRepositoryImpl();

    // Định dạng ngày phải khớp với định nghĩa trong Booking2Controller (yyyy-MM-dd)
    private final DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // THÊM CÁC DÒNG NÀY:
    private final DoctorRepository doctorRepo = new DoctorRepositoryImpl();

    // Các mốc thời gian cố định bạn yêu cầu
    private static final List<LocalTime> SLOT_TIMES = List.of(
            LocalTime.of(7, 0),
            LocalTime.of(9, 0),
            LocalTime.of(13, 0),
            LocalTime.of(15, 0)
    );

    @Override
    public List<DoctorScheduleDTO> getAvailableSchedulesByDoctorId(int doctorId) {
        EntityManager em = EntityManagerProvider.em();
        try {
            List<DoctorSchedule> entities = scheduleRepo.findAvailableByDoctorId(em, doctorId);

            return entities.stream()
                    .map(DoctorScheduleMapper::toDTO)
                    .collect(Collectors.toList());

        } finally {
            em.close();
        }
    }

    @Override
    public void updateScheduleStatus(int scheduleId, DoctorScheduleStatus newStatus) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            DoctorSchedule schedule = scheduleRepo.findById(em, scheduleId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch (ID=" + scheduleId + ")"));

            schedule.setStatus(newStatus);
            scheduleRepo.save(em, schedule);

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<DoctorScheduleDTO> getAvailableSchedulesByDeptAndDate(Integer departmentId, String dateStringQuery) {
        if (departmentId == null || dateStringQuery == null) {
            return List.of();
        }

        // 1. CHUYỂN ĐỔI STRING SANG LOCALDATE
        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(dateStringQuery, dbDateFormatter);
        } catch (Exception e) {
            System.err.println("Lỗi định dạng ngày khi parse: " + dateStringQuery);
            return List.of();
        }

        EntityManager em = EntityManagerProvider.em();
        try {
            // 2. TRUYỀN LOCALDATE VÀO REPOSITORY
            List<DoctorSchedule> entities = scheduleRepo.findAvailableByDeptAndDate(
                    em,
                    departmentId,
                    selectedDate // <-- Đã truyền LocalDate
            );

            return entities.stream()
                    .filter(s -> s.getStatus() == DoctorScheduleStatus.AVAILABLE)
                    .map(DoctorScheduleMapper::toDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Lỗi hệ thống khi tìm lịch theo Khoa và Ngày: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    @Override
    public int createSchedulesForDate(LocalDate date) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        int slotsCreated = 0;

        try {
            tx.begin();

            // Bước 1: Lấy tất cả bác sĩ
            List<Doctor> doctors = doctorRepo.findAll(em);

            // Bước 2: Lặp qua từng bác sĩ
            for (Doctor doctor : doctors) {

                // Bước 3: Lặp qua từng mốc thời gian
                for (LocalTime slotTime : SLOT_TIMES) {

                    // Bước 4: Kiểm tra xem lịch đã tồn tại chưa
                    boolean exists = scheduleRepo.existsByDoctorAndWorkDateAndTime(em, doctor, date, slotTime);

                    if (!exists) {
                        // Bước 5: Nếu chưa tồn tại, tạo lịch mới
                        DoctorSchedule newSchedule = new DoctorSchedule();
                        newSchedule.setDoctor(doctor);
                        newSchedule.setWorkDate(date);
                        newSchedule.setStartTime(slotTime);
                        newSchedule.setStatus(DoctorScheduleStatus.AVAILABLE);

                        // Bước 6: Lưu vào DB
                        scheduleRepo.save(em, newSchedule);
                        slotsCreated++;
                    }
                }
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Lỗi khi tạo lịch tự động: " + e.getMessage());
            e.printStackTrace();
            throw e; // Ném lại lỗi để controller xử lý
        } finally {
            em.close();
        }

        return slotsCreated;
    }


    @Override
    public void ensureSchedulesExistForNextDays(int days) {
        LocalDate today = LocalDate.now();

        System.out.println("Đang kiểm tra và đảm bảo lịch tồn tại cho " + days + " ngày tới...");

        for (int i = 0; i < days; i++) {
            LocalDate dateToCheck = today.plusDays(i);
            try {
                // Gọi hàm tạo lịch (hàm này đã có logic "exists",
                // nên nó sẽ chỉ tạo nếu lịch chưa có)
                int slotsCreated = createSchedulesForDate(dateToCheck);

                if (slotsCreated > 0) {
                    System.out.println("Đã tự động tạo " + slotsCreated + " lịch mới cho ngày " + dateToCheck);
                }

            } catch (Exception e) {
                // Nếu có lỗi (ví dụ 1 ngày bị lỗi),
                // ta chỉ log lỗi và tiếp tục cho ngày tiếp theo
                System.err.println("Lỗi khi tự động tạo lịch cho ngày " + dateToCheck + ": " + e.getMessage());
                // e.printStackTrace();
            }
        }
        System.out.println("Kiểm tra lịch hoàn tất.");
    }

}