package com.example.sistema_via_mail.exception;

/**
 * Excepción personalizada para errores en el procesamiento de comandos
 */
public class CommandException extends RuntimeException {
    
    private String errorType;
    private String command;
    
    public CommandException(String message) {
        super(message);
    }
    
    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CommandException(String command, String errorType, String message) {
        super(message);
        this.command = command;
        this.errorType = errorType;
    }
    
    public CommandException(String command, String errorType, String message, Throwable cause) {
        super(message, cause);
        this.command = command;
        this.errorType = errorType;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    /**
     * Crea una excepción de sintaxis
     */
    public static CommandException syntaxError(String command, String message) {
        return new CommandException(command, "ERROR_SINTAXIS", message);
    }
    
    /**
     * Crea una excepción de validación
     */
    public static CommandException validationError(String command, String message) {
        return new CommandException(command, "ERROR_VALIDACION", message);
    }
    
    /**
     * Crea una excepción de negocio
     */
    public static CommandException businessError(String command, String message) {
        return new CommandException(command, "ERROR_NEGOCIO", message);
    }
    
    /**
     * Crea una excepción de base de datos
     */
    public static CommandException databaseError(String command, String message, Throwable cause) {
        return new CommandException(command, "ERROR_BASE_DATOS", message, cause);
    }
    
    /**
     * Crea una excepción de sistema
     */
    public static CommandException systemError(String command, String message, Throwable cause) {
        return new CommandException(command, "ERROR_SISTEMA", message, cause);
    }
}

