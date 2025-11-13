package com.example.sistema_via_mail.repository;

import com.example.sistema_via_mail.model.Encomienda;
import com.example.sistema_via_mail.model.Ruta;
import com.example.sistema_via_mail.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Encomienda
 * Gestiona el envío de encomiendas (CU5)
 */
@Repository
public interface EncomiendaRepository extends JpaRepository<Encomienda, Long> {

    /**
     * Buscar encomienda por código
     * @param codigoEncomienda Código de la encomienda
     * @return Optional con la encomienda encontrada
     */
    Optional<Encomienda> findByCodigoEncomienda(String codigoEncomienda);

    /**
     * Buscar encomiendas por estado
     * @param estado Estado de la encomienda (REGISTRADA, EN_TRANSITO, ENTREGADA, CANCELADA)
     * @return Lista de encomiendas con el estado especificado
     */
    List<Encomienda> findByEstado(String estado);

    /**
     * Buscar encomiendas por ruta
     * @param ruta Ruta de la encomienda
     * @return Lista de encomiendas de la ruta
     */
    List<Encomienda> findByRuta(Ruta ruta);

    /**
     * Buscar encomiendas por vehículo
     * @param vehiculo Vehículo de la encomienda
     * @return Lista de encomiendas del vehículo
     */
    List<Encomienda> findByVehiculo(Vehiculo vehiculo);

    /**
     * Buscar encomiendas por fecha de envío
     * @param fechaEnvio Fecha de envío
     * @return Lista de encomiendas de la fecha especificada
     */
    List<Encomienda> findByFechaEnvio(LocalDate fechaEnvio);

    /**
     * Buscar encomiendas por nombre del remitente
     * @param nombreRemitente Nombre del remitente
     * @return Lista de encomiendas del remitente
     */
    List<Encomienda> findByNombreRemitenteContainingIgnoreCase(String nombreRemitente);

    /**
     * Buscar encomiendas por nombre del destinatario
     * @param nombreDestinatario Nombre del destinatario
     * @return Lista de encomiendas del destinatario
     */
    List<Encomienda> findByNombreDestinatarioContainingIgnoreCase(String nombreDestinatario);

    /**
     * Verificar si existe una encomienda con el código dado
     * @param codigoEncomienda Código de la encomienda
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodigoEncomienda(String codigoEncomienda);

    /**
     * Buscar encomiendas por ruta y estado
     * @param ruta Ruta de la encomienda
     * @param estado Estado de la encomienda
     * @return Lista de encomiendas que cumplen ambos criterios
     */
    List<Encomienda> findByRutaAndEstado(Ruta ruta, String estado);
}

