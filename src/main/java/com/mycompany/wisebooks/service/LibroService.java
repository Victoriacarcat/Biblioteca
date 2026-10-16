/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.CatalogoDAO;
import com.mycompany.wisebooks.dao.LibroDAO;
import com.mycompany.wisebooks.dto.FiltroLibroDTO;
import com.mycompany.wisebooks.dto.LibroDTO;
import com.mycompany.wisebooks.dto.LibroFormDTO;
import com.mycompany.wisebooks.dto.OpcionDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.Autor;
import com.mycompany.wisebooks.entity.Categoria;
import com.mycompany.wisebooks.entity.Editorial;
import com.mycompany.wisebooks.entity.Genero;
import com.mycompany.wisebooks.entity.Libro;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Reglas de negocio del catalogo de libros y de su stock.
 *
 * Quien puede hacer que:
 *   - Consultar el catalogo: cualquiera, incluso sin iniciar sesion
 *     (los libros desactivados solo los ve el personal).
 *   - Crear, editar y activar/desactivar libros: solo el ADMINISTRADOR.
 *   - Agregar o retirar copias: ADMINISTRADOR y BIBLIOTECARIO.
 *
 * Cada operacion corre en UNA transaccion y deja su registro en la auditoria.
 * Un libro nunca se borra: se desactiva.
 */
public class LibroService {

    // Largos maximos: coinciden con las columnas de las entidades
    private static final int MAX_TITULO = 255;
    private static final int MAX_ISBN = 20;
    private static final int MAX_DESCRIPCION = 5000;
    private static final int MAX_PORTADA = 500;
    private static final int MAX_UBICACION = 100;
    private static final int MAX_EDITORIAL = 150;
    private static final int MAX_AUTOR = 150;
    private static final int MAX_CATEGORIA = 100;
    private static final int MAX_GENERO = 100;

    // Tope de copias por operacion (evita errores de digitacion como 1000000)
    private static final int MAX_COPIAS = 10000;

    private final LibroDAO libroDAO = new LibroDAO();
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();
    private final AuditoriaService auditoriaService = new AuditoriaService();

    /*
     * Los datos del formulario ya validados y limpios. Un "record" es una clase
     * pequena e inmutable: solo guarda valores y crea sus getters solos
     * (se leen asi: datos.titulo()).
     */
    private record DatosLibro(String titulo, String isbn, LocalDate fechaPublicacion,
            String descripcion, String portadaUrl, String ubicacion,
            String editorial, String categoria, String genero, List<String> autores) {
    }

    // Consultas

    /* Busca libros. "actor" puede ser null (visitante). */
    public List<LibroDTO> buscar(UsuarioDTO actor, FiltroLibroDTO filtro) {
        FiltroLibroDTO f = (filtro == null) ? new FiltroLibroDTO() : filtro;
        boolean verInactivos = Autorizacion.esPersonal(actor);
        return Transaccion.consultar(em -> libroDAO.buscar(em, f, verInactivos).stream()
                .map(LibroDTO::desde)
                .toList());
    }

    /* Detalle de un libro. Un libro desactivado solo lo ve el personal. */
    public LibroDTO obtener(UsuarioDTO actor, Long idLibro) {
        boolean personal = Autorizacion.esPersonal(actor);
        return Transaccion.consultar(em -> {
            Libro libro = exigirLibro(em, idLibro);
            if (!libro.isActivo() && !personal) {
                throw new ValidacionException("El libro no existe.");
            }
            return LibroDTO.desde(libro);
        });
    }

    /* Categorias activas, para la lista desplegable del formulario y el filtro. */
    public List<OpcionDTO> listarCategorias() {
        return Transaccion.consultar(em -> catalogoDAO.listarCategoriasActivas(em).stream()
                .map(c -> new OpcionDTO(c.getId(), c.getNombre()))
                .toList());
    }

    /* Generos activos, para la lista desplegable del formulario y el filtro. */
    public List<OpcionDTO> listarGeneros() {
        return Transaccion.consultar(em -> catalogoDAO.listarGenerosActivos(em).stream()
                .map(g -> new OpcionDTO(g.getId(), g.getNombre()))
                .toList());
    }

    /* Editoriales activas, para sugerirlas en el formulario (y no crear duplicados). */
    public List<OpcionDTO> listarEditoriales() {
        return Transaccion.consultar(em -> catalogoDAO.listarEditorialesActivas(em).stream()
                .map(e -> new OpcionDTO(e.getId(), e.getNombre()))
                .toList());
    }

    /* Autores activos, para sugerirlos en el formulario (y no crear duplicados). */
    public List<OpcionDTO> listarAutores() {
        return Transaccion.consultar(em -> catalogoDAO.listarAutoresActivos(em).stream()
                .map(a -> new OpcionDTO(a.getId(), a.getNombre()))
                .toList());
    }

    // ------------------------------------------------------------------
    // Crear y editar (solo administrador)
    // ------------------------------------------------------------------

    /* Crea un libro con su stock inicial. Si el catalogo (autor, editorial...) no existe, lo crea. */
    public LibroDTO crear(UsuarioDTO actor, LibroFormDTO form) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            DatosLibro datos = validar(form);

            int stock = form.getStockInicial();
            if (stock < 0 || stock > MAX_COPIAS) {
                throw new ValidacionException(
                        "El stock inicial debe estar entre 0 y " + MAX_COPIAS + ".");
            }
            exigirIsbnLibre(em, datos.isbn(), null);

            Libro libro = new Libro();
            aplicar(em, libro, datos);
            libro.setStockTotal(stock);
            libro.setStockDisponible(stock);
            libroDAO.guardar(em, libro);

            auditoriaService.registrar(em, admin, AccionAuditoria.CREAR, "Libro", libro.getId(),
                    "Creó el libro \"" + datos.titulo() + "\" con " + stock + " copias.");
            return LibroDTO.desde(libro);
        });
    }

    /* Edita los datos del libro. El stock NO se cambia aqui (ver agregarCopias y retirarCopias). */
    public LibroDTO editar(UsuarioDTO actor, Long idLibro, LibroFormDTO form) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            Libro libro = exigirLibro(em, idLibro);
            DatosLibro datos = validar(form);
            exigirIsbnLibre(em, datos.isbn(), libro.getId());

            aplicar(em, libro, datos);

            auditoriaService.registrar(em, admin, AccionAuditoria.ACTUALIZAR, "Libro", libro.getId(),
                    "Editó el libro \"" + datos.titulo() + "\".");
            return LibroDTO.desde(libro);
        });
    }

    /* Activa o desactiva un libro (desactivado = no aparece en el catalogo publico). */
    public LibroDTO cambiarActivo(UsuarioDTO actor, Long idLibro, boolean activo) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            Libro libro = exigirLibro(em, idLibro);

            if (libro.isActivo() != activo) {
                libro.setActivo(activo);
                auditoriaService.registrar(em, admin, AccionAuditoria.CAMBIO_ESTADO, "Libro", libro.getId(),
                        (activo ? "Activó" : "Desactivó") + " el libro \"" + libro.getTitulo() + "\".");
            }
            return LibroDTO.desde(libro);
        });
    }
//Stock
    /* Suma copias nuevas: sube el stock total y el disponible. */
    public LibroDTO agregarCopias(UsuarioDTO actor, Long idLibro, int cantidad) {
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            exigirCantidad(cantidad);
            Libro libro = exigirLibro(em, idLibro);

            if ((long) libro.getStockTotal() + cantidad > MAX_COPIAS) {
                throw new ValidacionException(
                        "Un libro no puede tener más de " + MAX_COPIAS + " copias.");
            }
            libro.setStockTotal(libro.getStockTotal() + cantidad);
            libro.setStockDisponible(libro.getStockDisponible() + cantidad);

            auditoriaService.registrar(em, personal, AccionAuditoria.ACTUALIZAR, "Libro", libro.getId(),
                    "Agregó " + cantidad + " copias a \"" + libro.getTitulo()
                    + "\". Stock total: " + libro.getStockTotal() + ".");
            return LibroDTO.desde(libro);
        });
    }

    /* Da de baja copias libres: solo se pueden retirar las que estan disponibles. */
    public LibroDTO retirarCopias(UsuarioDTO actor, Long idLibro, int cantidad) {
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            exigirCantidad(cantidad);
            Libro libro = exigirLibro(em, idLibro);

            if (cantidad > libro.getStockDisponible()) {
                throw new ValidacionException("Solo hay " + libro.getStockDisponible()
                        + " copias disponibles; no se pueden retirar " + cantidad + ".");
            }
            libro.setStockTotal(libro.getStockTotal() - cantidad);
            libro.setStockDisponible(libro.getStockDisponible() - cantidad);

            auditoriaService.registrar(em, personal, AccionAuditoria.ACTUALIZAR, "Libro", libro.getId(),
                    "Retiró " + cantidad + " copias de \"" + libro.getTitulo()
                    + "\". Stock total: " + libro.getStockTotal() + ".");
            return LibroDTO.desde(libro);
        });
    }


    private Libro exigirLibro(EntityManager em, Long idLibro) {
        if (idLibro == null) {
            throw new ValidacionException("El libro no existe.");
        }
        return libroDAO.buscarPorId(em, idLibro)
                .orElseThrow(() -> new ValidacionException("El libro no existe."));
    }

    private void exigirCantidad(int cantidad) {
        if (cantidad < 1 || cantidad > MAX_COPIAS) {
            throw new ValidacionException("La cantidad debe estar entre 1 y " + MAX_COPIAS + ".");
        }
    }

    /* El ISBN, si se escribio, no puede estar en otro libro. */
    private void exigirIsbnLibre(EntityManager em, String isbn, Long idLibroActual) {
        if (isbn != null && libroDAO.existeIsbn(em, isbn, idLibroActual)) {
            throw new ValidacionException("Ya existe un libro con ese ISBN.");
        }
    }

    /* Valida y limpia los datos del formulario (sin tocar la base). */
    private DatosLibro validar(LibroFormDTO form) {
        if (form == null) {
            throw new ValidacionException("Faltan los datos del libro.");
        }
        String titulo = Texto.exigir(form.getTitulo(), "El título", MAX_TITULO);
        String isbn = Texto.opcional(form.getIsbn(), "El ISBN", MAX_ISBN);

        LocalDate fecha = form.getFechaPublicacion();
        if (fecha != null && fecha.isAfter(LocalDate.now())) {
            throw new ValidacionException("La fecha de publicación no puede ser futura.");
        }

        return new DatosLibro(
                titulo,
                isbn,
                fecha,
                Texto.opcional(form.getDescripcion(), "La descripción", MAX_DESCRIPCION),
                Texto.opcional(form.getPortadaUrl(), "La URL de la portada", MAX_PORTADA),
                Texto.opcional(form.getUbicacion(), "La ubicación", MAX_UBICACION),
                Texto.opcional(form.getEditorial(), "La editorial", MAX_EDITORIAL),
                Texto.exigir(form.getCategoria(), "La categoría", MAX_CATEGORIA),
                Texto.opcional(form.getGenero(), "El género", MAX_GENERO),
                limpiarAutores(form.getAutores()));
    }

    /* Quita vacios y repetidos (sin distinguir mayusculas). Exige al menos un autor. */
    private List<String> limpiarAutores(List<String> autores) {
        Map<String, String> unicos = new LinkedHashMap<>();
        if (autores != null) {
            for (String texto : autores) {
                String nombre = Texto.opcional(texto, "El nombre del autor", MAX_AUTOR);
                if (nombre != null) {
                    unicos.putIfAbsent(nombre.toLowerCase(), nombre);
                }
            }
        }
        if (unicos.isEmpty()) {
            throw new ValidacionException("El libro necesita al menos un autor.");
        }
        return new ArrayList<>(unicos.values());
    }

    /* Copia los datos validados al libro, creando los catalogos que no existan. */
    private void aplicar(EntityManager em, Libro libro, DatosLibro datos) {
        libro.setTitulo(datos.titulo());
        libro.setIsbn(datos.isbn());
        libro.setFechaPublicacion(datos.fechaPublicacion());
        libro.setDescripcion(datos.descripcion());
        libro.setPortadaUrl(datos.portadaUrl());
        libro.setUbicacion(datos.ubicacion());

        libro.setCategoria(categoria(em, datos.categoria()));
        libro.setEditorial(datos.editorial() == null ? null : editorial(em, datos.editorial()));
        libro.setGenero(datos.genero() == null ? null : genero(em, datos.genero()));

        Set<Autor> autores = new HashSet<>();
        for (String nombre : datos.autores()) {
            autores.add(autor(em, nombre));
        }
        libro.getAutores().clear();
        libro.getAutores().addAll(autores);
    }

    // Buscar por nombre y, si no existe, crear: el patron de los cuatro catalogos.

    private Categoria categoria(EntityManager em, String nombre) {
        return catalogoDAO.buscarPorNombre(em, Categoria.class, nombre).orElseGet(() -> {
            Categoria nueva = new Categoria();
            nueva.setNombre(nombre);
            catalogoDAO.guardar(em, nueva);
            return nueva;
        });
    }

    private Editorial editorial(EntityManager em, String nombre) {
        return catalogoDAO.buscarPorNombre(em, Editorial.class, nombre).orElseGet(() -> {
            Editorial nueva = new Editorial();
            nueva.setNombre(nombre);
            catalogoDAO.guardar(em, nueva);
            return nueva;
        });
    }

    private Genero genero(EntityManager em, String nombre) {
        return catalogoDAO.buscarPorNombre(em, Genero.class, nombre).orElseGet(() -> {
            Genero nuevo = new Genero();
            nuevo.setNombre(nombre);
            catalogoDAO.guardar(em, nuevo);
            return nuevo;
        });
    }

    private Autor autor(EntityManager em, String nombre) {
        return catalogoDAO.buscarPorNombre(em, Autor.class, nombre).orElseGet(() -> {
            Autor nuevo = new Autor();
            nuevo.setNombre(nombre);
            catalogoDAO.guardar(em, nuevo);
            return nuevo;
        });
    }
}

