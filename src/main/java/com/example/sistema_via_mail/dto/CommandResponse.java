package com.example.sistema_via_mail.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO para representar la respuesta de un comando
 */
public class CommandResponse {
    
    private String command;
    private ResponseStatus status;
    private String message;
    private String details;
    private Map<String, Object> data;
    private LocalDateTime timestamp;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public enum ResponseStatus {
        EXITOSO,
        ERROR,
        ADVERTENCIA
    }
    
    public CommandResponse() {
        this.data = new LinkedHashMap<>();
        this.timestamp = LocalDateTime.now();
    }
    
    public CommandResponse(String command, ResponseStatus status, String message) {
        this();
        this.command = command;
        this.status = status;
        this.message = message;
    }
    
    /**
     * Crea una respuesta exitosa
     */
    public static CommandResponse success(String command, String message) {
        return new CommandResponse(command, ResponseStatus.EXITOSO, message);
    }
    
    /**
     * Crea una respuesta de error
     */
    public static CommandResponse error(String command, String message) {
        return new CommandResponse(command, ResponseStatus.ERROR, message);
    }
    
    /**
     * Crea una respuesta de advertencia
     */
    public static CommandResponse warning(String command, String message) {
        return new CommandResponse(command, ResponseStatus.ADVERTENCIA, message);
    }
    
    // Getters y Setters
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public ResponseStatus getStatus() {
        return status;
    }
    
    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    /**
     * Añade un dato a la respuesta
     */
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Obtiene el timestamp formateado
     */
    public String getFormattedTimestamp() {
        return timestamp.format(formatter);
    }
    
    @Override
    public String toString() {
        return "CommandResponse{" +
                "command='" + command + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

