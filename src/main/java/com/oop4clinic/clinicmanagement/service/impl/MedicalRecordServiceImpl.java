package com.oop4clinic.clinicmanagement.service.impl;
import com.oop4clinic.clinicmanagement.dao.MedicalRecordReopository;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.mapper.MedicalRecordMapper;
import com.oop4clinic.clinicmanagement.dao.impl.MedicalRecordRepositoryImpl;
import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

public class MedicalRecordServiceImpl implements MedicalRecordService {
    private MedicalRecordRepositoryImpl medicalRecordImpl = new MedicalRecordRepositoryImpl();
    private final MedicalRecordReopository medicalRecordReopository = new MedicalRecordRepositoryImpl();
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
    public List<MedicalRecordDTO> getMedicalRecordsForDoctor(int doctorId) {
        EntityManager em = EntityManagerProvider.em();
        try {

            String jpql = "SELECT mr FROM MedicalRecord mr " +
                    "JOIN FETCH mr.patient p " +
                    "WHERE mr.doctor.id = :doctorId " +
                    "ORDER BY mr.createdAt DESC";
            TypedQuery<MedicalRecord> query = em.createQuery(jpql, MedicalRecord.class);
            query.setParameter("doctorId", doctorId);
            List<MedicalRecord> entities = query.getResultList();
            return entities.stream().map(MedicalRecordMapper::toDto).toList();
        } catch (RuntimeException ex) {
            ex.printStackTrace(); throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public List<MedicalRecordDTO> searchByPatientName(String keyword) {
        List<MedicalRecord> entities = medicalRecordReopository.searchByPatientName(keyword);
        return entities.stream().map(MedicalRecordMapper::toDto).toList();

    }

    @Override
    public boolean deleteMedicalRecord(Integer id) {
        return medicalRecordReopository.delete(id);
    }

    @Override
    public MedicalRecordDTO findByAppointmentId(int appointmentId) {
        EntityManager em = EntityManagerProvider.em();
        try {

            TypedQuery<MedicalRecord> query = em.createQuery(
                    "SELECT mr FROM MedicalRecord mr " +
                            "JOIN FETCH mr.patient p " +
                            "WHERE mr.appointment.id = :apptId",
                    MedicalRecord.class
            );
            query.setParameter("apptId", appointmentId);
            MedicalRecord entity = query.getSingleResult();
            return MedicalRecordMapper.toDto(entity);
        } catch (NoResultException e) {
            return null; // Không tìm thấy
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO dto, int appointmentId, int doctorId) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Appointment appointment = em.find(Appointment.class, appointmentId);
            if (appointment == null) {
                throw new IllegalArgumentException("Appointment không tồn tại (ID=" + appointmentId + ")");
            }

            Doctor doctor = em.find(Doctor.class, doctorId);
            if (doctor == null) {
                throw new IllegalArgumentException("Doctor không tồn tại (ID=" + doctorId + ")");
            }

            MedicalRecord entity = new MedicalRecord();

            entity.setAppointment(appointment);
            entity.setDoctor(doctor);
            entity.setPatient(appointment.getPatient());

            entity.setSymptoms(dto.getSymptoms());
            entity.setDiagnosis(dto.getDiagnosis());
            entity.setPrescription(dto.getPrescription());
            entity.setNotes(dto.getNotes());

            em.persist(entity);

            tx.commit();
            return MedicalRecordMapper.toDto(entity);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo hồ sơ: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override public boolean updateMedicalRecord(MedicalRecordDTO record) { EntityManager em = EntityManagerProvider.em(); MedicalRecordRepositoryImpl medicalRecordImpl = new MedicalRecordRepositoryImpl(); MedicalRecordMapper mapper = new MedicalRecordMapper(); MedicalRecord recordEntity = medicalRecordImpl.findById(record.getId()); mapper.updateEntityFromDto(record, recordEntity); try{ return medicalRecordImpl.update(em, recordEntity); } catch (Exception e){ e.printStackTrace(); return false; } finally { em.close(); } }

    @Override
    public MedicalRecordDTO updateMedicalRecordWithProfession(MedicalRecordDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("MedicalRecord ID không thể rỗng khi cập nhật.");
        }

        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            MedicalRecord entity = em.find(MedicalRecord.class, dto.getId());
            if (entity == null) {
                throw new IllegalArgumentException("Hồ sơ không tồn tại (ID=" + dto.getId() + ")");
            }

            entity.setSymptoms(dto.getSymptoms());
            entity.setDiagnosis(dto.getDiagnosis());
            entity.setPrescription(dto.getPrescription());
            entity.setNotes(dto.getNotes());
            System.out.println("DEBUG: Đang lưu đơn thuốc: '" + dto.getPrescription() + "'");
            MedicalRecord updatedEntity = em.merge(entity);

            tx.commit();
            return MedicalRecordMapper.toDto(updatedEntity);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật hồ sơ: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<MedicalRecordDTO> getMedicalRecordsForPatient(int patientId) {
        EntityManager em = EntityManagerProvider.em();
        try {
            String jpql = "SELECT mr FROM MedicalRecord mr " +
                    "JOIN FETCH mr.patient p " +
                    "WHERE mr.patient.id = :patientId " +
                    "ORDER BY mr.createdAt DESC";
            TypedQuery<MedicalRecord> query = em.createQuery(jpql, MedicalRecord.class);
            query.setParameter("patientId", patientId);

            List<MedicalRecord> entities = query.getResultList();

            return entities.stream()
                    .map(MedicalRecordMapper::toDto)
                    .collect(Collectors.toList());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            em.close();
        }
    }
}
