package com.example.sistema_via_mail.repository;

import com.example.sistema_via_mail.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Reporte
 * Almacena reportes y estadísticas generados (CU8)
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    /**
     * Buscar reportes por tipo
     * @param tipoReporte Tipo de reporte
     * @return Lista de reportes del tipo especificado
     */
    List<Reporte> findByTipoReporte(String tipoReporte);

    /**
     * Buscar reportes por rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de reportes generados en el rango de fechas
     */
    List<Reporte> findByFechaGeneracionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Buscar reportes por tipo y rango de fechas
     * @param tipoReporte Tipo de reporte
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de reportes que cumplen ambos criterios
     */
    List<Reporte> findByTipoReporteAndFechaGeneracionBetween(
            String tipoReporte,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /**
     * Obtener los últimos N reportes generados
     * @param tipoReporte Tipo de reporte
     * @return Lista de reportes ordenados por fecha descendente
     */
    List<Reporte> findTop10ByTipoReporteOrderByFechaGeneracionDesc(String tipoReporte);

    /**
     * Contar reportes por tipo
     * @param tipoReporte Tipo de reporte
     * @return Número de reportes del tipo
     */
    Long countByTipoReporte(String tipoReporte);
}

