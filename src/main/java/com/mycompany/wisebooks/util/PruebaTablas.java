package com.mycompany.wisebooks.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Map;

/*
 * Crea o actualiza las tablas a partir de las entidades y lista las que hay.
 */
public class PruebaTablas {

    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            // Este valor reemplaza al de persistence.xml, solo en esta ejecucion
            emf = Persistence.createEntityManagerFactory("WiseBooksPU",
                    Map.of("hibernate.hbm2ddl.auto", "update"));
            em = emf.createEntityManager();

            Object base = em.createNativeQuery("SELECT DATABASE()").getSingleResult();
            Object juego = em.createNativeQuery(
                    "SELECT DEFAULT_CHARACTER_SET_NAME FROM information_schema.SCHEMATA "
                    + "WHERE SCHEMA_NAME = DATABASE()").getSingleResult();
            System.out.println("Base de datos: " + base + " | juego de caracteres: " + juego);

            List<?> tablas = em.createNativeQuery("SHOW TABLES").getResultList();
            System.out.println("=== TABLAS EN LA BASE (" + tablas.size() + ") ===");
            for (Object t : tablas) {
                System.out.println(" - " + t);
            }
        } catch (Exception e) {
            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("CAUSA REAL: " + causa.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }
}


