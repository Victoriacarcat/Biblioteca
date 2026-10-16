package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.CopiaSeguridadDAO;
import com.mycompany.wisebooks.dto.CopiaSeguridadDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.CopiaSeguridad;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * Respaldos de la base de datos (solo ADMINISTRADOR).
 *
 * Genera un archivo .sql con mysqldump (el que trae XAMPP) y deja su
 * registro en copias_seguridad, tanto si sale bien como si falla.
 *
 * Nada va escrito a mano en el codigo:
 *   - Base, servidor, usuario y contrasena se leen de META-INF/persistence.xml.
 *   - La ruta de mysqldump y la carpeta de respaldos se pueden cambiar con
 *     propiedades de Java (-Dwisebooks.mysqldump=... y -Dwisebooks.respaldos=...)
 *     o variables de entorno (WISEBOOKS_MYSQLDUMP y WISEBOOKS_RESPALDOS).
 *     Si no se indican, se usan los valores por defecto de abajo.
 */
public class CopiaSeguridadService {

    private static final String MYSQLDUMP_POR_DEFECTO = "C:\\xampp\\mysql\\bin\\mysqldump.exe";
    private static final Path CARPETA_POR_DEFECTO =
            Paths.get(System.getProperty("user.home"), "WiseBooks", "respaldos");

    private static final int MAX_MENSAJE_ERROR = 500;
    private static final long MINUTOS_MAXIMOS = 5;
    private static final DateTimeFormatter FORMATO_NOMBRE = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private final CopiaSeguridadDAO copiaDAO = new CopiaSeguridadDAO();
    private final AuditoriaService auditoriaService = new AuditoriaService();

    /* Datos de conexion leidos de persistence.xml. */
    private record Conexion(String servidor, int puerto, String base, String usuario, String contrasena) {
    }

    /* Resultado de ejecutar mysqldump: tamano si salio bien, o el error. */
    private record Resultado(Long tamanoBytes, String error) {
    }

    // Consultas

    /* Todos los respaldos registrados, el mas reciente primero. */
    public List<CopiaSeguridadDTO> listar(UsuarioDTO actor) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            return copiaDAO.listar(em).stream()
                    .map(CopiaSeguridadDTO::desde)
                    .toList();
        });
    }

    /*
     * Ruta del archivo de un respaldo exitoso, para que el servlet lo envie
     * como descarga. Falla si el archivo ya no esta en disco.
     */
    public Path obtenerArchivo(UsuarioDTO actor, Long idCopia) {
        String nombre = Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            CopiaSeguridad copia = (idCopia == null) ? null : copiaDAO.buscarPorId(em, idCopia).orElse(null);
            if (copia == null || !copia.isExitosa()) {
                throw new ValidacionException("El respaldo no existe.");
            }
            return copia.getNombreArchivo();
        });
        Path archivo = carpetaRespaldos().resolve(nombre).normalize();
        if (!archivo.startsWith(carpetaRespaldos()) || !Files.isRegularFile(archivo)) {
            throw new ValidacionException("El archivo del respaldo ya no está en el servidor.");
        }
        return archivo;
    }

    // Generar

    /*
     * Genera un respaldo nuevo. mysqldump corre FUERA de la transaccion (puede
     * tardar); al terminar se guarda el registro. Si falla, el registro queda
     * con exitosa = false y se lanza un ValidacionException con el motivo.
     */
    public CopiaSeguridadDTO generar(UsuarioDTO actor) {
        Transaccion.consultar(em -> Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR));
        Conexion conexion = leerConexion();

        String nombre = "wisebooks_" + LocalDateTime.now().format(FORMATO_NOMBRE) + ".sql";
        Path archivo = carpetaRespaldos().resolve(nombre);
        Resultado resultado = ejecutarMysqldump(conexion, archivo);

        CopiaSeguridadDTO copia = Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            if (copiaDAO.existeNombreArchivo(em, nombre)) {
                throw new ValidacionException("Ya existe un respaldo con ese nombre. Intenta de nuevo.");
            }

            CopiaSeguridad registro = new CopiaSeguridad();
            registro.setNombreArchivo(nombre);
            registro.setCreadoPor(admin);
            registro.setExitosa(resultado.error() == null);
            registro.setTamanoBytes(resultado.tamanoBytes());
            registro.setMensajeError(Texto.recortar(resultado.error(), MAX_MENSAJE_ERROR));
            copiaDAO.guardar(em, registro);

            auditoriaService.registrar(em, admin, AccionAuditoria.RESPALDO, "CopiaSeguridad", registro.getId(),
                    registro.isExitosa() ? "Generó el respaldo " + nombre + "."
                    : "Falló el respaldo " + nombre + ": " + resultado.error());
            return CopiaSeguridadDTO.desde(registro);
        });

        if (!copia.isExitosa()) {
            throw new ValidacionException("No se pudo generar el respaldo: " + copia.getMensajeError());
        }
        return copia;
    }

    // Ayudas internas

    private Resultado ejecutarMysqldump(Conexion conexion, Path archivo) {
        Path mysqldump = Paths.get(configuracion("wisebooks.mysqldump", "WISEBOOKS_MYSQLDUMP",
                MYSQLDUMP_POR_DEFECTO));
        if (!Files.isRegularFile(mysqldump)) {
            return new Resultado(null, "No se encontró mysqldump en " + mysqldump
                    + ". Configura wisebooks.mysqldump con la ruta correcta.");
        }

        List<String> comando = new ArrayList<>(List.of(
                mysqldump.toString(),
                "--host=" + conexion.servidor(),
                "--port=" + conexion.puerto(),
                "--user=" + conexion.usuario(),
                "--default-character-set=utf8mb4",
                "--single-transaction",     // copia consistente sin bloquear las tablas
                "--routines",
                "--result-file=" + archivo,
                conexion.base()));

        ProcessBuilder proceso = new ProcessBuilder(comando).redirectErrorStream(true);
        // La contrasena va por variable de entorno, no en el comando
        // (asi no aparece en la lista de procesos del sistema)
        if (!conexion.contrasena().isEmpty()) {
            proceso.environment().put("MYSQL_PWD", conexion.contrasena());
        }

        try {
            Files.createDirectories(archivo.getParent());
            Process ejecucion = proceso.start();
            String salida;
            try (InputStream mensajes = ejecucion.getInputStream()) {
                salida = new String(mensajes.readAllBytes(), StandardCharsets.UTF_8).trim();
            }
            if (!ejecucion.waitFor(MINUTOS_MAXIMOS, TimeUnit.MINUTES)) {
                ejecucion.destroyForcibly();
                return fallo(archivo, "mysqldump tardó más de " + MINUTOS_MAXIMOS + " minutos.");
            }
            if (ejecucion.exitValue() != 0) {
                return fallo(archivo, salida.isEmpty() ? "mysqldump terminó con código " + ejecucion.exitValue() : salida);
            }
            return new Resultado(Files.size(archivo), null);
        } catch (IOException e) {
            return fallo(archivo, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return fallo(archivo, "El respaldo se interrumpió.");
        }
    }

    /* Borra el archivo a medio escribir y devuelve el error. */
    private Resultado fallo(Path archivo, String error) {
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            // Si no se puede borrar no es grave: el registro ya dice que fallo
        }
        return new Resultado(null, error);
    }

    /*
     * Lee la conexion directamente de META-INF/persistence.xml. (No sirve
     * EntityManagerFactory.getProperties(): Hibernate devuelve el usuario y la
     * contrasena ocultos como "****".) Servidor, puerto y base salen de la URL
     * jdbc:mariadb://localhost:3306/wisebooks
     */
    private Conexion leerConexion() {
        Map<String, String> propiedades = new HashMap<>();
        try (InputStream xml = getClass().getClassLoader().getResourceAsStream("META-INF/persistence.xml")) {
            if (xml == null) {
                throw new ValidacionException("No se encontró META-INF/persistence.xml.");
            }
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            // No resolver entidades externas del XML (buena practica de seguridad)
            fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            NodeList nodos = fabrica.newDocumentBuilder().parse(xml).getElementsByTagName("property");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element propiedad = (Element) nodos.item(i);
                propiedades.put(propiedad.getAttribute("name"), propiedad.getAttribute("value"));
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new ValidacionException("No se pudo leer persistence.xml: " + e.getMessage());
        }

        String url = propiedades.getOrDefault("jakarta.persistence.jdbc.url", "");
        URI uri = URI.create(url.replaceFirst("^jdbc:", ""));
        String base = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");
        if (uri.getHost() == null || base.isEmpty()) {
            throw new ValidacionException("No se pudo leer la conexión a la base desde persistence.xml.");
        }
        return new Conexion(
                uri.getHost(),
                uri.getPort() == -1 ? 3306 : uri.getPort(),
                base,
                propiedades.getOrDefault("jakarta.persistence.jdbc.user", "root"),
                propiedades.getOrDefault("jakarta.persistence.jdbc.password", ""));
    }

    private Path carpetaRespaldos() {
        return Paths.get(configuracion("wisebooks.respaldos", "WISEBOOKS_RESPALDOS",
                CARPETA_POR_DEFECTO.toString())).toAbsolutePath().normalize();
    }

    /* Valor de una propiedad de Java; si no esta, de la variable de entorno; si no, el por defecto. */
    private static String configuracion(String propiedad, String variable, String porDefecto) {
        String valor = System.getProperty(propiedad);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(variable);
        }
        return (valor == null || valor.isBlank()) ? porDefecto : valor.trim();
    }
}
