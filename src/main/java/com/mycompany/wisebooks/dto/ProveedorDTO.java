package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.Proveedor;
import java.time.LocalDateTime;

/*
 * Lo que se muestra de un proveedor (directorio de contactos).
 */
public class ProveedorDTO {

    private Long id;
    private String nombre;
    private String nit;
    private String personaContacto;
    private String telefono;
    private String correo;
    private String direccion;
    private boolean activo;
    private LocalDateTime creadoEn;

    public static ProveedorDTO desde(Proveedor p) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.id = p.getId();
        dto.nombre = p.getNombre();
        dto.nit = p.getNit();
        dto.personaContacto = p.getPersonaContacto();
        dto.telefono = p.getTelefono();
        dto.correo = p.getCorreo();
        dto.direccion = p.getDireccion();
        dto.activo = p.isActivo();
        dto.creadoEn = p.getCreadoEn();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNit() {
        return nit;
    }

    public String getPersonaContacto() {
        return personaContacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
