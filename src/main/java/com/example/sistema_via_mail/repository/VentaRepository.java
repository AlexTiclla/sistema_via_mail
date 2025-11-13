package com.example.sistema_via_mail.repository;

import com.example.sistema_via_mail.model.Usuario;
import com.example.sistema_via_mail.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Venta
 * Gestiona las ventas realizadas (CU6)
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    /**
     * Buscar venta por código
     * @param codigoVenta Código de la venta
     * @return Optional con la venta encontrada
     */
    Optional<Venta> findByCodigoVenta(String codigoVenta);

    /**
     * Buscar ventas por tipo
     * @param tipoVenta Tipo de venta (BOLETO, ENCOMIENDA)
     * @return Lista de ventas del tipo especificado
     */
    List<Venta> findByTipoVenta(String tipoVenta);

    /**
     * Buscar ventas por vendedor
     * @param vendedor Usuario vendedor
     * @return Lista de ventas realizadas por el vendedor
     */
    List<Venta> findByVendedor(Usuario vendedor);

    /**
     * Buscar ventas por tipo y referencia
     * @param tipoVenta Tipo de venta
     * @param idReferencia ID de referencia (boleto o encomienda)
     * @return Optional con la venta encontrada
     */
    Optional<Venta> findByTipoVentaAndIdReferencia(String tipoVenta, Integer idReferencia);

    /**
     * Verificar si existe una venta con el código dado
     * @param codigoVenta Código de la venta
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodigoVenta(String codigoVenta);

    /**
     * Buscar ventas por rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de ventas en el rango de fechas
     */
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtener total de ventas por tipo en un rango de fechas
     * @param tipoVenta Tipo de venta
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Suma total de ventas
     */
    @Query("SELECT SUM(v.montoTotal) FROM Venta v WHERE v.tipoVenta = :tipoVenta " +
           "AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Double sumMontoTotalByTipoVentaAndFechaVentaBetween(
            @Param("tipoVenta") String tipoVenta,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Contar ventas por vendedor en un rango de fechas
     * @param vendedor Usuario vendedor
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Número de ventas
     */
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.vendedor = :vendedor " +
           "AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Long countByVendedorAndFechaVentaBetween(
            @Param("vendedor") Usuario vendedor,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}

