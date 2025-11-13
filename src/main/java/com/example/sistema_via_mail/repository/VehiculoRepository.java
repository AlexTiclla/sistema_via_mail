package com.example.sistema_via_mail.repository;

import com.example.sistema_via_mail.model.Usuario;
import com.example.sistema_via_mail.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Vehiculo
 * Gestiona los vehículos de la empresa (CU2)
 */
@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    /**
     * Buscar vehículo por placa
     * @param placa Placa del vehículo
     * @return Optional con el vehículo encontrado
     */
    Optional<Vehiculo> findByPlaca(String placa);

    /**
     * Buscar vehículos por estado
     * @param estado Estado del vehículo (DISPONIBLE, EN_RUTA, MANTENIMIENTO)
     * @return Lista de vehículos con el estado especificado
     */
    List<Vehiculo> findByEstado(String estado);

    /**
     * Buscar vehículos por propietario
     * @param propietario Usuario propietario
     * @return Lista de vehículos del propietario
     */
    List<Vehiculo> findByPropietario(Usuario propietario);

    /**
     * Buscar vehículos por marca
     * @param marca Marca del vehículo
     * @return Lista de vehículos de la marca especificada
     */
    List<Vehiculo> findByMarca(String marca);

    /**
     * Verificar si existe un vehículo con la placa dada
     * @param placa Placa del vehículo
     * @return true si existe, false en caso contrario
     */
    boolean existsByPlaca(String placa);

    /**
     * Buscar vehículos disponibles con capacidad mínima
     * @param capacidadMinima Capacidad mínima de pasajeros
     * @param estado Estado del vehículo
     * @return Lista de vehículos disponibles
     */
    List<Vehiculo> findByCapacidadPasajerosGreaterThanEqualAndEstado(Integer capacidadMinima, String estado);
}

