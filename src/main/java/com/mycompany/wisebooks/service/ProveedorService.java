package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.ProveedorDAO;
import com.mycompany.wisebooks.dto.ProveedorDTO;
import com.mycompany.wisebooks.dto.ProveedorFormDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.Proveedor;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.regex.Pattern;

/*
 * Directorio de proveedores de libros.
 *
 * Quien puede hacer que:
 *   - Consultar: ADMINISTRADOR y BIBLIOTECARIO.
 *   - Crear, editar, activar y desactivar: solo el ADMINISTRADOR.
 *
 * Un proveedor no se borra: se desactiva (asi se conserva el historial).
 */
public class ProveedorService {

    // Largos maximos: coinciden con las columnas de la entidad Proveedor
    private static final int MAX_NOMBRE = 150;
    private static final int MAX_NIT = 30;
    private static final int MAX_CONTACTO = 150;
    private static final int MAX_TELEFONO = 20;
    private static final int MAX_CORREO = 150;
    private static final int MAX_DIRECCION = 255;

    // Mismos formatos que UsuarioService
    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_TELEFONO =
            Pattern.compile("^[0-9+\\s-]{7,20}$");
    // NIT colombiano: numeros con guion opcional para el digito de verificacion (900123456-7)
    private static final Pattern PATRON_NIT =
            Pattern.compile("^[0-9]{5,15}(-[0-9])?$");

    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final AuditoriaService auditoriaService = new AuditoriaService();

    private record DatosProveedor(String nombre, String nit, String personaContacto,
            String telefono, String correo, String direccion) {
    }

    // Consultas

    /* Lista en orden alfabetico. El texto (opcional) busca en nombre, NIT y contacto. */
    public List<ProveedorDTO> listar(UsuarioDTO actor, String texto, boolean incluirInactivos) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            return proveedorDAO.listar(em, texto, incluirInactivos).stream()
                    .map(ProveedorDTO::desde)
                    .toList();
        });
    }

    public ProveedorDTO obtener(UsuarioDTO actor, Long idProveedor) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            return ProveedorDTO.desde(exigirProveedor(em, idProveedor));
        });
    }

    // Cambios (solo administrador)

    public ProveedorDTO crear(UsuarioDTO actor, ProveedorFormDTO form) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            DatosProveedor datos = validar(form);
            exigirUnicos(em, datos, null);

            Proveedor proveedor = new Proveedor();
            aplicar(proveedor, datos);
            proveedorDAO.guardar(em, proveedor);

            auditoriaService.registrar(em, admin, AccionAuditoria.CREAR, "Proveedor", proveedor.getId(),
                    "Creó el proveedor \"" + datos.nombre() + "\".");
            return ProveedorDTO.desde(proveedor);
        });
    }

    public ProveedorDTO editar(UsuarioDTO actor, Long idProveedor, ProveedorFormDTO form) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            Proveedor proveedor = exigirProveedor(em, idProveedor);
            DatosProveedor datos = validar(form);
            exigirUnicos(em, datos, proveedor.getId());

            aplicar(proveedor, datos);

            auditoriaService.registrar(em, admin, AccionAuditoria.ACTUALIZAR, "Proveedor", proveedor.getId(),
                    "Editó el proveedor \"" + datos.nombre() + "\".");
            return ProveedorDTO.desde(proveedor);
        });
    }

    public ProveedorDTO cambiarActivo(UsuarioDTO actor, Long idProveedor, boolean activo) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            Proveedor proveedor = exigirProveedor(em, idProveedor);

            if (proveedor.isActivo() != activo) {
                proveedor.setActivo(activo);
                auditoriaService.registrar(em, admin, AccionAuditoria.CAMBIO_ESTADO, "Proveedor", proveedor.getId(),
                        (activo ? "Activó" : "Desactivó") + " el proveedor \"" + proveedor.getNombre() + "\".");
            }
            return ProveedorDTO.desde(proveedor);
        });
    }

    // Ayudas internas

    private Proveedor exigirProveedor(EntityManager em, Long idProveedor) {
        if (idProveedor == null) {
            throw new ValidacionException("El proveedor no existe.");
        }
        return proveedorDAO.buscarPorId(em, idProveedor)
                .orElseThrow(() -> new ValidacionException("El proveedor no existe."));
    }

    /* Nombre y NIT no pueden repetirse en otro proveedor. */
    private void exigirUnicos(EntityManager em, DatosProveedor datos, Long idActual) {
        if (proveedorDAO.existeNombre(em, datos.nombre(), idActual)) {
            throw new ValidacionException("Ya existe un proveedor con ese nombre.");
        }
        if (datos.nit() != null && proveedorDAO.existeNit(em, datos.nit(), idActual)) {
            throw new ValidacionException("Ya existe un proveedor con ese NIT.");
        }
    }

    /* Valida y limpia los datos del formulario (sin tocar la base). */
    private DatosProveedor validar(ProveedorFormDTO form) {
        if (form == null) {
            throw new ValidacionException("Faltan los datos del proveedor.");
        }
        String nit = Texto.opcional(form.getNit(), "El NIT", MAX_NIT);
        if (nit != null && !PATRON_NIT.matcher(nit).matches()) {
            throw new ValidacionException("El NIT solo puede tener números y un guion antes del dígito de verificación.");
        }
        String telefono = Texto.opcional(form.getTelefono(), "El teléfono", MAX_TELEFONO);
        if (telefono != null && !PATRON_TELEFONO.matcher(telefono).matches()) {
            throw new ValidacionException("El teléfono no es válido.");
        }
        String correo = Texto.opcional(form.getCorreo(), "El correo", MAX_CORREO);
        if (correo != null) {
            correo = correo.toLowerCase();
            if (!PATRON_CORREO.matcher(correo).matches()) {
                throw new ValidacionException("El correo no es válido.");
            }
        }
        return new DatosProveedor(
                Texto.exigir(form.getNombre(), "El nombre", MAX_NOMBRE),
                nit,
                Texto.opcional(form.getPersonaContacto(), "La persona de contacto", MAX_CONTACTO),
                telefono,
                correo,
                Texto.opcional(form.getDireccion(), "La dirección", MAX_DIRECCION));
    }

    private void aplicar(Proveedor proveedor, DatosProveedor datos) {
        proveedor.setNombre(datos.nombre());
        proveedor.setNit(datos.nit());
        proveedor.setPersonaContacto(datos.personaContacto());
        proveedor.setTelefono(datos.telefono());
        proveedor.setCorreo(datos.correo());
        proveedor.setDireccion(datos.direccion());
    }
}
