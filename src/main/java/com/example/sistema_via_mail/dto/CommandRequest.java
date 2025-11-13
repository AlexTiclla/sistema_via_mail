package com.example.sistema_via_mail.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para representar una solicitud de comando
 * Ejemplo: INSUSU["1234567","Juan","Pérez","CONDUCTOR","71234567","juan@mail.com","foto.jpg"]
 */
public class CommandRequest {
    
    private String operation;  // LIS, INS, UPD, DEL, REP
    private String entity;     // USU, VEH, RUT, BOL, ENC, VEN, PAG, REP
    private List<String> parameters;
    private String originalSubject;
    private String senderEmail;
    
    public CommandRequest() {
        this.parameters = new ArrayList<>();
    }
    
    public CommandRequest(String operation, String entity) {
        this();
        this.operation = operation;
        this.entity = entity;
    }
    
    // Getters y Setters
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getEntity() {
        return entity;
    }
    
    public void setEntity(String entity) {
        this.entity = entity;
    }
    
    public List<String> getParameters() {
        return parameters;
    }
    
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
    
    public String getOriginalSubject() {
        return originalSubject;
    }
    
    public void setOriginalSubject(String originalSubject) {
        this.originalSubject = originalSubject;
    }
    
    public String getSenderEmail() {
        return senderEmail;
    }
    
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
    
    /**
     * Obtiene el comando completo (operacion + entidad)
     * Ejemplo: INSUSU
     */
    public String getFullCommand() {
        return operation + entity;
    }
    
    /**
     * Verifica si el comando es de tipo Listar
     */
    public boolean isListCommand() {
        return "LIS".equals(operation);
    }
    
    /**
     * Verifica si el comando es de tipo Insertar
     */
    public boolean isInsertCommand() {
        return "INS".equals(operation);
    }
    
    /**
     * Verifica si el comando es de tipo Actualizar
     */
    public boolean isUpdateCommand() {
        return "UPD".equals(operation);
    }
    
    /**
     * Verifica si el comando es de tipo Eliminar
     */
    public boolean isDeleteCommand() {
        return "DEL".equals(operation);
    }
    
    /**
     * Verifica si el comando es de tipo Reporte
     */
    public boolean isReportCommand() {
        return "REP".equals(operation);
    }
    
    @Override
    public String toString() {
        return "CommandRequest{" +
                "operation='" + operation + '\'' +
                ", entity='" + entity + '\'' +
                ", parameters=" + parameters +
                ", senderEmail='" + senderEmail + '\'' +
                '}';
    }
}

