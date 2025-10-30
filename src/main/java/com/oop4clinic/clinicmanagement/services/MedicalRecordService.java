package com.oop4clinic.clinicmanagement.services;

import com.oop4clinic.clinicmanagement.dao.MedicalRecordDAO;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import com.oop4clinic.clinicmanagement.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MedicalRecordService {
    public List<MedicalRecord> getAll(){
        MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
        EntityManager em = EntityManagerProvider.em();
        try{
            return medicalRecordDAO.findAll(em);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            em.close();
        }
    }
    public boolean updateMedicalRecord(MedicalRecord record) {
        EntityManager em = EntityManagerProvider.em();
        MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
        try{
            return medicalRecordDAO.update(em,record);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }

    }
}
