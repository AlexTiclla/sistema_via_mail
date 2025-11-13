package com.example.sistema_via_mail.util;

import com.example.sistema_via_mail.dto.CommandResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Utilidad para formatear respuestas de comandos en formato texto para emails
 */
@Component
public class ResponseFormatter {
    
    private static final String SEPARATOR = "===========================================";
    private static final String LINE = "-------------------------------------------";
    
    /**
     * Formatea una respuesta de comando en texto plano para email
     * @param response Respuesta a formatear
     * @return String formateado para el cuerpo del email
     */
    public String format(CommandResponse response) {
        StringBuilder sb = new StringBuilder();
        
        // Encabezado
        sb.append(SEPARATOR).append("\n");
        sb.append("SISTEMA TRANS COMARAPA - RESPUESTA\n");
        sb.append(SEPARATOR).append("\n\n");
        
        // Información del comando
        sb.append("COMANDO: ").append(response.getCommand()).append("\n");
        sb.append("ESTADO: ").append(response.getStatus()).append("\n");
        sb.append("FECHA/HORA: ").append(response.getFormattedTimestamp()).append("\n");
        sb.append("\n");
        
        // Mensaje principal
        sb.append("MENSAJE: ").append(response.getMessage()).append("\n");
        
        // Detalles adicionales (si existen)
        if (response.getDetails() != null && !response.getDetails().isEmpty()) {
            sb.append("\nDETALLE:\n");
            sb.append(response.getDetails()).append("\n");
        }
        
        // Datos adicionales (si existen)
        if (response.getData() != null && !response.getData().isEmpty()) {
            sb.append("\n").append(LINE).append("\n");
            sb.append("DATOS:\n");
            sb.append(LINE).append("\n");
            formatData(response.getData(), sb);
        }
        
        // Pie de página
        sb.append("\n").append(SEPARATOR).append("\n");
        sb.append("Trans Comarapa - Sistema de Gestión\n");
        sb.append("Grupo04 SA\n");
        sb.append(SEPARATOR).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Formatea los datos del response
     */
    private void formatData(Map<String, Object> data, StringBuilder sb) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            sb.append("- ").append(formatKey(entry.getKey())).append(": ");
            sb.append(formatValue(entry.getValue())).append("\n");
        }
    }
    
    /**
     * Formatea una clave para hacerla más legible
     */
    private String formatKey(String key) {
        // Convertir snake_case o camelCase a Title Case con espacios
        return key.replaceAll("([A-Z])", " $1")
                  .replaceAll("_", " ")
                  .trim()
                  .substring(0, 1).toUpperCase() + 
               key.replaceAll("([A-Z])", " $1")
                  .replaceAll("_", " ")
                  .trim()
                  .substring(1);
    }
    
    /**
     * Formatea un valor según su tipo
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "N/A";
        }
        
        if (value instanceof Map) {
            return formatMapValue((Map<?, ?>) value);
        }
        
        return value.toString();
    }
    
    /**
     * Formatea un valor de tipo Map
     */
    private String formatMapValue(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("\n");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append("  * ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Formatea una respuesta de error
     */
    public String formatError(String command, String errorType, String message, String details) {
        CommandResponse response = CommandResponse.error(command, message);
        response.setDetails(details);
        response.addData("Tipo de Error", errorType);
        return format(response);
    }
    
    /**
     * Formatea una lista de resultados
     */
    public String formatList(CommandResponse response, java.util.List<?> items) {
        StringBuilder sb = new StringBuilder();
        
        // Encabezado
        sb.append(SEPARATOR).append("\n");
        sb.append("SISTEMA TRANS COMARAPA - RESPUESTA\n");
        sb.append(SEPARATOR).append("\n\n");
        
        // Información del comando
        sb.append("COMANDO: ").append(response.getCommand()).append("\n");
        sb.append("ESTADO: ").append(response.getStatus()).append("\n");
        sb.append("FECHA/HORA: ").append(response.getFormattedTimestamp()).append("\n");
        sb.append("\n");
        
        // Mensaje
        sb.append(response.getMessage()).append("\n");
        sb.append("REGISTROS ENCONTRADOS: ").append(items.size()).append("\n");
        sb.append("\n");
        
        // Lista de items
        if (!items.isEmpty()) {
            sb.append(LINE).append("\n");
            for (int i = 0; i < items.size(); i++) {
                sb.append("\n").append(i + 1).append(". ");
                sb.append(items.get(i).toString()).append("\n");
            }
            sb.append(LINE).append("\n");
        } else {
            sb.append("No se encontraron resultados.\n");
        }
        
        // Pie de página
        sb.append("\n").append(SEPARATOR).append("\n");
        sb.append("Trans Comarapa - Sistema de Gestión\n");
        sb.append("Grupo04 SA\n");
        sb.append(SEPARATOR).append("\n");
        
        return sb.toString();
    }
}

