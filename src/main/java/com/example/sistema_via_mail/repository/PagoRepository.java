package com.example.sistema_via_mail.repository;

import com.example.sistema_via_mail.model.Pago;
import com.example.sistema_via_mail.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pago
 * Gestiona los pagos en cuotas (CU7)
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /**
     * Buscar pagos por venta
     * @param venta Venta asociada
     * @return Lista de pagos de la venta
     */
    List<Pago> findByVenta(Venta venta);

    /**
     * Buscar pagos por estado
     * @param estado Estado del pago (PENDIENTE, PAGADO)
     * @return Lista de pagos con el estado especificado
     */
    List<Pago> findByEstado(String estado);

    /**
     * Buscar pago específico por venta y número de cuota
     * @param venta Venta asociada
     * @param numeroCuota Número de cuota (1 o 2)
     * @return Optional con el pago encontrado
     */
    Optional<Pago> findByVentaAndNumeroCuota(Venta venta, Integer numeroCuota);

    /**
     * Buscar pagos pendientes por venta
     * @param venta Venta asociada
     * @param estado Estado del pago
     * @return Lista de pagos pendientes
     */
    List<Pago> findByVentaAndEstado(Venta venta, String estado);

    /**
     * Buscar pagos por método de pago
     * @param metodoPago Método de pago (EFECTIVO, TRANSFERENCIA, TARJETA)
     * @return Lista de pagos con el método especificado
     */
    List<Pago> findByMetodoPago(String metodoPago);

    /**
     * Contar pagos pendientes
     * @return Número de pagos pendientes
     */
    Long countByEstado(String estado);

    /**
     * Verificar si todos los pagos de una venta están pagados
     * @param venta Venta asociada
     * @return true si todos los pagos están completos
     */
    @Query("SELECT CASE WHEN COUNT(p) = 0 THEN true ELSE false END FROM Pago p " +
           "WHERE p.venta = :venta AND p.estado = 'PENDIENTE'")
    boolean areAllPagosPagados(@Param("venta") Venta venta);

    /**
     * Obtener monto total pagado de una venta
     * @param venta Venta asociada
     * @return Suma de montos pagados
     */
    @Query("SELECT SUM(p.montoCuota) FROM Pago p " +
           "WHERE p.venta = :venta AND p.estado = 'PAGADO'")
    Double sumMontoPagadoByVenta(@Param("venta") Venta venta);

    /**
     * Obtener monto total pendiente de una venta
     * @param venta Venta asociada
     * @return Suma de montos pendientes
     */
    @Query("SELECT SUM(p.montoCuota) FROM Pago p " +
           "WHERE p.venta = :venta AND p.estado = 'PENDIENTE'")
    Double sumMontoPendienteByVenta(@Param("venta") Venta venta);
}

