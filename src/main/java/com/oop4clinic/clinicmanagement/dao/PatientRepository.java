package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.Patient;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    // 🔹 Lấy bệnh nhân theo ID
    Optional<Patient> findById(EntityManager em, int id);

    // 🔹 Thêm bệnh nhân mới
    Patient create(EntityManager em, Patient e);

    // Lấy danh sách toàn bộ bệnh nhân
    List<Patient> findAll(EntityManager em);

    // 🔹 Cập nhật thông tin bệnh nhân
    Patient update(EntityManager em, Patient e);

    // 🔹 Đếm tổng số bệnh nhân
    long countAll(EntityManager em);

    // 🔹 Lấy danh sách bệnh nhân mới nhất
    List<Patient> findNewest(EntityManager em, int limit);

    // ✅ MỚI THÊM: Lấy bệnh nhân theo số điện thoại (đăng nhập)
    Optional<Patient> findByPhone(EntityManager em, String phone);
}
