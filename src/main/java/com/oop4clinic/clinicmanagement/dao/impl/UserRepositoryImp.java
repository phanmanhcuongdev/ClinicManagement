package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.UserRepository;
import com.oop4clinic.clinicmanagement.model.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UserRepositoryImp implements UserRepository  {
    @Override
    public User getUserbyUsername(EntityManager em, String username) {
        try {
            String hql = "SELECT u FROM User u WHERE username = :username";
            TypedQuery<User> query = em.createQuery(hql, User.class);
            query.setParameter("username", username);

            List<User> results = query.getResultList(); // 👈 thay vì getSingleResult()
            if (!results.isEmpty()) {
                User userCur = results.get(0);
                System.out.println("✅ Found user: " + userCur.getCreatedAt());
                return userCur;
            } else {
                System.out.println("⚠ No user found for username: " + username);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String save(EntityManager em,User user) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            em.persist(user);

            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }
}
