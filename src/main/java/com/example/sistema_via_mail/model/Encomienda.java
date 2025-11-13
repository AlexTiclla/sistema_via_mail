package com.example.sistema_via_mail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Encomienda
 * Gestiona el envío de encomiendas (CU5)
 */
@Entity
@Table(name = "encomienda")
public class Encomienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encomienda")
    private Long idEncomienda;

    @NotBlank(message = "El código de encomienda es obligatorio")
    @Size(max = 50)
    @Column(name = "codigo_encomienda", unique = true, nullable = false, length = 50)
    private String codigoEncomienda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    @NotBlank(message = "El nombre del remitente es obligatorio")
    @Size(max = 200)
    @Column(name = "nombre_remitente", nullable = false, length = 200)
    private String nombreRemitente;

    @Size(max = 20)
    @Column(name = "telefono_remitente", length = 20)
    private String telefonoRemitente;

    @NotBlank(message = "El nombre del destinatario es obligatorio")
    @Size(max = 200)
    @Column(name = "nombre_destinatario", nullable = false, length = 200)
    private String nombreDestinatario;

    @Size(max = 20)
    @Column(name = "telefono_destinatario", length = 20)
    private String telefonoDestinatario;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "peso_kg", precision = 10, scale = 2)
    private BigDecimal pesoKg;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDate fechaEnvio;

    @Size(max = 255)
    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Size(max = 255)
    @Column(name = "qr_code", length = 255)
    private String qrCode;

    @Size(max = 100)
    @Column(name = "gps_actual", length = 100)
    private String gpsActual; // Formato: "latitud,longitud"

    @Column(name = "estado", length = 20)
    private String estado = "REGISTRADA"; // REGISTRADA, EN_TRANSITO, ENTREGADA, CANCELADA

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Constructor por defecto
    public Encomienda() {
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "REGISTRADA";
    }

    // Constructor con parámetros
    public Encomienda(String codigoEncomienda, String nombreRemitente, String nombreDestinatario, BigDecimal precio) {
        this();
        this.codigoEncomienda = codigoEncomienda;
        this.nombreRemitente = nombreRemitente;
        this.nombreDestinatario = nombreDestinatario;
        this.precio = precio;
    }

    // Getters y Setters
    public Long getIdEncomienda() {
        return idEncomienda;
    }

    public void setIdEncomienda(Long idEncomienda) {
        this.idEncomienda = idEncomienda;
    }

    public String getCodigoEncomienda() {
        return codigoEncomienda;
    }

    public void setCodigoEncomienda(String codigoEncomienda) {
        this.codigoEncomienda = codigoEncomienda;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getNombreRemitente() {
        return nombreRemitente;
    }

    public void setNombreRemitente(String nombreRemitente) {
        this.nombreRemitente = nombreRemitente;
    }

    public String getTelefonoRemitente() {
        return telefonoRemitente;
    }

    public void setTelefonoRemitente(String telefonoRemitente) {
        this.telefonoRemitente = telefonoRemitente;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getTelefonoDestinatario() {
        return telefonoDestinatario;
    }

    public void setTelefonoDestinatario(String telefonoDestinatario) {
        this.telefonoDestinatario = telefonoDestinatario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public LocalDate getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDate fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getGpsActual() {
        return gpsActual;
    }

    public void setGpsActual(String gpsActual) {
        this.gpsActual = gpsActual;
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
            estado = "REGISTRADA";
        }
    }

    @Override
    public String toString() {
        return "Encomienda{" +
                "idEncomienda=" + idEncomienda +
                ", codigoEncomienda='" + codigoEncomienda + '\'' +
                ", nombreRemitente='" + nombreRemitente + '\'' +
                ", nombreDestinatario='" + nombreDestinatario + '\'' +
                ", precio=" + precio +
                ", estado='" + estado + '\'' +
                '}';
    }
}

