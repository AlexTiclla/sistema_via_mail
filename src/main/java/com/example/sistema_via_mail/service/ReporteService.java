package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.exception.CommandException;
import com.example.sistema_via_mail.model.*;
import com.example.sistema_via_mail.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para generación de reportes y estadísticas (CU8)
 * Implementa diferentes tipos de reportes via comandos de correo
 */
@Service
@Transactional(readOnly = true)
public class ReporteService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);
    
    private final VentaRepository ventaRepository;
    private final BoletoRepository boletoRepository;
    private final EncomiendaRepository encomiendaRepository;
    private final PagoRepository pagoRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public ReporteService(VentaRepository ventaRepository,
                         BoletoRepository boletoRepository,
                         EncomiendaRepository encomiendaRepository,
                         PagoRepository pagoRepository,
                         RutaRepository rutaRepository,
                         ReporteRepository reporteRepository) {
        this.ventaRepository = ventaRepository;
        this.boletoRepository = boletoRepository;
        this.encomiendaRepository = encomiendaRepository;
        this.pagoRepository = pagoRepository;
        // rutaRepository y reporteRepository se mantienen en constructor para evitar romper inyección de dependencias
        // pero no se utilizan en esta versión inicial
    }
    
    /**
     * Procesa un comando de reporte
     * @param request Comando a procesar
     * @return Respuesta del comando
     */
    public CommandResponse processCommand(CommandRequest request) {
        logger.info("Procesando comando de reporte: {}", request.getFullCommand());
        
        try {
            if (!"REP".equals(request.getOperation())) {
                return CommandResponse.error(request.getFullCommand(), 
                    "Operación no soportada. Use REPREP para reportes");
            }
            
            return generarReporte(request);
            
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
     * REPREP - Generar reporte
     * Formato: REPREP["tipo_reporte","parametro"]
     * Tipos: VENTAS_DIA, BOLETOS_VENDIDOS, ENCOMIENDAS_TRANSITO, PAGOS_PENDIENTES, RUTAS_POPULARES
     */
    private CommandResponse generarReporte(CommandRequest request) {
        List<String> params = request.getParameters();
        String tipoReporte = params.get(0).toUpperCase();
        String parametro = params.get(1);
        
        logger.info("Generando reporte tipo: {} con parámetro: {}", tipoReporte, parametro);
        
        return switch (tipoReporte) {
            case "VENTAS_DIA" -> reporteVentasPorDia(request, parametro);
            case "BOLETOS_VENDIDOS" -> reporteBoletosVendidos(request, parametro);
            case "ENCOMIENDAS_TRANSITO" -> reporteEncomiendasEnTransito(request);
            case "PAGOS_PENDIENTES" -> reportePagosPendientes(request);
            case "RUTAS_POPULARES" -> reporteRutasPopulares(request, parametro);
            default -> CommandResponse.error(request.getFullCommand(), 
                "Tipo de reporte no soportado: " + tipoReporte);
        };
    }
    
    /**
     * Reporte de ventas por día
     * Parámetro: fecha en formato yyyy-MM-dd
     */
    private CommandResponse reporteVentasPorDia(CommandRequest request, String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr, DATE_FORMATTER);
        
        // Obtener ventas del día
        List<Venta> ventas = ventaRepository.findAll().stream()
            .filter(v -> v.getFechaVenta().toLocalDate().equals(fecha))
            .toList();
        
        // Calcular estadísticas
        double totalBoletos = ventas.stream()
            .filter(v -> "BOLETO".equals(v.getTipoVenta()))
            .mapToDouble(v -> v.getMontoTotal().doubleValue())
            .sum();
        
        double totalEncomiendas = ventas.stream()
            .filter(v -> "ENCOMIENDA".equals(v.getTipoVenta()))
            .mapToDouble(v -> v.getMontoTotal().doubleValue())
            .sum();
        
        double totalGeneral = totalBoletos + totalEncomiendas;
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Reporte de ventas generado correctamente");
        response.addData("Fecha", fechaStr);
        response.addData("Total Ventas", ventas.size());
        response.addData("Ventas Boletos", "Bs. " + String.format("%.2f", totalBoletos));
        response.addData("Ventas Encomiendas", "Bs. " + String.format("%.2f", totalEncomiendas));
        response.addData("Total General", "Bs. " + String.format("%.2f", totalGeneral));
        
        StringBuilder detalle = new StringBuilder();
        for (Venta v : ventas) {
            detalle.append(String.format("\n  - %s | %s | Bs. %.2f", 
                v.getCodigoVenta(), v.getTipoVenta(), v.getMontoTotal()));
        }
        response.addData("Detalle", detalle.toString());
        
        return response;
    }
    
    /**
     * Reporte de boletos vendidos
     * Parámetro: fecha en formato yyyy-MM-dd o "*" para todos
     */
    private CommandResponse reporteBoletosVendidos(CommandRequest request, String fechaStr) {
        List<Boleto> boletos;
        
        if ("*".equals(fechaStr)) {
            boletos = boletoRepository.findAll();
        } else {
            LocalDate fecha = LocalDate.parse(fechaStr, DATE_FORMATTER);
            boletos = boletoRepository.findByFechaViaje(fecha);
        }
        
        // Filtrar solo vendidos
        List<Boleto> boletosVendidos = boletos.stream()
            .filter(b -> "VENDIDO".equals(b.getEstado()) || "USADO".equals(b.getEstado()))
            .toList();
        
        double totalIngresos = boletosVendidos.stream()
            .mapToDouble(b -> b.getPrecio().doubleValue())
            .sum();
        
        // Agrupar por ruta
        Map<String, Long> boletospPorRuta = new HashMap<>();
        for (Boleto b : boletosVendidos) {
            String rutaNombre = b.getRuta() != null ? b.getRuta().getNombre() : "Sin ruta";
            boletospPorRuta.put(rutaNombre, boletospPorRuta.getOrDefault(rutaNombre, 0L) + 1);
        }
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Reporte de boletos vendidos generado correctamente");
        response.addData("Fecha", fechaStr);
        response.addData("Total Boletos", boletosVendidos.size());
        response.addData("Total Ingresos", "Bs. " + String.format("%.2f", totalIngresos));
        
        StringBuilder detalle = new StringBuilder();
        detalle.append("\n\nBoletos por Ruta:");
        for (Map.Entry<String, Long> entry : boletospPorRuta.entrySet()) {
            detalle.append(String.format("\n  - %s: %d boletos", entry.getKey(), entry.getValue()));
        }
        response.addData("Detalle", detalle.toString());
        
        return response;
    }
    
    /**
     * Reporte de encomiendas en tránsito
     */
    private CommandResponse reporteEncomiendasEnTransito(CommandRequest request) {
        List<Encomienda> encomiendas = encomiendaRepository.findByEstado("EN_TRANSITO");
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Reporte de encomiendas en tránsito generado correctamente");
        response.addData("Total Encomiendas", encomiendas.size());
        
        StringBuilder detalle = new StringBuilder();
        for (Encomienda e : encomiendas) {
            String ruta = e.getRuta() != null ? e.getRuta().getNombre() : "N/A";
            String vehiculo = e.getVehiculo() != null ? e.getVehiculo().getPlaca() : "N/A";
            String gps = e.getGpsActual() != null ? e.getGpsActual() : "Sin GPS";
            
            detalle.append(String.format("\n  - %s | %s → %s | %s | %s | GPS: %s", 
                e.getCodigoEncomienda(),
                e.getNombreRemitente(),
                e.getNombreDestinatario(),
                ruta,
                vehiculo,
                gps));
        }
        response.addData("Encomiendas", detalle.toString());
        
        return response;
    }
    
    /**
     * Reporte de pagos pendientes
     */
    private CommandResponse reportePagosPendientes(CommandRequest request) {
        List<Pago> pagosPendientes = pagoRepository.findByEstado("PENDIENTE");
        
        double totalPendiente = pagosPendientes.stream()
            .mapToDouble(p -> p.getMontoCuota().doubleValue())
            .sum();
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Reporte de pagos pendientes generado correctamente");
        response.addData("Total Pagos Pendientes", pagosPendientes.size());
        response.addData("Monto Total Pendiente", "Bs. " + String.format("%.2f", totalPendiente));
        
        StringBuilder detalle = new StringBuilder();
        for (Pago p : pagosPendientes) {
            String codigoVenta = p.getVenta() != null ? p.getVenta().getCodigoVenta() : "N/A";
            detalle.append(String.format("\n  - Venta: %s | Cuota: %d | Bs. %.2f", 
                codigoVenta,
                p.getNumeroCuota(),
                p.getMontoCuota()));
        }
        response.addData("Detalle", detalle.toString());
        
        return response;
    }
    
    /**
     * Reporte de rutas más populares
     * Parámetro: mes en formato yyyy-MM o "*" para todos
     */
    private CommandResponse reporteRutasPopulares(CommandRequest request, String parametro) {
        List<Boleto> boletos = boletoRepository.findAll();
        
        // Filtrar por mes si se especifica
        if (!"*".equals(parametro)) {
            YearMonth yearMonth = YearMonth.parse(parametro, DateTimeFormatter.ofPattern("yyyy-MM"));
            boletos = boletos.stream()
                .filter(b -> {
                    LocalDate fecha = b.getFechaViaje();
                    return fecha.getYear() == yearMonth.getYear() && 
                           fecha.getMonthValue() == yearMonth.getMonthValue();
                })
                .toList();
        }
        
        // Contar boletos por ruta
        Map<String, Integer> conteoRutas = new HashMap<>();
        Map<String, Double> ingresosRutas = new HashMap<>();
        
        for (Boleto b : boletos) {
            if (b.getRuta() != null) {
                String rutaNombre = b.getRuta().getNombre();
                conteoRutas.put(rutaNombre, conteoRutas.getOrDefault(rutaNombre, 0) + 1);
                ingresosRutas.put(rutaNombre, 
                    ingresosRutas.getOrDefault(rutaNombre, 0.0) + b.getPrecio().doubleValue());
            }
        }
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Reporte de rutas populares generado correctamente");
        response.addData("Período", parametro);
        response.addData("Total Boletos Analizados", boletos.size());
        
        StringBuilder detalle = new StringBuilder();
        detalle.append("\n\nRutas Ordenadas por Popularidad:");
        
        conteoRutas.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> {
                String ruta = entry.getKey();
                int cantidad = entry.getValue();
                double ingresos = ingresosRutas.getOrDefault(ruta, 0.0);
                detalle.append(String.format("\n  - %s: %d boletos | Bs. %.2f", 
                    ruta, cantidad, ingresos));
            });
        
        response.addData("Detalle", detalle.toString());
        
        return response;
    }
}

