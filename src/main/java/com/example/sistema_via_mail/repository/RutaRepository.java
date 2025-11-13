package com.example.sistema_via_mail.repository;

import com.example.sistema_via_mail.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Ruta
 * Gestiona las rutas disponibles (CU3)
 */
@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    /**
     * Buscar rutas por origen
     * @param origen Ciudad de origen
     * @return Lista de rutas desde el origen especificado
     */
    List<Ruta> findByOrigen(String origen);

    /**
     * Buscar rutas por destino
     * @param destino Ciudad de destino
     * @return Lista de rutas hacia el destino especificado
     */
    List<Ruta> findByDestino(String destino);

    /**
     * Buscar rutas por origen y destino
     * @param origen Ciudad de origen
     * @param destino Ciudad de destino
     * @return Lista de rutas que coinciden con origen y destino
     */
    List<Ruta> findByOrigenAndDestino(String origen, String destino);

    /**
     * Buscar rutas por estado
     * @param estado Estado de la ruta (ACTIVO, INACTIVO)
     * @return Lista de rutas con el estado especificado
     */
    List<Ruta> findByEstado(String estado);

    /**
     * Buscar rutas activas por origen y destino
     * @param origen Ciudad de origen
     * @param destino Ciudad de destino
     * @param estado Estado de la ruta
     * @return Lista de rutas activas
     */
    List<Ruta> findByOrigenAndDestinoAndEstado(String origen, String destino, String estado);

    /**
     * Buscar ruta por nombre
     * @param nombre Nombre de la ruta
     * @return Optional con la ruta encontrada
     */
    Optional<Ruta> findByNombre(String nombre);
}

