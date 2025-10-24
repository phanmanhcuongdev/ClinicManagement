package com.oop4clinic.clinicmanagement.model.mapper;

import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.entity.Patient;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper thủ công cho Patient <-> PatientDTO.
 * Không dùng MapStruct để tránh phụ thuộc, nhưng có thể thay sau này.
 */
public final class PatientMapper {

    private PatientMapper() {}

    // =================== ENTITY → DTO ===================
    public static PatientDTO toDto(Patient entity) {
        if (entity == null) return null;

        PatientDTO dto = new PatientDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setGender(entity.getGender());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setCccd(entity.getCccd());
        dto.setInsuranceCode(entity.getInsuranceCode());

        return dto;
    }

    public static List<PatientDTO> toDtoList(Collection<Patient> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .filter(Objects::nonNull)
                .map(PatientMapper::toDto)
                .collect(Collectors.toList());
    }

    // =================== DTO → ENTITY ===================
    public static Patient toEntity(PatientDTO dto) {
        if (dto == null) return null;

        Patient entity = new Patient();
        entity.setId(dto.getId());
        entity.setFullName(trimOrNull(dto.getFullName()));
        entity.setGender(dto.getGender());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setPhone(trimOrNull(dto.getPhone()));
        entity.setEmail(trimOrNull(dto.getEmail()));
        entity.setAddress(trimOrNull(dto.getAddress()));
        entity.setCccd(trimOrNull(dto.getCccd()));
        entity.setInsuranceCode(trimOrNull(dto.getInsuranceCode()));

        return entity;
    }

    /**
     * Cập nhật toàn bộ field từ DTO sang entity đã tồn tại (update).
     */
    public static void overwriteEntity(PatientDTO dto, Patient entity) {
        if (dto == null || entity == null) return;

        entity.setFullName(trimOrNull(dto.getFullName()));
        entity.setGender(dto.getGender());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setPhone(trimOrNull(dto.getPhone()));
        entity.setEmail(trimOrNull(dto.getEmail()));
        entity.setAddress(trimOrNull(dto.getAddress()));
        entity.setCccd(trimOrNull(dto.getCccd()));
        entity.setInsuranceCode(trimOrNull(dto.getInsuranceCode()));
    }

    /**
     * Cập nhật từng phần (PATCH): chỉ ghi đè khi DTO có giá trị khác null.
     */
    public static void patchEntity(PatientDTO dto, Patient entity) {
        if (dto == null || entity == null) return;

        if (dto.getFullName() != null) entity.setFullName(trimOrNull(dto.getFullName()));
        if (dto.getGender() != null) entity.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) entity.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getPhone() != null) entity.setPhone(trimOrNull(dto.getPhone()));
        if (dto.getEmail() != null) entity.setEmail(trimOrNull(dto.getEmail()));
        if (dto.getAddress() != null) entity.setAddress(trimOrNull(dto.getAddress()));
        if (dto.getCccd() != null) entity.setCccd(trimOrNull(dto.getCccd()));
        if (dto.getInsuranceCode() != null) entity.setInsuranceCode(trimOrNull(dto.getInsuranceCode()));
    }

    // =================== Helpers ===================
    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
