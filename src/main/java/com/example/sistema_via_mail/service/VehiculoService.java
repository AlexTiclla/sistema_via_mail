package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.exception.CommandException;
import com.example.sistema_via_mail.model.Usuario;
import com.example.sistema_via_mail.model.Vehiculo;
import com.example.sistema_via_mail.repository.UsuarioRepository;
import com.example.sistema_via_mail.repository.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de vehículos (CU2)
 * Implementa operaciones CRUD via comandos de correo
 */
@Service
@Transactional
public class VehiculoService {
    
    private static final Logger logger = LoggerFactory.getLogger(VehiculoService.class);
    
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public VehiculoService(VehiculoRepository vehiculoRepository, UsuarioRepository usuarioRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    /**
     * Procesa un comando de vehículo
     * @param request Comando a procesar
     * @return Respuesta del comando
     */
    public CommandResponse processCommand(CommandRequest request) {
        logger.info("Procesando comando de vehículo: {}", request.getFullCommand());
        
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
     * LISVEH - Listar vehículos
     * Formatos:
     * - LISVEH["*"] - Todos los vehículos
     * - LISVEH["ABC-123"] - Por placa
     */
    private CommandResponse listar(CommandRequest request) {
        List<String> params = request.getParameters();
        String criterio = params.get(0);
        
        List<Vehiculo> vehiculos;
        
        if ("*".equals(criterio)) {
            // Listar todos
            vehiculos = vehiculoRepository.findAll();
            logger.info("Listando todos los vehículos. Total: {}", vehiculos.size());
            
        } else {
            // Buscar por placa
            Optional<Vehiculo> vehiculo = vehiculoRepository.findByPlaca(criterio);
            vehiculos = vehiculo.map(List::of).orElse(new ArrayList<>());
            logger.info("Buscando vehículo por placa: {}. Encontrado: {}", criterio, vehiculo.isPresent());
        }
        
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Consulta ejecutada correctamente");
        response.addData("Total", vehiculos.size());
        response.addData("Vehículos", formatVehiculosList(vehiculos));
        
        return response;
    }
    
    /**
     * INSVEH - Insertar vehículo
     * Formato: INSVEH["placa","marca","modelo","capacidad","ci_propietario","foto"]
     * Mínimo: INSVEH["placa","marca","modelo","capacidad","ci_propietario"]
     */
    private CommandResponse insertar(CommandRequest request) {
        List<String> params = request.getParameters();
        
        String placa = params.get(0);
        
        // Verificar si ya existe
        if (vehiculoRepository.findByPlaca(placa).isPresent()) {
            throw CommandException.businessError(request.getFullCommand(), 
                "Ya existe un vehículo con placa: " + placa);
        }
        
        // Verificar que el propietario existe
        String ciPropietario = params.get(4);
        Usuario propietario = usuarioRepository.findByCi(ciPropietario)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un usuario con CI: " + ciPropietario));
        
        // Verificar que sea propietario
        if (!"PROPIETARIO".equals(propietario.getTipoUsuario())) {
            throw CommandException.businessError(request.getFullCommand(), 
                "El usuario con CI " + ciPropietario + " no es un PROPIETARIO");
        }
        
        // Crear nuevo vehículo
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placa);
        vehiculo.setMarca(params.get(1));
        vehiculo.setModelo(params.get(2));
        vehiculo.setCapacidadPasajeros(Integer.parseInt(params.get(3)));
        vehiculo.setPropietario(propietario);
        
        if (params.size() > 5 && !params.get(5).isEmpty()) {
            vehiculo.setFotoUrl(params.get(5));
        }
        
        // Guardar
        vehiculo = vehiculoRepository.save(vehiculo);
        logger.info("Vehículo creado exitosamente: ID={}, Placa={}", vehiculo.getIdVehiculo(), vehiculo.getPlaca());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Vehículo registrado correctamente");
        response.addData("ID", vehiculo.getIdVehiculo());
        response.addData("Placa", vehiculo.getPlaca());
        response.addData("Marca", vehiculo.getMarca());
        response.addData("Modelo", vehiculo.getModelo());
        response.addData("Capacidad", vehiculo.getCapacidadPasajeros() + " pasajeros");
        response.addData("Propietario", propietario.getNombre() + " " + propietario.getApellido());
        response.addData("Estado", vehiculo.getEstado());
        response.addData("Fecha Registro", vehiculo.getFechaRegistro());
        
        return response;
    }
    
    /**
     * UPDVEH - Actualizar vehículo
     * Formato: UPDVEH["placa","marca","modelo","capacidad","ci_propietario","foto","estado"]
     */
    private CommandResponse actualizar(CommandRequest request) {
        List<String> params = request.getParameters();
        String placa = params.get(0);
        
        // Buscar vehículo
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un vehículo con placa: " + placa));
        
        // Actualizar campos
        vehiculo.setMarca(params.get(1));
        vehiculo.setModelo(params.get(2));
        vehiculo.setCapacidadPasajeros(Integer.parseInt(params.get(3)));
        
        // Actualizar propietario si se proporciona
        String ciPropietario = params.get(4);
        Usuario propietario = usuarioRepository.findByCi(ciPropietario)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un usuario con CI: " + ciPropietario));
        
        if (!"PROPIETARIO".equals(propietario.getTipoUsuario())) {
            throw CommandException.businessError(request.getFullCommand(), 
                "El usuario con CI " + ciPropietario + " no es un PROPIETARIO");
        }
        
        vehiculo.setPropietario(propietario);
        
        if (params.size() > 5 && !params.get(5).isEmpty()) {
            vehiculo.setFotoUrl(params.get(5));
        }
        if (params.size() > 6 && !params.get(6).isEmpty()) {
            vehiculo.setEstado(params.get(6).toUpperCase());
        }
        
        // Guardar
        vehiculo = vehiculoRepository.save(vehiculo);
        logger.info("Vehículo actualizado exitosamente: ID={}, Placa={}", vehiculo.getIdVehiculo(), vehiculo.getPlaca());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Vehículo actualizado correctamente");
        response.addData("ID", vehiculo.getIdVehiculo());
        response.addData("Placa", vehiculo.getPlaca());
        response.addData("Marca", vehiculo.getMarca());
        response.addData("Modelo", vehiculo.getModelo());
        response.addData("Capacidad", vehiculo.getCapacidadPasajeros() + " pasajeros");
        response.addData("Estado", vehiculo.getEstado());
        
        return response;
    }
    
    /**
     * DELVEH - Eliminar vehículo
     * Formato: DELVEH["placa"]
     */
    private CommandResponse eliminar(CommandRequest request) {
        List<String> params = request.getParameters();
        String placa = params.get(0);
        
        // Buscar vehículo
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un vehículo con placa: " + placa));
        
        // Cambiar estado a INACTIVO en lugar de eliminar físicamente
        vehiculo.setEstado("INACTIVO");
        vehiculo = vehiculoRepository.save(vehiculo);
        
        logger.info("Vehículo desactivado exitosamente: ID={}, Placa={}", vehiculo.getIdVehiculo(), vehiculo.getPlaca());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Vehículo eliminado correctamente");
        response.addData("Placa", vehiculo.getPlaca());
        response.addData("Marca", vehiculo.getMarca());
        response.addData("Modelo", vehiculo.getModelo());
        response.addData("Estado", vehiculo.getEstado());
        
        return response;
    }
    
    /**
     * Formatea una lista de vehículos para mostrar en la respuesta
     */
    private String formatVehiculosList(List<Vehiculo> vehiculos) {
        if (vehiculos.isEmpty()) {
            return "No se encontraron vehículos";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Vehiculo v : vehiculos) {
            String propietario = v.getPropietario() != null ? 
                v.getPropietario().getNombre() + " " + v.getPropietario().getApellido() : "N/A";
            
            sb.append(String.format("\n  - Placa: %s | %s %s | Cap: %d | Propietario: %s | %s", 
                v.getPlaca(), 
                v.getMarca(), 
                v.getModelo(), 
                v.getCapacidadPasajeros(),
                propietario,
                v.getEstado()));
        }
        return sb.toString();
    }
}

