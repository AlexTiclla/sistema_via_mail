package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.exception.CommandException;
import com.example.sistema_via_mail.model.Pago;
import com.example.sistema_via_mail.model.Venta;
import com.example.sistema_via_mail.repository.PagoRepository;
import com.example.sistema_via_mail.repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de pagos (CU7)
 * Implementa sistema de cuotas via comandos de correo
 */
@Service
@Transactional
public class PagoService {
    
    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);
    
    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    
    public PagoService(PagoRepository pagoRepository, VentaRepository ventaRepository) {
        this.pagoRepository = pagoRepository;
        this.ventaRepository = ventaRepository;
    }
    
    /**
     * Procesa un comando de pago
     * @param request Comando a procesar
     * @return Respuesta del comando
     */
    public CommandResponse processCommand(CommandRequest request) {
        logger.info("Procesando comando de pago: {}", request.getFullCommand());
        
        try {
            return switch (request.getOperation()) {
                case "LIS" -> listar(request);
                case "INS" -> insertar(request);
                case "UPD" -> actualizar(request);
                default -> CommandResponse.error(request.getFullCommand(), 
                    "Operación no soportada: " + request.getOperation());
            };
        } catch (CommandException e) {
            logger.error("Error al procesar comando: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al procesar comando: {}", e.getMessage(), e);
            throw CommandException.systemError(request.getFullCommand(), 
                "Error inesperado: " + e.getMessage(), e);
        }
    }
    
    /**
     * LISPAG - Listar pagos de una venta
     * Formato: LISPAG["codigo_venta"]
     */
    private CommandResponse listar(CommandRequest request) {
        List<String> params = request.getParameters();
        String codigoVenta = params.get(0);
        
        // Buscar venta
        Venta venta = ventaRepository.findByCodigoVenta(codigoVenta)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe una venta con código: " + codigoVenta));
        
        // Obtener pagos
        List<Pago> pagos = pagoRepository.findByVenta(venta);
        logger.info("Listando pagos de venta {}. Total: {}", codigoVenta, pagos.size());
        
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Consulta ejecutada correctamente");
        response.addData("Código Venta", codigoVenta);
        response.addData("Monto Total", "Bs. " + venta.getMontoTotal());
        response.addData("Total Pagos", pagos.size());
        response.addData("Pagos", formatPagosList(pagos));
        
        return response;
    }
    
    /**
     * INSPAG - Registrar pago (cuota)
     * Formato: INSPAG["codigo_venta","numero_cuota","monto","metodo_pago"]
     */
    private CommandResponse insertar(CommandRequest request) {
        List<String> params = request.getParameters();
        
        String codigoVenta = params.get(0);
        int numeroCuota = Integer.parseInt(params.get(1));
        BigDecimal montoCuota = new BigDecimal(params.get(2));
        String metodoPago = params.get(3).toUpperCase();
        
        // Buscar venta
        Venta venta = ventaRepository.findByCodigoVenta(codigoVenta)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe una venta con código: " + codigoVenta));
        
        // Verificar que no exista ya un pago para esa cuota
        if (pagoRepository.findByVentaAndNumeroCuota(venta, numeroCuota).isPresent()) {
            throw CommandException.businessError(request.getFullCommand(), 
                "Ya existe un pago registrado para la cuota " + numeroCuota + " de la venta " + codigoVenta);
        }
        
        // Validar número de cuota (1 o 2)
        if (numeroCuota < 1 || numeroCuota > 2) {
            throw CommandException.businessError(request.getFullCommand(), 
                "Número de cuota inválido: " + numeroCuota + ". Debe ser 1 o 2");
        }
        
        // Validar que el monto de la cuota no exceda el monto total
        BigDecimal totalPagado = pagoRepository.findByVenta(venta).stream()
            .map(Pago::getMontoCuota)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPagado.add(montoCuota).compareTo(venta.getMontoTotal()) > 0) {
            throw CommandException.businessError(request.getFullCommand(), 
                "El monto de la cuota excede el monto total de la venta. " +
                "Total venta: Bs. " + venta.getMontoTotal() + ", Ya pagado: Bs. " + totalPagado);
        }
        
        // Crear nuevo pago
        Pago pago = new Pago();
        pago.setVenta(venta);
        pago.setNumeroCuota(numeroCuota);
        pago.setMontoCuota(montoCuota);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago(metodoPago);
        pago.setEstado("PAGADO");
        
        // Guardar
        pago = pagoRepository.save(pago);
        logger.info("Pago registrado exitosamente: ID={}, Venta={}, Cuota={}", 
            pago.getIdPago(), codigoVenta, numeroCuota);
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Pago registrado correctamente");
        response.addData("ID", pago.getIdPago());
        response.addData("Venta", codigoVenta);
        response.addData("Cuota", numeroCuota + " de 2");
        response.addData("Monto", "Bs. " + pago.getMontoCuota());
        response.addData("Método", pago.getMetodoPago());
        response.addData("Estado", pago.getEstado());
        response.addData("Fecha", pago.getFechaPago());
        
        // Información adicional del saldo
        BigDecimal nuevoTotalPagado = totalPagado.add(montoCuota);
        BigDecimal saldo = venta.getMontoTotal().subtract(nuevoTotalPagado);
        response.addData("Total Pagado", "Bs. " + nuevoTotalPagado);
        response.addData("Saldo Pendiente", "Bs. " + saldo);
        
        return response;
    }
    
    /**
     * UPDPAG - Actualizar estado de pago
     * Formato: UPDPAG["codigo_venta","numero_cuota","estado"]
     */
    private CommandResponse actualizar(CommandRequest request) {
        List<String> params = request.getParameters();
        
        String codigoVenta = params.get(0);
        int numeroCuota = Integer.parseInt(params.get(1));
        String nuevoEstado = params.get(2).toUpperCase();
        
        // Buscar venta
        Venta venta = ventaRepository.findByCodigoVenta(codigoVenta)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe una venta con código: " + codigoVenta));
        
        // Buscar pago
        Pago pago = pagoRepository.findByVentaAndNumeroCuota(venta, numeroCuota)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un pago registrado para la cuota " + numeroCuota + " de la venta " + codigoVenta));
        
        // Actualizar estado
        pago.setEstado(nuevoEstado);
        pago = pagoRepository.save(pago);
        
        logger.info("Pago actualizado exitosamente: ID={}, Venta={}, Cuota={}, Nuevo estado={}", 
            pago.getIdPago(), codigoVenta, numeroCuota, nuevoEstado);
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Estado de pago actualizado correctamente");
        response.addData("Venta", codigoVenta);
        response.addData("Cuota", numeroCuota);
        response.addData("Estado", pago.getEstado());
        response.addData("Monto", "Bs. " + pago.getMontoCuota());
        
        return response;
    }
    
    /**
     * Formatea una lista de pagos para mostrar en la respuesta
     */
    private String formatPagosList(List<Pago> pagos) {
        if (pagos.isEmpty()) {
            return "No se encontraron pagos registrados";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Pago p : pagos) {
            sb.append(String.format("\n  - Cuota %d | Bs. %.2f | %s | %s | %s", 
                p.getNumeroCuota(),
                p.getMontoCuota(),
                p.getMetodoPago(),
                p.getEstado(),
                p.getFechaPago() != null ? p.getFechaPago().toString() : "N/A"));
        }
        return sb.toString();
    }
}

