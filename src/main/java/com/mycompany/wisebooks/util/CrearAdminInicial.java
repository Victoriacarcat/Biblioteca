package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.dto.LoginDTO;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.service.UsuarioService;

public class CrearAdminInicial {

    private static final String NOMBRE = "Juan";
    private static final String APELLIDOS = "Silva";
    private static final String TIPO_DOCUMENTO = "CC";
    private static final String DOCUMENTO = "1000000001";
    private static final String TELEFONO = "3000000000";
    private static final String CORREO = "admin@wisebooks.com";

    // Reemplaza este valor antes de ejecutar.
    private static final String CONTRASENA = "Admin2026!";

    public static void main(String[] args) {
 

        try {
            UsuarioService servicio = new UsuarioService();

            RegistroDTO registro = new RegistroDTO();
            registro.setNombre(NOMBRE);
            registro.setApellidos(APELLIDOS);
            registro.setTipoDocumento(TIPO_DOCUMENTO);
            registro.setDocumento(DOCUMENTO);
            registro.setTelefono(TELEFONO);
            registro.setCorreo(CORREO);
            registro.setContrasena(CONTRASENA);

            UsuarioDTO admin = servicio.crearPrimerAdministrador(registro);
            System.out.println("Administrador creado: " + admin.getNombreCompleto()
                    + " (" + admin.getCorreo() + "), rol " + admin.getRol());

            LoginDTO login = new LoginDTO();
            login.setCorreo(CORREO);
            login.setContrasena(CONTRASENA);

            UsuarioDTO sesion = servicio.iniciarSesion(login);
            System.out.println("Inicio de sesión correcto: " + sesion.getNombreCompleto()
                    + ", estado " + sesion.getEstado());

            login.setContrasena(CONTRASENA + "x");
            try {
                servicio.iniciarSesion(login);
                System.out.println("ERROR: se aceptó una contraseña incorrecta");
            } catch (ValidacionException e) {
                System.out.println("Contraseña incorrecta rechazada: " + e.getMessage());
            }
        } catch (ValidacionException e) {
            System.out.println("No se pudo crear o iniciar sesión: " + e.getMessage());
        } finally {
            JPAUtil.cerrar();
        }
    }
}