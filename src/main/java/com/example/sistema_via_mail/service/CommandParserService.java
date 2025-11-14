package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.exception.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio para parsear comandos desde el asunto del correo
 * Formato esperado: <OPERACION><ENTIDAD>[<parametros>]
 * Ejemplo: INSUSU["1234567","Juan","Pérez","CONDUCTOR"]
 */
@Service
public class CommandParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandParserService.class);
    
    // Operaciones válidas
    private static final List<String> VALID_OPERATIONS = List.of("LIS", "INS", "UPD", "DEL", "REP");
    
    // Entidades válidas
    private static final List<String> VALID_ENTITIES = List.of("USU", "VEH", "RUT", "BOL", "ENC", "VEN", "PAG", "REP");
    
    // Patrón regex para parsear comandos
    // Formato: OPERACION (3 letras) + ENTIDAD (3 letras) + [parámetros entre corchetes]
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^([A-Z]{3})([A-Z]{3})\\[(.*)\\]$");
    
    /**
     * Parsea un comando desde el asunto del correo
     * @param subject Asunto del correo
     * @param senderEmail Email del remitente
     * @return CommandRequest parseado
     * @throws CommandException Si el formato es inválido
     */
    public CommandRequest parse(String subject, String senderEmail) {
        logger.debug("Parseando comando: {}", subject);
        
        if (subject == null || subject.trim().isEmpty()) {
            throw CommandException.syntaxError(subject, "El asunto del correo está vacío");
        }
        
        String cleanSubject = subject.trim();
        
        // Verificar si es el comando HELP especial
        if (cleanSubject.equalsIgnoreCase("HELP") || cleanSubject.equalsIgnoreCase("AYUDA")) {
            logger.debug("Comando HELP detectado");
            CommandRequest request = new CommandRequest();
            request.setOperation("HLP");
            request.setEntity("SYS");
            request.setParameters(new ArrayList<>());
            request.setSenderEmail(senderEmail);
            return request;
        }
        
        // Aplicar patrón regex
        Matcher matcher = COMMAND_PATTERN.matcher(cleanSubject);
        
        if (!matcher.matches()) {
            throw CommandException.syntaxError(cleanSubject, 
                "Formato de comando inválido. Use: <OPERACION><ENTIDAD>[<parametros>]");
        }
        
        String operation = matcher.group(1);
        String entity = matcher.group(2);
        String parametersString = matcher.group(3);
        
        // Validar operación
        if (!VALID_OPERATIONS.contains(operation)) {
            throw CommandException.syntaxError(cleanSubject, 
                "Operación inválida: " + operation + ". Operaciones válidas: " + VALID_OPERATIONS);
        }
        
        // Validar entidad
        if (!VALID_ENTITIES.contains(entity)) {
            throw CommandException.syntaxError(cleanSubject, 
                "Entidad inválida: " + entity + ". Entidades válidas: " + VALID_ENTITIES);
        }
        
        // Parsear parámetros
        List<String> parameters = parseParameters(parametersString);
        
        // Crear CommandRequest
        CommandRequest request = new CommandRequest(operation, entity);
        request.setParameters(parameters);
        request.setOriginalSubject(cleanSubject);
        request.setSenderEmail(senderEmail);
        
        logger.debug("Comando parseado exitosamente: {}", request);
        
        return request;
    }
    
    /**
     * Parsea los parámetros de un comando
     * Formato: "param1","param2","param3"
     * @param parametersString String con los parámetros
     * @return Lista de parámetros
     */
    private List<String> parseParameters(String parametersString) {
        List<String> parameters = new ArrayList<>();
        
        if (parametersString == null || parametersString.trim().isEmpty()) {
            return parameters;
        }
        
        // Patrón para encontrar strings entre comillas, manejando comillas escapadas
        Pattern paramPattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = paramPattern.matcher(parametersString);
        
        while (matcher.find()) {
            String param = matcher.group(1);
            parameters.add(param);
        }
        
        logger.debug("Parámetros parseados: {}", parameters);
        
        return parameters;
    }
    
    /**
     * Valida si un comando tiene el formato básico correcto
     * @param subject Asunto a validar
     * @return true si el formato es válido
     */
    public boolean isValidCommandFormat(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            return false;
        }
        return COMMAND_PATTERN.matcher(subject.trim()).matches();
    }
    
    /**
     * Obtiene las operaciones válidas
     */
    public List<String> getValidOperations() {
        return new ArrayList<>(VALID_OPERATIONS);
    }
    
    /**
     * Obtiene las entidades válidas
     */
    public List<String> getValidEntities() {
        return new ArrayList<>(VALID_ENTITIES);
    }
}

