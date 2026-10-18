package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.SancionDAO;
import com.mycompany.wisebooks.dao.UsuarioDAO;
import com.mycompany.wisebooks.dto.LoginDTO;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.TipoDocumento;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.ContrasenaUtil;
import com.mycompany.wisebooks.util.PermisoException;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/*
 * Reglas de negocio de los usuarios: registro, inicio de sesion, cuenta
 * propia y administracion de usuarios (solo administrador).
 * Valida, decide y delega el acceso a la base en UsuarioDAO.
 * Cada operacion publica corre dentro de UNA transaccion (ver util/Transaccion).
 *
 * Cuentas de administrador:
 *   - El administrador puede crear desde la web cuentas de cualquier rol,
 *     incluidos otros administradores (queda en la auditoria).
 *   - Lo que NO se puede desde la web es cambiar el rol o el estado de un
 *     administrador que ya existe, ni subir a alguien a administrador
 *     cambiandole el rol: asi un administrador no puede degradar o dejar
 *     fuera a los demas.
 *   - El primer administrador se crea desde el servidor (util/CrearAdministrador).
 */
public class UsuarioService {

    private static final int LONGITUD_MINIMA_CONTRASENA = 8;

    // Busqueda de lectores por el personal: minimo de letras y tope de resultados
    private static final int MIN_TEXTO_BUSQUEDA = 3;
    private static final int MAX_RESULTADOS_LECTORES = 20;

    private static final String MENSAJE_ADMIN_PROTEGIDO =
            "El rol y el estado de los administradores no se pueden cambiar desde la aplicación.";

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_DOCUMENTO =
            Pattern.compile("^[A-Za-z0-9]{4,20}$");
    private static final Pattern PATRON_TELEFONO =
            Pattern.compile("^[0-9+\\s-]{7,20}$");

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SancionDAO sancionDAO = new SancionDAO();
    private final AuditoriaService auditoriaService = new AuditoriaService();

    /** Registro publico: SIEMPRE crea un lector. */
    public UsuarioDTO registrar(RegistroDTO dto) {
        if (!dto.isAceptaTerminos()) {
            throw new ValidacionException("Debes aceptar los términos y condiciones.");
        }
        return Transaccion.ejecutar(em -> UsuarioDTO.desde(crearUsuario(em, dto, Rol.LECTOR)));
    }

    /*
     * Crea una cuenta de ADMINISTRADOR sin necesitar a otro administrador:
     * la primera del sistema, o una de emergencia si ningun administrador
     * puede entrar. (Con un administrador activo, lo normal es crearla desde
     * la web con crearCuenta.)
     * SOLO para util/CrearAdministrador, que se ejecuta en el servidor: no
     * pide actor porque quien la usa ya tiene acceso al servidor y a la base.
     * Nunca debe llamarse desde un controller.
     * Queda en la auditoria como accion del sistema (sin actor).
     */
    public UsuarioDTO crearAdministradorDesdeServidor(RegistroDTO dto) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = crearUsuario(em, dto, Rol.ADMINISTRADOR);
            auditoriaService.registrar(em, null, AccionAuditoria.CREAR, "Usuario", admin.getId(),
                    "Se creó la cuenta de administrador " + admin.getCorreo() + " desde el servidor.");
            return UsuarioDTO.desde(admin);
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
            // Un usuario SUSPENDIDO si puede entrar: puede tener hasta MAX_RESERVAS_SUSPENDIDO
            // reservas, pero no recibir prestamos ni renovarlos
            return UsuarioDTO.desde(usuario);
        });
    }

    // Cuenta propia (cualquier usuario con sesion)

    /* Cambia la contrasena propia. Exige la actual para confirmar que es el dueno de la cuenta. */
    public void cambiarContrasena(UsuarioDTO actor, String actual, String nueva) {
        Transaccion.ejecutarAccion(em -> {
            // Un suspendido tambien puede (sigue pudiendo iniciar sesion); un inactivo no
            Usuario usuario = (actor == null || actor.getId() == null) ? null : em.find(Usuario.class, actor.getId());
            if (usuario == null || usuario.getEstado() == EstadoUsuario.INACTIVO) {
                throw new PermisoException("Debes iniciar sesión.");
            }
            if (!ContrasenaUtil.verificar(actual, usuario.getContrasenaHash())) {
                throw new ValidacionException("La contraseña actual no es correcta.");
            }
            exigirContrasenaValida(nueva);
            usuario.setContrasenaHash(ContrasenaUtil.encriptar(nueva));
            auditoriaService.registrar(em, usuario, AccionAuditoria.ACTUALIZAR, "Usuario", usuario.getId(),
                    "Cambió su contraseña.");
        });
    }

    // Busqueda de lectores (administrador y bibliotecario)

    /*
     * Lectores cuyo nombre, apellidos, correo o documento contienen el texto.
     * La usa el personal para elegir a quien entregar un prestamo directo o
     * registrar una sancion. El texto es obligatorio (minimo
     * MIN_TEXTO_BUSQUEDA letras) y se devuelven como maximo
     * MAX_RESULTADOS_LECTORES: asi no se entrega la lista completa de lectores.
     */
    public List<UsuarioDTO> buscarLectores(UsuarioDTO actor, String texto) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            String filtro = Texto.limpiar(texto);
            if (filtro.length() < MIN_TEXTO_BUSQUEDA) {
                throw new ValidacionException("Escribe al menos " + MIN_TEXTO_BUSQUEDA
                        + " letras del nombre, correo o documento del lector.");
            }
            return usuarioDAO.listar(em, filtro, Rol.LECTOR).stream()
                    .limit(MAX_RESULTADOS_LECTORES)
                    .map(UsuarioDTO::desde)
                    .toList();
        });
    }

    // Administracion de usuarios (solo administrador)

    /* Lista de usuarios. Filtros opcionales: texto (nombre, correo, documento) y rol. */
    public List<UsuarioDTO> listar(UsuarioDTO actor, String texto, Rol rol) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            return usuarioDAO.listar(em, texto, rol).stream()
                    .map(UsuarioDTO::desde)
                    .toList();
        });
    }

    public UsuarioDTO obtener(UsuarioDTO actor, Long idUsuario) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            return UsuarioDTO.desde(exigirUsuario(em, idUsuario));
        });
    }

    /*
     * El administrador crea una cuenta de cualquier rol: LECTOR (por ejemplo,
     * alguien que se registra en el mostrador), BIBLIOTECARIO o ADMINISTRADOR.
     * Queda en la auditoria con el rol asignado.
     */
    public UsuarioDTO crearCuenta(UsuarioDTO actor, RegistroDTO dto, Rol rol) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            if (rol == null) {
                throw new ValidacionException("Selecciona el rol de la cuenta.");
            }
            Usuario nuevo = crearUsuario(em, dto, rol);
            auditoriaService.registrar(em, admin, AccionAuditoria.CREAR, "Usuario", nuevo.getId(),
                    "Creó la cuenta de " + nuevo.getCorreo() + " con rol " + rol.name().toLowerCase() + ".");
            return UsuarioDTO.desde(nuevo);
        });
    }

    /*
     * Cambia el rol de otro usuario entre LECTOR y BIBLIOTECARIO. Nadie puede
     * cambiarse el rol a si mismo, dar el rol ADMINISTRADOR ni cambiar el rol
     * de un administrador.
     */
    public UsuarioDTO cambiarRol(UsuarioDTO actor, Long idUsuario, Rol nuevoRol) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            Usuario usuario = exigirOtroUsuario(em, admin, idUsuario, "cambiar tu propio rol");
            if (nuevoRol == null) {
                throw new ValidacionException("Selecciona un rol válido.");
            }
            if (nuevoRol == Rol.ADMINISTRADOR || usuario.getRol() == Rol.ADMINISTRADOR) {
                throw new ValidacionException(MENSAJE_ADMIN_PROTEGIDO);
            }
            if (usuario.getRol() != nuevoRol) {
                Rol anterior = usuario.getRol();
                usuario.setRol(nuevoRol);
                auditoriaService.registrar(em, admin, AccionAuditoria.ACTUALIZAR, "Usuario", usuario.getId(),
                        "Cambió el rol de " + usuario.getCorreo() + ": " + anterior.name().toLowerCase()
                        + " -> " + nuevoRol.name().toLowerCase() + ".");
            }
            return UsuarioDTO.desde(usuario);
        });
    }

    /*
     * Activa, suspende o desactiva a otro usuario. Nadie puede cambiar su
     * propio estado ni el de un administrador.
     * Para volver a ACTIVO a un suspendido, primero debe estar al dia: sin
     * sanciones pendientes.
     */
    public UsuarioDTO cambiarEstado(UsuarioDTO actor, Long idUsuario, EstadoUsuario nuevoEstado) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            Usuario usuario = exigirOtroUsuario(em, admin, idUsuario, "cambiar el estado de tu propia cuenta");
            if (nuevoEstado == null) {
                throw new ValidacionException("Selecciona un estado válido.");
            }
            if (usuario.getRol() == Rol.ADMINISTRADOR) {
                throw new ValidacionException(MENSAJE_ADMIN_PROTEGIDO);
            }
            if (usuario.getEstado() == nuevoEstado) {
                return UsuarioDTO.desde(usuario);
            }
            if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO && nuevoEstado == EstadoUsuario.ACTIVO) {
                long pendientes = sancionDAO.contarPendientesDeUsuario(em, usuario.getId());
                if (pendientes > 0) {
                    throw new ValidacionException("No se puede reactivar: tiene " + pendientes
                            + (pendientes == 1 ? " sanción pendiente." : " sanciones pendientes."));
                }
            }

            EstadoUsuario anterior = usuario.getEstado();
            usuario.setEstado(nuevoEstado);
            auditoriaService.registrar(em, admin, AccionAuditoria.CAMBIO_ESTADO, "Usuario", usuario.getId(),
                    "Cambió el estado de " + usuario.getCorreo() + ": " + anterior.name().toLowerCase()
                    + " -> " + nuevoEstado.name().toLowerCase() + ".");
            return UsuarioDTO.desde(usuario);
        });
    }

    private Usuario exigirUsuario(EntityManager em, Long idUsuario) {
        if (idUsuario == null) {
            throw new ValidacionException("El usuario no existe.");
        }
        return usuarioDAO.buscarPorId(em, idUsuario)
                .orElseThrow(() -> new ValidacionException("El usuario no existe."));
    }

    /* Como exigirUsuario, pero rechaza que el administrador actue sobre su propia cuenta. */
    private Usuario exigirOtroUsuario(EntityManager em, Usuario admin, Long idUsuario, String accion) {
        Usuario usuario = exigirUsuario(em, idUsuario);
        if (usuario.getId().equals(admin.getId())) {
            throw new ValidacionException("No puedes " + accion + ".");
        }
        return usuario;
    }

    private void exigirContrasenaValida(String contrasena) {
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA_CONTRASENA) {
            throw new ValidacionException("La contraseña debe tener al menos "
                    + LONGITUD_MINIMA_CONTRASENA + " caracteres.");
        }
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
        exigirContrasenaValida(contrasena);

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
