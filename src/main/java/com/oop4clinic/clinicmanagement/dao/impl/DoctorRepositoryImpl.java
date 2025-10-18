package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.DoctorRepository;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import jakarta.persistence.EntityManager;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public class DoctorRepositoryImpl implements DoctorRepository {
    @Override
    public List<Doctor> findAll(EntityManager em) {
        return em.createQuery(
                "select d from Doctor d join fetch d.department order by d.id",
                Doctor.class
        ).getResultList();
    }

    @Override
    public Doctor save(EntityManager em,Doctor doc)
    {
        if(doc.getId() == null)
        {
            em.persist(doc);
            return doc;
        }
        return em.merge(doc);
    }

    @Override
    public boolean existsByEmail(EntityManager em, String email) {
        if (email == null) return false;
        Long cnt = em.createQuery(
                "select count(d) from Doctor d where lower(d.email) = lower(:email)",
                Long.class
        ).setParameter("email", email)
         .getSingleResult();
        return cnt != null && cnt > 0;
    }

    @Override
    public boolean existsByPhone(EntityManager em, String phone) {
        if (phone == null) return false;
        Long cnt = em.createQuery(
                "select count(d) from Doctor d where d.phone = :phone",
                Long.class
        ).setParameter("phone", phone)
         .getSingleResult();
        return cnt != null && cnt > 0;
    }
}
