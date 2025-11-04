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
        try {
            String hql =  "SELECT u FROM User u Where username = :username";
            TypedQuery<User> query =  em.createQuery(hql,User.class);

            query.setParameter("username",username);

            User userCur =  query.getSingleResult();
            if(userCur != null) System.out.println(userCur.getCreatedAt());
            return userCur;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

}
