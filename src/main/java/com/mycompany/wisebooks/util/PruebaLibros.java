/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.dao.UsuarioDAO;
import com.mycompany.wisebooks.dto.FiltroLibroDTO;
import com.mycompany.wisebooks.dto.LibroDTO;
import com.mycompany.wisebooks.dto.LibroFormDTO;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.service.LibroService;
import com.mycompany.wisebooks.service.UsuarioService;
import java.util.ArrayList;
import java.util.List;

/**
 * Prueba temporal de LibroService (se puede borrar cuando ya no haga falta).
 * Las lineas importantes empiezan con ">>"; busca [OK] y [FALLO].
 *
 * No necesita tu contrasena: toma al administrador directamente de la base
 * por su correo. Crea (una sola vez) un lector de prueba y, en cada ejecucion,
 * un libro nuevo de prueba.
 */
public class PruebaLibros {

    // ====== CAMBIA ESTO por el correo de tu administrador ======
    private static final String ADMIN_CORREO = "admin@wisebooks.com";
    // ===========================================================

    private static final String LECTOR_CORREO = "lector.prueba@wisebooks.com";

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
        LibroService libroService = new LibroService();

        UsuarioDTO admin = buscarUsuario(ADMIN_CORREO);
        if (admin == null || admin.getRol() != Rol.ADMINISTRADOR) {
            System.out.println(">> No encontré un administrador con el correo " + ADMIN_CORREO
                    + ". Cambia la constante ADMIN_CORREO.");
            return;
        }
        UsuarioDTO lector = obtenerLector(usuarioService);
        comprobar("el lector de prueba tiene rol LECTOR", lector.getRol() == Rol.LECTOR);

        long auditoriasAntes = contarAuditoria();
        String sufijo = String.valueOf(System.currentTimeMillis());
        String isbn = "PRU-" + sufijo;
        String titulo = "Libro de prueba " + sufijo;

        // 1. Crear
        System.out.println(">> 1. Crear libro (admin)");
        LibroDTO libro = libroService.crear(admin,
                formulario(titulo, isbn, List.of("Autor de prueba", "Segundo autor"), 3));
        mostrar(libro);
        final Long idLibro = libro.getId();
        comprobar("el libro se creó y tiene id", idLibro != null);
        comprobar("stock total = 3", libro.getStockTotal() == 3);
        comprobar("stock disponible = 3", libro.getStockDisponible() == 3);
        comprobar("tiene 2 autores", libro.getAutores().size() == 2);
        comprobar("categoría Novela", "Novela".equalsIgnoreCase(libro.getCategoria()));

        // 2. Stock
        System.out.println(">> 2. Agregar y retirar copias");
        libro = libroService.agregarCopias(admin, idLibro, 2);
        mostrar(libro);
        comprobar("tras agregar 2: total 5 y disponibles 5",
                libro.getStockTotal() == 5 && libro.getStockDisponible() == 5);
        libro = libroService.retirarCopias(admin, idLibro, 1);
        mostrar(libro);
        comprobar("tras retirar 1: total 4 y disponibles 4",
                libro.getStockTotal() == 4 && libro.getStockDisponible() == 4);

        // 3. Busqueda
        System.out.println(">> 3. Búsqueda");
        FiltroLibroDTO filtro = new FiltroLibroDTO();
        filtro.setTexto(sufijo);
        comprobar("un visitante encuentra el libro por título o ISBN",
                libroService.buscar(null, filtro).size() == 1);
        FiltroLibroDTO porAutor = new FiltroLibroDTO();
        porAutor.setTexto("Segundo autor");
        comprobar("un visitante encuentra el libro por autor",
                libroService.buscar(null, porAutor).stream()
                        .anyMatch(l -> l.getId().equals(idLibro)));

        // 4. Desactivar y reactivar
        System.out.println(">> 4. Desactivar y reactivar");
        libroService.cambiarActivo(admin, idLibro, false);
        comprobar("el visitante ya no ve el libro desactivado",
                libroService.buscar(null, filtro).isEmpty());
        comprobar("el lector tampoco lo ve", libroService.buscar(lector, filtro).isEmpty());
        comprobar("el administrador sí lo ve", libroService.buscar(admin, filtro).size() == 1);
        esperarRechazo("un visitante pide el detalle del libro desactivado",
                ValidacionException.class, () -> libroService.obtener(null, idLibro));
        libroService.cambiarActivo(admin, idLibro, true);
        comprobar("reactivado: el visitante lo ve otra vez",
                libroService.buscar(null, filtro).size() == 1);

        // 5. Rechazos esperados
        System.out.println(">> 5. Cosas que deben ser rechazadas");
        esperarRechazo("un lector intenta crear un libro", PermisoException.class,
                () -> libroService.crear(lector, formulario("No debería crearse", null, List.of("X"), 1)));
        esperarRechazo("un lector intenta agregar copias", PermisoException.class,
                () -> libroService.agregarCopias(lector, idLibro, 1));
        esperarRechazo("un visitante intenta crear un libro", PermisoException.class,
                () -> libroService.crear(null, formulario("No debería crearse", null, List.of("X"), 1)));
        esperarRechazo("crear un libro sin autores", ValidacionException.class,
                () -> libroService.crear(admin, formulario("Sin autor", null, List.of(), 1)));
        esperarRechazo("crear un libro con ISBN repetido", ValidacionException.class,
                () -> libroService.crear(admin, formulario("Otro título", isbn, List.of("Autor de prueba"), 1)));
        esperarRechazo("retirar más copias de las disponibles", ValidacionException.class,
                () -> libroService.retirarCopias(admin, idLibro, 99));
        esperarRechazo("agregar 0 copias", ValidacionException.class,
                () -> libroService.agregarCopias(admin, idLibro, 0));

        libro = libroService.obtener(admin, idLibro);
        comprobar("los rechazos no cambiaron el stock (4 de 4)",
                libro.getStockTotal() == 4 && libro.getStockDisponible() == 4);

        // 6. Auditoria: 5 acciones correctas (crear, agregar, retirar, desactivar, reactivar)
        long nuevas = contarAuditoria() - auditoriasAntes;
        System.out.println(">> Registros nuevos en auditoría: " + nuevas);
        comprobar("auditoría: exactamente 5 registros (los rechazos no dejan huella)", nuevas == 5);

        System.out.println(">> RESULTADO: " + correctas + " de " + total + " comprobaciones correctas.");
    }

    // ---------------------------------------------------------------
    // Ayudas de la prueba
    // ---------------------------------------------------------------

    private static UsuarioDTO buscarUsuario(String correo) {
        return Transaccion.consultar(em -> new UsuarioDAO().buscarPorCorreo(em, correo)
                .map(UsuarioDTO::desde)
                .orElse(null));
    }

    /** Devuelve el lector de prueba; lo crea si todavia no existe. */
    private static UsuarioDTO obtenerLector(UsuarioService usuarioService) {
        UsuarioDTO existente = buscarUsuario(LECTOR_CORREO);
        if (existente != null) {
            return existente;
        }
        RegistroDTO r = new RegistroDTO();
        r.setNombre("Lector");
        r.setApellidos("Prueba");
        r.setTipoDocumento("CC");
        r.setDocumento("2000000002");
        r.setTelefono("3100000000");
        r.setCorreo(LECTOR_CORREO);
        r.setContrasena("PruebaLector1");
        r.setAceptaTerminos(true);
        return usuarioService.registrar(r);
    }

    private static long contarAuditoria() {
        return Transaccion.consultar(em -> em
                .createQuery("SELECT COUNT(a) FROM Auditoria a", Long.class)
                .getSingleResult());
    }

    private static LibroFormDTO formulario(String titulo, String isbn, List<String> autores, int stock) {
        LibroFormDTO f = new LibroFormDTO();
        f.setTitulo(titulo);
        f.setIsbn(isbn);
        f.setDescripcion("Libro creado por PruebaLibros");
        f.setUbicacion("Estante de prueba");
        f.setEditorial("Editorial de prueba");
        f.setCategoria("Novela");
        f.setGenero("Realismo mágico");
        f.setAutores(new ArrayList<>(autores));
        f.setStockInicial(stock);
        return f;
    }

    private static void mostrar(LibroDTO l) {
        System.out.println(">>    id=" + l.getId() + " | " + l.getTitulo()
                + " | autores: " + l.getAutoresTexto()
                + " | stock " + l.getStockDisponible() + "/" + l.getStockTotal()
                + " | activo=" + l.isActivo());
    }

    private static void comprobar(String que, boolean ok) {
        total++;
        if (ok) {
            correctas++;
        }
        System.out.println(">> " + (ok ? "[OK]    " : "[FALLO] ") + que);
    }

    /** La accion DEBE fallar con exactamente ese tipo de excepcion. */
    private static void esperarRechazo(String que, Class<? extends ValidacionException> tipo,
            Runnable accion) {
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
