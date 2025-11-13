package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.exception.CommandException;
import com.example.sistema_via_mail.model.Ruta;
import com.example.sistema_via_mail.repository.RutaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de rutas (CU3)
 * Implementa operaciones CRUD via comandos de correo
 */
@Service
@Transactional
public class RutaService {
    
    private static final Logger logger = LoggerFactory.getLogger(RutaService.class);
    
    private final RutaRepository rutaRepository;
    
    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }
    
    /**
     * Procesa un comando de ruta
     * @param request Comando a procesar
     * @return Respuesta del comando
     */
    public CommandResponse processCommand(CommandRequest request) {
        logger.info("Procesando comando de ruta: {}", request.getFullCommand());
        
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
     * LISRUT - Listar rutas
     * Formatos:
     * - LISRUT["*"] - Todas las rutas
     * - LISRUT["1"] - Por ID
     */
    private CommandResponse listar(CommandRequest request) {
        List<String> params = request.getParameters();
        String criterio = params.get(0);
        
        List<Ruta> rutas;
        
        if ("*".equals(criterio)) {
            // Listar todas
            rutas = rutaRepository.findAll();
            logger.info("Listando todas las rutas. Total: {}", rutas.size());
            
        } else {
            // Buscar por ID
            Long id = Long.parseLong(criterio);
            Optional<Ruta> ruta = rutaRepository.findById(id);
            rutas = ruta.map(List::of).orElse(new ArrayList<>());
            logger.info("Buscando ruta por ID: {}. Encontrada: {}", id, ruta.isPresent());
        }
        
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Consulta ejecutada correctamente");
        response.addData("Total", rutas.size());
        response.addData("Rutas", formatRutasList(rutas));
        
        return response;
    }
    
    /**
     * INSRUT - Insertar ruta
     * Formato: INSRUT["nombre","origen","destino","distancia","duracion","precio","gps_origen","gps_destino"]
     * Mínimo: INSRUT["nombre","origen","destino","distancia","duracion","precio"]
     */
    private CommandResponse insertar(CommandRequest request) {
        List<String> params = request.getParameters();
        
        // Crear nueva ruta
        Ruta ruta = new Ruta();
        ruta.setNombre(params.get(0));
        ruta.setOrigen(params.get(1));
        ruta.setDestino(params.get(2));
        ruta.setDistanciaKm(new BigDecimal(params.get(3)));
        ruta.setDuracionHoras(new BigDecimal(params.get(4)));
        ruta.setPrecioBase(new BigDecimal(params.get(5)));
        
        if (params.size() > 6 && !params.get(6).isEmpty()) {
            ruta.setGpsOrigen(params.get(6));
        }
        if (params.size() > 7 && !params.get(7).isEmpty()) {
            ruta.setGpsDestino(params.get(7));
        }
        
        // Guardar
        ruta = rutaRepository.save(ruta);
        logger.info("Ruta creada exitosamente: ID={}, Nombre={}", ruta.getIdRuta(), ruta.getNombre());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Ruta registrada correctamente");
        response.addData("ID", ruta.getIdRuta());
        response.addData("Nombre", ruta.getNombre());
        response.addData("Origen", ruta.getOrigen());
        response.addData("Destino", ruta.getDestino());
        response.addData("Distancia", ruta.getDistanciaKm() + " km");
        response.addData("Duración", ruta.getDuracionHoras() + " horas");
        response.addData("Precio Base", "Bs. " + ruta.getPrecioBase());
        response.addData("Estado", ruta.getEstado());
        response.addData("Fecha Registro", ruta.getFechaRegistro());
        
        return response;
    }
    
    /**
     * UPDRUT - Actualizar ruta
     * Formato: UPDRUT["id","nombre","origen","destino","distancia","duracion","precio","gps_origen","gps_destino","estado"]
     */
    private CommandResponse actualizar(CommandRequest request) {
        List<String> params = request.getParameters();
        Long id = Long.parseLong(params.get(0));
        
        // Buscar ruta
        Ruta ruta = rutaRepository.findById(id)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe una ruta con ID: " + id));
        
        // Actualizar campos
        ruta.setNombre(params.get(1));
        ruta.setOrigen(params.get(2));
        ruta.setDestino(params.get(3));
        ruta.setDistanciaKm(new BigDecimal(params.get(4)));
        ruta.setDuracionHoras(new BigDecimal(params.get(5)));
        ruta.setPrecioBase(new BigDecimal(params.get(6)));
        
        if (params.size() > 7 && !params.get(7).isEmpty()) {
            ruta.setGpsOrigen(params.get(7));
        }
        if (params.size() > 8 && !params.get(8).isEmpty()) {
            ruta.setGpsDestino(params.get(8));
        }
        if (params.size() > 9 && !params.get(9).isEmpty()) {
            ruta.setEstado(params.get(9).toUpperCase());
        }
        
        // Guardar
        ruta = rutaRepository.save(ruta);
        logger.info("Ruta actualizada exitosamente: ID={}, Nombre={}", ruta.getIdRuta(), ruta.getNombre());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Ruta actualizada correctamente");
        response.addData("ID", ruta.getIdRuta());
        response.addData("Nombre", ruta.getNombre());
        response.addData("Precio Base", "Bs. " + ruta.getPrecioBase());
        response.addData("Estado", ruta.getEstado());
        
        return response;
    }
    
    /**
     * DELRUT - Eliminar ruta
     * Formato: DELRUT["id"]
     */
    private CommandResponse eliminar(CommandRequest request) {
        List<String> params = request.getParameters();
        Long id = Long.parseLong(params.get(0));
        
        // Buscar ruta
        Ruta ruta = rutaRepository.findById(id)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe una ruta con ID: " + id));
        
        // Desactivar en lugar de eliminar físicamente
        ruta.setEstado("INACTIVO");
        ruta = rutaRepository.save(ruta);
        
        logger.info("Ruta desactivada exitosamente: ID={}, Nombre={}", ruta.getIdRuta(), ruta.getNombre());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Ruta eliminada correctamente");
        response.addData("ID", ruta.getIdRuta());
        response.addData("Nombre", ruta.getNombre());
        response.addData("Estado", ruta.getEstado());
        
        return response;
    }
    
    /**
     * Formatea una lista de rutas para mostrar en la respuesta
     */
    private String formatRutasList(List<Ruta> rutas) {
        if (rutas.isEmpty()) {
            return "No se encontraron rutas";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Ruta r : rutas) {
            sb.append(String.format("\n  - ID: %d | %s | %s → %s | Precio: Bs. %.2f | %s", 
                r.getIdRuta(), 
                r.getNombre(), 
                r.getOrigen(),
                r.getDestino(),
                r.getPrecioBase(),
                r.getEstado()));
        }
        return sb.toString();
    }
}

