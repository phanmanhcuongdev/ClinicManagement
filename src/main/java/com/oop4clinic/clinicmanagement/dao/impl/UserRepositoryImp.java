package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.UserRepository;
import com.oop4clinic.clinicmanagement.model.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

public class UserRepositoryImp implements UserRepository  {
    @Override
    public User getUserbyUsername(EntityManager em, String username) {
        String hql =  "SELECT u FROM User u Where username = :username";
        TypedQuery<User> query =  em.createQuery(hql,User.class);
        query.setParameter("username",username);

        return query.getResultStream().findFirst().orElse(null);
    }

//    @Override
//    public String save(EntityManager em,User user) {
//        EntityTransaction transaction = em.getTransaction();
//
//        try {
//            transaction.begin();
//
//            em.persist(user);
//
//            transaction.commit();
//
//        } catch (Exception e) {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//            return e.getMessage();
//        }
//        return null;
//    }
    @Override
    public void save(EntityManager em, User user) {
        em.persist(user);
    }

    @Override
    public void update(EntityManager em,User existing)
    {
        em.merge(existing);
    }

}
