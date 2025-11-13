package com.example.sistema_via_mail.util;

import com.example.sistema_via_mail.dto.CommandResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para ResponseFormatter
 */
class ResponseFormatterTest {

    private ResponseFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new ResponseFormatter();
    }

    @Test
    @DisplayName("Debe formatear correctamente una respuesta exitosa")
    void testFormatSuccessResponse() {
        CommandResponse response = CommandResponse.success("INSUSU", "Usuario registrado correctamente");
        response.addData("ID", 1);
        response.addData("CI", "1234567");
        response.addData("Nombre", "Juan Pérez");

        String formatted = formatter.format(response);

        assertTrue(formatted.contains("SISTEMA TRANS COMARAPA"));
        assertTrue(formatted.contains("COMANDO: INSUSU"));
        assertTrue(formatted.contains("ESTADO: EXITOSO"));
        assertTrue(formatted.contains("Usuario registrado correctamente"));
        assertTrue(formatted.contains("ID: 1"));
        assertTrue(formatted.contains("CI: 1234567"));
        assertTrue(formatted.contains("Nombre: Juan Pérez"));
        assertTrue(formatted.contains("Grupo04 SA"));
    }

    @Test
    @DisplayName("Debe formatear correctamente una respuesta de error")
    void testFormatErrorResponse() {
        CommandResponse response = CommandResponse.error("INSUSU", "Error de validación");
        response.setDetails("CI inválido: 123. Debe contener 5-20 dígitos");

        String formatted = formatter.format(response);

        assertTrue(formatted.contains("ESTADO: ERROR"));
        assertTrue(formatted.contains("Error de validación"));
        assertTrue(formatted.contains("DETALLE:"));
        assertTrue(formatted.contains("CI inválido"));
    }

    @Test
    @DisplayName("Debe formatear correctamente respuesta sin datos adicionales")
    void testFormatResponseWithoutData() {
        CommandResponse response = CommandResponse.success("DELUSU", "Usuario eliminado correctamente");

        String formatted = formatter.format(response);

        assertTrue(formatted.contains("Usuario eliminado correctamente"));
        assertFalse(formatted.contains("DATOS:"));
    }

    @Test
    @DisplayName("Debe formatear correctamente una lista de resultados")
    void testFormatList() {
        CommandResponse response = CommandResponse.success("LISUSU", "Consulta ejecutada correctamente");
        List<String> items = Arrays.asList(
            "Usuario 1: Juan Pérez - CONDUCTOR",
            "Usuario 2: María López - SECRETARIA",
            "Usuario 3: Pedro Díaz - PROPIETARIO"
        );

        String formatted = formatter.formatList(response, items);

        assertTrue(formatted.contains("REGISTROS ENCONTRADOS: 3"));
        assertTrue(formatted.contains("1. Usuario 1"));
        assertTrue(formatted.contains("2. Usuario 2"));
        assertTrue(formatted.contains("3. Usuario 3"));
    }

    @Test
    @DisplayName("Debe formatear correctamente lista vacía")
    void testFormatEmptyList() {
        CommandResponse response = CommandResponse.success("LISUSU", "Consulta ejecutada correctamente");
        List<String> items = Arrays.asList();

        String formatted = formatter.formatList(response, items);

        assertTrue(formatted.contains("REGISTROS ENCONTRADOS: 0"));
        assertTrue(formatted.contains("No se encontraron resultados"));
    }

    @Test
    @DisplayName("Debe formatear correctamente error con tipo")
    void testFormatErrorWithType() {
        String formatted = formatter.formatError(
            "INSUSU",
            "ERROR_VALIDACION",
            "Error al procesar comando",
            "Parámetros inválidos"
        );

        assertTrue(formatted.contains("ERROR_VALIDACION"));
        assertTrue(formatted.contains("Error al procesar comando"));
        assertTrue(formatted.contains("Parámetros inválidos"));
    }

    @Test
    @DisplayName("Debe incluir timestamp en formato correcto")
    void testFormatWithTimestamp() {
        CommandResponse response = CommandResponse.success("INSUSU", "Operación exitosa");
        String formatted = formatter.format(response);

        assertTrue(formatted.contains("FECHA/HORA:"));
        // Verifica que tiene formato yyyy-MM-dd HH:mm:ss
        assertTrue(formatted.matches("(?s).*FECHA/HORA: \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*"));
    }

    @Test
    @DisplayName("Debe formatear correctamente datos con valores nulos")
    void testFormatWithNullValues() {
        CommandResponse response = CommandResponse.success("INSUSU", "Operación exitosa");
        response.addData("Campo1", "Valor1");
        response.addData("Campo2", null);
        response.addData("Campo3", "Valor3");

        String formatted = formatter.format(response);

        assertTrue(formatted.contains("Campo1: Valor1"));
        assertTrue(formatted.contains("Campo2: N/A"));
        assertTrue(formatted.contains("Campo3: Valor3"));
    }

    @Test
    @DisplayName("Debe incluir separadores visuales")
    void testFormatWithSeparators() {
        CommandResponse response = CommandResponse.success("INSUSU", "Operación exitosa");
        String formatted = formatter.format(response);

        assertTrue(formatted.contains("==========================================="));
        assertTrue(formatted.contains("-------------------------------------------"));
    }
}

