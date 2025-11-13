package com.example.sistema_via_mail.util;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para CommandValidator
 */
class CommandValidatorTest {

    private CommandValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CommandValidator();
    }

    @Test
    @DisplayName("Debe validar correctamente comando INSUSU válido")
    void testValidateInsertUsuarioValid() {
        CommandRequest request = createRequest("INS", "USU", 
            Arrays.asList("1234567", "Juan", "Pérez", "CONDUCTOR", "71234567", "juan@mail.com", "foto.jpg"));

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Debe rechazar INSUSU con CI inválido")
    void testValidateInsertUsuarioInvalidCI() {
        CommandRequest request = createRequest("INS", "USU", 
            Arrays.asList("123", "Juan", "Pérez", "CONDUCTOR"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("CI inválido"));
    }

    @Test
    @DisplayName("Debe rechazar INSUSU con email inválido")
    void testValidateInsertUsuarioInvalidEmail() {
        CommandRequest request = createRequest("INS", "USU", 
            Arrays.asList("1234567", "Juan", "Pérez", "CONDUCTOR", "71234567", "email_invalido", "foto.jpg"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("Email inválido"));
    }

    @Test
    @DisplayName("Debe rechazar INSUSU con tipo de usuario inválido")
    void testValidateInsertUsuarioInvalidType() {
        CommandRequest request = createRequest("INS", "USU", 
            Arrays.asList("1234567", "Juan", "Pérez", "ADMIN", "71234567", "juan@mail.com", "foto.jpg"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("Tipo de usuario inválido"));
    }

    @Test
    @DisplayName("Debe rechazar INSUSU con parámetros insuficientes")
    void testValidateInsertUsuarioInsufficientParams() {
        CommandRequest request = createRequest("INS", "USU", 
            Arrays.asList("1234567", "Juan"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("mínimo 4 parámetros"));
    }

    @Test
    @DisplayName("Debe validar correctamente comando INSVEH válido")
    void testValidateInsertVehiculoValid() {
        CommandRequest request = createRequest("INS", "VEH", 
            Arrays.asList("ABC-123", "Toyota", "Hiace", "15", "1234567", "foto.jpg"));

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Debe rechazar INSVEH con capacidad negativa")
    void testValidateInsertVehiculoNegativeCapacity() {
        CommandRequest request = createRequest("INS", "VEH", 
            Arrays.asList("ABC-123", "Toyota", "Hiace", "-5", "1234567", "foto.jpg"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("debe ser un número positivo"));
    }

    @Test
    @DisplayName("Debe validar correctamente comando INSBOL válido")
    void testValidateInsertBoletoValid() {
        CommandRequest request = createRequest("INS", "BOL", 
            Arrays.asList("BOL-001", "1", "ABC-123", "Juan Pérez", "1234567", 
                         "15", "50.00", "2025-11-20", "08:00", "qr.png"));

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Debe rechazar INSBOL con fecha inválida")
    void testValidateInsertBoletoInvalidDate() {
        CommandRequest request = createRequest("INS", "BOL", 
            Arrays.asList("BOL-001", "1", "ABC-123", "Juan Pérez", "1234567", 
                         "15", "50.00", "2025-13-40", "08:00", "qr.png"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("Fecha inválida"));
    }

    @Test
    @DisplayName("Debe rechazar INSBOL con hora inválida")
    void testValidateInsertBoletoInvalidTime() {
        CommandRequest request = createRequest("INS", "BOL", 
            Arrays.asList("BOL-001", "1", "ABC-123", "Juan Pérez", "1234567", 
                         "15", "50.00", "2025-11-20", "25:00", "qr.png"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("Hora inválida"));
    }

    @Test
    @DisplayName("Debe validar correctamente GPS")
    void testValidateGPS() {
        CommandRequest validGPS = createRequest("INS", "RUT", 
            Arrays.asList("Ruta 1", "Origen", "Destino", "100.5", "2.5", "50.00", 
                         "-17.7833,-63.1821", "-17.9167,-64.5167"));

        assertDoesNotThrow(() -> validator.validate(validGPS));

        CommandRequest invalidGPS = createRequest("INS", "RUT", 
            Arrays.asList("Ruta 1", "Origen", "Destino", "100.5", "2.5", "50.00", 
                         "invalid_gps", "-17.9167,-64.5167"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(invalidGPS);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("GPS inválidas"));
    }

    @Test
    @DisplayName("Debe rechazar número de cuota inválido")
    void testValidateInvalidCuotaNumber() {
        CommandRequest request = createRequest("INS", "PAG", 
            Arrays.asList("VEN-001", "3", "25.00", "EFECTIVO"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("Número de cuota inválido"));
    }

    @Test
    @DisplayName("Debe rechazar método de pago inválido")
    void testValidateInvalidPaymentMethod() {
        CommandRequest request = createRequest("INS", "PAG", 
            Arrays.asList("VEN-001", "1", "25.00", "BITCOIN"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("Método de pago inválido"));
    }

    @Test
    @DisplayName("Debe validar correctamente encomienda con peso decimal")
    void testValidateEncomiendaWithDecimalWeight() {
        CommandRequest request = createRequest("INS", "ENC", 
            Arrays.asList("ENC-001", "1", "ABC-123", "Juan", "71111111", 
                         "María", "72222222", "Paquete", "5.5", "25.00", 
                         "2025-11-20", "foto.jpg", "qr.png"));

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Debe rechazar LISUSU sin parámetros")
    void testValidateListUsuarioWithoutParams() {
        CommandRequest request = createRequest("LIS", "USU", Arrays.asList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validator.validate(request);
        });
        
        assertTrue(exception.getAllErrorsAsString().contains("requiere al menos 1 parámetro"));
    }

    // Helper method
    private CommandRequest createRequest(String operation, String entity, List<String> params) {
        CommandRequest request = new CommandRequest(operation, entity);
        request.setParameters(params);
        return request;
    }
}

