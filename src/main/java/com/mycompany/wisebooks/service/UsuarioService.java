package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.UsuarioDAO;
import com.mycompany.wisebooks.dto.LoginDTO;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.TipoDocumento;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.ContrasenaUtil;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.regex.Pattern;

/*
 * Reglas de negocio de los usuarios: registro e inicio de sesion.
 * Valida, decide y delega el acceso a la base en UsuarioDAO.
 * Cada operacion publica corre dentro de UNA transaccion (ver util/Transaccion).
 */
public class UsuarioService {

    private static final int LONGITUD_MINIMA_CONTRASENA = 8;

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_DOCUMENTO =
            Pattern.compile("^[A-Za-z0-9]{4,20}$");
    private static final Pattern PATRON_TELEFONO =
            Pattern.compile("^[0-9+\\s-]{7,20}$");

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Registro publico: SIEMPRE crea un lector. */
    public UsuarioDTO registrar(RegistroDTO dto) {
        if (!dto.isAceptaTerminos()) {
            throw new ValidacionException("Debes aceptar los términos y condiciones.");
        }
        return Transaccion.ejecutar(em -> UsuarioDTO.desde(crearUsuario(em, dto, Rol.LECTOR)));
    }

    /*
     * Crea el primer administrador. Solo funciona si todavia no existe ninguno.
     * Se usa una unica vez, desde Java (no desde un formulario publico).
     */
    public UsuarioDTO crearPrimerAdministrador(RegistroDTO dto) {
        return Transaccion.ejecutar(em -> {
            if (usuarioDAO.existeRol(em, Rol.ADMINISTRADOR)) {
                throw new ValidacionException("Ya existe un administrador.");
            }
            return UsuarioDTO.desde(crearUsuario(em, dto, Rol.ADMINISTRADOR));
        });
    }

    /* Inicio de sesion. Devuelve la ficha del usuario si todo es correcto. */
    public UsuarioDTO iniciarSesion(LoginDTO dto) {
        String correo = Texto.limpiar(dto.getCorreo()).toLowerCase();
        String contrasena = dto.getContrasena();

        if (correo.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            throw new ValidacionException("Escribe tu correo y tu contraseña.");
        }

        return Transaccion.consultar(em -> {
            Optional<Usuario> encontrado = usuarioDAO.buscarPorCorreo(em, correo);

            // Mismo mensaje si el correo no existe o la contrasena falla:
            // asi no se revela si un correo esta registrado.
            if (encontrado.isEmpty()
                    || !ContrasenaUtil.verificar(contrasena, encontrado.get().getContrasenaHash())) {
                throw new ValidacionException("Correo o contraseña incorrectos.");
            }

            Usuario usuario = encontrado.get();
            if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
                throw new ValidacionException("Tu cuenta está desactivada. Contacta a la biblioteca.");
            }
            // Un usuario SUSPENDIDO si puede entrar (solo no podra reservar ni pedir prestamos)
            return UsuarioDTO.desde(usuario);
        });
    }

    /* Valida los datos, comprueba duplicados y guarda el usuario con el rol indicado. */
    private Usuario crearUsuario(EntityManager em, RegistroDTO dto, Rol rol) {
        String nombre = Texto.exigir(dto.getNombre(), "El nombre", 100);
        String apellidos = Texto.exigir(dto.getApellidos(), "Los apellidos", 100);
        TipoDocumento tipo = convertirTipoDocumento(dto.getTipoDocumento());

        String documento = Texto.exigir(dto.getDocumento(), "El número de documento", 20);
        if (!PATRON_DOCUMENTO.matcher(documento).matches()) {
            throw new ValidacionException(
                    "El documento debe tener entre 4 y 20 caracteres, solo letras y números.");
        }

        String telefono = Texto.exigir(dto.getTelefono(), "El teléfono", 20);
        if (!PATRON_TELEFONO.matcher(telefono).matches()) {
            throw new ValidacionException("El teléfono no es válido.");
        }

        String correo = Texto.exigir(dto.getCorreo(), "El correo", 150).toLowerCase();
        if (!PATRON_CORREO.matcher(correo).matches()) {
            throw new ValidacionException("El correo no es válido.");
        }

        String contrasena = dto.getContrasena();
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA_CONTRASENA) {
            throw new ValidacionException("La contraseña debe tener al menos "
                    + LONGITUD_MINIMA_CONTRASENA + " caracteres.");
        }

        if (usuarioDAO.existeCorreo(em, correo)) {
            throw new ValidacionException("Ya existe una cuenta con ese correo.");
        }
        if (usuarioDAO.existeDocumento(em, tipo, documento)) {
            throw new ValidacionException("Ya existe una cuenta con ese documento.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setTipoDocumento(tipo);
        usuario.setDocumento(documento);
        usuario.setTelefono(telefono);
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(ContrasenaUtil.encriptar(contrasena));
        usuario.setRol(rol);
        usuario.setEstado(EstadoUsuario.ACTIVO);

        return usuarioDAO.guardar(em, usuario);
    }

    private TipoDocumento convertirTipoDocumento(String texto) {
        try {
            return TipoDocumento.valueOf(Texto.limpiar(texto).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Selecciona un tipo de documento válido.");
        }
    }
}
