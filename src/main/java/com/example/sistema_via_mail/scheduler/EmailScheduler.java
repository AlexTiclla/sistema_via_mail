package com.example.sistema_via_mail.scheduler;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.service.CommandExecutorService;
import com.example.sistema_via_mail.service.CommandParserService;
import com.example.sistema_via_mail.service.EmailService;
import com.example.sistema_via_mail.util.ResponseFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Programador de tareas para revisión de correos electrónicos
 * Revisa periódicamente la bandeja de entrada para procesar comandos
 */
@Component
public class EmailScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EmailScheduler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EmailService emailService;
    private final CommandParserService commandParserService;
    private final CommandExecutorService commandExecutorService;
    private final ResponseFormatter responseFormatter;

    @Value("${scheduler.email.check.interval:60000}")
    private long checkInterval;

    private long executionCount = 0;

    public EmailScheduler(EmailService emailService,
                         CommandParserService commandParserService,
                         CommandExecutorService commandExecutorService,
                         ResponseFormatter responseFormatter) {
        this.emailService = emailService;
        this.commandParserService = commandParserService;
        this.commandExecutorService = commandExecutorService;
        this.responseFormatter = responseFormatter;
    }

    /**
     * Tarea programada que revisa la bandeja de entrada periódicamente
     * Se ejecuta según el intervalo configurado en application.properties
     */
    @Scheduled(fixedDelayString = "${scheduler.email.check.interval:60000}")
    public void checkEmailInbox() {
        executionCount++;
        String currentTime = LocalDateTime.now().format(formatter);
        
        logger.info("===============================================");
        logger.info("Iniciando revisión de correos #{}", executionCount);
        logger.info("Fecha/Hora: {}", currentTime);
        logger.info("Mensajes procesados en caché: {}", emailService.getProcessedCacheSize());
        logger.info("===============================================");

        try {
            // Revisar bandeja de entrada
            List<EmailService.EmailMessage> messages = emailService.checkInbox();

            if (messages.isEmpty()) {
                logger.info("No se encontraron nuevos correos para procesar");
            } else {
                logger.info("Correos nuevos encontrados: {}", messages.size());
                
                // Procesar cada mensaje
                for (EmailService.EmailMessage message : messages) {
                    processEmailMessage(message);
                }
            }

        } catch (Exception e) {
            logger.error("Error durante la revisión de correos: {}", e.getMessage(), e);
        }

        logger.info("Revisión de correos finalizada. Próxima revisión en {} segundos", 
                   checkInterval / 1000);
        logger.info("===============================================\n");
    }

    /**
     * Procesa un mensaje de correo electrónico individual
     * @param message Mensaje a procesar
     */
    private void processEmailMessage(EmailService.EmailMessage message) {
        String senderEmail = extractEmail(message.getFrom());
        String subject = message.getSubject();
        
        // Limpiar el asunto si tiene prefijos como "Asunto: " o "Subject: "
        if (subject != null) {
            subject = subject.replaceFirst("^(Asunto:|Subject:)\\s*", "").trim();
        }
        
        try {
            logger.info("Procesando mensaje:");
            logger.info("  - De: {}", message.getFrom());
            logger.info("  - Asunto original: {}", message.getSubject());
            logger.info("  - Asunto limpio: {}", subject);
            logger.info("  - Fecha: {}", message.getReceivedDate());

            // Parsear comando
            CommandRequest commandRequest = commandParserService.parse(subject, senderEmail);
            logger.info("Comando parseado: {}", commandRequest);
            
            // Ejecutar comando
            CommandResponse commandResponse = commandExecutorService.execute(commandRequest);
            logger.info("Comando ejecutado: {}", commandResponse.getStatus());
            
            // Formatear respuesta
            String responseBody = responseFormatter.format(commandResponse);
            
            // Enviar respuesta
            if (senderEmail != null) {
                try {
                    emailService.sendResponse(
                        senderEmail,
                        "RE: " + subject + " - " + commandResponse.getStatus(),
                        responseBody
                    );
                    logger.info("✓ Respuesta enviada correctamente a: {}", senderEmail);
                } catch (Exception emailError) {
                    logger.error("✗ ERROR: No se pudo enviar la respuesta a: {}", senderEmail);
                    logger.error("Causa: {}", emailError.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("Error al procesar mensaje: {}", e.getMessage(), e);
            
            // Enviar respuesta de error
            if (senderEmail != null) {
                try {
                    String errorResponse = responseFormatter.formatError(
                        subject,
                        "ERROR_PROCESAMIENTO",
                        "Error al procesar el comando",
                        e.getMessage()
                    );
                    
                    emailService.sendResponse(
                        senderEmail,
                        "RE: " + subject + " - ERROR",
                        errorResponse
                    );
                    logger.info("Respuesta de error enviada a: {}", senderEmail);
                } catch (Exception ex) {
                    logger.error("Error al enviar respuesta de error: {}", ex.getMessage());
                }
            }
        }
    }

    /**
     * Extrae la dirección de correo del campo From
     * @param fromField Campo From del correo
     * @return Dirección de correo electrónico
     */
    private String extractEmail(String fromField) {
        if (fromField == null || fromField.isEmpty()) {
            return null;
        }

        // Extraer email del formato "Nombre <email@domain.com>"
        if (fromField.contains("<") && fromField.contains(">")) {
            int start = fromField.indexOf("<") + 1;
            int end = fromField.indexOf(">");
            return fromField.substring(start, end);
        }

        // Si no tiene el formato anterior, asumir que es solo el email
        return fromField.trim();
    }

    /**
     * Obtiene el número de ejecuciones realizadas
     * @return Contador de ejecuciones
     */
    public long getExecutionCount() {
        return executionCount;
    }
}

