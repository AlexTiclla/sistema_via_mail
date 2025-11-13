package com.example.sistema_via_mail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Venta
 * Gestiona las ventas realizadas (CU6)
 */
@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @NotBlank(message = "El código de venta es obligatorio")
    @Size(max = 50)
    @Column(name = "codigo_venta", unique = true, nullable = false, length = 50)
    private String codigoVenta;

    @NotBlank(message = "El tipo de venta es obligatorio")
    @Column(name = "tipo_venta", nullable = false, length = 20)
    private String tipoVenta; // BOLETO, ENCOMIENDA

    @Min(value = 1, message = "El ID de referencia debe ser mayor a 0")
    @Column(name = "id_referencia", nullable = false)
    private Integer idReferencia; // id_boleto o id_encomienda

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_vendedor")
    private Usuario vendedor;

    @DecimalMin(value = "0.01", message = "El monto total debe ser mayor a 0")
    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    // Constructor por defecto
    public Venta() {
        this.fechaVenta = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Venta(String codigoVenta, String tipoVenta, Integer idReferencia, BigDecimal montoTotal) {
        this();
        this.codigoVenta = codigoVenta;
        this.tipoVenta = tipoVenta;
        this.idReferencia = idReferencia;
        this.montoTotal = montoTotal;
    }

    // Getters y Setters
    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public String getCodigoVenta() {
        return codigoVenta;
    }

    public void setCodigoVenta(String codigoVenta) {
        this.codigoVenta = codigoVenta;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public Integer getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(Integer idReferencia) {
        this.idReferencia = idReferencia;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaVenta == null) {
            fechaVenta = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", codigoVenta='" + codigoVenta + '\'' +
                ", tipoVenta='" + tipoVenta + '\'' +
                ", idReferencia=" + idReferencia +
                ", montoTotal=" + montoTotal +
                ", fechaVenta=" + fechaVenta +
                '}';
    }
}

