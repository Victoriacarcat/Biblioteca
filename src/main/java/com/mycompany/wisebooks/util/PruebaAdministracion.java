package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.dao.UsuarioDAO;
import com.mycompany.wisebooks.dto.AuditoriaDTO;
import com.mycompany.wisebooks.dto.CopiaSeguridadDTO;
import com.mycompany.wisebooks.dto.EstadisticasDTO;
import com.mycompany.wisebooks.dto.LibroDTO;
import com.mycompany.wisebooks.dto.LibroFormDTO;
import com.mycompany.wisebooks.dto.LoginDTO;
import com.mycompany.wisebooks.dto.ProveedorDTO;
import com.mycompany.wisebooks.dto.ProveedorFormDTO;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.SancionDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.EstadoSancion;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.TipoSancion;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.service.AuditoriaService;
import com.mycompany.wisebooks.service.CopiaSeguridadService;
import com.mycompany.wisebooks.service.DashboardService;
import com.mycompany.wisebooks.service.FavoritoService;
import com.mycompany.wisebooks.service.LibroService;
import com.mycompany.wisebooks.service.ProveedorService;
import com.mycompany.wisebooks.service.SancionService;
import com.mycompany.wisebooks.service.UsuarioService;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 * Prueba temporal de UsuarioService (administracion), FavoritoService,
 * ProveedorService, AuditoriaService (consultas), DashboardService y
 * CopiaSeguridadService.
 * Las lineas importantes empiezan con ">>"; busca [OK] y [FALLO].
 *
 * Usa al administrador, los dos lectores de prueba y un bibliotecario de
 * prueba (se crean una sola vez). Los respaldos de prueba se guardan en
 * target/respaldos-prueba (no en la carpeta real de respaldos).
 */
public class PruebaAdministracion {

    private static final String ADMIN_CORREO = "admin@wisebooks.com";
    private static final String LECTOR1_CORREO = "lector.prueba@wisebooks.com";
    private static final String LECTOR2_CORREO = "lector2.prueba@wisebooks.com";
    private static final String BIBLIO_CORREO = "biblio.prueba@wisebooks.com";
    private static final String CONTRASENA_LECTOR = "PruebaLector1";

    private static int total = 0;
    private static int correctas = 0;

    public static void main(String[] args) {
        try {
            ejecutar();
        } catch (RuntimeException e) {
            System.out.println(">> La prueba se detuvo por un error inesperado:");
            e.printStackTrace(System.out);
        } finally {
            JPAUtil.cerrar();
        }
    }

    private static void ejecutar() {
        UsuarioService usuarioService = new UsuarioService();
        UsuarioDTO admin = buscarUsuario(ADMIN_CORREO);
        if (admin == null || admin.getRol() != Rol.ADMINISTRADOR) {
            System.out.println(">> No encontré un administrador con el correo " + ADMIN_CORREO
                    + ". Cambia la constante ADMIN_CORREO.");
            return;
        }
        UsuarioDTO lector1 = obtenerLector(usuarioService, LECTOR1_CORREO, "2000000002", "Lector");
        UsuarioDTO lector2 = obtenerLector(usuarioService, LECTOR2_CORREO, "2000000003", "Lector2");

        limpiar(lector1, lector2);
        try {
            probar(admin, lector1, lector2);
        } finally {
            limpiar(lector1, lector2);
        }
        System.out.println(">> RESULTADO: " + correctas + " de " + total + " comprobaciones correctas.");
    }

    private static void probar(final UsuarioDTO admin, final UsuarioDTO lector1, final UsuarioDTO lector2) {
        final UsuarioService usuarioService = new UsuarioService();
        final SancionService sancionService = new SancionService();
        final String sufijo = String.valueOf(System.currentTimeMillis());

        System.out.println(">> 1. Crear personal");
        UsuarioDTO biblio = buscarUsuario(BIBLIO_CORREO);
        if (biblio == null) {
            biblio = usuarioService.crearPersonal(admin,
                    registro("Bibliotecario", BIBLIO_CORREO, "2000000099"), Rol.BIBLIOTECARIO);
        }
        final UsuarioDTO bibliotecario = biblio;
        comprobar("existe un bibliotecario de prueba", bibliotecario.getRol() == Rol.BIBLIOTECARIO);
        esperarRechazo("crear un lector desde la administración", ValidacionException.class,
                () -> usuarioService.crearPersonal(admin, registro("X", "x" + sufijo + "@wisebooks.com",
                        "9" + sufijo.substring(4)), Rol.LECTOR));
        esperarRechazo("un bibliotecario intenta crear personal", PermisoException.class,
                () -> usuarioService.crearPersonal(bibliotecario, registro("X", "y" + sufijo + "@wisebooks.com",
                        "8" + sufijo.substring(4)), Rol.BIBLIOTECARIO));

        System.out.println(">> 2. Listar y cambiar rol");
        comprobar("el admin busca lectores por texto",
                usuarioService.listar(admin, "lector.prueba", Rol.LECTOR).stream()
                        .anyMatch(u -> u.getId().equals(lector1.getId())));
        esperarRechazo("un lector intenta listar usuarios", PermisoException.class,
                () -> usuarioService.listar(lector1, null, null));
        esperarRechazo("el admin intenta cambiarse su propio rol", ValidacionException.class,
                () -> usuarioService.cambiarRol(admin, admin.getId(), Rol.LECTOR));
        comprobar("el admin cambia el rol de otro (lector -> bibliotecario)",
                usuarioService.cambiarRol(admin, lector2.getId(), Rol.BIBLIOTECARIO).getRol() == Rol.BIBLIOTECARIO);
        usuarioService.cambiarRol(admin, lector2.getId(), Rol.LECTOR);

        System.out.println(">> 3. Suspender y reactivar");
        esperarRechazo("el admin intenta cambiar su propio estado", ValidacionException.class,
                () -> usuarioService.cambiarEstado(admin, admin.getId(), EstadoUsuario.SUSPENDIDO));
        comprobar("suspender a un lector",
                usuarioService.cambiarEstado(admin, lector2.getId(), EstadoUsuario.SUSPENDIDO).getEstado()
                == EstadoUsuario.SUSPENDIDO);
        SancionDTO deuda = sancionService.registrar(admin, lector2.getId(), null, TipoSancion.OTRO,
                new BigDecimal("1000"), "Prueba de reactivación");
        esperarRechazo("reactivar con sanciones pendientes", ValidacionException.class,
                () -> usuarioService.cambiarEstado(admin, lector2.getId(), EstadoUsuario.ACTIVO));
        sancionService.pagar(admin, deuda.getId());
        comprobar("al pagar, se puede reactivar",
                usuarioService.cambiarEstado(admin, lector2.getId(), EstadoUsuario.ACTIVO).getEstado()
                == EstadoUsuario.ACTIVO);

        System.out.println(">> 4. Cambiar contraseña");
        esperarRechazo("con la contraseña actual equivocada", ValidacionException.class,
                () -> usuarioService.cambiarContrasena(lector1, "otra-cosa", "NuevaClave123"));
        esperarRechazo("con una contraseña nueva muy corta", ValidacionException.class,
                () -> usuarioService.cambiarContrasena(lector1, CONTRASENA_LECTOR, "corta"));
        usuarioService.cambiarContrasena(lector1, CONTRASENA_LECTOR, "NuevaClave123");
        comprobar("inicia sesión con la contraseña nueva", iniciaSesion(usuarioService, LECTOR1_CORREO, "NuevaClave123"));
        comprobar("ya no entra con la anterior", !iniciaSesion(usuarioService, LECTOR1_CORREO, CONTRASENA_LECTOR));
        usuarioService.cambiarContrasena(lector1, "NuevaClave123", CONTRASENA_LECTOR);

        System.out.println(">> 5. Favoritos");
        final FavoritoService favoritoService = new FavoritoService();
        final Long idLibro = crearLibro(admin, "Libro favoritos " + sufijo, "FAV-" + sufijo).getId();
        favoritoService.agregar(lector1, idLibro);
        favoritoService.agregar(lector1, idLibro);
        comprobar("agregar a favoritos", favoritoService.esFavorito(lector1, idLibro));
        comprobar("agregarlo dos veces no lo duplica",
                favoritoService.misFavoritos(lector1).stream().filter(l -> l.getId().equals(idLibro)).count() == 1);
        favoritoService.quitar(lector1, idLibro);
        comprobar("quitar de favoritos", !favoritoService.esFavorito(lector1, idLibro));
        esperarRechazo("el admin intenta usar favoritos", PermisoException.class,
                () -> favoritoService.agregar(admin, idLibro));
        esperarRechazo("agregar un libro que no existe", ValidacionException.class,
                () -> favoritoService.agregar(lector1, -1L));

        System.out.println(">> 6. Proveedores");
        final ProveedorService proveedorService = new ProveedorService();
        final String nombre = "Distribuidora Prueba " + sufijo;
        final String nit = "9" + sufijo.substring(sufijo.length() - 8) + "-1";
        ProveedorDTO prov = proveedorService.crear(admin, proveedor(nombre, nit, "ventas@distribuidora.com"));
        comprobar("crear proveedor (correo guardado en minúsculas)",
                prov.getId() != null && prov.isActivo() && "ventas@distribuidora.com".equals(prov.getCorreo()));
        esperarRechazo("nombre repetido (sin importar mayúsculas)", ValidacionException.class,
                () -> proveedorService.crear(admin, proveedor(nombre.toUpperCase(), null, null)));
        esperarRechazo("NIT repetido", ValidacionException.class,
                () -> proveedorService.crear(admin, proveedor("Otro " + sufijo, nit, null)));
        esperarRechazo("NIT con letras", ValidacionException.class,
                () -> proveedorService.crear(admin, proveedor("Otro " + sufijo, "ABC123", null)));
        esperarRechazo("correo inválido", ValidacionException.class,
                () -> proveedorService.crear(admin, proveedor("Otro " + sufijo, null, "no-es-correo")));
        esperarRechazo("un bibliotecario intenta crear un proveedor", PermisoException.class,
                () -> proveedorService.crear(bibliotecario, proveedor("Otro " + sufijo, null, null)));
        comprobar("editar proveedor",
                "Ana Ruiz".equals(proveedorService.editar(admin, prov.getId(),
                        proveedorConContacto(nombre, nit, "Ana Ruiz")).getPersonaContacto()));
        comprobar("el bibliotecario sí puede consultarlos",
                proveedorService.listar(bibliotecario, sufijo, false).size() == 1);
        proveedorService.cambiarActivo(admin, prov.getId(), false);
        comprobar("un proveedor desactivado no sale en la lista normal",
                proveedorService.listar(admin, sufijo, false).isEmpty()
                && proveedorService.listar(admin, sufijo, true).size() == 1);
        esperarRechazo("un lector intenta ver los proveedores", PermisoException.class,
                () -> proveedorService.listar(lector1, null, false));

        System.out.println(">> 7. Auditoría");
        final AuditoriaService auditoriaService = new AuditoriaService();
        LocalDate hoy = LocalDate.now();
        List<AuditoriaDTO> deHoy = auditoriaService.listar(admin, null, null, hoy, hoy);
        comprobar("hay registros de hoy", !deHoy.isEmpty());
        comprobar("filtrar por acción (CREAR) solo trae esa acción",
                auditoriaService.listar(admin, null, AccionAuditoria.CREAR, hoy, hoy).stream()
                        .allMatch(a -> a.getAccion() == AccionAuditoria.CREAR));
        List<AuditoriaDTO> historial = auditoriaService.historialDe(admin, "Proveedor", prov.getId());
        comprobar("historial del proveedor: crear, editar y desactivar (3)", historial.size() == 3);
        esperarRechazo("un bibliotecario intenta ver la auditoría", PermisoException.class,
                () -> auditoriaService.listar(bibliotecario, null, null, null, null));
        esperarRechazo("fecha inicial posterior a la final", ValidacionException.class,
                () -> auditoriaService.listar(admin, null, null, hoy, hoy.minusDays(1)));

        System.out.println(">> 8. Dashboard");
        final DashboardService dashboardService = new DashboardService();
        EstadisticasDTO publico = dashboardService.estadisticas(null);
        comprobar("el visitante ve los números públicos", publico.getTotalLibros() > 0 && publico.getLectoresActivos() > 0);
        comprobar("el visitante NO ve los datos internos", publico.getCopiasTotales() == null
                && publico.getMontoSancionesPendientes() == null);
        EstadisticasDTO interno = dashboardService.estadisticas(bibliotecario);
        comprobar("el personal ve los datos internos", interno.getCopiasTotales() != null
                && interno.getPorcentajeOcupacion() != null);
        comprobar("el lector solo ve los públicos", dashboardService.estadisticas(lector1).getCopiasTotales() == null);
        comprobar("el resumen del lector funciona", dashboardService.resumenLector(lector1) != null);
        esperarRechazo("el admin pide el resumen de lector", PermisoException.class,
                () -> dashboardService.resumenLector(admin));

        System.out.println(">> 9. Copias de seguridad");
        final CopiaSeguridadService copiaService = new CopiaSeguridadService();
        String carpetaAnterior = System.getProperty("wisebooks.respaldos");
        String dumpAnterior = System.getProperty("wisebooks.mysqldump");
        System.setProperty("wisebooks.respaldos", Path.of("target", "respaldos-prueba").toString());
        try {
            CopiaSeguridadDTO copia = copiaService.generar(admin);
            comprobar("se generó el respaldo (" + copia.getNombreArchivo() + ", " + copia.getTamanoTexto() + ")",
                    copia.isExitosa() && copia.getTamanoBytes() != null && copia.getTamanoBytes() > 0);
            Path archivo = copiaService.obtenerArchivo(admin, copia.getId());
            comprobar("el archivo existe y contiene la tabla libros", contiene(archivo, "CREATE TABLE `libros`"));
            comprobar("aparece en la lista de respaldos",
                    copiaService.listar(admin).stream().anyMatch(c -> c.getId().equals(copia.getId())));
            esperarRechazo("un bibliotecario intenta generar un respaldo", PermisoException.class,
                    () -> copiaService.generar(bibliotecario));

            System.setProperty("wisebooks.mysqldump", Path.of("target", "no-existe", "mysqldump.exe").toString());
            esperarRechazo("mysqldump en una ruta que no existe", ValidacionException.class,
                    () -> copiaService.generar(admin));
            comprobar("el respaldo fallido también queda registrado",
                    !copiaService.listar(admin).get(0).isExitosa());
        } finally {
            restaurar("wisebooks.respaldos", carpetaAnterior);
            restaurar("wisebooks.mysqldump", dumpAnterior);
        }
    }

    // Ayudas de la prueba

    private static UsuarioDTO buscarUsuario(String correo) {
        return Transaccion.consultar(em -> new UsuarioDAO().buscarPorCorreo(em, correo)
                .map(UsuarioDTO::desde)
                .orElse(null));
    }

    private static UsuarioDTO obtenerLector(UsuarioService usuarioService, String correo,
            String documento, String nombre) {
        UsuarioDTO existente = buscarUsuario(correo);
        if (existente != null) {
            return existente;
        }
        RegistroDTO r = registro(nombre, correo, documento);
        r.setAceptaTerminos(true);
        return usuarioService.registrar(r);
    }

    private static RegistroDTO registro(String nombre, String correo, String documento) {
        RegistroDTO r = new RegistroDTO();
        r.setNombre(nombre);
        r.setApellidos("Prueba");
        r.setTipoDocumento("CC");
        r.setDocumento(documento);
        r.setTelefono("3100000000");
        r.setCorreo(correo);
        r.setContrasena(CONTRASENA_LECTOR);
        return r;
    }

    private static boolean iniciaSesion(UsuarioService usuarioService, String correo, String contrasena) {
        LoginDTO login = new LoginDTO();
        login.setCorreo(correo);
        login.setContrasena(contrasena);
        try {
            usuarioService.iniciarSesion(login);
            return true;
        } catch (ValidacionException e) {
            return false;
        }
    }

    private static LibroDTO crearLibro(UsuarioDTO admin, String titulo, String isbn) {
        LibroFormDTO f = new LibroFormDTO();
        f.setTitulo(titulo);
        f.setIsbn(isbn);
        f.setCategoria("Novela");
        f.setAutores(new ArrayList<>(List.of("Autor de prueba")));
        f.setStockInicial(1);
        return new LibroService().crear(admin, f);
    }

    private static ProveedorFormDTO proveedor(String nombre, String nit, String correo) {
        ProveedorFormDTO f = new ProveedorFormDTO();
        f.setNombre(nombre);
        f.setNit(nit);
        f.setCorreo(correo == null ? null : correo.toUpperCase());
        f.setTelefono("601 555 1234");
        return f;
    }

    private static ProveedorFormDTO proveedorConContacto(String nombre, String nit, String contacto) {
        ProveedorFormDTO f = proveedor(nombre, nit, "ventas@distribuidora.com");
        f.setPersonaContacto(contacto);
        return f;
    }

    private static boolean contiene(Path archivo, String texto) {
        try {
            return Files.readString(archivo).contains(texto);
        } catch (java.io.IOException e) {
            return false;
        }
    }

    private static void restaurar(String propiedad, String valor) {
        if (valor == null) {
            System.clearProperty(propiedad);
        } else {
            System.setProperty(propiedad, valor);
        }
    }

    /* Deja a los lectores ACTIVO, como LECTOR y sin sanciones pendientes. */
    private static void limpiar(UsuarioDTO lector1, UsuarioDTO lector2) {
        Transaccion.ejecutarAccion(em -> {
            for (Long id : List.of(lector1.getId(), lector2.getId())) {
                Usuario u = em.find(Usuario.class, id);
                u.setEstado(EstadoUsuario.ACTIVO);
                u.setRol(Rol.LECTOR);
            }
            em.createQuery("UPDATE Sancion s SET s.estado = :nuevo "
                    + "WHERE s.usuario.id IN (:ids) AND s.estado = :pendiente")
                    .setParameter("nuevo", EstadoSancion.ANULADA)
                    .setParameter("ids", List.of(lector1.getId(), lector2.getId()))
                    .setParameter("pendiente", EstadoSancion.PENDIENTE)
                    .executeUpdate();
        });
    }

    private static void comprobar(String que, boolean ok) {
        total++;
        if (ok) {
            correctas++;
        }
        System.out.println(">> " + (ok ? "[OK]    " : "[FALLO] ") + que);
    }

    /* La accion DEBE fallar con exactamente ese tipo de excepcion. */
    private static void esperarRechazo(String que, Class<? extends ValidacionException> tipo, Runnable accion) {
        total++;
        try {
            accion.run();
            System.out.println(">> [FALLO] " + que + " -> NO fue rechazado");
        } catch (ValidacionException e) {
            if (e.getClass().equals(tipo)) {
                correctas++;
                System.out.println(">> [OK]    " + que + " -> rechazado: " + e.getMessage());
            } else {
                System.out.println(">> [FALLO] " + que + " -> se rechazó con otro tipo ("
                        + e.getClass().getSimpleName() + "): " + e.getMessage());
            }
        }
    }
}
