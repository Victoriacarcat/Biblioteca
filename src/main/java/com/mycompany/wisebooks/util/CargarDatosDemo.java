package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.Auditoria;
import com.mycompany.wisebooks.entity.Autor;
import com.mycompany.wisebooks.entity.Categoria;
import com.mycompany.wisebooks.entity.Editorial;
import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.EstadoSancion;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Genero;
import com.mycompany.wisebooks.entity.Libro;
import com.mycompany.wisebooks.entity.Prestamo;
import com.mycompany.wisebooks.entity.Proveedor;
import com.mycompany.wisebooks.entity.Reserva;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Sancion;
import com.mycompany.wisebooks.entity.TipoDocumento;
import com.mycompany.wisebooks.entity.TipoSancion;
import com.mycompany.wisebooks.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Carga datos de DEMOSTRACION para que la aplicacion tenga contenido:
 * catalogo (libros, autores, categorias, generos, editoriales), lectores y
 * bibliotecarios ficticios, prestamos (devueltos, activos, vencidos,
 * perdidos, anulado), reservas en todos sus estados, sanciones, favoritos y
 * proveedores. Se ejecuta desde NetBeans (clic derecho > Run File).
 *
 * Reglas de la carga:
 *   - No borra ni modifica datos existentes. Categorias, generos, editoriales
 *     y autores con el mismo nombre se REUTILIZAN (solo se leen).
 *   - Todo va en UNA transaccion: o se guarda todo, o nada.
 *   - Si ya se cargo antes (existe la cuenta MARCA), no crea nada (solo
 *     completa las portadas de los libros de la demo): se puede
 *     ejecutar varias veces sin duplicar.
 *   - Si un libro (ISBN), usuario (correo o documento) o proveedor (nombre o
 *     NIT) de la demo ya existe por otro motivo, se detiene sin guardar nada.
 *   - Las fechas son relativas al dia en que se ejecuta ("hace 30 dias").
 *   - Se escriben las entidades directamente (no los services) porque los
 *     services usan la fecha actual y aqui se necesita un historial pasado.
 *     Por eso se aplican a mano las mismas reglas que ellos: stock, mora de
 *     ReglasBiblioteca.TARIFA_MORA_DIARIA por dia, plazos de 14 y 7 dias...
 *
 * Todas las personas son ficticias. Todas las cuentas de la demo usan el
 * correo @demo.wisebooks.com y la contrasena CONTRASENA_DEMO.
 */
public class CargarDatosDemo {

    private static final String DOMINIO = "@demo.wisebooks.com";
    // Si esta cuenta existe, la demo ya se cargo
    private static final String MARCA = "laura.mendez" + DOMINIO;
    private static final String CONTRASENA_DEMO = "Demo2026!";

    // Valor especial para el parametro "portada" de libro(): usa la imagen
    // CARPETA_PORTADAS + clave + ".jpg" que viene con el proyecto (tomadas de
    // Open Library), asi la demo no necesita internet.
    private static final String PORTADA_PROPIA = "PORTADA_PROPIA";
    private static final String CARPETA_PORTADAS = "/Recursos/Imagenes/Portadas/";
    // Las portadas de la primera version de la demo eran enlaces a Open Library
    private static final String URL_OPEN_LIBRARY = "https://covers.openlibrary.org/";

    // Dias de antiguedad de casi todo el catalogo y las cuentas
    private static final int ALTA_GENERAL = 200;

    private final EntityManager em;
    private final LocalDateTime ahora = LocalDateTime.now();

    // Lo creado, por una clave corta, para enlazarlo despues
    private final Map<String, Libro> libros = new HashMap<>();
    private final Map<String, Usuario> usuarios = new HashMap<>();
    // Fecha de alta que se pondra al final a cada libro y usuario
    private final Map<Libro, LocalDateTime> altaLibros = new LinkedHashMap<>();
    private final Map<Usuario, LocalDateTime> altaUsuarios = new LinkedHashMap<>();

    // Contadores para el resumen final
    private int totalPrestamos = 0;
    private int totalReservas = 0;
    private int totalSanciones = 0;
    private int totalFavoritos = 0;
    private int totalProveedores = 0;
    // Para repartir las entregas entre los dos bibliotecarios
    private int turno = 0;
    // true = la demo ya estaba cargada: libro() solo actualiza la portada
    private boolean soloPortadas = false;
    private int portadasActualizadas = 0;

    public static void main(String[] args) {
        try {
            String resumen = Transaccion.ejecutar(em -> new CargarDatosDemo(em).cargar());
            System.out.println(resumen);
        } catch (ValidacionException e) {
            System.out.println("No se cargaron los datos (no se guardó nada): " + e.getMessage());
        } finally {
            JPAUtil.cerrar();
        }
    }

    private CargarDatosDemo(EntityManager em) {
        this.em = em;
    }

    private String cargar() {
        if (existeCorreo(MARCA)) {
            // No se crea nada; solo se ponen las portadas locales a los libros
            // de la demo que todavia no las tienen (cargas anteriores)
            soloPortadas = true;
            crearLibros();
            return "Los datos de demostración ya estaban cargados: no se duplicó nada. "
                    + "Portadas actualizadas: " + portadasActualizadas + ".";
        }
        crearLibros();
        crearUsuarios();
        crearPrestamos();
        crearReservas();
        crearSancionesManuales();
        crearFavoritos();
        crearProveedores();

        em.flush();
        ajustarFechasDeAlta();
        registrarAuditoria();

        return "Datos de demostración cargados: " + libros.size() + " libros, "
                + usuarios.size() + " usuarios, " + totalPrestamos + " préstamos, "
                + totalReservas + " reservas, " + totalSanciones + " sanciones, "
                + totalFavoritos + " favoritos y " + totalProveedores + " proveedores.\n"
                + "Contraseña de todas las cuentas de la demo: " + CONTRASENA_DEMO
                + " (por ejemplo, " + MARCA + " es bibliotecaria y camila.torres" + DOMINIO + " lectora).";
    }

    // ===================== Catalogo =====================

    private void crearLibros() {
        // ---- Novela ----
        libro("cien", "Cien años de soledad", "9788497592208", 1967, "Novela", "Realismo mágico", "Debolsillo",
                5, "Estante A-1", "/Recursos/Imagenes/portada-cienAñosdeSoledad.jpg",
                "La historia de siete generaciones de la familia Buendía en el pueblo de Macondo.",
                "Gabriel García Márquez");
        libro("amor", "El amor en los tiempos del cólera", "9788497592451", 1985, "Novela", "Romance", "Debolsillo",
                3, "Estante A-1", PORTADA_PROPIA,
                "Florentino Ariza espera más de cincuenta años para volver a declarar su amor a Fermina Daza.",
                "Gabriel García Márquez");
        libro("cronica", "Crónica de una muerte anunciada", "9788497592437", 1981, "Novela", "Drama", "Debolsillo",
                4, "Estante A-1", PORTADA_PROPIA,
                "Todo el pueblo sabía que los hermanos Vicario iban a matar a Santiago Nasar. Nadie lo impidió.",
                "Gabriel García Márquez");
        libro("casa", "La casa de los espíritus", "9788466367912", 1982, "Novela", "Realismo mágico", "Debolsillo",
                3, "Estante A-2", PORTADA_PROPIA,
                "La saga de la familia Trueba a lo largo de cuatro generaciones en un país latinoamericano.",
                "Isabel Allende");
        libro("pedro", "Pedro Páramo", "9788437604183", 1955, "Novela", "Realismo mágico", "Cátedra",
                3, "Estante A-2", PORTADA_PROPIA,
                "Juan Preciado viaja a Comala en busca de su padre y encuentra un pueblo habitado por murmullos.",
                "Juan Rulfo");
        libro("maria", "María", "9788437606101", 1867, "Novela", "Romance", "Cátedra",
                2, "Estante A-2", PORTADA_PROPIA,
                "El amor de Efraín y María en una hacienda del Valle del Cauca. Un clásico del romanticismo.",
                "Jorge Isaacs");
        libro("tunel", "El túnel", "9788432250026", 1948, "Novela", "Drama", "Seix Barral",
                2, "Estante A-3", PORTADA_PROPIA,
                "El pintor Juan Pablo Castel confiesa desde la cárcel el crimen de la única persona que lo entendió.",
                "Ernesto Sabato");
        libro("rayuela", "Rayuela", "9788420437484", 1963, "Novela", "Drama", "Alfaguara",
                2, "Estante A-3", PORTADA_PROPIA,
                "Una novela que se puede leer en distinto orden: la vida de Horacio Oliveira entre París y Buenos Aires.",
                "Julio Cortázar");
        libro("quijote", "Don Quijote de la Mancha", "9788417958596", 1605, "Novela", "Clásico", null,
                3, "Estante A-4", PORTADA_PROPIA,
                "Un hidalgo enloquecido por los libros de caballerías sale a deshacer agravios junto a Sancho Panza.",
                "Miguel de Cervantes");
        libro("lazarillo", "Lazarillo de Tormes", "9788491050292", 1554, "Novela", "Clásico", "Penguin Clásicos",
                2, "Estante A-4", PORTADA_PROPIA,
                "Las aventuras de un niño pobre que sobrevive sirviendo a varios amos. La primera novela picaresca.",
                "Anónimo");
        libro("orgullo", "Orgullo y prejuicio", "9788415083696", 1813, "Novela", "Romance", null,
                3, "Estante A-5", PORTADA_PROPIA,
                "Elizabeth Bennet y el señor Darcy deben superar su orgullo y sus prejuicios.",
                "Jane Austen");
        libro("sombra", "La sombra del viento", "9788408112310", 2001, "Novela", "Misterio", "Booket",
                4, "Estante A-5", PORTADA_PROPIA,
                "En la Barcelona de posguerra, Daniel descubre un libro maldito en el Cementerio de los Libros Olvidados.",
                "Carlos Ruiz Zafón");
        libro("rosa", "El nombre de la rosa", "9788497592581", 1980, "Novela", "Misterio", "Debolsillo",
                1, "Estante A-5", PORTADA_PROPIA,
                "Fray Guillermo de Baskerville investiga una serie de muertes en una abadía del siglo XIV.",
                "Umberto Eco");
        libro("1984", "1984", "9788499890944", 1949, "Novela", "Distopía", "Debolsillo",
                4, "Estante B-1", "/Recursos/Imagenes/portada-1984.jpg",
                "Winston Smith vive bajo la vigilancia constante del Gran Hermano.",
                "George Orwell");
        libro("rebelion", "Rebelión en la granja", "9788499890951", 1945, "Novela", "Clásico", "Debolsillo",
                3, "Estante B-1", PORTADA_PROPIA,
                "Los animales de una granja expulsan a los humanos, pero pronto unos son más iguales que otros.",
                "George Orwell");
        libro("mundofeliz", "Un mundo feliz", "9788497594257", 1932, "Novela", "Distopía", "Debolsillo",
                2, "Estante B-1", PORTADA_PROPIA,
                "Una sociedad que fabrica a las personas en laboratorios y las mantiene felices con una droga.",
                "Aldous Huxley");
        libro("fahrenheit", "Fahrenheit 451", null, 1953, "Novela", "Distopía", "Minotauro",
                2, "Estante B-1", PORTADA_PROPIA,
                "Guy Montag es un bombero cuyo trabajo es quemar libros, hasta que empieza a leerlos.",
                "Ray Bradbury");
        libro("dune", "Dune", "9788497596824", 1965, "Novela", "Ciencia ficción", "Debolsillo",
                3, "Estante B-2", PORTADA_PROPIA,
                "Paul Atreides llega a Arrakis, el planeta desierto del que depende todo el imperio.",
                "Frank Herbert");
        libro("fundacion", "Fundación", "9788497599245", 1951, "Novela", "Ciencia ficción", "Plaza & Janés",
                2, "Estante B-2", PORTADA_PROPIA,
                "El matemático Hari Seldon prevé la caída del Imperio Galáctico y un plan para acortar la barbarie.",
                "Isaac Asimov");
        libro("terramar", "Historias de Terramar I", "9788445076682", 1968, "Novela", "Fantasía", "Booket",
                2, "Estante B-3", PORTADA_PROPIA,
                "Ged, un joven con un gran talento para la magia, aprende en la escuela de hechiceros de Roke.",
                "Ursula K. Le Guin");
        libro("comunidad", "El Señor de los Anillos: La Comunidad del Anillo", "9788445000663", 1954, "Novela",
                "Fantasía", "Minotauro", 3, "Estante B-3", PORTADA_PROPIA,
                "Frodo hereda un anillo que debe ser destruido antes de que su creador lo recupere.",
                "J. R. R. Tolkien");
        libro("dostorres", "El Señor de los Anillos: Las Dos Torres", "9788445000670", 1954, "Novela",
                "Fantasía", "Booket", 1, "Estante B-3", PORTADA_PROPIA,
                "La Comunidad se ha dividido: Frodo y Sam siguen hacia Mordor mientras los demás defienden Rohan.",
                "J. R. R. Tolkien");
        libro("it", "It (Eso)", "9788497593793", 1986, "Novela", "Terror", "Debolsillo",
                2, "Estante B-4", PORTADA_PROPIA,
                "Siete amigos de Derry se enfrentan de niños, y otra vez de adultos, a un ser que adopta la forma de un payaso.",
                "Stephen King");
        libro("frankenstein", "Frankenstein o el moderno Prometeo", "9788491058083", 1818, "Novela", "Terror",
                "Penguin Clásicos", 2, "Estante B-4", PORTADA_PROPIA,
                "Victor Frankenstein da vida a una criatura y luego la abandona.",
                "Mary Shelley");
        libro("metamorfosis", "La metamorfosis", "9788420651361", 1915, "Novela", "Clásico", "Alianza Editorial",
                3, "Estante B-5", PORTADA_PROPIA,
                "Gregorio Samsa despierta una mañana convertido en un enorme insecto.",
                "Franz Kafka");
        libro("moscas", "El señor de las moscas", "9788420674179", 1954, "Novela", "Aventura", "Alianza Editorial",
                2, "Estante B-5", PORTADA_PROPIA,
                "Un grupo de niños queda solo en una isla desierta e intenta organizarse.",
                "William Golding");

        // ---- Cuento ----
        libro("ficciones", "Ficciones", "9788499089508", 1944, "Cuento", "Fantasía", "Debolsillo",
                3, "Estante C-1", PORTADA_PROPIA,
                "Cuentos sobre laberintos, bibliotecas infinitas y espejos.",
                "Jorge Luis Borges");
        libro("aleph", "El Aleph", "9788499089515", 1949, "Cuento", "Fantasía", "Debolsillo",
                2, "Estante C-1", PORTADA_PROPIA,
                "Diecisiete cuentos, entre ellos el del punto que contiene todos los puntos del universo.",
                "Jorge Luis Borges");
        libro("bestiario", "Bestiario", "9788420416571", 1951, "Cuento", "Fantasía", "Alfaguara",
                2, "Estante C-1", PORTADA_PROPIA,
                "Ocho cuentos en los que lo extraño irrumpe en la vida cotidiana.",
                "Julio Cortázar");
        libro("chejov", "Cuentos imprescindibles", "9788491051923", 1899, "Cuento", "Clásico", "Penguin Clásicos",
                1, "Estante C-2", PORTADA_PROPIA,
                "Una selección de los mejores cuentos del maestro ruso del relato breve.",
                "Antón Chéjov");

        // ---- Poesia ----
        libro("veinte", "Veinte poemas de amor y una canción desesperada", "9788426392299", 1924, "Poesía",
                "Romance", "Edelvives", 3, "Estante C-3", PORTADA_PROPIA,
                "El poemario más leído en lengua española.",
                "Pablo Neruda");
        libro("cid", "Poema de mio Cid", "9788491050254", 1200, "Poesía", "Clásico", "Penguin Clásicos",
                1, "Estante C-3", PORTADA_PROPIA,
                "El cantar de gesta del destierro y la recuperación del honor de Rodrigo Díaz de Vivar.",
                "Anónimo");

        // ---- Infantil y juvenil ----
        libro("principito", "El principito", "9788478887194", 1943, "Infantil y juvenil", "Clásico", "Salamandra",
                5, "Estante D-1", PORTADA_PROPIA,
                "Un aviador perdido en el desierto conoce a un pequeño príncipe que viene de otro planeta.",
                "Antoine de Saint-Exupéry");
        libro("hp1", "Harry Potter y la piedra filosofal", "9788478884452", 1997, "Infantil y juvenil", "Fantasía",
                "Salamandra", 6, "Estante D-2", PORTADA_PROPIA,
                "Harry descubre en su undécimo cumpleaños que es un mago y entra al colegio Hogwarts.",
                "J. K. Rowling");
        libro("hp2", "Harry Potter y la cámara secreta", "9788478884957", 1998, "Infantil y juvenil", "Fantasía",
                "Salamandra", 4, "Estante D-2", PORTADA_PROPIA,
                "En su segundo año en Hogwarts, alguien está convirtiendo en piedra a los alumnos.",
                "J. K. Rowling");
        libro("brujas", "Las brujas", "9788420482903", 1983, "Infantil y juvenil", "Fantasía", "Alfaguara",
                2, "Estante D-3", PORTADA_PROPIA,
                "Un niño y su abuela descubren una reunión de brujas que odian a los niños.",
                "Roald Dahl");
        libro("matilda", "Matilda", "9788419507280", 1988, "Infantil y juvenil", "Aventura", "Alfaguara",
                2, "Estante D-3", PORTADA_PROPIA,
                "Una niña genial, amante de los libros, se enfrenta a sus padres y a una directora terrible.",
                "Roald Dahl");
        libro("momo", "Momo", "9788491225133", 1973, "Infantil y juvenil", "Fantasía", "Alfaguara",
                2, "Estante D-4", PORTADA_PROPIA,
                "Una niña que sabe escuchar se enfrenta a los hombres grises que roban el tiempo.",
                "Michael Ende");
        libro("interminable", "La historia interminable", "9788420471549", 1979, "Infantil y juvenil", "Fantasía",
                "Alfaguara", 3, "Estante D-4", PORTADA_PROPIA,
                "Bastián lee un libro sobre el reino de Fantasía y termina formando parte de la historia.",
                "Michael Ende");

        // ---- Ciencia ----
        libro("tiempo", "Breve historia del tiempo", null, 1988, "Ciencia", "Divulgación", "Crítica",
                3, "Estante E-1", "/Recursos/Imagenes/PORTADA-breveHistoriaDelTiempo.jpg",
                "Del big bang a los agujeros negros, explicado para lectores sin formación en física.",
                "Stephen Hawking");
        libro("cosmos", "Cosmos", "9788408053040", 1980, "Ciencia", "Divulgación", "Editorial Planeta",
                1, "Estante E-1", PORTADA_PROPIA,
                "Un viaje por la historia de la ciencia y nuestro lugar en el universo.",
                "Carl Sagan");
        libro("especies", "El origen de las especies", "9788492355143", 1859, "Ciencia", "Clásico", null,
                1, "Estante E-2", PORTADA_PROPIA,
                "La obra en la que Darwin expuso la teoría de la evolución por selección natural.",
                "Charles Darwin");

        // ---- Historia ----
        libro("sapiens", "Sapiens: De animales a dioses", "9788499926223", 2011, "Historia", "Divulgación", "Debate",
                4, "Estante E-3", PORTADA_PROPIA,
                "Una breve historia de la humanidad, desde la Edad de Piedra hasta hoy.",
                "Yuval Noah Harari");
        libro("atlas", "Atlas histórico universal (edición 1995)", null, 1995, "Historia", null, null,
                1, "Depósito", null,
                "Edición antigua retirada de la sala; se conserva por su historial.",
                "Autores varios");

        // ---- Filosofia ----
        libro("sofia", "El mundo de Sofía", "9788478443222", 1991, "Filosofía", "Clásico", "Siruela",
                3, "Estante E-4", PORTADA_PROPIA,
                "Una adolescente recibe cartas misteriosas que le enseñan la historia de la filosofía.",
                "Jostein Gaarder");
        libro("zaratustra", "Así habló Zaratustra", "9788497644501", 1883, "Filosofía", "Clásico", null,
                1, "Estante E-4", PORTADA_PROPIA,
                "La obra en la que Nietzsche presenta la idea del superhombre.",
                "Friedrich Nietzsche");

        // ---- Tecnologia ----
        libro("codigolimpio", "La artesanía del código limpio", "9788441544994", 2021, "Tecnología", null,
                "Anaya Multimedia", 2, "Estante F-1", PORTADA_PROPIA,
                "Disciplinas, estándares y ética para escribir software profesional.",
                "Robert C. Martin");
        libro("clean", "Clean Code", "9780132350884", 2008, "Tecnología", null, "Prentice Hall",
                2, "Estante F-1", PORTADA_PROPIA,
                "Guía práctica para escribir código legible y fácil de mantener.",
                "Robert C. Martin");
        libro("pragmatic", "The Pragmatic Programmer", "9780201616224", 1999, "Tecnología", null, "Addison-Wesley",
                2, "Estante F-1", PORTADA_PROPIA,
                "Consejos prácticos para el oficio de programar.",
                "Andrew Hunt", "David Thomas");
        libro("patrones", "Design Patterns", "9780201633610", 1994, "Tecnología", null, "Addison-Wesley",
                1, "Estante F-2", PORTADA_PROPIA,
                "Los 23 patrones de diseño clásicos de la programación orientada a objetos.",
                "Erich Gamma", "Richard Helm", "Ralph Johnson", "John Vlissides");

        if (soloPortadas) {
            return;
        }
        // Retirado del catalogo: se ve como desactivado en Inventario
        libros.get("atlas").setActivo(false);

        // Libros agregados hace poco (para "nuevos este mes" del dashboard)
        altaLibros.put(libros.get("sombra"), hace(6));
        altaLibros.put(libros.get("codigolimpio"), hace(4));
        altaLibros.put(libros.get("momo"), hace(2));
    }

    /*
     * Crea un libro con todas sus copias disponibles. Las copias prestadas o
     * apartadas se descuentan despues, al crear los prestamos y reservas.
     * portada: PORTADA_PROPIA (imagen de CARPETA_PORTADAS con el nombre de la
     * clave), otra ruta local que empieza con "/" o null (portada por defecto).
     */
    private void libro(String clave, String titulo, String isbn, int anio, String categoria, String genero,
            String editorial, int copias, String ubicacion, String portada, String descripcion, String... autores) {
        String urlPortada = PORTADA_PROPIA.equals(portada) ? CARPETA_PORTADAS + clave + ".jpg" : portada;
        if (soloPortadas) {
            actualizarPortada(titulo, isbn, urlPortada);
            return;
        }
        if (isbn != null && !em.createQuery("SELECT l.id FROM Libro l WHERE l.isbn = :isbn", Long.class)
                .setParameter("isbn", isbn).getResultList().isEmpty()) {
            throw new ValidacionException("Ya existe un libro con el ISBN " + isbn + " (\"" + titulo + "\").");
        }
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setIsbn(isbn);
        libro.setFechaPublicacion(LocalDate.of(anio, 1, 1));
        libro.setDescripcion(descripcion);
        libro.setPortadaUrl(urlPortada);
        libro.setStockTotal(copias);
        libro.setStockDisponible(copias);
        libro.setUbicacion(ubicacion);
        libro.setCategoria(categoria(categoria));
        libro.setGenero(genero == null ? null : genero(genero));
        libro.setEditorial(editorial == null ? null : editorial(editorial));
        for (String nombre : autores) {
            libro.getAutores().add(autor(nombre));
        }
        em.persist(libro);
        libros.put(clave, libro);
        altaLibros.put(libro, hace(ALTA_GENERAL));
    }

    /*
     * Pone la portada local a un libro de una carga anterior. Solo cambia
     * libros con el mismo titulo e ISBN que no tengan portada o que tengan
     * la antigua de Open Library: nunca una portada puesta por otra persona.
     */
    private void actualizarPortada(String titulo, String isbn, String urlPortada) {
        if (urlPortada == null) {
            return;
        }
        TypedQuery<Libro> consulta = em.createQuery(
                "SELECT l FROM Libro l WHERE l.titulo = :titulo AND "
                + (isbn == null ? "l.isbn IS NULL" : "l.isbn = :isbn")
                + " AND (l.portadaUrl IS NULL OR l.portadaUrl LIKE :antigua)", Libro.class)
                .setParameter("titulo", titulo)
                .setParameter("antigua", URL_OPEN_LIBRARY + "%");
        if (isbn != null) {
            consulta.setParameter("isbn", isbn);
        }
        for (Libro libro : consulta.getResultList()) {
            libro.setPortadaUrl(urlPortada);
            portadasActualizadas++;
        }
    }

    // Buscan por nombre y, si no existe, lo crean (no modifican lo existente)

    private Categoria categoria(String nombre) {
        List<Categoria> encontradas = em.createQuery(
                "SELECT c FROM Categoria c WHERE c.nombre = :nombre", Categoria.class)
                .setParameter("nombre", nombre).getResultList();
        if (!encontradas.isEmpty()) {
            return encontradas.get(0);
        }
        Categoria nueva = new Categoria();
        nueva.setNombre(nombre);
        em.persist(nueva);
        return nueva;
    }

    private Genero genero(String nombre) {
        List<Genero> encontrados = em.createQuery(
                "SELECT g FROM Genero g WHERE g.nombre = :nombre", Genero.class)
                .setParameter("nombre", nombre).getResultList();
        if (!encontrados.isEmpty()) {
            return encontrados.get(0);
        }
        Genero nuevo = new Genero();
        nuevo.setNombre(nombre);
        em.persist(nuevo);
        return nuevo;
    }

    private Editorial editorial(String nombre) {
        List<Editorial> encontradas = em.createQuery(
                "SELECT e FROM Editorial e WHERE e.nombre = :nombre", Editorial.class)
                .setParameter("nombre", nombre).getResultList();
        if (!encontradas.isEmpty()) {
            return encontradas.get(0);
        }
        Editorial nueva = new Editorial();
        nueva.setNombre(nombre);
        em.persist(nueva);
        return nueva;
    }

    private Autor autor(String nombre) {
        List<Autor> encontrados = em.createQuery(
                "SELECT a FROM Autor a WHERE a.nombre = :nombre", Autor.class)
                .setParameter("nombre", nombre).getResultList();
        if (!encontrados.isEmpty()) {
            return encontrados.get(0);
        }
        Autor nuevo = new Autor();
        nuevo.setNombre(nombre);
        em.persist(nuevo);
        return nuevo;
    }

    // ===================== Usuarios =====================

    private void crearUsuarios() {
        // Personal (bibliotecarios): registran las entregas y devoluciones
        usuario("laura", "Laura", "Méndez Ortiz", TipoDocumento.CC, "1099000001", "3005550001",
                "laura.mendez", Rol.BIBLIOTECARIO, EstadoUsuario.ACTIVO, 300);
        usuario("andres", "Andrés Felipe", "Rojas Duarte", TipoDocumento.CC, "1099000002", "3005550002",
                "andres.rojas", Rol.BIBLIOTECARIO, EstadoUsuario.ACTIVO, 280);

        // Lectores
        lector("camila", "Camila", "Torres Restrepo", TipoDocumento.CC, "1099000101", "camila.torres", ALTA_GENERAL);
        lector("mateo", "Mateo", "Gómez Arango", TipoDocumento.CC, "1099000102", "mateo.gomez", ALTA_GENERAL);
        lector("valentina", "Valentina", "Herrera Ríos", TipoDocumento.CC, "1099000103", "valentina.herrera", ALTA_GENERAL);
        lector("santiago", "Santiago", "Pérez Londoño", TipoDocumento.CC, "1099000104", "santiago.perez", ALTA_GENERAL);
        lector("isabella", "Isabella", "Castro Mejía", TipoDocumento.CC, "1099000105", "isabella.castro", ALTA_GENERAL);
        lector("sebastian", "Sebastián", "Morales Quintero", TipoDocumento.CC, "1099000106", "sebastian.morales", ALTA_GENERAL);
        lector("mariana", "Mariana", "Vargas Zapata", TipoDocumento.CC, "1099000107", "mariana.vargas", ALTA_GENERAL);
        lector("nicolas", "Nicolás", "Ramírez Ospina", TipoDocumento.CC, "1099000108", "nicolas.ramirez", ALTA_GENERAL);
        lector("daniela", "Daniela", "Jiménez Cárdenas", TipoDocumento.CC, "1099000109", "daniela.jimenez", ALTA_GENERAL);
        lector("samuel", "Samuel", "Ortiz Bermúdez", TipoDocumento.TI, "1099000110", "samuel.ortiz", ALTA_GENERAL);
        lector("gabriela", "Gabriela", "Rincón Salazar", TipoDocumento.TI, "1099000111", "gabriela.rincon", ALTA_GENERAL);
        lector("tomas", "Tomás", "Aguirre Velásquez", TipoDocumento.CC, "1099000112", "tomas.aguirre", ALTA_GENERAL);
        lector("lucia", "Lucía", "Fernández Gil", TipoDocumento.CE, "1099000113", "lucia.fernandez", ALTA_GENERAL);
        lector("juanjose", "Juan José", "Muñoz Patiño", TipoDocumento.CC, "1099000114", "juanjose.munoz", ALTA_GENERAL);
        lector("sara", "Sara", "Mendoza Lozano", TipoDocumento.CC, "1099000115", "sara.mendoza", ALTA_GENERAL);
        lector("alejandro", "Alejandro", "Navarro Beltrán", TipoDocumento.CC, "1099000116", "alejandro.navarro", ALTA_GENERAL);
        lector("paula", "Paula", "Cifuentes Rueda", TipoDocumento.CC, "1099000117", "paula.cifuentes", ALTA_GENERAL);
        lector("david", "David", "Escobar Montoya", TipoDocumento.PA, "1099000118", "david.escobar", ALTA_GENERAL);
        // Cuentas nuevas (se registraron hace pocos dias)
        lector("antonia", "Antonia", "Pineda Guzmán", TipoDocumento.CC, "1099000119", "antonia.pineda", 5);
        lector("emilio", "Emilio", "Sandoval Cruz", TipoDocumento.CC, "1099000120", "emilio.sandoval", 12);

        // Suspendidos: tienen sanciones pendientes (ver crearPrestamos)
        usuarios.get("sebastian").setEstado(EstadoUsuario.SUSPENDIDO);
        usuarios.get("nicolas").setEstado(EstadoUsuario.SUSPENDIDO);
        // Dado de baja: solo conserva su historial, sin nada pendiente
        usuarios.get("alejandro").setEstado(EstadoUsuario.INACTIVO);
    }

    private void lector(String clave, String nombre, String apellidos, TipoDocumento tipo, String documento,
            String correo, int diasDeAlta) {
        // El telefono usa los ultimos digitos del documento (numeros ficticios)
        String telefono = "300555" + documento.substring(documento.length() - 4);
        usuario(clave, nombre, apellidos, tipo, documento, telefono, correo, Rol.LECTOR, EstadoUsuario.ACTIVO,
                diasDeAlta);
    }

    private void usuario(String clave, String nombre, String apellidos, TipoDocumento tipo, String documento,
            String telefono, String inicioCorreo, Rol rol, EstadoUsuario estado, int diasDeAlta) {
        String correo = inicioCorreo + DOMINIO;
        if (existeCorreo(correo)) {
            throw new ValidacionException("Ya existe una cuenta con el correo " + correo + ".");
        }
        boolean documentoUsado = !em.createQuery(
                "SELECT u.id FROM Usuario u WHERE u.tipoDocumento = :tipo AND u.documento = :documento", Long.class)
                .setParameter("tipo", tipo).setParameter("documento", documento)
                .getResultList().isEmpty();
        if (documentoUsado) {
            throw new ValidacionException("Ya existe una cuenta con el documento " + tipo + " " + documento + ".");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setTipoDocumento(tipo);
        usuario.setDocumento(documento);
        usuario.setTelefono(telefono);
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(ContrasenaUtil.encriptar(CONTRASENA_DEMO));
        usuario.setRol(rol);
        usuario.setEstado(estado);
        em.persist(usuario);
        usuarios.put(clave, usuario);
        altaUsuarios.put(usuario, hace(diasDeAlta));
    }

    private boolean existeCorreo(String correo) {
        return !em.createQuery("SELECT u.id FROM Usuario u WHERE u.correo = :correo", Long.class)
                .setParameter("correo", correo).getResultList().isEmpty();
    }

    // ===================== Prestamos =====================

    private void crearPrestamos() {
        // ---- Devueltos: (lector, libro, hace cuantos dias se presto, dias que lo tuvo,
        //      renovaciones, estado de la mora si se devolvio tarde) ----
        Prestamo comunidadCamila = devuelto("camila", "comunidad", 50, 25, 1, EstadoSancion.PAGADA);
        devuelto("camila", "sapiens", 120, 12, 0, null);
        devuelto("camila", "hp1", 170, 10, 0, null);
        devuelto("camila", "tunel", 140, 10, 0, null);
        devuelto("mateo", "1984", 90, 14, 0, null);
        devuelto("mateo", "rebelion", 75, 9, 0, null);
        devuelto("mateo", "zaratustra", 12, 6, 0, null);
        Prestamo casaValentina = devuelto("valentina", "casa", 40, 11, 0, null);
        devuelto("valentina", "rayuela", 130, 30, 1, EstadoSancion.ANULADA);
        Prestamo principitoSantiago = devuelto("santiago", "principito", 65, 12, 0, null);
        devuelto("santiago", "dune", 150, 20, 1, null);
        devuelto("santiago", "patrones", 22, 9, 0, null);
        devuelto("isabella", "amor", 110, 13, 0, null);
        devuelto("isabella", "hp2", 80, 14, 0, null);
        devuelto("isabella", "casa", 14, 7, 0, null);
        devuelto("sebastian", "cien", 140, 19, 0, EstadoSancion.PAGADA);
        devuelto("mariana", "veinte", 35, 8, 0, null);
        devuelto("mariana", "mundofeliz", 95, 12, 0, null);
        devuelto("mariana", "hp1", 10, 6, 0, null);
        devuelto("nicolas", "1984", 55, 26, 0, EstadoSancion.PENDIENTE);
        devuelto("nicolas", "aleph", 160, 14, 0, null);
        devuelto("nicolas", "fahrenheit", 120, 40, 1, EstadoSancion.PAGADA);
        devuelto("daniela", "it", 70, 10, 0, null);
        devuelto("daniela", "pedro", 8, 5, 0, null);
        devuelto("samuel", "brujas", 45, 7, 0, null);
        devuelto("samuel", "hp1", 100, 15, 0, EstadoSancion.PAGADA);
        devuelto("gabriela", "matilda", 30, 9, 0, null);
        devuelto("gabriela", "interminable", 120, 14, 0, null);
        devuelto("gabriela", "veinte", 5, 3, 0, null);
        devuelto("tomas", "frankenstein", 25, 10, 0, null);
        devuelto("tomas", "terramar", 85, 22, 1, EstadoSancion.PENDIENTE);
        devuelto("tomas", "cid", 45, 10, 0, null);
        devuelto("lucia", "quijote", 150, 28, 2, null);
        devuelto("lucia", "bestiario", 20, 6, 0, null);
        devuelto("lucia", "dune", 15, 8, 0, null);
        devuelto("juanjose", "sapiens", 60, 13, 0, null);
        devuelto("juanjose", "clean", 140, 14, 0, null);
        devuelto("juanjose", "fahrenheit", 28, 11, 0, null);
        devuelto("sara", "orgullo", 50, 16, 0, EstadoSancion.PAGADA);
        devuelto("sara", "cronica", 18, 5, 0, null);
        devuelto("sara", "chejov", 110, 12, 0, null);
        devuelto("alejandro", "sofia", 175, 14, 0, null);
        devuelto("alejandro", "tiempo", 150, 12, 0, null);
        devuelto("paula", "ficciones", 40, 12, 0, null);
        devuelto("paula", "pragmatic", 100, 20, 1, null);
        devuelto("david", "cosmos", 33, 14, 0, null);
        devuelto("david", "especies", 90, 10, 0, null);
        devuelto("david", "lazarillo", 65, 9, 0, null);

        // ---- Activos: (lector, libro, hace cuantos dias se presto, renovaciones) ----
        // Si ya paso la fecha limite (14 dias + 7 por renovacion), aparecen como vencidos
        Prestamo hp1Isabella = activo("isabella", "hp1", 6, 0);
        Prestamo itTomas = activo("tomas", "it", 10, 0);
        activo("camila", "cien", 5, 0);
        activo("mateo", "dune", 9, 0);
        activo("valentina", "interminable", 20, 1);
        activo("santiago", "ficciones", 3, 0);
        activo("mariana", "orgullo", 12, 0);
        activo("daniela", "sombra", 4, 0);
        activo("emilio", "codigolimpio", 3, 0);
        activo("gabriela", "principito", 8, 0);
        activo("lucia", "amor", 13, 0);           // vence manana
        activo("juanjose", "comunidad", 27, 2);   // ya uso las 2 renovaciones
        activo("david", "tiempo", 7, 0);
        activo("sara", "pedro", 2, 0);
        activo("nicolas", "rosa", 30, 0);         // vencido 16 dias
        activo("nicolas", "fundacion", 25, 0);    // vencido 11 dias
        activo("paula", "metamorfosis", 18, 0);   // vencido 4 dias
        activo("samuel", "hp2", 16, 0);           // vencido 2 dias
        activo("sebastian", "dostorres", 40, 0);  // vencido 26 dias

        // ---- Perdidos (salen del inventario y generan la sancion) ----
        perdido("sebastian", "hp2", 60, 30, "65000", EstadoSancion.PENDIENTE);
        perdido("daniela", "tunel", 100, 25, "48000", EstadoSancion.PAGADA);

        // ---- Anulado (registrado por error) ----
        anulado("mateo", "cronica", 45);

        // Prestamos que salieron de una reserva (quedan como RETIRADA)
        retiradaDe(comunidadCamila);
        retiradaDe(casaValentina);
        retiradaDe(hp1Isabella);
        retiradaDe(itTomas);

        // El libro de Santiago volvio danado: el personal registro la sancion
        Sancion danado = nuevaSancion(principitoSantiago.getUsuario(), principitoSantiago, TipoSancion.LIBRO_DANADO,
                "18000", null, principitoSantiago.getFechaDevolucion(), principitoSantiago.getRecibidoPor(),
                "Devolvió el libro con varias páginas rotas.");
        pagar(danado, 1);
    }

    /*
     * Prestamo ya devuelto. No cambia el stock: la copia salio y volvio.
     * Si se devolvio despues de la fecha limite genera la MORA igual que
     * SancionService (dias de calendario x TARIFA_MORA_DIARIA, sin
     * registradoPor porque la genera el sistema), con el estado indicado.
     */
    private Prestamo devuelto(String lector, String libro, int haceDias, int diasConElLibro, int renovaciones,
            EstadoSancion estadoMora) {
        Prestamo prestamo = nuevoPrestamo(lector, libro, haceDias, renovaciones);
        LocalDateTime devolucion = prestamo.getFechaPrestamo().toLocalDate().plusDays(diasConElLibro)
                .atTime(10 + diasConElLibro % 7, (diasConElLibro * 7) % 60);
        if (devolucion.isAfter(ahora)) {
            throw new IllegalStateException("Fecha de devolución en el futuro: " + libro + " de " + lector);
        }
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucion(devolucion);
        prestamo.setRecibidoPor(personalDeTurno());

        long dias = ChronoUnit.DAYS.between(prestamo.getFechaLimite().toLocalDate(), devolucion.toLocalDate());
        if (dias > 0) {
            BigDecimal importe = ReglasBiblioteca.TARIFA_MORA_DIARIA.multiply(BigDecimal.valueOf(dias));
            Sancion mora = nuevaSancion(prestamo.getUsuario(), prestamo, TipoSancion.MORA, importe.toPlainString(),
                    (int) dias, devolucion, null,
                    "Devolución con " + dias + (dias == 1 ? " día" : " días") + " de retraso.");
            if (estadoMora == EstadoSancion.PAGADA) {
                pagar(mora, 2);
            } else if (estadoMora == EstadoSancion.ANULADA) {
                mora.setEstado(EstadoSancion.ANULADA);
                mora.setDetalle(mora.getDetalle()
                        + " | Anulada: El retraso coincidió con el cierre de la biblioteca por festivo.");
            }
        }
        return prestamo;
    }

    /* Prestamo que el lector todavia tiene: ocupa una copia. */
    private Prestamo activo(String lector, String libro, int haceDias, int renovaciones) {
        Prestamo prestamo = nuevoPrestamo(lector, libro, haceDias, renovaciones);
        apartarCopia(prestamo.getLibro());
        return prestamo;
    }

    /* El lector perdio el libro: la copia sale del inventario (baja el total, como en PrestamoService). */
    private void perdido(String lector, String libro, int haceDias, int diasHastaReportarlo, String importe,
            EstadoSancion estado) {
        Prestamo prestamo = nuevoPrestamo(lector, libro, haceDias, 0);
        Libro copia = prestamo.getLibro();
        // La entrega ocupo una copia y la perdida la saca del total
        apartarCopia(copia);
        copia.setStockTotal(copia.getStockTotal() - 1);
        prestamo.setEstado(EstadoPrestamo.PERDIDO);
        prestamo.setRecibidoPor(personalDeTurno());

        LocalDateTime reporte = prestamo.getFechaPrestamo().plusDays(diasHastaReportarlo);
        Sancion sancion = nuevaSancion(prestamo.getUsuario(), prestamo, TipoSancion.LIBRO_PERDIDO, importe,
                null, reporte, prestamo.getRecibidoPor(), "Reposición del libro \"" + copia.getTitulo() + "\".");
        if (estado == EstadoSancion.PAGADA) {
            pagar(sancion, 5);
        }
    }

    /* Prestamo anulado por error: la copia volvio al stock, asi que no lo cambia. */
    private void anulado(String lector, String libro, int haceDias) {
        Prestamo prestamo = nuevoPrestamo(lector, libro, haceDias, 0);
        prestamo.setEstado(EstadoPrestamo.ANULADO);
    }

    private Prestamo nuevoPrestamo(String lector, String libro, int haceDias, int renovaciones) {
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuarios.get(lector));
        prestamo.setLibro(libros.get(libro));
        prestamo.setRegistradoPor(personalDeTurno());
        prestamo.setFechaPrestamo(hace(haceDias));
        prestamo.setFechaLimite(prestamo.getFechaPrestamo().plusDays(
                ReglasBiblioteca.DIAS_PRESTAMO + (long) ReglasBiblioteca.DIAS_RENOVACION * renovaciones));
        prestamo.setRenovaciones(renovaciones);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        em.persist(prestamo);
        totalPrestamos++;
        return prestamo;
    }

    // ===================== Reservas =====================

    private void crearReservas() {
        // Pendientes: esperan que el personal las apruebe
        pendiente("antonia", "hp1", 1);
        pendiente("paula", "sombra", 1);
        pendiente("david", "dune", 2);
        pendiente("sebastian", "principito", 2);
        pendiente("tomas", "comunidad", 3);

        // Aprobadas: la copia esta apartada y el lector tiene 3 dias para retirarla
        aprobada("mariana", "sapiens", 1);
        aprobada("juanjose", "cosmos", 2);
        aprobada("lucia", "rayuela", 1);

        // Cerradas
        rechazada("samuel", "it", 20, "Libro para lectores adultos: se le recomendó otra obra.");
        rechazada("gabriela", "especies", 50, "Es la única copia y es de consulta en sala.");
        cancelada("daniela", "rosa", 15);
        cancelada("david", "fundacion", 60);
        vencida("nicolas", "dune", 22);
        vencida("sara", "quijote", 35);
    }

    private void pendiente(String lector, String libro, int haceDias) {
        nuevaReserva(lector, libro, hace(haceDias), EstadoReserva.PENDIENTE);
    }

    /* Aprobada hace poco: aparta una copia, como ReservaService.aprobar. */
    private void aprobada(String lector, String libro, int haceDias) {
        Reserva reserva = nuevaReserva(lector, libro, hace(haceDias), EstadoReserva.APROBADA);
        resolver(reserva, reserva.getFechaReserva().plusHours(2));
        if (reserva.getFechaLimiteRetiro().isBefore(ahora)) {
            throw new IllegalStateException("La reserva aprobada ya estaría vencida: " + libro);
        }
        apartarCopia(reserva.getLibro());
    }

    private void rechazada(String lector, String libro, int haceDias, String motivo) {
        Reserva reserva = nuevaReserva(lector, libro, hace(haceDias), EstadoReserva.RECHAZADA);
        reserva.setResueltaPor(personalDeTurno());
        reserva.setResueltaEn(reserva.getFechaReserva().plusHours(4));
        reserva.setObservaciones(motivo);
    }

    /* Cancelada por el propio lector antes de retirarla. */
    private void cancelada(String lector, String libro, int haceDias) {
        nuevaReserva(lector, libro, hace(haceDias), EstadoReserva.CANCELADA);
    }

    /* Se aprobo pero el lector no la retiro en 3 dias (la copia ya volvio al stock). */
    private void vencida(String lector, String libro, int haceDias) {
        Reserva reserva = nuevaReserva(lector, libro, hace(haceDias), EstadoReserva.VENCIDA);
        resolver(reserva, reserva.getFechaReserva().plusHours(3));
        reserva.setObservaciones("Venció: no se retiró en el plazo.");
    }

    /* Reserva que termino en este prestamo: se aprobo el dia anterior a la entrega. */
    private void retiradaDe(Prestamo prestamo) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(prestamo.getUsuario());
        reserva.setLibro(prestamo.getLibro());
        reserva.setFechaReserva(prestamo.getFechaPrestamo().minusDays(2));
        reserva.setEstado(EstadoReserva.RETIRADA);
        resolver(reserva, prestamo.getFechaPrestamo().minusDays(1));
        em.persist(reserva);
        prestamo.setReserva(reserva);
        totalReservas++;
    }

    private Reserva nuevaReserva(String lector, String libro, LocalDateTime fecha, EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuarios.get(lector));
        reserva.setLibro(libros.get(libro));
        reserva.setFechaReserva(fecha);
        reserva.setEstado(estado);
        em.persist(reserva);
        totalReservas++;
        return reserva;
    }

    /* Aprobacion: quien y cuando, y el plazo de retiro (DIAS_RETIRO_RESERVA). */
    private void resolver(Reserva reserva, LocalDateTime cuando) {
        reserva.setResueltaPor(personalDeTurno());
        reserva.setResueltaEn(cuando);
        reserva.setFechaLimiteRetiro(cuando.plusDays(ReglasBiblioteca.DIAS_RETIRO_RESERVA));
    }

    // ===================== Sanciones =====================

    /* Sanciones que no salen de un prestamo devuelto o perdido. */
    private void crearSancionesManuales() {
        nuevaSancion(usuarios.get("mateo"), null, TipoSancion.OTRO, "5000", null, hace(9), usuarios.get("laura"),
                "Reposición del carné de la biblioteca.");
    }

    /* diasRetraso solo para MORA (la base lo exige); null en los demas tipos. */
    private Sancion nuevaSancion(Usuario usuario, Prestamo prestamo, TipoSancion tipo, String importe,
            Integer diasRetraso, LocalDateTime fecha, Usuario registradoPor, String detalle) {
        Sancion sancion = new Sancion();
        sancion.setUsuario(usuario);
        sancion.setPrestamo(prestamo);
        sancion.setTipo(tipo);
        sancion.setImporte(new BigDecimal(importe));
        sancion.setDiasRetraso(diasRetraso);
        sancion.setEstado(EstadoSancion.PENDIENTE);
        sancion.setFechaEmision(fecha);
        sancion.setRegistradoPor(registradoPor);
        sancion.setDetalle(detalle);
        em.persist(sancion);
        totalSanciones++;
        return sancion;
    }

    /* La marca como pagada unos dias despues de emitida (nunca en el futuro). */
    private void pagar(Sancion sancion, int diasDespues) {
        LocalDateTime pago = sancion.getFechaEmision().plusDays(diasDespues);
        sancion.setEstado(EstadoSancion.PAGADA);
        sancion.setFechaPago(pago.isAfter(ahora) ? ahora : pago);
    }

    // ===================== Favoritos y proveedores =====================

    private void crearFavoritos() {
        favoritos("camila", "cien", "sapiens", "comunidad", "hp1");
        favoritos("mateo", "dune", "1984", "fundacion");
        favoritos("valentina", "casa", "rayuela", "interminable");
        favoritos("santiago", "ficciones", "aleph", "principito");
        favoritos("isabella", "hp1", "hp2", "amor");
        favoritos("mariana", "orgullo", "veinte");
        favoritos("daniela", "sombra", "it");
        favoritos("samuel", "hp1", "brujas");
        favoritos("gabriela", "principito", "matilda", "momo");
        favoritos("tomas", "it", "frankenstein", "terramar");
        favoritos("lucia", "quijote", "rayuela");
        favoritos("juanjose", "pragmatic", "clean", "codigolimpio");
        favoritos("paula", "metamorfosis");
        favoritos("david", "cosmos", "tiempo", "especies");
        favoritos("sara", "pedro", "cronica");
        favoritos("antonia", "hp1");
    }

    private void favoritos(String lector, String... claves) {
        for (String clave : claves) {
            usuarios.get(lector).getFavoritos().add(libros.get(clave));
            totalFavoritos++;
        }
    }

    private void crearProveedores() {
        proveedor("Distribuidora Andina de Libros S.A.S.", "900456781-3", "Marcela Quintero", "6015550101",
                "ventas@distriandina.example", "Calle 45 # 12-30, Bogotá", true);
        proveedor("Librería Mayorista El Faro Ltda.", "900567892-5", "Hernán Salcedo", "6045550102",
                "pedidos@elfaro.example", "Carrera 50 # 30-15, Medellín", true);
        proveedor("Ediciones Escolares del Pacífico", "900678903-7", "Rosa Angulo", "6025550103",
                "contacto@edpacifico.example", "Avenida 6N # 23-40, Cali", true);
        proveedor("Importadora Técnica de Libros S.A.S.", "900789014-9", "Felipe Arboleda", "6015550104",
                "info@importec.example", "Calle 100 # 8-60, Bogotá", false);
    }

    private void proveedor(String nombre, String nit, String contacto, String telefono, String correo,
            String direccion, boolean activo) {
        boolean repetido = !em.createQuery(
                "SELECT p.id FROM Proveedor p WHERE p.nombre = :nombre OR p.nit = :nit", Long.class)
                .setParameter("nombre", nombre).setParameter("nit", nit)
                .getResultList().isEmpty();
        if (repetido) {
            throw new ValidacionException("Ya existe el proveedor \"" + nombre + "\" o el NIT " + nit + ".");
        }
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(nombre);
        proveedor.setNit(nit);
        proveedor.setPersonaContacto(contacto);
        proveedor.setTelefono(telefono);
        proveedor.setCorreo(correo);
        proveedor.setDireccion(direccion);
        proveedor.setActivo(activo);
        em.persist(proveedor);
        totalProveedores++;
    }

    // ===================== Ayudas =====================

    /*
     * creado_en lo pone @PrePersist con la fecha actual y JPA no lo deja
     * cambiar (updatable = false). Para que el historial sea creible (nadie
     * pidio un libro antes de registrarse) se ajusta con SQL, solo en las
     * filas que acaba de crear esta carga.
     */
    private void ajustarFechasDeAlta() {
        for (Map.Entry<Libro, LocalDateTime> alta : altaLibros.entrySet()) {
            em.createNativeQuery("UPDATE libros SET creado_en = ?1, actualizado_en = ?1 WHERE id_libro = ?2")
                    .setParameter(1, alta.getValue()).setParameter(2, alta.getKey().getId())
                    .executeUpdate();
        }
        for (Map.Entry<Usuario, LocalDateTime> alta : altaUsuarios.entrySet()) {
            em.createNativeQuery("UPDATE usuarios SET creado_en = ?1, actualizado_en = ?1 WHERE id_usuario = ?2")
                    .setParameter(1, alta.getValue()).setParameter(2, alta.getKey().getId())
                    .executeUpdate();
        }
    }

    /* Deja constancia de la carga en la auditoria (sin usuario: la hizo el sistema). */
    private void registrarAuditoria() {
        Auditoria registro = new Auditoria();
        registro.setAccion(AccionAuditoria.CREAR);
        registro.setEntidad("DatosDemo");
        registro.setDetalle("Carga de datos de demostración (util/CargarDatosDemo): " + libros.size()
                + " libros, " + usuarios.size() + " usuarios, " + totalPrestamos + " préstamos, "
                + totalReservas + " reservas y " + totalSanciones + " sanciones.");
        em.persist(registro);
    }

    /* Una copia deja de estar disponible (prestamo activo o reserva aprobada). */
    private void apartarCopia(Libro libro) {
        if (libro.getStockDisponible() < 1) {
            throw new IllegalStateException("No quedan copias disponibles de \"" + libro.getTitulo() + "\"");
        }
        libro.setStockDisponible(libro.getStockDisponible() - 1);
    }

    /* Alterna entre los dos bibliotecarios de la demo. */
    private Usuario personalDeTurno() {
        turno++;
        return usuarios.get(turno % 2 == 0 ? "laura" : "andres");
    }

    /* Un momento de hace "dias" dias, en horario de atencion (entre 9:00 y 16:59). */
    private LocalDateTime hace(int dias) {
        return ahora.toLocalDate().minusDays(dias).atTime(9 + dias % 8, (dias * 13) % 60);
    }
}
