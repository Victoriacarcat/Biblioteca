/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

/**
 * Se lanza cuando algo no cumple una regla de negocio o una validacion
 * (por ejemplo, "El correo ya está registrado").
 * El mensaje esta pensado para mostrarse tal cual al usuario.
 */
public class ValidacionException extends RuntimeException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}