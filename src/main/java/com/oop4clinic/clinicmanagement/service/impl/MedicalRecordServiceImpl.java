package com.oop4clinic.clinicmanagement.service.impl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.mapper.MedicalRecordMapper;
import com.oop4clinic.clinicmanagement.dao.impl.MedicalRecordImpl;
import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MedicalRecordServiceImpl implements MedicalRecordService {
    private MedicalRecordImpl medicalRecordImpl = new MedicalRecordImpl();
    private MedicalRecordMapper mapper = new MedicalRecordMapper();

    @Override
    public List<MedicalRecordDTO> getAll(){
        EntityManager em = EntityManagerProvider.em();
        try{
            // Sử dụng medicalRecordImpl.findAll(em) từ file gốc
            return mapper.toDtoList(medicalRecordImpl.findAll(em));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            em.close();
        }
    }

    @Override
    public boolean updateMedicalRecord(MedicalRecordDTO record) {
        EntityManager em = EntityManagerProvider.em();
        // Khai báo lại impl và mapper là không cần thiết vì đã có ở trên
        // MedicalRecordImpl medicalRecordImpl = new MedicalRecordImpl();
        // MedicalRecordMapper mapper = new MedicalRecordMapper();

        MedicalRecord recordEntity = medicalRecordImpl.findById(em,record.getId());

        if (recordEntity == null) {
            em.close();
            return false;
        }

        mapper.updateEntityFromDto(record, recordEntity);

        try{
            return medicalRecordImpl.update(em, recordEntity);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // THÊM: Phương thức lấy hồ sơ theo ID bệnh nhân (phục vụ lọc dữ liệu)
    @Override
    public List<MedicalRecordDTO> getByPatientId(int patientId){
        EntityManager em = EntityManagerProvider.em();
        try{
            // Gọi phương thức DAO mới
            return mapper.toDtoList(medicalRecordImpl.findByPatientId(em, patientId));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            em.close();
        }
    }

    // THÊM: Phương thức lấy chi tiết hồ sơ theo ID (phục vụ xem chi tiết)
    @Override
    public MedicalRecordDTO getById(int id) {
        EntityManager em = EntityManagerProvider.em();
        try {
            // Lấy Entity từ DAO
            MedicalRecord entity = medicalRecordImpl.findById(em, id);
            // Chuyển Entity sang DTO
            return mapper.toDto(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}