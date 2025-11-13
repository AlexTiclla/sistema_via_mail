package com.example.sistema_via_mail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Ruta
 * Gestiona las rutas disponibles (CU3)
 */
@Entity
@Table(name = "ruta")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long idRuta;

    @NotBlank(message = "El nombre de la ruta es obligatorio")
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El origen es obligatorio")
    @Size(max = 100)
    @Column(name = "origen", nullable = false, length = 100)
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    @Size(max = 100)
    @Column(name = "destino", nullable = false, length = 100)
    private String destino;

    @Column(name = "distancia_km", precision = 10, scale = 2)
    private BigDecimal distanciaKm;

    @Column(name = "duracion_horas", precision = 5, scale = 2)
    private BigDecimal duracionHoras;

    @DecimalMin(value = "0.01", message = "El precio base debe ser mayor a 0")
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Size(max = 100)
    @Column(name = "gps_origen", length = 100)
    private String gpsOrigen; // Formato: "latitud,longitud"

    @Size(max = 100)
    @Column(name = "gps_destino", length = 100)
    private String gpsDestino; // Formato: "latitud,longitud"

    @Column(name = "estado", length = 20)
    private String estado = "ACTIVO"; // ACTIVO, INACTIVO

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Constructor por defecto
    public Ruta() {
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "ACTIVO";
    }

    // Constructor con parámetros
    public Ruta(String nombre, String origen, String destino, BigDecimal precioBase) {
        this();
        this.nombre = nombre;
        this.origen = origen;
        this.destino = destino;
        this.precioBase = precioBase;
    }

    // Getters y Setters
    public Long getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Long idRuta) {
        this.idRuta = idRuta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public BigDecimal getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(BigDecimal distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public BigDecimal getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(BigDecimal duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public String getGpsOrigen() {
        return gpsOrigen;
    }

    public void setGpsOrigen(String gpsOrigen) {
        this.gpsOrigen = gpsOrigen;
    }

    public String getGpsDestino() {
        return gpsDestino;
    }

    public void setGpsDestino(String gpsDestino) {
        this.gpsDestino = gpsDestino;
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
        return "Ruta{" +
                "idRuta=" + idRuta +
                ", nombre='" + nombre + '\'' +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                ", precioBase=" + precioBase +
                ", estado='" + estado + '\'' +
                '}';
    }
}

