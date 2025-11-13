package com.example.sistema_via_mail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entidad Vehiculo
 * Gestiona los vehículos de la empresa (CU2)
 */
@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 20)
    @Column(name = "placa", unique = true, nullable = false, length = 20)
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50)
    @Column(name = "marca", nullable = false, length = 50)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50)
    @Column(name = "modelo", nullable = false, length = 50)
    private String modelo;

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Column(name = "capacidad_pasajeros", nullable = false)
    private Integer capacidadPasajeros;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario")
    private Usuario propietario;

    @Size(max = 255)
    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "estado", length = 20)
    private String estado = "DISPONIBLE"; // DISPONIBLE, EN_RUTA, MANTENIMIENTO

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Constructor por defecto
    public Vehiculo() {
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "DISPONIBLE";
    }

    // Constructor con parámetros
    public Vehiculo(String placa, String marca, String modelo, Integer capacidadPasajeros) {
        this();
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.capacidadPasajeros = capacidadPasajeros;
    }

    // Getters y Setters
    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public void setCapacidadPasajeros(Integer capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
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
            estado = "DISPONIBLE";
        }
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "idVehiculo=" + idVehiculo +
                ", placa='" + placa + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", capacidadPasajeros=" + capacidadPasajeros +
                ", estado='" + estado + '\'' +
                '}';
    }
}

