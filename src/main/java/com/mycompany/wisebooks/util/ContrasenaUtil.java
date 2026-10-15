/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Encripta y verifica contrasenas con BCrypt.
 * Nunca se guarda la contrasena real: solo su hash.
 */
public class ContrasenaUtil {

    private ContrasenaUtil() {
    }

    public static String encriptar(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("La contrasena no puede estar vacia");
        }
        return BCrypt.hashpw(contrasena, BCrypt.gensalt());
    }

    /* Comprueba si la contrasena escrita corresponde al hash guardado. */
    public static boolean verificar(String contrasena, String hash) {
        if (contrasena == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(contrasena, hash);
        } catch (IllegalArgumentException e) {
            // El hash guardado no tiene formato BCrypt valido
            return false;
        }
    }
}
