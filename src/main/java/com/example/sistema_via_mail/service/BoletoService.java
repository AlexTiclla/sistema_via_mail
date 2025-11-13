package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.exception.CommandException;
import com.example.sistema_via_mail.model.Boleto;
import com.example.sistema_via_mail.model.Ruta;
import com.example.sistema_via_mail.model.Vehiculo;
import com.example.sistema_via_mail.repository.BoletoRepository;
import com.example.sistema_via_mail.repository.RutaRepository;
import com.example.sistema_via_mail.repository.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de boletos (CU4)
 * Implementa operaciones CRUD via comandos de correo
 */
@Service
@Transactional
public class BoletoService {
    
    private static final Logger logger = LoggerFactory.getLogger(BoletoService.class);
    
    private final BoletoRepository boletoRepository;
    private final RutaRepository rutaRepository;
    private final VehiculoRepository vehiculoRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public BoletoService(BoletoRepository boletoRepository, 
                        RutaRepository rutaRepository,
                        VehiculoRepository vehiculoRepository) {
        this.boletoRepository = boletoRepository;
        this.rutaRepository = rutaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }
    
    /**
     * Procesa un comando de boleto
     * @param request Comando a procesar
     * @return Respuesta del comando
     */
    public CommandResponse processCommand(CommandRequest request) {
        logger.info("Procesando comando de boleto: {}", request.getFullCommand());
        
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
     * LISBOL - Listar boletos
     * Formatos:
     * - LISBOL["*"] - Todos los boletos
     * - LISBOL["BOL-001"] - Por código
     * - LISBOL["2025-11-15"] - Por fecha
     */
    private CommandResponse listar(CommandRequest request) {
        List<String> params = request.getParameters();
        String criterio = params.get(0);
        
        List<Boleto> boletos;
        
        if ("*".equals(criterio)) {
            // Listar todos
            boletos = boletoRepository.findAll();
            logger.info("Listando todos los boletos. Total: {}", boletos.size());
            
        } else if (criterio.matches("\\d{4}-\\d{2}-\\d{2}")) {
            // Buscar por fecha
            LocalDate fecha = LocalDate.parse(criterio, DATE_FORMATTER);
            boletos = boletoRepository.findByFechaViaje(fecha);
            logger.info("Buscando boletos por fecha: {}. Total: {}", fecha, boletos.size());
            
        } else {
            // Buscar por código
            Optional<Boleto> boleto = boletoRepository.findByCodigoBoleto(criterio);
            boletos = boleto.map(List::of).orElse(new ArrayList<>());
            logger.info("Buscando boleto por código: {}. Encontrado: {}", criterio, boleto.isPresent());
        }
        
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Consulta ejecutada correctamente");
        response.addData("Total", boletos.size());
        response.addData("Boletos", formatBoletosList(boletos));
        
        return response;
    }
    
    /**
     * INSBOL - Insertar boleto
     * Formato: INSBOL["codigo","id_ruta","placa_vehiculo","nombre_pasajero","ci","asiento","precio","fecha","hora","qr"]
     */
    private CommandResponse insertar(CommandRequest request) {
        List<String> params = request.getParameters();
        
        String codigoBoleto = params.get(0);
        
        // Verificar si ya existe
        if (boletoRepository.findByCodigoBoleto(codigoBoleto).isPresent()) {
            throw CommandException.businessError(request.getFullCommand(), 
                "Ya existe un boleto con código: " + codigoBoleto);
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
        
        // Parsear fecha y hora
        LocalDate fechaViaje = LocalDate.parse(params.get(7), DATE_FORMATTER);
        LocalTime horaSalida = LocalTime.parse(params.get(8), TIME_FORMATTER);
        int asiento = Integer.parseInt(params.get(5));
        
        // Verificar disponibilidad de asiento
        Optional<Boleto> boletoExistente = boletoRepository
            .findByVehiculoAndFechaViajeAndHoraSalidaAndAsiento(vehiculo, fechaViaje, horaSalida, asiento);
        
        if (boletoExistente.isPresent() && !"CANCELADO".equals(boletoExistente.get().getEstado())) {
            throw CommandException.businessError(request.getFullCommand(), 
                "El asiento " + asiento + " ya está ocupado para ese vehículo, fecha y hora");
        }
        
        // Validar capacidad del vehículo
        if (asiento > vehiculo.getCapacidadPasajeros()) {
            throw CommandException.businessError(request.getFullCommand(), 
                "El asiento " + asiento + " excede la capacidad del vehículo (" + 
                vehiculo.getCapacidadPasajeros() + " pasajeros)");
        }
        
        // Crear nuevo boleto
        Boleto boleto = new Boleto();
        boleto.setCodigoBoleto(codigoBoleto);
        boleto.setRuta(ruta);
        boleto.setVehiculo(vehiculo);
        boleto.setNombrePasajero(params.get(3));
        boleto.setCiPasajero(params.get(4));
        boleto.setAsiento(asiento);
        boleto.setPrecio(new BigDecimal(params.get(6)));
        boleto.setFechaViaje(fechaViaje);
        boleto.setHoraSalida(horaSalida);
        boleto.setQrCode(params.get(9));
        
        // Guardar
        boleto = boletoRepository.save(boleto);
        logger.info("Boleto creado exitosamente: ID={}, Código={}", boleto.getIdBoleto(), boleto.getCodigoBoleto());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Boleto registrado correctamente");
        response.addData("ID", boleto.getIdBoleto());
        response.addData("Código", boleto.getCodigoBoleto());
        response.addData("Pasajero", boleto.getNombrePasajero() + " (CI: " + boleto.getCiPasajero() + ")");
        response.addData("Ruta", ruta.getNombre());
        response.addData("Vehículo", vehiculo.getPlaca());
        response.addData("Asiento", boleto.getAsiento());
        response.addData("Fecha Viaje", boleto.getFechaViaje() + " " + boleto.getHoraSalida());
        response.addData("Precio", "Bs. " + boleto.getPrecio());
        response.addData("Estado", boleto.getEstado());
        
        return response;
    }
    
    /**
     * UPDBOL - Actualizar estado de boleto
     * Formato: UPDBOL["codigo","estado"]
     */
    private CommandResponse actualizar(CommandRequest request) {
        List<String> params = request.getParameters();
        String codigoBoleto = params.get(0);
        String nuevoEstado = params.get(1).toUpperCase();
        
        // Buscar boleto
        Boleto boleto = boletoRepository.findByCodigoBoleto(codigoBoleto)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un boleto con código: " + codigoBoleto));
        
        // Actualizar estado
        boleto.setEstado(nuevoEstado);
        boleto = boletoRepository.save(boleto);
        
        logger.info("Boleto actualizado exitosamente: ID={}, Código={}, Nuevo estado={}", 
            boleto.getIdBoleto(), boleto.getCodigoBoleto(), nuevoEstado);
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Estado de boleto actualizado correctamente");
        response.addData("Código", boleto.getCodigoBoleto());
        response.addData("Pasajero", boleto.getNombrePasajero());
        response.addData("Estado", boleto.getEstado());
        
        return response;
    }
    
    /**
     * DELBOL - Eliminar (cancelar) boleto
     * Formato: DELBOL["codigo"]
     */
    private CommandResponse eliminar(CommandRequest request) {
        List<String> params = request.getParameters();
        String codigoBoleto = params.get(0);
        
        // Buscar boleto
        Boleto boleto = boletoRepository.findByCodigoBoleto(codigoBoleto)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un boleto con código: " + codigoBoleto));
        
        // Cancelar en lugar de eliminar físicamente
        boleto.setEstado("CANCELADO");
        boleto = boletoRepository.save(boleto);
        
        logger.info("Boleto cancelado exitosamente: ID={}, Código={}", boleto.getIdBoleto(), boleto.getCodigoBoleto());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Boleto cancelado correctamente");
        response.addData("Código", boleto.getCodigoBoleto());
        response.addData("Pasajero", boleto.getNombrePasajero());
        response.addData("Estado", boleto.getEstado());
        
        return response;
    }
    
    /**
     * Formatea una lista de boletos para mostrar en la respuesta
     */
    private String formatBoletosList(List<Boleto> boletos) {
        if (boletos.isEmpty()) {
            return "No se encontraron boletos";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Boleto b : boletos) {
            String ruta = b.getRuta() != null ? b.getRuta().getNombre() : "N/A";
            String vehiculo = b.getVehiculo() != null ? b.getVehiculo().getPlaca() : "N/A";
            
            sb.append(String.format("\n  - %s | %s | %s → %s | Asiento: %d | Bs. %.2f | %s", 
                b.getCodigoBoleto(),
                b.getNombrePasajero(),
                ruta,
                vehiculo,
                b.getAsiento(),
                b.getPrecio(),
                b.getEstado()));
        }
        return sb.toString();
    }
}

