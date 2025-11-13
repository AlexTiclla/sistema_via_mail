package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.exception.CommandException;
import com.example.sistema_via_mail.model.Encomienda;
import com.example.sistema_via_mail.model.Ruta;
import com.example.sistema_via_mail.model.Vehiculo;
import com.example.sistema_via_mail.repository.EncomiendaRepository;
import com.example.sistema_via_mail.repository.RutaRepository;
import com.example.sistema_via_mail.repository.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de encomiendas (CU5)
 * Implementa operaciones CRUD via comandos de correo
 */
@Service
@Transactional
public class EncomiendaService {
    
    private static final Logger logger = LoggerFactory.getLogger(EncomiendaService.class);
    
    private final EncomiendaRepository encomiendaRepository;
    private final RutaRepository rutaRepository;
    private final VehiculoRepository vehiculoRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public EncomiendaService(EncomiendaRepository encomiendaRepository,
                            RutaRepository rutaRepository,
                            VehiculoRepository vehiculoRepository) {
        this.encomiendaRepository = encomiendaRepository;
        this.rutaRepository = rutaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }
    
    /**
     * Procesa un comando de encomienda
     * @param request Comando a procesar
     * @return Respuesta del comando
     */
    public CommandResponse processCommand(CommandRequest request) {
        logger.info("Procesando comando de encomienda: {}", request.getFullCommand());
        
        try {
            return switch (request.getOperation()) {
                case "LIS" -> listar(request);
                case "INS" -> insertar(request);
                case "UPD" -> actualizar(request);
                case "DEL" -> eliminar(request);
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
     * LISENC - Listar encomiendas
     * Formatos:
     * - LISENC["*"] - Todas las encomiendas
     * - LISENC["ENC-001"] - Por código
     * - LISENC["EN_TRANSITO"] - Por estado
     */
    private CommandResponse listar(CommandRequest request) {
        List<String> params = request.getParameters();
        String criterio = params.get(0);
        
        List<Encomienda> encomiendas;
        
        if ("*".equals(criterio)) {
            // Listar todas
            encomiendas = encomiendaRepository.findAll();
            logger.info("Listando todas las encomiendas. Total: {}", encomiendas.size());
            
        } else if (criterio.matches("^[A-Z_]+$")) {
            // Buscar por estado (letras mayúsculas y guion bajo)
            encomiendas = encomiendaRepository.findByEstado(criterio.toUpperCase());
            logger.info("Buscando encomiendas por estado: {}. Total: {}", criterio, encomiendas.size());
            
        } else {
            // Buscar por código
            Optional<Encomienda> encomienda = encomiendaRepository.findByCodigoEncomienda(criterio);
            encomiendas = encomienda.map(List::of).orElse(new ArrayList<>());
            logger.info("Buscando encomienda por código: {}. Encontrada: {}", criterio, encomienda.isPresent());
        }
        
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Consulta ejecutada correctamente");
        response.addData("Total", encomiendas.size());
        response.addData("Encomiendas", formatEncomiendasList(encomiendas));
        
        return response;
    }
    
    /**
     * INSENC - Insertar encomienda
     * Formato: INSENC["codigo","id_ruta","placa","remitente","tel_rem","destinatario","tel_dest","desc","peso","precio","fecha","foto","qr"]
     */
    private CommandResponse insertar(CommandRequest request) {
        List<String> params = request.getParameters();
        
        String codigoEncomienda = params.get(0);
        
        // Verificar si ya existe
        if (encomiendaRepository.findByCodigoEncomienda(codigoEncomienda).isPresent()) {
            throw CommandException.businessError(request.getFullCommand(), 
                "Ya existe una encomienda con código: " + codigoEncomienda);
        }
        
        // Buscar ruta
        Long idRuta = Long.parseLong(params.get(1));
        Ruta ruta = rutaRepository.findById(idRuta)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe una ruta con ID: " + idRuta));
        
        // Buscar vehículo
        String placaVehiculo = params.get(2);
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placaVehiculo)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un vehículo con placa: " + placaVehiculo));
        
        // Parsear fecha
        LocalDate fechaEnvio = LocalDate.parse(params.get(10), DATE_FORMATTER);
        
        // Crear nueva encomienda
        Encomienda encomienda = new Encomienda();
        encomienda.setCodigoEncomienda(codigoEncomienda);
        encomienda.setRuta(ruta);
        encomienda.setVehiculo(vehiculo);
        encomienda.setNombreRemitente(params.get(3));
        encomienda.setTelefonoRemitente(params.get(4));
        encomienda.setNombreDestinatario(params.get(5));
        encomienda.setTelefonoDestinatario(params.get(6));
        encomienda.setDescripcion(params.get(7));
        encomienda.setPesoKg(new BigDecimal(params.get(8)));
        encomienda.setPrecio(new BigDecimal(params.get(9)));
        encomienda.setFechaEnvio(fechaEnvio);
        
        if (params.size() > 11 && !params.get(11).isEmpty()) {
            encomienda.setFotoUrl(params.get(11));
        }
        if (params.size() > 12 && !params.get(12).isEmpty()) {
            encomienda.setQrCode(params.get(12));
        }
        
        // Guardar
        encomienda = encomiendaRepository.save(encomienda);
        logger.info("Encomienda creada exitosamente: ID={}, Código={}", 
            encomienda.getIdEncomienda(), encomienda.getCodigoEncomienda());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Encomienda registrada correctamente");
        response.addData("ID", encomienda.getIdEncomienda());
        response.addData("Código", encomienda.getCodigoEncomienda());
        response.addData("Remitente", encomienda.getNombreRemitente() + " (" + encomienda.getTelefonoRemitente() + ")");
        response.addData("Destinatario", encomienda.getNombreDestinatario() + " (" + encomienda.getTelefonoDestinatario() + ")");
        response.addData("Ruta", ruta.getNombre());
        response.addData("Vehículo", vehiculo.getPlaca());
        response.addData("Peso", encomienda.getPesoKg() + " kg");
        response.addData("Precio", "Bs. " + encomienda.getPrecio());
        response.addData("Fecha Envío", encomienda.getFechaEnvio());
        response.addData("Estado", encomienda.getEstado());
        
        return response;
    }
    
    /**
     * UPDENC - Actualizar encomienda (estado y GPS)
     * Formato: UPDENC["codigo","estado","gps"]
     */
    private CommandResponse actualizar(CommandRequest request) {
        List<String> params = request.getParameters();
        String codigoEncomienda = params.get(0);
        String nuevoEstado = params.get(1).toUpperCase();
        
        // Buscar encomienda
        Encomienda encomienda = encomiendaRepository.findByCodigoEncomienda(codigoEncomienda)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe una encomienda con código: " + codigoEncomienda));
        
        // Actualizar estado
        encomienda.setEstado(nuevoEstado);
        
        // Actualizar GPS si se proporciona
        if (params.size() > 2 && !params.get(2).isEmpty()) {
            encomienda.setGpsActual(params.get(2));
        }
        
        encomienda = encomiendaRepository.save(encomienda);
        
        logger.info("Encomienda actualizada exitosamente: ID={}, Código={}, Estado={}, GPS={}", 
            encomienda.getIdEncomienda(), encomienda.getCodigoEncomienda(), 
            nuevoEstado, encomienda.getGpsActual());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Encomienda actualizada correctamente");
        response.addData("Código", encomienda.getCodigoEncomienda());
        response.addData("Estado", encomienda.getEstado());
        if (encomienda.getGpsActual() != null) {
            response.addData("GPS Actual", encomienda.getGpsActual());
        }
        
        return response;
    }
    
    /**
     * DELENC - Eliminar encomienda
     * Formato: DELENC["codigo"]
     */
    private CommandResponse eliminar(CommandRequest request) {
        List<String> params = request.getParameters();
        String codigoEncomienda = params.get(0);
        
        // Buscar encomienda
        Encomienda encomienda = encomiendaRepository.findByCodigoEncomienda(codigoEncomienda)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe una encomienda con código: " + codigoEncomienda));
        
        // Eliminar (se podría cambiar a un estado CANCELADA)
        encomiendaRepository.delete(encomienda);
        
        logger.info("Encomienda eliminada exitosamente: Código={}", codigoEncomienda);
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Encomienda eliminada correctamente");
        response.addData("Código", codigoEncomienda);
        response.addData("Remitente", encomienda.getNombreRemitente());
        response.addData("Destinatario", encomienda.getNombreDestinatario());
        
        return response;
    }
    
    /**
     * Formatea una lista de encomiendas para mostrar en la respuesta
     */
    private String formatEncomiendasList(List<Encomienda> encomiendas) {
        if (encomiendas.isEmpty()) {
            return "No se encontraron encomiendas";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Encomienda e : encomiendas) {
            String ruta = e.getRuta() != null ? e.getRuta().getNombre() : "N/A";
            String vehiculo = e.getVehiculo() != null ? e.getVehiculo().getPlaca() : "N/A";
            String gps = e.getGpsActual() != null ? e.getGpsActual() : "N/A";
            
            sb.append(String.format("\n  - %s | %s → %s | %s | %s | %.2f kg | Bs. %.2f | GPS: %s | %s", 
                e.getCodigoEncomienda(),
                e.getNombreRemitente(),
                e.getNombreDestinatario(),
                ruta,
                vehiculo,
                e.getPesoKg(),
                e.getPrecio(),
                gps,
                e.getEstado()));
        }
        return sb.toString();
    }
}

