package com.todolist.todolist.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "persistanceUnit";

    private static EntityManagerFactory factory;

    static {
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Throwable ex) {
            System.err.println("La création de l'EntityManagerFactory a échoué : " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Récupère l'EntityManagerFactory.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return factory;
    }

    /**
     * Récupère une nouvelle instance d'EntityManager.
     * À appeler au début d'une méthode ou d'une requête.
     */
    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    /**
     * Ferme la Factory.
     * À appeler UNIQUEMENT à l'arrêt complet de l'application (ex: contextDestroyed).
     */
    public static void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
