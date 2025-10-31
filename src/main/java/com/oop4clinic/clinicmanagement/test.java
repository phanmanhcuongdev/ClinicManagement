package com.oop4clinic.clinicmanagement;

import com.oop4clinic.clinicmanagement.dao.impl.DoctorRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import jakarta.persistence.EntityManager;

public class test {
    public static void main(String[] args) {
        DoctorRepositoryImpl doctorRepository = new DoctorRepositoryImpl();
        EntityManager em = EntityManagerProvider.em();
        Doctor t = doctorRepository.findById(em,1);
        System.out.println(t.getDateOfBirth());
    }
}
