package com.example.sistema_via_mail.util;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utilidad para validar comandos y sus parámetros
 */
@Component
public class CommandValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandValidator.class);
    
    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{7,20}$");
    private static final Pattern CI_PATTERN = Pattern.compile("^[0-9]{5,20}$");
    private static final Pattern GPS_PATTERN = Pattern.compile("^-?[0-9]{1,3}\\.[0-9]+,-?[0-9]{1,3}\\.[0-9]+$");
    
    // Formateadores de fecha/hora
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * Valida un comando completo según la entidad y operación
     * @param request Comando a validar
     * @throws ValidationException Si la validación falla
     */
    public void validate(CommandRequest request) throws ValidationException {
        logger.debug("Validando comando: {}", request.getFullCommand());
        
        List<String> errors = new ArrayList<>();
        
        String entity = request.getEntity();
        String operation = request.getOperation();
        List<String> params = request.getParameters();
        
        try {
            switch (entity) {
                case "USU" -> validateUsuarioCommand(operation, params, errors);
                case "VEH" -> validateVehiculoCommand(operation, params, errors);
                case "RUT" -> validateRutaCommand(operation, params, errors);
                case "BOL" -> validateBoletoCommand(operation, params, errors);
                case "ENC" -> validateEncomiendaCommand(operation, params, errors);
                case "VEN" -> validateVentaCommand(operation, params, errors);
                case "PAG" -> validatePagoCommand(operation, params, errors);
                case "REP" -> validateReporteCommand(operation, params, errors);
                default -> errors.add("Entidad desconocida: " + entity);
            }
        } catch (Exception e) {
            errors.add("Error durante validación: " + e.getMessage());
        }
        
        if (!errors.isEmpty()) {
            logger.error("Errores de validación: {}", errors);
            throw new ValidationException("Validación fallida", errors);
        }
        
        logger.debug("Comando validado exitosamente");
    }
    
    // ============================================
    // Validaciones por Entidad
    // ============================================
    
    private void validateUsuarioCommand(String op, List<String> params, List<String> errors) {
        switch (op) {
            case "LIS" -> {
                if (params.isEmpty()) {
                    errors.add("LISUSU requiere al menos 1 parámetro");
                }
            }
            case "INS" -> {
                if (params.size() < 4) {
                    errors.add("INSUSU requiere mínimo 4 parámetros: [ci,nombre,apellido,tipo_usuario]");
                } else {
                    validateCI(params.get(0), errors);
                    validateNotEmpty(params.get(1), "nombre", errors);
                    validateNotEmpty(params.get(2), "apellido", errors);
                    validateTipoUsuario(params.get(3), errors);
                    if (params.size() > 4) validatePhone(params.get(4), errors);
                    if (params.size() > 5) validateEmail(params.get(5), errors);
                }
            }
            case "UPD" -> {
                if (params.size() < 5) {
                    errors.add("UPDUSU requiere mínimo 5 parámetros: [ci,nombre,apellido,tipo_usuario,telefono]");
                } else {
                    validateCI(params.get(0), errors);
                    validateNotEmpty(params.get(1), "nombre", errors);
                    validateNotEmpty(params.get(2), "apellido", errors);
                    validateTipoUsuario(params.get(3), errors);
                    if (params.size() > 4 && !params.get(4).isEmpty()) validatePhone(params.get(4), errors);
                    if (params.size() > 5 && !params.get(5).isEmpty()) validateEmail(params.get(5), errors);
                }
            }
            case "DEL" -> {
                if (params.isEmpty()) {
                    errors.add("DELUSU requiere el CI del usuario");
                } else {
                    validateCI(params.get(0), errors);
                }
            }
        }
    }
    
    private void validateVehiculoCommand(String op, List<String> params, List<String> errors) {
        switch (op) {
            case "LIS" -> {
                if (params.isEmpty()) {
                    errors.add("LISVEH requiere al menos 1 parámetro");
                }
            }
            case "INS" -> {
                if (params.size() < 5) {
                    errors.add("INSVEH requiere mínimo 5 parámetros: [placa,marca,modelo,capacidad,ci_propietario]");
                } else {
                    validateNotEmpty(params.get(0), "placa", errors);
                    validateNotEmpty(params.get(1), "marca", errors);
                    validateNotEmpty(params.get(2), "modelo", errors);
                    validatePositiveInteger(params.get(3), "capacidad", errors);
                    validateCI(params.get(4), errors);
                }
            }
            case "UPD" -> {
                if (params.size() < 5) {
                    errors.add("UPDVEH requiere mínimo 5 parámetros");
                } else {
                    validateNotEmpty(params.get(0), "placa", errors);
                    validatePositiveInteger(params.get(3), "capacidad", errors);
                }
            }
            case "DEL" -> {
                if (params.isEmpty()) {
                    errors.add("DELVEH requiere la placa del vehículo");
                } else {
                    validateNotEmpty(params.get(0), "placa", errors);
                }
            }
        }
    }
    
    private void validateRutaCommand(String op, List<String> params, List<String> errors) {
        switch (op) {
            case "LIS" -> {
                if (params.isEmpty()) {
                    errors.add("LISRUT requiere al menos 1 parámetro");
                }
            }
            case "INS" -> {
                if (params.size() < 6) {
                    errors.add("INSRUT requiere mínimo 6 parámetros: [nombre,origen,destino,distancia,duracion,precio]");
                } else {
                    validateNotEmpty(params.get(0), "nombre", errors);
                    validateNotEmpty(params.get(1), "origen", errors);
                    validateNotEmpty(params.get(2), "destino", errors);
                    validatePositiveDecimal(params.get(3), "distancia", errors);
                    validatePositiveDecimal(params.get(4), "duracion", errors);
                    validatePositiveDecimal(params.get(5), "precio", errors);
                    if (params.size() > 6 && !params.get(6).isEmpty()) validateGPS(params.get(6), errors);
                    if (params.size() > 7 && !params.get(7).isEmpty()) validateGPS(params.get(7), errors);
                }
            }
            case "UPD" -> {
                if (params.size() < 7) {
                    errors.add("UPDRUT requiere mínimo 7 parámetros");
                } else {
                    validatePositiveInteger(params.get(0), "id_ruta", errors);
                    validatePositiveDecimal(params.get(5), "precio", errors);
                }
            }
            case "DEL" -> {
                if (params.isEmpty()) {
                    errors.add("DELRUT requiere el ID de la ruta");
                } else {
                    validatePositiveInteger(params.get(0), "id_ruta", errors);
                }
            }
        }
    }
    
    private void validateBoletoCommand(String op, List<String> params, List<String> errors) {
        switch (op) {
            case "LIS" -> {
                if (params.isEmpty()) {
                    errors.add("LISBOL requiere al menos 1 parámetro");
                }
            }
            case "INS" -> {
                if (params.size() < 10) {
                    errors.add("INSBOL requiere 10 parámetros");
                } else {
                    validateNotEmpty(params.get(0), "codigo_boleto", errors);
                    validatePositiveInteger(params.get(1), "id_ruta", errors);
                    validateNotEmpty(params.get(2), "placa_vehiculo", errors);
                    validateNotEmpty(params.get(3), "nombre_pasajero", errors);
                    validateCI(params.get(4), errors);
                    validatePositiveInteger(params.get(5), "asiento", errors);
                    validatePositiveDecimal(params.get(6), "precio", errors);
                    validateDate(params.get(7), errors);
                    validateTime(params.get(8), errors);
                }
            }
            case "UPD" -> {
                if (params.size() < 2) {
                    errors.add("UPDBOL requiere 2 parámetros: [codigo,estado]");
                } else {
                    validateNotEmpty(params.get(0), "codigo_boleto", errors);
                    validateEstadoBoleto(params.get(1), errors);
                }
            }
            case "DEL" -> {
                if (params.isEmpty()) {
                    errors.add("DELBOL requiere el código del boleto");
                } else {
                    validateNotEmpty(params.get(0), "codigo_boleto", errors);
                }
            }
        }
    }
    
    private void validateEncomiendaCommand(String op, List<String> params, List<String> errors) {
        switch (op) {
            case "LIS" -> {
                if (params.isEmpty()) {
                    errors.add("LISENC requiere al menos 1 parámetro");
                }
            }
            case "INS" -> {
                if (params.size() < 11) {
                    errors.add("INSENC requiere mínimo 11 parámetros");
                } else {
                    validateNotEmpty(params.get(0), "codigo_encomienda", errors);
                    validatePositiveInteger(params.get(1), "id_ruta", errors);
                    validateNotEmpty(params.get(2), "placa_vehiculo", errors);
                    validateNotEmpty(params.get(3), "nombre_remitente", errors);
                    validatePhone(params.get(4), errors);
                    validateNotEmpty(params.get(5), "nombre_destinatario", errors);
                    validatePhone(params.get(6), errors);
                    validatePositiveDecimal(params.get(8), "peso", errors);
                    validatePositiveDecimal(params.get(9), "precio", errors);
                    validateDate(params.get(10), errors);
                }
            }
            case "UPD" -> {
                if (params.size() < 2) {
                    errors.add("UPDENC requiere mínimo 2 parámetros: [codigo,estado]");
                } else {
                    validateNotEmpty(params.get(0), "codigo_encomienda", errors);
                    validateEstadoEncomienda(params.get(1), errors);
                    if (params.size() > 2 && !params.get(2).isEmpty()) validateGPS(params.get(2), errors);
                }
            }
            case "DEL" -> {
                if (params.isEmpty()) {
                    errors.add("DELENC requiere el código de la encomienda");
                } else {
                    validateNotEmpty(params.get(0), "codigo_encomienda", errors);
                }
            }
        }
    }
    
    private void validateVentaCommand(String op, List<String> params, List<String> errors) {
        switch (op) {
            case "LIS" -> {
                if (params.isEmpty()) {
                    errors.add("LISVEN requiere al menos 1 parámetro");
                }
            }
            case "INS" -> {
                if (params.size() < 5) {
                    errors.add("INSVEN requiere 5 parámetros: [codigo,tipo,codigo_ref,ci_vendedor,monto]");
                } else {
                    validateNotEmpty(params.get(0), "codigo_venta", errors);
                    validateTipoVenta(params.get(1), errors);
                    validateNotEmpty(params.get(2), "codigo_referencia", errors);
                    validateCI(params.get(3), errors);
                    validatePositiveDecimal(params.get(4), "monto", errors);
                }
            }
        }
    }
    
    private void validatePagoCommand(String op, List<String> params, List<String> errors) {
        switch (op) {
            case "LIS" -> {
                if (params.isEmpty()) {
                    errors.add("LISPAG requiere el código de venta");
                } else {
                    validateNotEmpty(params.get(0), "codigo_venta", errors);
                }
            }
            case "INS" -> {
                if (params.size() < 4) {
                    errors.add("INSPAG requiere 4 parámetros: [codigo_venta,numero_cuota,monto,metodo]");
                } else {
                    validateNotEmpty(params.get(0), "codigo_venta", errors);
                    validateNumeroCuota(params.get(1), errors);
                    validatePositiveDecimal(params.get(2), "monto", errors);
                    validateMetodoPago(params.get(3), errors);
                }
            }
            case "UPD" -> {
                if (params.size() < 3) {
                    errors.add("UPDPAG requiere 3 parámetros: [codigo_venta,numero_cuota,estado]");
                } else {
                    validateNotEmpty(params.get(0), "codigo_venta", errors);
                    validateNumeroCuota(params.get(1), errors);
                    validateEstadoPago(params.get(2), errors);
                }
            }
        }
    }
    
    private void validateReporteCommand(String op, List<String> params, List<String> errors) {
        if (!"REP".equals(op)) {
            errors.add("Operación inválida para reportes. Use REP");
            return;
        }
        
        if (params.size() < 2) {
            errors.add("REPREP requiere 2 parámetros: [tipo_reporte,parametro]");
        } else {
            validateTipoReporte(params.get(0), errors);
        }
    }
    
    // ============================================
    // Validaciones Específicas
    // ============================================
    
    private void validateNotEmpty(String value, String fieldName, List<String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.add("El campo '" + fieldName + "' no puede estar vacío");
        }
    }
    
    private void validateEmail(String email, List<String> errors) {
        if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("Email inválido: " + email);
        }
    }
    
    private void validatePhone(String phone, List<String> errors) {
        if (phone != null && !phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            errors.add("Teléfono inválido: " + phone + ". Debe contener 7-20 dígitos");
        }
    }
    
    private void validateCI(String ci, List<String> errors) {
        if (ci == null || ci.isEmpty() || !CI_PATTERN.matcher(ci).matches()) {
            errors.add("CI inválido: " + ci + ". Debe contener 5-20 dígitos");
        }
    }
    
    private void validateGPS(String gps, List<String> errors) {
        if (gps != null && !gps.isEmpty() && !GPS_PATTERN.matcher(gps).matches()) {
            errors.add("Coordenadas GPS inválidas: " + gps + ". Formato: 'lat,long'");
        }
    }
    
    private void validatePositiveInteger(String value, String fieldName, List<String> errors) {
        try {
            int num = Integer.parseInt(value);
            if (num <= 0) {
                errors.add("El campo '" + fieldName + "' debe ser un número positivo");
            }
        } catch (NumberFormatException e) {
            errors.add("El campo '" + fieldName + "' debe ser un número entero válido");
        }
    }
    
    private void validatePositiveDecimal(String value, String fieldName, List<String> errors) {
        try {
            double num = Double.parseDouble(value);
            if (num <= 0) {
                errors.add("El campo '" + fieldName + "' debe ser un número positivo");
            }
        } catch (NumberFormatException e) {
            errors.add("El campo '" + fieldName + "' debe ser un número decimal válido");
        }
    }
    
    private void validateDate(String date, List<String> errors) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            errors.add("Fecha inválida: " + date + ". Formato esperado: yyyy-MM-dd");
        }
    }
    
    private void validateTime(String time, List<String> errors) {
        try {
            LocalTime.parse(time, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            errors.add("Hora inválida: " + time + ". Formato esperado: HH:mm");
        }
    }
    
    private void validateTipoUsuario(String tipo, List<String> errors) {
        List<String> validTypes = List.of("PROPIETARIO", "SECRETARIA", "CONDUCTOR");
        if (!validTypes.contains(tipo.toUpperCase())) {
            errors.add("Tipo de usuario inválido: " + tipo + ". Tipos válidos: " + validTypes);
        }
    }
    
    private void validateEstadoBoleto(String estado, List<String> errors) {
        List<String> validStates = List.of("VENDIDO", "USADO", "CANCELADO");
        if (!validStates.contains(estado.toUpperCase())) {
            errors.add("Estado de boleto inválido: " + estado + ". Estados válidos: " + validStates);
        }
    }
    
    private void validateEstadoEncomienda(String estado, List<String> errors) {
        List<String> validStates = List.of("REGISTRADA", "EN_TRANSITO", "ENTREGADA");
        if (!validStates.contains(estado.toUpperCase())) {
            errors.add("Estado de encomienda inválido: " + estado + ". Estados válidos: " + validStates);
        }
    }
    
    private void validateTipoVenta(String tipo, List<String> errors) {
        List<String> validTypes = List.of("BOLETO", "ENCOMIENDA");
        if (!validTypes.contains(tipo.toUpperCase())) {
            errors.add("Tipo de venta inválido: " + tipo + ". Tipos válidos: " + validTypes);
        }
    }
    
    private void validateNumeroCuota(String numero, List<String> errors) {
        try {
            int num = Integer.parseInt(numero);
            if (num < 1 || num > 2) {
                errors.add("Número de cuota inválido: " + numero + ". Debe ser 1 o 2");
            }
        } catch (NumberFormatException e) {
            errors.add("Número de cuota debe ser un entero (1 o 2)");
        }
    }
    
    private void validateMetodoPago(String metodo, List<String> errors) {
        List<String> validMethods = List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA");
        if (!validMethods.contains(metodo.toUpperCase())) {
            errors.add("Método de pago inválido: " + metodo + ". Métodos válidos: " + validMethods);
        }
    }
    
    private void validateEstadoPago(String estado, List<String> errors) {
        List<String> validStates = List.of("PENDIENTE", "PAGADO");
        if (!validStates.contains(estado.toUpperCase())) {
            errors.add("Estado de pago inválido: " + estado + ". Estados válidos: " + validStates);
        }
    }
    
    private void validateTipoReporte(String tipo, List<String> errors) {
        List<String> validTypes = List.of(
            "VENTAS_DIA", "BOLETOS_VENDIDOS", "ENCOMIENDAS_TRANSITO",
            "PAGOS_PENDIENTES", "RUTAS_POPULARES"
        );
        if (!validTypes.contains(tipo.toUpperCase())) {
            errors.add("Tipo de reporte inválido: " + tipo + ". Tipos válidos: " + validTypes);
        }
    }
}

