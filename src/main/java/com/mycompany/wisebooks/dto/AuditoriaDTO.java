package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.Auditoria;
import java.time.LocalDateTime;

/*
 * Lo que se muestra de un registro de auditoria (bitacora del administrador).
 * IMPORTANTE: desde(...) debe llamarse mientras el EntityManager sigue abierto,
 * porque lee el usuario (relacion LAZY).
 */
public class AuditoriaDTO {

    private Long id;
    private String usuario;
    private String correoUsuario;
    private AccionAuditoria accion;
    private String entidad;
    private Long idRegistro;
    private String detalle;
    private String ip;
    private LocalDateTime fecha;

    public static AuditoriaDTO desde(Auditoria a) {
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.id = a.getId();
        // Vacio = accion sin usuario (por ejemplo, un login fallido)
        if (a.getUsuario() != null) {
            dto.usuario = a.getUsuario().getNombre() + " " + a.getUsuario().getApellidos();
            dto.correoUsuario = a.getUsuario().getCorreo();
        }
        dto.accion = a.getAccion();
        dto.entidad = a.getEntidad();
        dto.idRegistro = a.getIdRegistro();
        dto.detalle = a.getDetalle();
        dto.ip = a.getIp();
        dto.fecha = a.getFecha();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public AccionAuditoria getAccion() {
        return accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public Long getIdRegistro() {
        return idRegistro;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getIp() {
        return ip;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
