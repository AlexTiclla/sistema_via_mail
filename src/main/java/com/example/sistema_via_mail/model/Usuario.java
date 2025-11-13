package com.example.sistema_via_mail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entidad Usuario
 * Gestiona propietarios, secretarias y conductores (CU1)
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank(message = "El CI es obligatorio")
    @Size(max = 20)
    @Column(name = "ci", unique = true, nullable = false, length = 20)
    private String ci;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El tipo de usuario es obligatorio")
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private String tipoUsuario; // PROPIETARIO, SECRETARIA, CONDUCTOR

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Email(message = "El email debe ser válido")
    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 255)
    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "estado", length = 20)
    private String estado = "ACTIVO"; // ACTIVO, INACTIVO

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Constructor por defecto
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "ACTIVO";
    }

    // Constructor con parámetros
    public Usuario(String ci, String nombre, String apellido, String tipoUsuario) {
        this();
        this.ci = ci;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "ACTIVO";
        }
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", ci='" + ci + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}

