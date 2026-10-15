/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 * Ejecuta un bloque de trabajo dentro de UNA sola transaccion: o se guarda
 * todo, o no se guarda nada. Si algo falla hace rollback automaticamente.
 *
 * Uso en un service:
 *   return Transaccion.ejecutar(em -> {
 *       ... varias operaciones con el mismo em ...
 *       return resultado;
 *   });
 */
public final class Transaccion {

    private Transaccion() {
    }

    /* Con transaccion, devolviendo un resultado. */
    public static <T> T ejecutar(Function<EntityManager, T> trabajo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T resultado = trabajo.apply(em);
            em.getTransaction().commit();
            return resultado;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw traducir(e);
        } finally {
            em.close();
        }
    }

    /* Con transaccion, sin devolver nada. */
    public static void ejecutarAccion(Consumer<EntityManager> trabajo) {
        ejecutar(em -> {
            trabajo.accept(em);
            return null;
        });
    }

    /*
     * Solo lectura (sin transaccion). Los DTO deben crearse DENTRO del bloque,
     * mientras el EntityManager sigue abierto (si no, las relaciones LAZY fallan).
     */
    public static <T> T consultar(Function<EntityManager, T> trabajo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return trabajo.apply(em);
        } finally {
            em.close();
        }
    }

    /*
     * Si dos personas cambiaron el mismo registro a la vez (bloqueo optimista
     * con @Version), se convierte en un mensaje claro para el usuario.
     */
    private static RuntimeException traducir(RuntimeException e) {
        Throwable actual = e;
        while (actual != null) {
            if (actual instanceof OptimisticLockException) {
                return new ValidacionException(
                        "Otra persona modificó este registro al mismo tiempo. Intenta de nuevo.");
            }
            actual = actual.getCause();
        }
        return e;
    }
}
