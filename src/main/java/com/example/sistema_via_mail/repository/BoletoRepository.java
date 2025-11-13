package com.example.sistema_via_mail.repository;

import com.example.sistema_via_mail.model.Boleto;
import com.example.sistema_via_mail.model.Ruta;
import com.example.sistema_via_mail.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Boleto
 * Gestiona los boletos de pasajeros (CU4)
 */
@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {

    /**
     * Buscar boleto por código
     * @param codigoBoleto Código del boleto
     * @return Optional con el boleto encontrado
     */
    Optional<Boleto> findByCodigoBoleto(String codigoBoleto);

    /**
     * Buscar boletos por fecha de viaje
     * @param fechaViaje Fecha del viaje
     * @return Lista de boletos para la fecha especificada
     */
    List<Boleto> findByFechaViaje(LocalDate fechaViaje);

    /**
     * Buscar boletos por ruta
     * @param ruta Ruta del boleto
     * @return Lista de boletos de la ruta
     */
    List<Boleto> findByRuta(Ruta ruta);

    /**
     * Buscar boletos por vehículo
     * @param vehiculo Vehículo del boleto
     * @return Lista de boletos del vehículo
     */
    List<Boleto> findByVehiculo(Vehiculo vehiculo);

    /**
     * Buscar boletos por estado
     * @param estado Estado del boleto (VENDIDO, USADO, CANCELADO)
     * @return Lista de boletos con el estado especificado
     */
    List<Boleto> findByEstado(String estado);

    /**
     * Buscar boletos por CI del pasajero
     * @param ciPasajero CI del pasajero
     * @return Lista de boletos del pasajero
     */
    List<Boleto> findByCiPasajero(String ciPasajero);

    /**
     * Buscar boletos por ruta y fecha de viaje
     * @param ruta Ruta del boleto
     * @param fechaViaje Fecha del viaje
     * @return Lista de boletos que cumplen ambos criterios
     */
    List<Boleto> findByRutaAndFechaViaje(Ruta ruta, LocalDate fechaViaje);

    /**
     * Verificar si existe un boleto con el código dado
     * @param codigoBoleto Código del boleto
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodigoBoleto(String codigoBoleto);

    /**
     * Verificar disponibilidad de asiento
     * @param vehiculo Vehículo
     * @param fechaViaje Fecha del viaje
     * @param asiento Número de asiento
     * @return true si el asiento está ocupado
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Boleto b " +
           "WHERE b.vehiculo = :vehiculo AND b.fechaViaje = :fechaViaje " +
           "AND b.asiento = :asiento AND b.estado != 'CANCELADO'")
    boolean existsByVehiculoAndFechaViajeAndAsiento(
            @Param("vehiculo") Vehiculo vehiculo,
            @Param("fechaViaje") LocalDate fechaViaje,
            @Param("asiento") Integer asiento
    );
    
    /**
     * Buscar boleto por vehículo, fecha, hora y asiento
     * @param vehiculo Vehículo
     * @param fechaViaje Fecha del viaje
     * @param horaSalida Hora de salida
     * @param asiento Número de asiento
     * @return Optional con el boleto encontrado
     */
    Optional<Boleto> findByVehiculoAndFechaViajeAndHoraSalidaAndAsiento(
            Vehiculo vehiculo,
            LocalDate fechaViaje,
            java.time.LocalTime horaSalida,
            Integer asiento
    );
}

