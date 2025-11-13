package com.example.sistema_via_mail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Pago
 * Gestiona los pagos en cuotas (CU7)
 */
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @Min(value = 1, message = "El número de cuota debe ser 1 o 2")
    @Max(value = 2, message = "El número de cuota debe ser 1 o 2")
    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota; // 1 o 2

    @DecimalMin(value = "0.01", message = "El monto de la cuota debe ser mayor a 0")
    @Column(name = "monto_cuota", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoCuota;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago; // EFECTIVO, TRANSFERENCIA, TARJETA

    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, PAGADO

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Constructor por defecto
    public Pago() {
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    // Constructor con parámetros
    public Pago(Venta venta, Integer numeroCuota, BigDecimal montoCuota) {
        this();
        this.venta = venta;
        this.numeroCuota = numeroCuota;
        this.montoCuota = montoCuota;
    }

    // Getters y Setters
    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
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
            estado = "PENDIENTE";
        }
    }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", numeroCuota=" + numeroCuota +
                ", montoCuota=" + montoCuota +
                ", metodoPago='" + metodoPago + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}

