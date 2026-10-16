package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.CopiaSeguridad;
import java.time.LocalDateTime;

/*
 * Lo que se muestra de un respaldo en la lista del administrador.
 * IMPORTANTE: desde(...) debe llamarse mientras el EntityManager sigue abierto,
 * porque lee quien lo creo (relacion LAZY).
 */
public class CopiaSeguridadDTO {

    private Long id;
    private String nombreArchivo;
    private Long tamanoBytes;
    private boolean exitosa;
    private String mensajeError;
    private String creadoPor;
    private LocalDateTime creadoEn;

    public static CopiaSeguridadDTO desde(CopiaSeguridad c) {
        CopiaSeguridadDTO dto = new CopiaSeguridadDTO();
        dto.id = c.getId();
        dto.nombreArchivo = c.getNombreArchivo();
        dto.tamanoBytes = c.getTamanoBytes();
        dto.exitosa = c.isExitosa();
        dto.mensajeError = c.getMensajeError();
        dto.creadoPor = c.getCreadoPor().getNombre() + " " + c.getCreadoPor().getApellidos();
        dto.creadoEn = c.getCreadoEn();
        return dto;
    }

    /* Tamano legible: "512 B", "1.4 KB", "3.2 MB". Vacio si el respaldo fallo. */
    public String getTamanoTexto() {
        if (tamanoBytes == null) {
            return "";
        }
        if (tamanoBytes < 1024) {
            return tamanoBytes + " B";
        }
        if (tamanoBytes < 1024 * 1024) {
            return String.format("%.1f KB", tamanoBytes / 1024.0);
        }
        return String.format("%.1f MB", tamanoBytes / (1024.0 * 1024.0));
    }

    public Long getId() {
        return id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public boolean isExitosa() {
        return exitosa;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
