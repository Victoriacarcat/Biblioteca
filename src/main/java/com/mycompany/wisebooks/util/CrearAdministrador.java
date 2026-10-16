package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.dto.LoginDTO;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.service.UsuarioService;

/*
 * Crea una cuenta de ADMINISTRADOR sin necesitar a otro administrador:
 *   - la PRIMERA del sistema (al instalarlo), o
 *   - una de emergencia, si ningun administrador puede entrar.
 * Con un administrador activo, los demas se crean desde la web (Usuarios).
 * Se ejecuta en el servidor, con acceso a la base, desde NetBeans
 * (clic derecho > Run File).
 *
 * No se insertan administradores a mano con SQL: la contrasena debe guardarse
 * cifrada con BCrypt y el registro pasa por las mismas validaciones (correo y
 * documento sin repetir) y queda en la auditoria.
 *
 * Antes de ejecutar: cambia los datos de abajo. Despues, el nuevo
 * administrador debe cambiar su contrasena desde su cuenta.
 */
public class CrearAdministrador {

    private static final String NOMBRE = "Juan";
    private static final String APELLIDOS = "Silva";
    private static final String TIPO_DOCUMENTO = "CC";
    private static final String DOCUMENTO = "1000000001";
    private static final String TELEFONO = "3000000000";
    private static final String CORREO = "admin@wisebooks.com";

    // Contrasena temporal: reemplazala antes de ejecutar (minimo 8 caracteres)
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

            UsuarioDTO admin = servicio.crearAdministradorDesdeServidor(registro);
            System.out.println("Administrador creado: " + admin.getNombreCompleto()
                    + " (" + admin.getCorreo() + "), rol " + admin.getRol());

            // Comprueba que la cuenta nueva puede iniciar sesion
            LoginDTO login = new LoginDTO();
            login.setCorreo(CORREO);
            login.setContrasena(CONTRASENA);
            UsuarioDTO sesion = servicio.iniciarSesion(login);
            System.out.println("Inicio de sesión correcto: " + sesion.getNombreCompleto()
                    + ", estado " + sesion.getEstado());
        } catch (ValidacionException e) {
            // Por ejemplo: "Ya existe una cuenta con ese correo."
            System.out.println("No se pudo crear el administrador: " + e.getMessage());
        } finally {
            JPAUtil.cerrar();
        }
    }
}
