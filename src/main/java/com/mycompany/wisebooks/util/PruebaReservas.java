/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.dao.UsuarioDAO;
import com.mycompany.wisebooks.dto.LibroDTO;
import com.mycompany.wisebooks.dto.LibroFormDTO;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.ReservaDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Reserva;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.service.LibroService;
import com.mycompany.wisebooks.service.ReservaService;
import com.mycompany.wisebooks.service.UsuarioService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/*
 * Prueba temporal de ReservaService 
 * Las lineas importantes empiezan con ">>"; busca [OK] y [FALLO].
 *
 * Usa al administrador (por su correo, sin contrasena) y dos lectores de prueba
 * (se crean una sola vez). En cada ejecucion crea 4 libros nuevos de prueba y
 * al terminar deja a los lectores sin reservas vivas y en estado ACTIVO.
 */
public class PruebaReservas {

    private static final String ADMIN_CORREO = "admin@wisebooks.com";


    private static final String LECTOR1_CORREO = "lector.prueba@wisebooks.com";
    private static final String LECTOR2_CORREO = "lector2.prueba@wisebooks.com";

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

    private static void probar(final UsuarioDTO admin, final UsuarioDTO lector1,
            final UsuarioDTO lector2) {
        final LibroService libroService = new LibroService();
        final ReservaService reservaService = new ReservaService();

        String sufijo = String.valueOf(System.currentTimeMillis());
        final Long idA = crearLibro(libroService, admin, "Libro A reservas " + sufijo, "RA-" + sufijo, 1).getId();
        final Long idB = crearLibro(libroService, admin, "Libro B reservas " + sufijo, "RB-" + sufijo, 1).getId();
        final Long idC = crearLibro(libroService, admin, "Libro C reservas " + sufijo, "RC-" + sufijo, 1).getId();
        final Long idD = crearLibro(libroService, admin, "Libro D reservas " + sufijo, "RD-" + sufijo, 2).getId();

        // 1. Reservar: aparta una copia y fija el plazo para recogerla
        System.out.println(">> 1. Reservar");
        final ReservaDTO r1 = reservaService.crear(lector1, idA);
        comprobar("la reserva nace PENDIENTE", r1.getEstado() == EstadoReserva.PENDIENTE);
        comprobar("reservar aparta la copia (A con 0 disponibles)",
                disponibles(libroService, admin, idA) == 0);
        comprobar("tiene plazo para recogerla de unos " + ReglasBiblioteca.DIAS_RETIRO_RESERVA + " días",
                r1.getFechaLimiteRetiro() != null
                && r1.getFechaLimiteRetiro().isAfter(LocalDateTime.now().plusDays(ReglasBiblioteca.DIAS_RETIRO_RESERVA - 1))
                && r1.getFechaLimiteRetiro().isBefore(LocalDateTime.now().plusDays(ReglasBiblioteca.DIAS_RETIRO_RESERVA + 1)));
        esperarRechazo("reservar dos veces el mismo libro", ValidacionException.class,
                () -> reservaService.crear(lector1, idA));
        esperarRechazo("un visitante intenta reservar", PermisoException.class,
                () -> reservaService.crear(null, idA));
        esperarRechazo("un administrador intenta reservar (solo lectores)", PermisoException.class,
                () -> reservaService.crear(admin, idA));
        esperarRechazo("reservar un libro que no existe", ValidacionException.class,
                () -> reservaService.crear(lector1, 999999999L));
        esperarRechazo("un lector intenta aprobar", PermisoException.class,
                () -> reservaService.aprobar(lector1, r1.getId()));

        // 2. Sin copias no se puede reservar
        System.out.println(">> 2. Sin copias disponibles");
        esperarRechazo("reservar cuando no quedan copias", ValidacionException.class,
                () -> reservaService.crear(lector2, idA));

        // 3. Aprobar: solo confirma; no toca el stock ni el plazo
        System.out.println(">> 3. Aprobar");
        ReservaDTO r1aprobada = reservaService.aprobar(admin, r1.getId());
        comprobar("queda APROBADA", r1aprobada.getEstado() == EstadoReserva.APROBADA);
        comprobar("aprobar no vuelve a bajar el stock (A sigue con 0)",
                disponibles(libroService, admin, idA) == 0);
        comprobar("aprobar no cambia el plazo para recogerla",
                // Se compara al segundo: la base guarda microsegundos y Java puede tener mas precision
                r1aprobada.getFechaLimiteRetiro().truncatedTo(ChronoUnit.SECONDS)
                        .equals(r1.getFechaLimiteRetiro().truncatedTo(ChronoUnit.SECONDS)));
        esperarRechazo("aprobar una reserva ya aprobada", ValidacionException.class,
                () -> reservaService.aprobar(admin, r1.getId()));
        esperarRechazo("un lector intenta cancelar la reserva de otro", PermisoException.class,
                () -> reservaService.cancelar(lector2, r1.getId()));

        // 4. Cancelar libera la copia (aprobada o pendiente)
        System.out.println(">> 4. Cancelar");
        ReservaDTO r1cancelada = reservaService.cancelar(lector1, r1.getId());
        comprobar("queda CANCELADA", r1cancelada.getEstado() == EstadoReserva.CANCELADA);
        comprobar("cancelar una aprobada devuelve la copia (A con 1)",
                disponibles(libroService, admin, idA) == 1);
        esperarRechazo("cancelar una reserva ya cancelada", ValidacionException.class,
                () -> reservaService.cancelar(lector1, r1.getId()));
        final ReservaDTO r2 = reservaService.crear(lector2, idA);
        comprobar("con la copia libre, otro lector ya puede reservar A (A con 0)",
                r2.getEstado() == EstadoReserva.PENDIENTE && disponibles(libroService, admin, idA) == 0);
        reservaService.cancelar(lector2, r2.getId());
        comprobar("cancelar una pendiente devuelve la copia (A con 1)",
                disponibles(libroService, admin, idA) == 1);

        // 5. Varios lectores pueden reservar el mismo libro (una copia cada uno)
        System.out.println(">> 5. Varios lectores, mismo libro");
        final ReservaDTO d1 = reservaService.crear(lector1, idD);
        final ReservaDTO d2 = reservaService.crear(lector2, idD);
        comprobar("dos lectores reservan D y cada uno aparta su copia (D con 0 de 2)",
                d1.getEstado() == EstadoReserva.PENDIENTE && d2.getEstado() == EstadoReserva.PENDIENTE
                && disponibles(libroService, admin, idD) == 0);
        reservaService.cancelar(lector1, d1.getId());
        reservaService.cancelar(lector2, d2.getId());
        comprobar("al cancelar las dos, D vuelve a 2", disponibles(libroService, admin, idD) == 2);

        // 6. Rechazar devuelve la copia
        System.out.println(">> 6. Rechazar");
        final ReservaDTO r3 = reservaService.crear(lector1, idB);
        ReservaDTO r3rechazada = reservaService.rechazar(admin, r3.getId(), "Sin inventario");
        comprobar("queda RECHAZADA con su observación",
                r3rechazada.getEstado() == EstadoReserva.RECHAZADA
                && "Sin inventario".equals(r3rechazada.getObservaciones()));
        comprobar("rechazar devuelve la copia (B con 1)", disponibles(libroService, admin, idB) == 1);
        esperarRechazo("cancelar una reserva rechazada", ValidacionException.class,
                () -> reservaService.cancelar(lector1, r3.getId()));
        esperarRechazo("un lector intenta rechazar", PermisoException.class,
                () -> reservaService.rechazar(lector1, r3.getId(), null));

        // 7. Usuario suspendido
        System.out.println(">> 7. Usuario suspendido");
        fijarEstado(lector2.getId(), EstadoUsuario.SUSPENDIDO);
        final ReservaDTO s1 = reservaService.crear(lector2, idA);
        final ReservaDTO s2 = reservaService.crear(lector2, idB);
        comprobar("un suspendido puede reservar (2 reservas)",
                s1.getEstado() == EstadoReserva.PENDIENTE && s2.getEstado() == EstadoReserva.PENDIENTE);
        esperarRechazo("un suspendido no puede pasar de 2 reservas", ValidacionException.class,
                () -> reservaService.crear(lector2, idC));
        comprobar("un suspendido sí puede cancelar",
                reservaService.cancelar(lector2, s1.getId()).getEstado() == EstadoReserva.CANCELADA);
        reservaService.cancelar(lector2, s2.getId());
        fijarEstado(lector2.getId(), EstadoUsuario.ACTIVO);

        comprobar("al cancelarlas, A y B vuelven a 1",
                disponibles(libroService, admin, idA) == 1 && disponibles(libroService, admin, idB) == 1);

        // 8. Vencimiento: una PENDIENTE que nadie atendio y una APROBADA que nadie recogio
        System.out.println(">> 8. Vencimiento");
        final ReservaDTO r4 = reservaService.crear(lector1, idC);
        comprobar("C con 0 disponibles tras reservar", disponibles(libroService, admin, idC) == 0);
        envejecer(r4.getId());
        ReservaDTO r4vencida = buscarEnMisReservas(reservaService, lector1, r4.getId());
        comprobar("la reserva PENDIENTE con el plazo cumplido pasó a VENCIDA",
                r4vencida != null && r4vencida.getEstado() == EstadoReserva.VENCIDA);
        comprobar("queda la observación de por qué venció",
                r4vencida != null && r4vencida.getObservaciones() != null
                && r4vencida.getObservaciones().contains("no se recogió"));
        comprobar("al vencer, la copia volvió (C con 1)", disponibles(libroService, admin, idC) == 1);

        final ReservaDTO r5 = reservaService.crear(lector1, idC);
        reservaService.aprobar(admin, r5.getId());
        envejecer(r5.getId());
        comprobar("la reserva APROBADA sin recoger también pasó a VENCIDA",
                estadoDe(reservaService, lector1, r5.getId()) == EstadoReserva.VENCIDA);
        comprobar("y su copia volvió (C con 1)", disponibles(libroService, admin, idC) == 1);

        // 9. Listados
        System.out.println(">> 9. Listados");
        comprobar("el personal ve las dos reservas vencidas en el listado",
                reservaService.listar(admin, EstadoReserva.VENCIDA).stream()
                        .filter(x -> x.getId().equals(r4.getId()) || x.getId().equals(r5.getId()))
                        .count() == 2);
        esperarRechazo("un lector intenta listar todas las reservas", PermisoException.class,
                () -> reservaService.listar(lector1, null));
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
        RegistroDTO r = new RegistroDTO();
        r.setNombre(nombre);
        r.setApellidos("Prueba");
        r.setTipoDocumento("CC");
        r.setDocumento(documento);
        r.setTelefono("3100000000");
        r.setCorreo(correo);
        r.setContrasena("PruebaLector1");
        r.setAceptaTerminos(true);
        return usuarioService.registrar(r);
    }

    private static LibroDTO crearLibro(LibroService libroService, UsuarioDTO admin,
            String titulo, String isbn, int copias) {
        LibroFormDTO f = new LibroFormDTO();
        f.setTitulo(titulo);
        f.setIsbn(isbn);
        f.setDescripcion("Libro creado por PruebaReservas");
        f.setUbicacion("Estante de prueba");
        f.setEditorial("Editorial de prueba");
        f.setCategoria("Novela");
        f.setGenero("Realismo mágico");
        f.setAutores(new ArrayList<>(List.of("Autor de prueba")));
        f.setStockInicial(copias);
        return libroService.crear(admin, f);
    }

    private static int disponibles(LibroService libroService, UsuarioDTO admin, Long idLibro) {
        return libroService.obtener(admin, idLibro).getStockDisponible();
    }

    /**
     * Estado actual de una reserva, leyendola desde "mis reservas" del lector.
     */
    private static EstadoReserva estadoDe(ReservaService reservaService, UsuarioDTO lector, Long idReserva) {
        ReservaDTO reserva = buscarEnMisReservas(reservaService, lector, idReserva);
        return reserva == null ? null : reserva.getEstado();
    }

    /**
     * Una reserva tal como la ve el lector en "mis reservas" (o null si no esta).
     */
    private static ReservaDTO buscarEnMisReservas(ReservaService reservaService, UsuarioDTO lector,
            Long idReserva) {
        return reservaService.misReservas(lector).stream()
                .filter(x -> x.getId().equals(idReserva))
                .findFirst()
                .orElse(null);
    }

    /**
     * Cambia el estado de un usuario directamente en la base (solo para la
     * prueba).
     */
    private static void fijarEstado(Long idUsuario, EstadoUsuario estado) {
        Transaccion.ejecutarAccion(em -> em.find(Usuario.class, idUsuario).setEstado(estado));
    }

    /**
     * Hace que el plazo de retiro de una reserva ya haya pasado (solo para la
     * prueba).
     */
    private static void envejecer(Long idReserva) {
        Transaccion.ejecutarAccion(em -> {
            Reserva r = em.find(Reserva.class, idReserva);
            r.setFechaReserva(LocalDateTime.now().minusDays(5));
            r.setFechaLimiteRetiro(LocalDateTime.now().minusDays(1));
        });
    }

    /**
     * Deja a los lectores en ACTIVO y sin reservas vivas. No devuelve las
     * copias apartadas: solo afecta a los libros de prueba de una ejecucion
     * anterior, que no se vuelven a usar (cada ejecucion crea los suyos).
     */
    private static void limpiar(UsuarioDTO lector1, UsuarioDTO lector2) {
        fijarEstado(lector2.getId(), EstadoUsuario.ACTIVO);
        Transaccion.ejecutarAccion(em -> em.createQuery(
                "UPDATE Reserva r SET r.estado = :nuevo "
                + "WHERE r.usuario.id IN (:ids) AND r.estado IN (:vivas)")
                .setParameter("nuevo", EstadoReserva.CANCELADA)
                .setParameter("ids", List.of(lector1.getId(), lector2.getId()))
                .setParameter("vivas", List.of(EstadoReserva.PENDIENTE, EstadoReserva.APROBADA))
                .executeUpdate());
    }

    private static void comprobar(String que, boolean ok) {
        total++;
        if (ok) {
            correctas++;
        }
        System.out.println(">> " + (ok ? "[OK]    " : "[FALLO] ") + que);
    }

    /**
     * La accion DEBE fallar con exactamente ese tipo de excepcion.
     */
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
