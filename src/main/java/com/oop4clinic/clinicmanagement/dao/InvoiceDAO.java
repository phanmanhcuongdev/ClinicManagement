package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.*;

public class InvoiceDAO {
    public List<Invoice>  findAll(EntityManager em){
        try{
            String jpql = """
                    SELECT i FROM Invoice i 
                    JOIN FETCH i.patient 
                    JOIN FETCH i.appointment                    
                    """;
            TypedQuery<Invoice> query = em.createQuery(jpql,Invoice.class);
            return query.getResultList();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
