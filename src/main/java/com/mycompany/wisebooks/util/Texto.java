/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

/*
 * Utilidades para validar texto que llega de un formulario.
 * Las usan todos los services, para no repetir las mismas comprobaciones.
 */
public final class Texto {

    private Texto() {
    }

    /* Quita espacios; si llega null devuelve cadena vacia. */
    public static String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    /* Exige que el texto no este vacio ni supere el maximo. Devuelve el texto limpio. */
    public static String exigir(String texto, String campo, int maximo) {
        String limpio = limpiar(texto);
        if (limpio.isEmpty()) {
            throw new ValidacionException(campo + " es obligatorio.");
        }
        if (limpio.length() > maximo) {
            throw new ValidacionException(campo + " no puede superar " + maximo + " caracteres.");
        }
        return limpio;
    }

    /* Texto opcional: si viene vacio devuelve null; si trae algo, no puede superar el maximo. */
    public static String opcional(String texto, String campo, int maximo) {
        String limpio = limpiar(texto);
        if (limpio.isEmpty()) {
            return null;
        }
        if (limpio.length() > maximo) {
            throw new ValidacionException(campo + " no puede superar " + maximo + " caracteres.");
        }
        return limpio;
    }

    /* Recorta un texto largo para que quepa en su columna (sin lanzar error). */
    public static String recortar(String texto, int maximo) {
        if (texto == null || texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo);
    }
}
