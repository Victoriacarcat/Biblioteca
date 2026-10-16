/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.TipoDocumento;
import com.mycompany.wisebooks.entity.Usuario;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * Lo que se puede mostrar o guardar en sesion de un usuario. No incluye la
 * contrasena ni su hash, a proposito.
 * Serializable: vive en la sesion HTTP y Tomcat guarda las sesiones en disco
 * al reiniciarse.
 */
public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String documento;
    private String telefono;
    private String correo;
    private Rol rol;
    private EstadoUsuario estado;
    private LocalDateTime creadoEn;


    public static UsuarioDTO desde(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.id = u.getId();
        dto.nombre = u.getNombre();
        dto.apellidos = u.getApellidos();
        dto.tipoDocumento = u.getTipoDocumento();
        dto.documento = u.getDocumento();
        dto.telefono = u.getTelefono();
        dto.correo = u.getCorreo();
        dto.rol = u.getRol();
        dto.estado = u.getEstado();
        dto.creadoEn = u.getCreadoEn();
        return dto;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public Rol getRol() {
        return rol;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
