package com.example.sistema_via_mail.integration;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.service.CommandExecutorService;
import com.example.sistema_via_mail.service.CommandParserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para el flujo completo de comandos
 * Estos tests requieren una base de datos de prueba
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommandIntegrationTest {

    @Autowired
    private CommandParserService parserService;

    @Autowired
    private CommandExecutorService executorService;

    @Test
    @DisplayName("Flujo completo: Crear y listar usuario")
    void testCompleteFlowCreateAndListUser() {
        // 1. Crear usuario
        String createCommand = "INSUSU[\"9999999\",\"Test\",\"Usuario\",\"CONDUCTOR\",\"71234567\",\"test@mail.com\",\"foto.jpg\"]";
        CommandRequest createRequest = parserService.parse(createCommand, "admin@test.com");
        CommandResponse createResponse = executorService.execute(createRequest);

        assertEquals(CommandResponse.ResponseStatus.EXITOSO, createResponse.getStatus());
        assertTrue(createResponse.getMessage().contains("registrado correctamente"));

        // 2. Listar usuarios (debería incluir el creado)
        String listCommand = "LISUSU[\"9999999\"]";
        CommandRequest listRequest = parserService.parse(listCommand, "admin@test.com");
        CommandResponse listResponse = executorService.execute(listRequest);

        assertEquals(CommandResponse.ResponseStatus.EXITOSO, listResponse.getStatus());
    }

    @Test
    @DisplayName("Flujo completo: Intentar crear usuario duplicado")
    void testCompleteFlowDuplicateUser() {
        // 1. Crear usuario primera vez
        String createCommand = "INSUSU[\"8888888\",\"Test\",\"Usuario\",\"CONDUCTOR\",\"71234567\",\"test@mail.com\",\"foto.jpg\"]";
        CommandRequest createRequest1 = parserService.parse(createCommand, "admin@test.com");
        CommandResponse createResponse1 = executorService.execute(createRequest1);

        assertEquals(CommandResponse.ResponseStatus.EXITOSO, createResponse1.getStatus());

        // 2. Intentar crear usuario con mismo CI (debería fallar)
        CommandRequest createRequest2 = parserService.parse(createCommand, "admin@test.com");
        CommandResponse createResponse2 = executorService.execute(createRequest2);

        assertEquals(CommandResponse.ResponseStatus.ERROR, createResponse2.getStatus());
        assertTrue(createResponse2.getMessage().contains("Ya existe"));
    }

    @Test
    @DisplayName("Flujo completo: Actualizar y eliminar usuario")
    void testCompleteFlowUpdateAndDeleteUser() {
        // 1. Crear usuario
        String createCommand = "INSUSU[\"7777777\",\"Original\",\"Usuario\",\"CONDUCTOR\",\"71234567\",\"original@mail.com\",\"foto.jpg\"]";
        CommandRequest createRequest = parserService.parse(createCommand, "admin@test.com");
        executorService.execute(createRequest);

        // 2. Actualizar usuario
        String updateCommand = "UPDUSU[\"7777777\",\"Actualizado\",\"Usuario Modificado\",\"CONDUCTOR\",\"71234567\",\"updated@mail.com\",\"foto_nueva.jpg\",\"ACTIVO\"]";
        CommandRequest updateRequest = parserService.parse(updateCommand, "admin@test.com");
        CommandResponse updateResponse = executorService.execute(updateRequest);

        assertEquals(CommandResponse.ResponseStatus.EXITOSO, updateResponse.getStatus());
        assertTrue(updateResponse.getMessage().contains("actualizado correctamente"));

        // 3. Eliminar usuario
        String deleteCommand = "DELUSU[\"7777777\"]";
        CommandRequest deleteRequest = parserService.parse(deleteCommand, "admin@test.com");
        CommandResponse deleteResponse = executorService.execute(deleteRequest);

        assertEquals(CommandResponse.ResponseStatus.EXITOSO, deleteResponse.getStatus());
    }

    @Test
    @DisplayName("Flujo de validación: Comando con sintaxis incorrecta")
    void testValidationFlowInvalidSyntax() {
        // Comando sin corchetes
        assertThrows(Exception.class, () -> {
            parserService.parse("INSUSU1234567", "test@test.com");
        });
    }

    @Test
    @DisplayName("Flujo de validación: Parámetros inválidos")
    void testValidationFlowInvalidParameters() {
        // CI muy corto
        String command = "INSUSU[\"123\",\"Test\",\"Usuario\",\"CONDUCTOR\"]";
        CommandRequest request = parserService.parse(command, "admin@test.com");
        CommandResponse response = executorService.execute(request);

        assertEquals(CommandResponse.ResponseStatus.ERROR, response.getStatus());
        assertTrue(response.getDetails().contains("CI inválido"));
    }

    @Test
    @DisplayName("Flujo completo: Listar con criterio inexistente")
    void testCompleteFlowListNonExistent() {
        String command = "LISUSU[\"0000000\"]";
        CommandRequest request = parserService.parse(command, "admin@test.com");
        CommandResponse response = executorService.execute(request);

        assertEquals(CommandResponse.ResponseStatus.EXITOSO, response.getStatus());
        // Debería retornar 0 resultados
        assertEquals(0, response.getData().get("Total"));
    }
}

