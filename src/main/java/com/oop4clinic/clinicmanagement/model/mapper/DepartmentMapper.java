package com.oop4clinic.clinicmanagement.model.mapper;

import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper thuần túy giữa Department (entity JPA) và DepartmentDTO (dùng cho UI/service).
 * Controller chỉ nên đụng DepartmentDTO.
 */
public final class DepartmentMapper {

    private DepartmentMapper() {
        // chặn new
    }

    // =========================
    // ENTITY -> DTO
    // =========================
    public static DepartmentDTO toDto(Department entity) {
        if (entity == null) return null;

        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBaseFee(entity.getBaseFee());
        dto.setDescription(entity.getDescription());

        // map danh sách tên bác sĩ để hiển thị read-only bên UI
        if (entity.getDoctors() != null) {
            List<String> doctorNames = entity.getDoctors()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(Doctor::getFullName)
                    .filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());
            dto.setDoctorNames(doctorNames);
        } else {
            dto.setDoctorNames(Collections.emptyList());
        }

        return dto;
    }

    // =========================
    // LIST<ENTITY> -> LIST<DTO>
    // =========================
    public static List<DepartmentDTO> toDtoList(List<Department> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(DepartmentMapper::toDto)
                .collect(Collectors.toList());
    }

    // =========================
    // DTO -> ENTITY (cho create)
    // =========================
    public static Department toEntity(DepartmentDTO dto) {
        if (dto == null) return null;

        Department entity = new Department();

        // id thường để null, JPA sẽ tự generate. Nếu bạn muốn cho phép setId thủ công thì mở comment.
        // entity.setId(dto.getId());

        entity.setName(dto.getName());
        entity.setBaseFee(dto.getBaseFee());
        entity.setDescription(dto.getDescription());

        // KHÔNG set doctors ở đây
        // KHÔNG set appointments ở đây
        return entity;
    }

    // =========================
    // UPDATE ENTITY TỒN TẠI từ DTO
    // =========================
    /**
     * copy các field cho phép chỉnh sửa từ dto sang entity.
     * dùng cho update: bạn fetch entity từ DB bằng repo, rồi applyToEntity(dto, entity), rồi repo.save(entity)
     */
    public static void applyToEntity(DepartmentDTO dto, Department entity) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setBaseFee(dto.getBaseFee());
        entity.setDescription(dto.getDescription());
        // doctors & appointments không đụng ở đây
    }
}
