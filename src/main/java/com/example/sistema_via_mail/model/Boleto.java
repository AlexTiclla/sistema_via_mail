package com.example.sistema_via_mail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad Boleto
 * Gestiona los boletos de pasajeros (CU4)
 */
@Entity
@Table(name = "boleto")
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleto")
    private Long idBoleto;

    @NotBlank(message = "El código de boleto es obligatorio")
    @Size(max = 50)
    @Column(name = "codigo_boleto", unique = true, nullable = false, length = 50)
    private String codigoBoleto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    @NotBlank(message = "El nombre del pasajero es obligatorio")
    @Size(max = 200)
    @Column(name = "nombre_pasajero", nullable = false, length = 200)
    private String nombrePasajero;

    @NotBlank(message = "El CI del pasajero es obligatorio")
    @Size(max = 20)
    @Column(name = "ci_pasajero", nullable = false, length = 20)
    private String ciPasajero;

    @Min(value = 1, message = "El número de asiento debe ser mayor a 0")
    @Column(name = "asiento", nullable = false)
    private Integer asiento;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_viaje", nullable = false)
    private LocalDate fechaViaje;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @Size(max = 255)
    @Column(name = "qr_code", length = 255)
    private String qrCode;

    @Column(name = "estado", length = 20)
    private String estado = "VENDIDO"; // VENDIDO, USADO, CANCELADO

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    // Constructor por defecto
    public Boleto() {
        this.fechaEmision = LocalDateTime.now();
        this.estado = "VENDIDO";
    }

    // Constructor con parámetros
    public Boleto(String codigoBoleto, String nombrePasajero, String ciPasajero, Integer asiento, BigDecimal precio) {
        this();
        this.codigoBoleto = codigoBoleto;
        this.nombrePasajero = nombrePasajero;
        this.ciPasajero = ciPasajero;
        this.asiento = asiento;
        this.precio = precio;
    }

    // Getters y Setters
    public Long getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(Long idBoleto) {
        this.idBoleto = idBoleto;
    }

    public String getCodigoBoleto() {
        return codigoBoleto;
    }

    public void setCodigoBoleto(String codigoBoleto) {
        this.codigoBoleto = codigoBoleto;
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

    public String getNombrePasajero() {
        return nombrePasajero;
    }

    public void setNombrePasajero(String nombrePasajero) {
        this.nombrePasajero = nombrePasajero;
    }

    public String getCiPasajero() {
        return ciPasajero;
    }

    public void setCiPasajero(String ciPasajero) {
        this.ciPasajero = ciPasajero;
    }

    public Integer getAsiento() {
        return asiento;
    }

    public void setAsiento(Integer asiento) {
        this.asiento = asiento;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public LocalDate getFechaViaje() {
        return fechaViaje;
    }

    public void setFechaViaje(LocalDate fechaViaje) {
        this.fechaViaje = fechaViaje;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaEmision == null) {
            fechaEmision = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "VENDIDO";
        }
    }

    @Override
    public String toString() {
        return "Boleto{" +
                "idBoleto=" + idBoleto +
                ", codigoBoleto='" + codigoBoleto + '\'' +
                ", nombrePasajero='" + nombrePasajero + '\'' +
                ", ciPasajero='" + ciPasajero + '\'' +
                ", asiento=" + asiento +
                ", precio=" + precio +
                ", estado='" + estado + '\'' +
                '}';
    }
}

