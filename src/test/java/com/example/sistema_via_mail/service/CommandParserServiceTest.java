package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.exception.CommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para CommandParserService
 */
class CommandParserServiceTest {

    private CommandParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new CommandParserService();
    }

    @Test
    @DisplayName("Debe parsear correctamente un comando INSUSU")
    void testParseInsertUsuario() {
        String subject = "INSUSU[\"1234567\",\"Juan\",\"Pérez\",\"CONDUCTOR\"]";
        String email = "test@example.com";

        CommandRequest result = parserService.parse(subject, email);

        assertEquals("INS", result.getOperation());
        assertEquals("USU", result.getEntity());
        assertEquals(4, result.getParameters().size());
        assertEquals("1234567", result.getParameters().get(0));
        assertEquals("Juan", result.getParameters().get(1));
        assertEquals("Pérez", result.getParameters().get(2));
        assertEquals("CONDUCTOR", result.getParameters().get(3));
        assertEquals(email, result.getSenderEmail());
    }

    @Test
    @DisplayName("Debe parsear correctamente un comando LISUSU")
    void testParseListUsuario() {
        String subject = "LISUSU[\"*\"]";
        String email = "test@example.com";

        CommandRequest result = parserService.parse(subject, email);

        assertEquals("LIS", result.getOperation());
        assertEquals("USU", result.getEntity());
        assertEquals(1, result.getParameters().size());
        assertEquals("*", result.getParameters().get(0));
    }

    @Test
    @DisplayName("Debe parsear correctamente un comando de boleto con múltiples parámetros")
    void testParseBoletoComplejo() {
        String subject = "INSBOL[\"BOL-001\",\"1\",\"ABC-123\",\"Juan Pérez\",\"1234567\",\"15\",\"50.00\",\"2025-11-15\",\"08:00\",\"qr_001.png\"]";
        String email = "test@example.com";

        CommandRequest result = parserService.parse(subject, email);

        assertEquals("INS", result.getOperation());
        assertEquals("BOL", result.getEntity());
        assertEquals(10, result.getParameters().size());
        assertEquals("BOL-001", result.getParameters().get(0));
        assertEquals("50.00", result.getParameters().get(6));
    }

    @Test
    @DisplayName("Debe lanzar excepción con asunto vacío")
    void testParseEmptySubject() {
        assertThrows(CommandException.class, () -> {
            parserService.parse("", "test@example.com");
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción con formato inválido")
    void testParseInvalidFormat() {
        String subject = "INSERTARUSUARIO[\"1234567\"]";
        
        assertThrows(CommandException.class, () -> {
            parserService.parse(subject, "test@example.com");
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción con operación inválida")
    void testParseInvalidOperation() {
        String subject = "XXUSU[\"1234567\"]";
        
        assertThrows(CommandException.class, () -> {
            parserService.parse(subject, "test@example.com");
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción con entidad inválida")
    void testParseInvalidEntity() {
        String subject = "INSXXX[\"1234567\"]";
        
        assertThrows(CommandException.class, () -> {
            parserService.parse(subject, "test@example.com");
        });
    }

    @Test
    @DisplayName("Debe parsear correctamente parámetros con comillas internas")
    void testParseParametersWithQuotes() {
        String subject = "INSUSU[\"1234567\",\"Juan José\",\"Pérez López\",\"CONDUCTOR\"]";
        
        CommandRequest result = parserService.parse(subject, "test@example.com");
        
        assertEquals(4, result.getParameters().size());
        assertEquals("Juan José", result.getParameters().get(1));
        assertEquals("Pérez López", result.getParameters().get(2));
    }

    @Test
    @DisplayName("Debe validar formato de comando correctamente")
    void testIsValidCommandFormat() {
        assertTrue(parserService.isValidCommandFormat("INSUSU[\"123\"]"));
        assertTrue(parserService.isValidCommandFormat("LISBOL[\"*\"]"));
        assertFalse(parserService.isValidCommandFormat("INVALID"));
        assertFalse(parserService.isValidCommandFormat(""));
    }

    @Test
    @DisplayName("Debe parsear comando REPREP correctamente")
    void testParseReporte() {
        String subject = "REPREP[\"VENTAS_DIA\",\"2025-11-15\"]";
        
        CommandRequest result = parserService.parse(subject, "test@example.com");
        
        assertEquals("REP", result.getOperation());
        assertEquals("REP", result.getEntity());
        assertEquals(2, result.getParameters().size());
    }

    @Test
    @DisplayName("Debe identificar correctamente tipos de comandos")
    void testCommandTypeIdentification() {
        CommandRequest listCmd = parserService.parse("LISUSU[\"*\"]", "test@test.com");
        CommandRequest insCmd = parserService.parse("INSUSU[\"123\",\"A\",\"B\",\"C\"]", "test@test.com");
        CommandRequest updCmd = parserService.parse("UPDUSU[\"123\",\"A\",\"B\",\"C\",\"D\"]", "test@test.com");
        CommandRequest delCmd = parserService.parse("DELUSU[\"123\"]", "test@test.com");
        CommandRequest repCmd = parserService.parse("REPREP[\"VENTAS_DIA\",\"2025-11-15\"]", "test@test.com");

        assertTrue(listCmd.isListCommand());
        assertTrue(insCmd.isInsertCommand());
        assertTrue(updCmd.isUpdateCommand());
        assertTrue(delCmd.isDeleteCommand());
        assertTrue(repCmd.isReportCommand());
    }
}

