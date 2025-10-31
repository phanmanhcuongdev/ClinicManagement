package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.User;
import jakarta.persistence.EntityManager;

public interface UserRepository {
    User getUserbyUsername(EntityManager em, String username);
    String save(EntityManager em,User user);
}
