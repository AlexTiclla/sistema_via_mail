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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de ventas (CU6)
 * Implementa operaciones de registro via comandos de correo
 */
@Service
@Transactional
public class VentaService {
    
    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);
    
    private final VentaRepository ventaRepository;
    private final BoletoRepository boletoRepository;
    private final EncomiendaRepository encomiendaRepository;
    private final UsuarioRepository usuarioRepository;
    
    public VentaService(VentaRepository ventaRepository,
                       BoletoRepository boletoRepository,
                       EncomiendaRepository encomiendaRepository,
                       UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.boletoRepository = boletoRepository;
        this.encomiendaRepository = encomiendaRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    /**
     * Procesa un comando de venta
     * @param request Comando a procesar
     * @return Respuesta del comando
     */
    public CommandResponse processCommand(CommandRequest request) {
        logger.info("Procesando comando de venta: {}", request.getFullCommand());
        
        try {
            return switch (request.getOperation()) {
                case "LIS" -> listar(request);
                case "INS" -> insertar(request);
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
     * LISVEN - Listar ventas
     * Formatos:
     * - LISVEN["*"] - Todas las ventas
     * - LISVEN["VEN-001"] - Por código
     */
    private CommandResponse listar(CommandRequest request) {
        List<String> params = request.getParameters();
        String criterio = params.get(0);
        
        List<Venta> ventas;
        
        if ("*".equals(criterio)) {
            // Listar todas
            ventas = ventaRepository.findAll();
            logger.info("Listando todas las ventas. Total: {}", ventas.size());
            
        } else {
            // Buscar por código
            Optional<Venta> venta = ventaRepository.findByCodigoVenta(criterio);
            ventas = venta.map(List::of).orElse(new ArrayList<>());
            logger.info("Buscando venta por código: {}. Encontrada: {}", criterio, venta.isPresent());
        }
        
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Consulta ejecutada correctamente");
        response.addData("Total", ventas.size());
        response.addData("Ventas", formatVentasList(ventas));
        
        return response;
    }
    
    /**
     * INSVEN - Insertar venta
     * Formato: INSVEN["codigo_venta","tipo","codigo_referencia","ci_vendedor","monto"]
     * Tipo puede ser: BOLETO o ENCOMIENDA
     */
    private CommandResponse insertar(CommandRequest request) {
        List<String> params = request.getParameters();
        
        String codigoVenta = params.get(0);
        
        // Verificar si ya existe
        if (ventaRepository.findByCodigoVenta(codigoVenta).isPresent()) {
            throw CommandException.businessError(request.getFullCommand(), 
                "Ya existe una venta con código: " + codigoVenta);
        }
        
        String tipoVenta = params.get(1).toUpperCase();
        String codigoReferencia = params.get(2);
        String ciVendedor = params.get(3);
        BigDecimal montoTotal = new BigDecimal(params.get(4));
        
        // Buscar vendedor
        Usuario vendedor = usuarioRepository.findByCi(ciVendedor)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un usuario con CI: " + ciVendedor));
        
        // Verificar que sea secretaria o propietario
        if (!"SECRETARIA".equals(vendedor.getTipoUsuario()) && 
            !"PROPIETARIO".equals(vendedor.getTipoUsuario())) {
            throw CommandException.businessError(request.getFullCommand(), 
                "El usuario con CI " + ciVendedor + " no puede realizar ventas");
        }
        
        // Verificar y obtener la referencia según el tipo
        Long idReferencia;
        String detalleReferencia;
        
        if ("BOLETO".equals(tipoVenta)) {
            Boleto boleto = boletoRepository.findByCodigoBoleto(codigoReferencia)
                .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                    "No existe un boleto con código: " + codigoReferencia));
            idReferencia = boleto.getIdBoleto();
            detalleReferencia = "Boleto: " + boleto.getCodigoBoleto() + " - " + boleto.getNombrePasajero();
            
        } else if ("ENCOMIENDA".equals(tipoVenta)) {
            Encomienda encomienda = encomiendaRepository.findByCodigoEncomienda(codigoReferencia)
                .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                    "No existe una encomienda con código: " + codigoReferencia));
            idReferencia = encomienda.getIdEncomienda();
            detalleReferencia = "Encomienda: " + encomienda.getCodigoEncomienda() + " - " + 
                encomienda.getNombreRemitente() + " → " + encomienda.getNombreDestinatario();
            
        } else {
            throw CommandException.businessError(request.getFullCommand(), 
                "Tipo de venta inválido: " + tipoVenta + ". Use BOLETO o ENCOMIENDA");
        }
        
        // Crear nueva venta
        Venta venta = new Venta();
        venta.setCodigoVenta(codigoVenta);
        venta.setTipoVenta(tipoVenta);
        venta.setIdReferencia(idReferencia.intValue());
        venta.setVendedor(vendedor);
        venta.setMontoTotal(montoTotal);
        
        // Guardar
        venta = ventaRepository.save(venta);
        logger.info("Venta creada exitosamente: ID={}, Código={}", venta.getIdVenta(), venta.getCodigoVenta());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Venta registrada correctamente");
        response.addData("ID", venta.getIdVenta());
        response.addData("Código", venta.getCodigoVenta());
        response.addData("Tipo", venta.getTipoVenta());
        response.addData("Referencia", detalleReferencia);
        response.addData("Vendedor", vendedor.getNombre() + " " + vendedor.getApellido());
        response.addData("Monto Total", "Bs. " + venta.getMontoTotal());
        response.addData("Fecha", venta.getFechaVenta());
        
        return response;
    }
    
    /**
     * Formatea una lista de ventas para mostrar en la respuesta
     */
    private String formatVentasList(List<Venta> ventas) {
        if (ventas.isEmpty()) {
            return "No se encontraron ventas";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Venta v : ventas) {
            String vendedor = v.getVendedor() != null ? 
                v.getVendedor().getNombre() + " " + v.getVendedor().getApellido() : "N/A";
            
            sb.append(String.format("\n  - %s | %s | Ref ID: %d | Vendedor: %s | Bs. %.2f | %s", 
                v.getCodigoVenta(),
                v.getTipoVenta(),
                v.getIdReferencia(),
                vendedor,
                v.getMontoTotal(),
                v.getFechaVenta()));
        }
        return sb.toString();
    }
}

