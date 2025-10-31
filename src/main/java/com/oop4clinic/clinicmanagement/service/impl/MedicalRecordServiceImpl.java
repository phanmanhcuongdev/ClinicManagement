package com.oop4clinic.clinicmanagement.service.impl;
import com.oop4clinic.clinicmanagement.model.mapper.MedicalRecordMapper;
import com.oop4clinic.clinicmanagement.dao.impl.MedicalRecordImpl;
import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import com.oop4clinic.clinicmanagement.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MedicalRecordServiceImpl implements MedicalRecordService {
    private MedicalRecordImpl medicalRecordImpl = new MedicalRecordImpl();
    private MedicalRecordMapper mapper = new MedicalRecordMapper();

    @Override
    public List<MedicalRecordDTO> getAll(){
        EntityManager em = EntityManagerProvider.em();
        try{
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
        MedicalRecordImpl medicalRecordImpl = new MedicalRecordImpl();
        MedicalRecordMapper mapper = new MedicalRecordMapper();
        MedicalRecord recordEntity = medicalRecordImpl.findById(em,record.getId());
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
}
