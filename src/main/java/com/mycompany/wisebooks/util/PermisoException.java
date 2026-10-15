/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

/*
 * Se lanza cuando el usuario no tiene permiso para una accion
 * (por ejemplo, un lector intentando crear un libro).
 * Hereda de ValidacionException: si no se atrapa aparte, se trata igual.
 * Asi el servlet puede responder "sin permiso" distinto de "dato invalido".
 */
public class PermisoException extends ValidacionException {

    public PermisoException(String mensaje) {
        super(mensaje);
    }
}
