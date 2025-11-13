package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.exception.CommandException;
import com.example.sistema_via_mail.model.Usuario;
import com.example.sistema_via_mail.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de usuarios (CU1)
 * Implementa operaciones CRUD via comandos de correo
 */
@Service
@Transactional
public class UsuarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    /**
     * Procesa un comando de usuario
     * @param request Comando a procesar
     * @return Respuesta del comando
     */
    public CommandResponse processCommand(CommandRequest request) {
        logger.info("Procesando comando de usuario: {}", request.getFullCommand());
        
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
     * LISUSU - Listar usuarios
     * Formatos:
     * - LISUSU["*"] - Todos los usuarios
     * - LISUSU["1234567"] - Por CI
     * - LISUSU["CONDUCTOR"] - Por tipo
     */
    private CommandResponse listar(CommandRequest request) {
        List<String> params = request.getParameters();
        String criterio = params.get(0);
        
        List<Usuario> usuarios;
        
        if ("*".equals(criterio)) {
            // Listar todos
            usuarios = usuarioRepository.findAll();
            logger.info("Listando todos los usuarios. Total: {}", usuarios.size());
            
        } else if (criterio.matches("\\d+")) {
            // Buscar por CI
            Optional<Usuario> usuario = usuarioRepository.findByCi(criterio);
            usuarios = usuario.map(List::of).orElse(new ArrayList<>());
            logger.info("Buscando usuario por CI: {}. Encontrado: {}", criterio, usuario.isPresent());
            
        } else {
            // Buscar por tipo
            usuarios = usuarioRepository.findByTipoUsuario(criterio.toUpperCase());
            logger.info("Buscando usuarios por tipo: {}. Total: {}", criterio, usuarios.size());
        }
        
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Consulta ejecutada correctamente");
        response.addData("Total", usuarios.size());
        response.addData("Usuarios", formatUsuariosList(usuarios));
        
        return response;
    }
    
    /**
     * INSUSU - Insertar usuario
     * Formato: INSUSU["ci","nombre","apellido","tipo","telefono","email","foto"]
     * Mínimo: INSUSU["ci","nombre","apellido","tipo"]
     */
    private CommandResponse insertar(CommandRequest request) {
        List<String> params = request.getParameters();
        
        String ci = params.get(0);
        
        // Verificar si ya existe
        if (usuarioRepository.findByCi(ci).isPresent()) {
            throw CommandException.businessError(request.getFullCommand(), 
                "Ya existe un usuario con CI: " + ci);
        }
        
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setCi(ci);
        usuario.setNombre(params.get(1));
        usuario.setApellido(params.get(2));
        usuario.setTipoUsuario(params.get(3).toUpperCase());
        
        if (params.size() > 4 && !params.get(4).isEmpty()) {
            usuario.setTelefono(params.get(4));
        }
        if (params.size() > 5 && !params.get(5).isEmpty()) {
            usuario.setEmail(params.get(5));
        }
        if (params.size() > 6 && !params.get(6).isEmpty()) {
            usuario.setFotoUrl(params.get(6));
        }
        
        // Guardar
        usuario = usuarioRepository.save(usuario);
        logger.info("Usuario creado exitosamente: ID={}, CI={}", usuario.getIdUsuario(), usuario.getCi());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Usuario registrado correctamente");
        response.addData("ID", usuario.getIdUsuario());
        response.addData("CI", usuario.getCi());
        response.addData("Nombre Completo", usuario.getNombre() + " " + usuario.getApellido());
        response.addData("Tipo", usuario.getTipoUsuario());
        response.addData("Estado", usuario.getEstado());
        response.addData("Fecha Registro", usuario.getFechaRegistro());
        
        return response;
    }
    
    /**
     * UPDUSU - Actualizar usuario
     * Formato: UPDUSU["ci","nombre","apellido","tipo","telefono","email","foto","estado"]
     */
    private CommandResponse actualizar(CommandRequest request) {
        List<String> params = request.getParameters();
        String ci = params.get(0);
        
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByCi(ci)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un usuario con CI: " + ci));
        
        // Actualizar campos
        usuario.setNombre(params.get(1));
        usuario.setApellido(params.get(2));
        usuario.setTipoUsuario(params.get(3).toUpperCase());
        
        if (params.size() > 4 && !params.get(4).isEmpty()) {
            usuario.setTelefono(params.get(4));
        }
        if (params.size() > 5 && !params.get(5).isEmpty()) {
            usuario.setEmail(params.get(5));
        }
        if (params.size() > 6 && !params.get(6).isEmpty()) {
            usuario.setFotoUrl(params.get(6));
        }
        if (params.size() > 7 && !params.get(7).isEmpty()) {
            usuario.setEstado(params.get(7).toUpperCase());
        }
        
        // Guardar
        usuario = usuarioRepository.save(usuario);
        logger.info("Usuario actualizado exitosamente: ID={}, CI={}", usuario.getIdUsuario(), usuario.getCi());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Usuario actualizado correctamente");
        response.addData("ID", usuario.getIdUsuario());
        response.addData("CI", usuario.getCi());
        response.addData("Nombre Completo", usuario.getNombre() + " " + usuario.getApellido());
        response.addData("Tipo", usuario.getTipoUsuario());
        response.addData("Estado", usuario.getEstado());
        
        return response;
    }
    
    /**
     * DELUSU - Eliminar (desactivar) usuario
     * Formato: DELUSU["ci"]
     */
    private CommandResponse eliminar(CommandRequest request) {
        List<String> params = request.getParameters();
        String ci = params.get(0);
        
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByCi(ci)
            .orElseThrow(() -> CommandException.businessError(request.getFullCommand(), 
                "No existe un usuario con CI: " + ci));
        
        // Desactivar (no eliminar físicamente)
        usuario.setEstado("INACTIVO");
        usuario = usuarioRepository.save(usuario);
        
        logger.info("Usuario desactivado exitosamente: ID={}, CI={}", usuario.getIdUsuario(), usuario.getCi());
        
        // Preparar respuesta
        CommandResponse response = CommandResponse.success(request.getFullCommand(), 
            "Usuario desactivado correctamente");
        response.addData("CI", usuario.getCi());
        response.addData("Nombre", usuario.getNombre() + " " + usuario.getApellido());
        response.addData("Estado", usuario.getEstado());
        
        return response;
    }
    
    /**
     * Formatea una lista de usuarios para mostrar en la respuesta
     */
    private String formatUsuariosList(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            return "No se encontraron usuarios";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Usuario u : usuarios) {
            sb.append(String.format("\n  - CI: %s | %s %s | %s | %s", 
                u.getCi(), 
                u.getNombre(), 
                u.getApellido(), 
                u.getTipoUsuario(), 
                u.getEstado()));
        }
        return sb.toString();
    }
}

