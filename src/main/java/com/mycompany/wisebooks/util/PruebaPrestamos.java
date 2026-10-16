package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.dao.UsuarioDAO;
import com.mycompany.wisebooks.dto.LibroDTO;
import com.mycompany.wisebooks.dto.LibroFormDTO;
import com.mycompany.wisebooks.dto.PrestamoDTO;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.ReservaDTO;
import com.mycompany.wisebooks.dto.ResumenLectorDTO;
import com.mycompany.wisebooks.dto.SancionDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.EstadoSancion;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Prestamo;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.TipoSancion;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.service.DashboardService;
import com.mycompany.wisebooks.service.LibroService;
import com.mycompany.wisebooks.service.PrestamoService;
import com.mycompany.wisebooks.service.ReservaService;
import com.mycompany.wisebooks.service.SancionService;
import com.mycompany.wisebooks.service.UsuarioService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/*
 * Prueba temporal de PrestamoService y SancionService.
 * Las lineas importantes empiezan con ">>"; busca [OK] y [FALLO].
 *
 * Usa al administrador (por su correo, sin contrasena) y los dos lectores de
 * prueba de PruebaReservas (se crean una sola vez). En cada ejecucion crea
 * libros nuevos de prueba. Al empezar y al terminar deja a los lectores en
 * ACTIVO, sin prestamos activos, sin reservas vivas y sin sanciones pendientes.
 */
public class PruebaPrestamos {

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

    private static void probar(final UsuarioDTO admin, final UsuarioDTO lector1, final UsuarioDTO lector2) {
        final LibroService libroService = new LibroService();
        final ReservaService reservaService = new ReservaService();
        final PrestamoService prestamoService = new PrestamoService();
        final SancionService sancionService = new SancionService();
        final DashboardService dashboardService = new DashboardService();

        String sufijo = String.valueOf(System.currentTimeMillis());
        final Long idA = crearLibro(libroService, admin, "Libro A préstamos " + sufijo, "PA-" + sufijo, 2).getId();
        final Long idB = crearLibro(libroService, admin, "Libro B préstamos " + sufijo, "PB-" + sufijo, 1).getId();
        final Long idC = crearLibro(libroService, admin, "Libro C préstamos " + sufijo, "PC-" + sufijo, 1).getId();

        System.out.println(">> 1. Préstamo directo en el mostrador");
        PrestamoDTO pA = prestamoService.entregarDirecto(admin, lector1.getId(), idA);
        comprobar("el préstamo nace ACTIVO", pA.getEstado() == EstadoPrestamo.ACTIVO);
        comprobar("la fecha límite es en " + ReglasBiblioteca.DIAS_PRESTAMO + " días",
                pA.getFechaLimite().toLocalDate().equals(
                        pA.getFechaPrestamo().toLocalDate().plusDays(ReglasBiblioteca.DIAS_PRESTAMO)));
        comprobar("prestar baja el stock disponible (A con 1 de 2)", disponibles(libroService, admin, idA) == 1);
        comprobar("queda registrado quién lo entregó", pA.getRegistradoPor() != null);
        esperarRechazo("un lector intenta registrar un préstamo", PermisoException.class,
                () -> prestamoService.entregarDirecto(lector1, lector1.getId(), idA));
        esperarRechazo("prestar dos veces el mismo libro al mismo lector", ValidacionException.class,
                () -> prestamoService.entregarDirecto(admin, lector1.getId(), idA));
        esperarRechazo("prestarle un libro al administrador", ValidacionException.class,
                () -> prestamoService.entregarDirecto(admin, admin.getId(), idA));
        esperarRechazo("prestar un libro que no existe", ValidacionException.class,
                () -> prestamoService.entregarDirecto(admin, lector1.getId(), -1L));
        fijarEstado(lector2.getId(), EstadoUsuario.SUSPENDIDO);
        esperarRechazo("prestarle a un lector suspendido", ValidacionException.class,
                () -> prestamoService.entregarDirecto(admin, lector2.getId(), idA));
        fijarEstado(lector2.getId(), EstadoUsuario.ACTIVO);

        System.out.println(">> 2. Entrega de una reserva aprobada");
        ReservaDTO rB = reservaService.crear(lector2, idB);
        esperarRechazo("entregar una reserva que todavía está pendiente", ValidacionException.class,
                () -> prestamoService.entregarReserva(admin, rB.getId()));
        reservaService.aprobar(admin, rB.getId());
        PrestamoDTO pB = prestamoService.entregarReserva(admin, rB.getId());
        comprobar("el préstamo queda ligado a la reserva", rB.getId().equals(pB.getIdReserva()));
        comprobar("la reserva pasa a RETIRADA", estadoReserva(reservaService, lector2, rB.getId()) == EstadoReserva.RETIRADA);
        comprobar("el stock no baja dos veces (B sigue con 0)", disponibles(libroService, admin, idB) == 0);
        esperarRechazo("entregar dos veces la misma reserva", ValidacionException.class,
                () -> prestamoService.entregarReserva(admin, rB.getId()));

        System.out.println(">> 3. Prioridad de las reservas pendientes");
        ReservaDTO rC = reservaService.crear(lector2, idC);
        esperarRechazo("prestar directo un libro que otro lector está esperando", ValidacionException.class,
                () -> prestamoService.entregarDirecto(admin, lector1.getId(), idC));
        esperarRechazo("prestar directo un libro que el propio lector tiene reservado", ValidacionException.class,
                () -> prestamoService.entregarDirecto(admin, lector2.getId(), idC));
        reservaService.cancelar(lector2, rC.getId());

        System.out.println(">> 4. Renovar");
        PrestamoDTO renovado = prestamoService.renovar(lector1, pA.getId());
        comprobar("la renovación suma " + ReglasBiblioteca.DIAS_RENOVACION + " días",
                // Se compara al segundo: la base guarda microsegundos y Java puede tener mas precision
                renovado.getFechaLimite().truncatedTo(ChronoUnit.SECONDS).equals(
                        pA.getFechaLimite().plusDays(ReglasBiblioteca.DIAS_RENOVACION).truncatedTo(ChronoUnit.SECONDS)));
        comprobar("lleva 1 renovación", renovado.getRenovaciones() == 1);
        esperarRechazo("un lector intenta renovar el préstamo de otro", PermisoException.class,
                () -> prestamoService.renovar(lector2, pA.getId()));
        ReservaDTO rA = reservaService.crear(lector2, idA);
        esperarRechazo("renovar cuando otro lector reservó el libro", ValidacionException.class,
                () -> prestamoService.renovar(lector1, pA.getId()));
        reservaService.cancelar(lector2, rA.getId());
        comprobar("sin reservas de otros, se renueva otra vez (2)",
                prestamoService.renovar(lector1, pA.getId()).getRenovaciones() == 2);
        esperarRechazo("renovar más de " + ReglasBiblioteca.MAX_RENOVACIONES + " veces", ValidacionException.class,
                () -> prestamoService.renovar(lector1, pA.getId()));

        System.out.println(">> 5. Devolver a tiempo");
        PrestamoDTO devuelto = prestamoService.devolver(admin, pA.getId());
        comprobar("queda DEVUELTO con fecha y quien lo recibió",
                devuelto.getEstado() == EstadoPrestamo.DEVUELTO && devuelto.getFechaDevolucion() != null
                && devuelto.getRecibidoPor() != null);
        comprobar("la copia vuelve al stock (A con 2)", disponibles(libroService, admin, idA) == 2);
        comprobar("devolver a tiempo no genera sanción", sancionService.misSanciones(lector1).stream()
                .noneMatch(s -> pA.getId().equals(s.getIdPrestamo())));
        esperarRechazo("devolver dos veces el mismo préstamo", ValidacionException.class,
                () -> prestamoService.devolver(admin, pA.getId()));

        System.out.println(">> 6. Devolver tarde (mora automática)");
        PrestamoDTO pC = prestamoService.entregarDirecto(admin, lector1.getId(), idC);
        vencer(pC.getId(), 3);
        comprobar("el préstamo se ve como vencido", buscarPrestamo(prestamoService, lector1, pC.getId()).isVencido());
        comprobar("aparece en la lista de vencidos del personal",
                prestamoService.listarVencidos(admin).stream().anyMatch(p -> p.getId().equals(pC.getId())));
        esperarRechazo("renovar un préstamo vencido", ValidacionException.class,
                () -> prestamoService.renovar(lector1, pC.getId()));
        esperarRechazo("prestarle otro libro a quien tiene uno vencido", ValidacionException.class,
                () -> prestamoService.entregarDirecto(admin, lector1.getId(), idA));
        ResumenLectorDTO resumen = dashboardService.resumenLector(lector1);
        comprobar("el resumen del lector muestra 1 vencido y no está al día",
                resumen.getPrestamosVencidos() == 1 && !resumen.isAlDia());

        prestamoService.devolver(admin, pC.getId());
        SancionDTO mora = sancionService.misSanciones(lector1).stream()
                .filter(s -> pC.getId().equals(s.getIdPrestamo()))
                .findFirst()
                .orElse(null);
        comprobar("se generó una sanción por MORA", mora != null && mora.getTipo() == TipoSancion.MORA);
        BigDecimal esperado = ReglasBiblioteca.TARIFA_MORA_DIARIA.multiply(BigDecimal.valueOf(3));
        comprobar("3 días de retraso x $" + ReglasBiblioteca.TARIFA_MORA_DIARIA + " = $" + esperado,
                mora != null && mora.getDiasRetraso() == 3 && mora.getImporte().compareTo(esperado) == 0);
        comprobar("la mora la generó el sistema", mora != null && "Sistema".equals(mora.getRegistradoPor()));
        comprobar("queda PENDIENTE y ligada al préstamo",
                mora != null && mora.isPendiente() && pC.getId().equals(mora.getIdPrestamo()));

        System.out.println(">> 7. Pagar y registrar sanciones");
        final Long idMora = mora == null ? -1L : mora.getId();
        esperarRechazo("un lector intenta marcar su sanción como pagada", PermisoException.class,
                () -> sancionService.pagar(lector1, idMora));
        SancionDTO pagada = sancionService.pagar(admin, idMora);
        comprobar("queda PAGADA con fecha de pago", pagada.getEstado() == EstadoSancion.PAGADA && pagada.getFechaPago() != null);
        esperarRechazo("pagar dos veces la misma sanción", ValidacionException.class,
                () -> sancionService.pagar(admin, idMora));

        SancionDTO danado = sancionService.registrar(admin, null, pB.getId(), TipoSancion.LIBRO_DANADO,
                new BigDecimal("20000"), "Páginas rayadas");
        comprobar("libro dañado: se cobra a quien tenía el préstamo",
                danado.getIdUsuario().equals(lector2.getId()) && danado.getRegistradoPor() != null);
        esperarRechazo("dos sanciones del mismo tipo para el mismo préstamo", ValidacionException.class,
                () -> sancionService.registrar(admin, null, pB.getId(), TipoSancion.LIBRO_DANADO,
                        new BigDecimal("1000"), null));
        esperarRechazo("registrar una MORA a mano", ValidacionException.class,
                () -> sancionService.registrar(admin, lector1.getId(), null, TipoSancion.MORA,
                        new BigDecimal("1000"), null));
        esperarRechazo("registrar una sanción de $0", ValidacionException.class,
                () -> sancionService.registrar(admin, lector1.getId(), null, TipoSancion.OTRO,
                        BigDecimal.ZERO, "Prueba"));
        esperarRechazo("sanción por OTRO motivo sin describirlo", ValidacionException.class,
                () -> sancionService.registrar(admin, lector1.getId(), null, TipoSancion.OTRO,
                        new BigDecimal("1000"), "  "));
        esperarRechazo("préstamo que no es del usuario indicado", ValidacionException.class,
                () -> sancionService.registrar(admin, lector1.getId(), pB.getId(), TipoSancion.OTRO,
                        new BigDecimal("1000"), "Prueba"));

        System.out.println(">> 8. Anular una sanción");
        esperarRechazo("un lector intenta anular una sanción", PermisoException.class,
                () -> sancionService.anular(lector2, danado.getId(), "No fui yo"));
        esperarRechazo("anular sin motivo", ValidacionException.class,
                () -> sancionService.anular(admin, danado.getId(), ""));
        SancionDTO anulada = sancionService.anular(admin, danado.getId(), "Registrada por error");
        comprobar("queda ANULADA y el motivo queda en el detalle",
                anulada.getEstado() == EstadoSancion.ANULADA && anulada.getDetalle().contains("Registrada por error"));

        System.out.println(">> 9. Libro perdido");
        int totalBAntes = libroService.obtener(admin, idB).getStockTotal();
        esperarRechazo("marcar perdido sin valor a cobrar", ValidacionException.class,
                () -> prestamoService.marcarPerdido(admin, pB.getId(), null, null));
        comprobar("el rechazo no cambió el inventario", libroService.obtener(admin, idB).getStockTotal() == totalBAntes);
        PrestamoDTO perdido = prestamoService.marcarPerdido(admin, pB.getId(), new BigDecimal("50000"), "Lo extravió");
        comprobar("queda PERDIDO", perdido.getEstado() == EstadoPrestamo.PERDIDO);
        comprobar("la copia sale del inventario (B: total " + (totalBAntes - 1) + ")",
                libroService.obtener(admin, idB).getStockTotal() == totalBAntes - 1);
        comprobar("se generó la sanción LIBRO_PERDIDO de $50000",
                sancionService.misSanciones(lector2).stream().anyMatch(s -> s.getTipo() == TipoSancion.LIBRO_PERDIDO
                && s.isPendiente() && s.getImporte().compareTo(new BigDecimal("50000")) == 0));
        comprobar("el lector debe $50000 en su resumen",
                dashboardService.resumenLector(lector2).getDeudaPendiente().compareTo(new BigDecimal("50000")) == 0);

        System.out.println(">> 10. Anular un préstamo");
        PrestamoDTO pA2 = prestamoService.entregarDirecto(admin, lector1.getId(), idA);
        esperarRechazo("un lector intenta anular un préstamo", PermisoException.class,
                () -> prestamoService.anular(lector1, pA2.getId(), "Error"));
        PrestamoDTO anulado = prestamoService.anular(admin, pA2.getId(), "Se registró al lector equivocado");
        comprobar("queda ANULADO y la copia vuelve (A con 2)",
                anulado.getEstado() == EstadoPrestamo.ANULADO && disponibles(libroService, admin, idA) == 2);

        System.out.println(">> 11. Listados");
        comprobar("el lector ve sus préstamos (activos e históricos)",
                prestamoService.misPrestamos(lector1).size() >= 3);
        comprobar("el personal filtra por estado",
                prestamoService.listar(admin, EstadoPrestamo.PERDIDO).stream().anyMatch(p -> p.getId().equals(pB.getId())));
        esperarRechazo("un lector intenta listar todos los préstamos", PermisoException.class,
                () -> prestamoService.listar(lector1, null));
        esperarRechazo("un lector intenta listar todas las sanciones", PermisoException.class,
                () -> sancionService.listar(lector1, null));
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
        f.setDescripcion("Libro creado por PruebaPrestamos");
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

    private static EstadoReserva estadoReserva(ReservaService reservaService, UsuarioDTO lector, Long idReserva) {
        return reservaService.misReservas(lector).stream()
                .filter(r -> r.getId().equals(idReserva))
                .map(ReservaDTO::getEstado)
                .findFirst()
                .orElse(null);
    }

    private static PrestamoDTO buscarPrestamo(PrestamoService prestamoService, UsuarioDTO lector, Long idPrestamo) {
        return prestamoService.misPrestamos(lector).stream()
                .filter(p -> p.getId().equals(idPrestamo))
                .findFirst()
                .orElseThrow();
    }

    private static void fijarEstado(Long idUsuario, EstadoUsuario estado) {
        Transaccion.ejecutarAccion(em -> em.find(Usuario.class, idUsuario).setEstado(estado));
    }

    /* Hace que el prestamo haya vencido hace "dias" dias (solo para la prueba). */
    private static void vencer(Long idPrestamo, int dias) {
        Transaccion.ejecutarAccion(em -> {
            Prestamo p = em.find(Prestamo.class, idPrestamo);
            p.setFechaPrestamo(LocalDateTime.now().minusDays(ReglasBiblioteca.DIAS_PRESTAMO + dias));
            p.setFechaLimite(LocalDateTime.now().minusDays(dias));
        });
    }

    /* Deja a los lectores en ACTIVO, sin prestamos activos, reservas vivas ni sanciones pendientes. */
    private static void limpiar(UsuarioDTO lector1, UsuarioDTO lector2) {
        List<Long> ids = List.of(lector1.getId(), lector2.getId());
        fijarEstado(lector2.getId(), EstadoUsuario.ACTIVO);
        Transaccion.ejecutarAccion(em -> {
            em.createQuery("UPDATE Prestamo p SET p.estado = :nuevo, p.fechaDevolucion = :ahora "
                    + "WHERE p.usuario.id IN (:ids) AND p.estado = :activo")
                    .setParameter("nuevo", EstadoPrestamo.DEVUELTO)
                    .setParameter("ahora", LocalDateTime.now())
                    .setParameter("ids", ids)
                    .setParameter("activo", EstadoPrestamo.ACTIVO)
                    .executeUpdate();
            em.createQuery("UPDATE Reserva r SET r.estado = :nuevo "
                    + "WHERE r.usuario.id IN (:ids) AND r.estado IN (:vivas)")
                    .setParameter("nuevo", EstadoReserva.CANCELADA)
                    .setParameter("ids", ids)
                    .setParameter("vivas", List.of(EstadoReserva.PENDIENTE, EstadoReserva.APROBADA))
                    .executeUpdate();
            em.createQuery("UPDATE Sancion s SET s.estado = :nuevo "
                    + "WHERE s.usuario.id IN (:ids) AND s.estado = :pendiente")
                    .setParameter("nuevo", EstadoSancion.ANULADA)
                    .setParameter("ids", ids)
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
