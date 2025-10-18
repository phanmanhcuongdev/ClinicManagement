package com.oop4clinic.clinicmanagement.dao.jpa;

import jakarta.persistence.*;

public class EntityManagerProvider {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("clinicDB");

    private EntityManagerProvider() {}

    public static EntityManager em(){
        return EMF.createEntityManager();
    }

    public static void init() { EMF.isOpen(); }

    public static void close()
    {
        if(EMF.isOpen()) EMF.close();
    }
}
