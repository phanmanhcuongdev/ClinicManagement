package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.DoctorScheduleRepository;
import com.oop4clinic.clinicmanagement.model.entity.DoctorSchedule;
import com.oop4clinic.clinicmanagement.model.enums.DoctorScheduleStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate; // <-- Cần import
import java.util.List;
import java.util.Optional;

public class DoctorScheduleRepositoryImpl implements DoctorScheduleRepository {

    @Override
    public Optional<DoctorSchedule> findById(EntityManager em, int id) {
        try {
            DoctorSchedule ds = em.find(DoctorSchedule.class, id);
            return Optional.ofNullable(ds);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<DoctorSchedule> findAvailableByDoctorId(EntityManager em, int doctorId) {
        return em.createQuery(
                        """
                        select ds from DoctorSchedule ds
                        where ds.doctor.id = :doctorId
                        and ds.status = :status
                        order by ds.workDate asc, ds.startTime asc
                        """,
                        DoctorSchedule.class
                )
                .setParameter("doctorId", doctorId)
                .setParameter("status", DoctorScheduleStatus.AVAILABLE)
                .getResultList();
    }

    @Override
    public DoctorSchedule save(EntityManager em, DoctorSchedule schedule) {
        if (schedule.getId() == null) {
            em.persist(schedule);
            return schedule;
        }
        return em.merge(schedule);
    }

    // PHƯƠNG THỨC MỚI: Triển khai truy vấn bằng LocalDate
    @Override
    public List<DoctorSchedule> findAvailableByDeptAndDate(
            EntityManager em,
            int departmentId,
            LocalDate workDate
    ) {
        // JPQL: Lọc theo Khoa, Ngày (LocalDate) và Trạng thái AVAILABLE
        String jpql = "SELECT ds FROM DoctorSchedule ds " +
                "JOIN ds.doctor d " +
                "WHERE d.department.id = :deptId " +
                "AND ds.workDate = :date "
                + "AND ds.status = :status "
                + "ORDER BY ds.startTime ASC";

        return em.createQuery(jpql, DoctorSchedule.class)
                .setParameter("deptId", departmentId)
                .setParameter("date", workDate) // Truyền LocalDate
                .setParameter("status", DoctorScheduleStatus.AVAILABLE)
                .getResultList();
    }
}