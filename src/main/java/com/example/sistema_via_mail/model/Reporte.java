package com.example.sistema_via_mail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entidad Reporte
 * Almacena reportes y estadísticas generados (CU8)
 */
@Entity
@Table(name = "reporte")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long idReporte;

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Size(max = 50)
    @Column(name = "tipo_reporte", nullable = false, length = 50)
    private String tipoReporte;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "parametros", columnDefinition = "TEXT")
    private String parametros; // JSON con parámetros del reporte

    @Column(name = "resultado", columnDefinition = "TEXT")
    private String resultado; // JSON con resultado del reporte

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    // Constructor por defecto
    public Reporte() {
        this.fechaGeneracion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Reporte(String tipoReporte, String descripcion) {
        this();
        this.tipoReporte = tipoReporte;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Long idReporte) {
        this.idReporte = idReporte;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getParametros() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaGeneracion == null) {
            fechaGeneracion = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Reporte{" +
                "idReporte=" + idReporte +
                ", tipoReporte='" + tipoReporte + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaGeneracion=" + fechaGeneracion +
                '}';
    }
}

