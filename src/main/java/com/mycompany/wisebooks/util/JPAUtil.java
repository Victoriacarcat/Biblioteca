package com.mycompany.wisebooks.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Crea una sola vez la conexion con la base de datos (EntityManagerFactory)
 * y entrega un EntityManager nuevo cada vez que se pide.
 */
public class JPAUtil {

    private static final String UNIDAD_PERSISTENCIA = "WiseBooksPU";

    private static final EntityManagerFactory FABRICA = crearFabrica();

    private static EntityManagerFactory crearFabrica() {
        try {
            return Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA);
        } catch (Exception e) {
            System.err.println("No se pudo iniciar JPA: " + e.getMessage());
            if (esErrorDeValidacion(e)) {
                System.err.println("La base no coincide con las entidades (falta una tabla o una columna). "
                        + "Ejecuta util/PruebaTablas una vez para crear o actualizar las tablas.");
            }
            throw new ExceptionInInitializerError(e);
        }
    }

    /* true si en la cadena de causas aparece un error de "Schema-validation". */
    private static boolean esErrorDeValidacion(Throwable error) {
        for (Throwable t = error; t != null; t = t.getCause()) {
            if (t.getMessage() != null && t.getMessage().contains("Schema-validation")) {
                return true;
            }
        }
        return false;
    }

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        return FABRICA.createEntityManager();
    }

    public static void cerrar() {
        if (FABRICA != null && FABRICA.isOpen()) {
            FABRICA.close();
        }
    }
}
