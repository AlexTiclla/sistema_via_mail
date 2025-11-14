package com.example.sistema_via_mail.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.FlagTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.HashSet;
import java.util.Set;

/**
 * Servicio de correo electrónico
 * Maneja el envío y recepción de correos electrónicos
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    
    // Caché de mensajes procesados (evita procesar el mismo mensaje múltiples veces)
    private final Set<String> processedMessageIds = new HashSet<>();

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.pop3.host}")
    private String pop3Host;

    @Value("${spring.mail.properties.mail.pop3.port}")
    private int pop3Port;

    @Value("${email.inbox.folder:INBOX}")
    private String inboxFolder;

    @Value("${email.max.messages.per.cycle:10}")
    private int maxMessagesPerCycle;

    @Value("${email.mark.as.read:true}")
    private boolean markAsRead;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Revisa la bandeja de entrada y obtiene los correos no leídos
     * @return Lista de mensajes no leídos
     */
    public List<EmailMessage> checkInbox() {
        List<EmailMessage> emailMessages = new ArrayList<>();
        Store store = null;
        Folder folder = null;

        try {
            logger.info("=== INICIANDO CONEXIÓN POP3 ===");
            logger.info("Servidor: {}:{}", pop3Host, pop3Port);
            logger.info("Usuario: {}", username);
            logger.info("Carpeta a revisar: {}", inboxFolder);
            logger.debug("Password configurada: {}", (password != null && !password.isEmpty()) ? "SI (longitud: " + password.length() + ")" : "NO");

            // Configurar propiedades POP3
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "pop3");
            props.setProperty("mail.pop3.host", pop3Host);
            props.setProperty("mail.pop3.port", String.valueOf(pop3Port));
            props.setProperty("mail.pop3.ssl.enable", "false");
            props.setProperty("mail.pop3.auth", "true");
            props.setProperty("mail.pop3.auth.mechanisms", "PLAIN LOGIN");
            props.setProperty("mail.pop3.starttls.enable", "false");
            props.setProperty("mail.pop3.starttls.required", "false");
            props.setProperty("mail.pop3.connectiontimeout", "10000");
            props.setProperty("mail.pop3.timeout", "10000");
            props.setProperty("mail.debug", "true"); // Habilitar debug de JavaMail

            logger.debug("Propiedades POP3 configuradas:");
            props.forEach((key, value) -> logger.debug("  {} = {}", key, value));

            // Crear sesión y conectar
            logger.info("Creando sesión JavaMail...");
            Session session = Session.getInstance(props);
            session.setDebug(true); // Habilitar debug completo
            
            logger.info("Obteniendo store POP3...");
            store = session.getStore("pop3");
            
            logger.info("Intentando conectar con credenciales...");
            logger.debug("Host: {}, User: {}", pop3Host, username);
            logger.info("Intentando conectar con credenciales...");
            logger.debug("Host: {}, User: {}", pop3Host, username);
            store.connect(pop3Host, username, password);

            logger.info("✓ Conexión POP3 establecida exitosamente");
            logger.info("Store conectado: {}", store.isConnected());

            // Abrir carpeta INBOX
            logger.info("Abriendo carpeta: {}", inboxFolder);
            folder = store.getFolder(inboxFolder);
            
            if (!folder.exists()) {
                logger.error("✗ La carpeta '{}' no existe en el servidor", inboxFolder);
                logger.info("Carpetas disponibles:");
                Folder[] folders = store.getDefaultFolder().list();
                for (Folder f : folders) {
                    logger.info("  - {}", f.getName());
                }
                return emailMessages;
            }
            
            folder.open(Folder.READ_WRITE);
            logger.info("✓ Carpeta '{}' abierta correctamente", inboxFolder);
            logger.info("Total de mensajes en carpeta: {}", folder.getMessageCount());

            // Buscar mensajes no leídos
            logger.info("Buscando mensajes no leídos...");
            Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            logger.info("✓ Mensajes no leídos encontrados: {}", messages.length);

            // Procesar los mensajes (limitar a maxMessagesPerCycle)
            int processCount = Math.min(messages.length, maxMessagesPerCycle);
            logger.info("Procesando {} de {} mensajes (límite: {})", 
                       processCount, messages.length, maxMessagesPerCycle);
            
            for (int i = 0; i < processCount; i++) {
                Message message = messages[i];
                try {
                    // Verificar que la carpeta sigue abierta
                    if (!folder.isOpen()) {
                        logger.error("✗ La carpeta se cerró inesperadamente. Abortando procesamiento.");
                        break;
                    }
                    
                    // Obtener ID único del mensaje
                    String messageId = getMessageId(message);
                    
                    // Verificar si ya fue procesado
                    if (processedMessageIds.contains(messageId)) {
                        logger.info("⏭️ Mensaje {} ya procesado anteriormente (ID: {}). Saltando...", 
                                   i + 1, messageId);
                        continue;
                    }
                    
                    logger.debug("Procesando mensaje {}/{}... (ID: {})", i + 1, processCount, messageId);
                    EmailMessage emailMessage = extractEmailMessage(message);
                    
                    // Agregar a la caché de procesados INMEDIATAMENTE
                    processedMessageIds.add(messageId);
                    logger.info("✓ Mensaje {} agregado a caché (ID: {})", i + 1, messageId);
                    
                    // Marcar como leído INMEDIATAMENTE antes de añadir a la lista
                    if (markAsRead) {
                        try {
                            message.setFlag(Flags.Flag.SEEN, true);
                            logger.info("✓ Mensaje {} marcado como leído", i + 1);
                        } catch (MessagingException me) {
                            logger.warn("⚠️ No se pudo marcar como leído (usando caché): {}", me.getMessage());
                        }
                    }
                    
                    emailMessages.add(emailMessage);
                } catch (IllegalStateException e) {
                    logger.error("✗ Error de estado al procesar mensaje {}/{}: {}", 
                               i + 1, processCount, e.getMessage());
                    logger.warn("La carpeta puede haberse cerrado. Intentando continuar...");
                    // No continuar si la carpeta está cerrada
                    if (e.getMessage().contains("Folder is not Open")) {
                        logger.error("Abortando procesamiento de mensajes restantes.");
                        break;
                    }
                } catch (Exception e) {
                    logger.error("✗ Error al procesar mensaje {}/{}: {} - {}", 
                               i + 1, processCount, e.getClass().getSimpleName(), e.getMessage());
                    logger.debug("Stack trace del error:", e);
                }
            }
            
            logger.info("✓ Procesamiento completado: {} mensajes procesados", emailMessages.size());

        } catch (AuthenticationFailedException e) {
            logger.error("=== ERROR DE AUTENTICACIÓN POP3 ===");
            logger.error("Servidor: {}:{}", pop3Host, pop3Port);
            logger.error("Usuario: {}", username);
            logger.error("Mensaje de error: {}", e.getMessage());
            logger.error("Causa: Credenciales incorrectas o acceso POP3 no habilitado");
            logger.error("Soluciones posibles:");
            logger.error("  1. Verifica las credenciales en application.properties");
            logger.error("  2. Accede al webmail y habilita el acceso POP3");
            logger.error("  3. Contacta al administrador del servidor");
            logger.debug("Stack trace completo:", e);
            
        } catch (MessagingException e) {
            logger.error("=== ERROR DE MENSAJERÍA ===");
            logger.error("Tipo: {}", e.getClass().getSimpleName());
            logger.error("Mensaje: {}", e.getMessage());
            
            if (e.getCause() != null) {
                logger.error("Causa raíz: {} - {}", 
                           e.getCause().getClass().getSimpleName(), 
                           e.getCause().getMessage());
            }
            
            logger.debug("Stack trace completo:", e);
            
        } catch (Exception e) {
            logger.error("=== ERROR GENERAL ===");
            logger.error("Tipo: {}", e.getClass().getSimpleName());
            logger.error("Mensaje: {}", e.getMessage());
            logger.error("Stack trace:", e);
        } finally {
            // Cerrar recursos
            logger.debug("Cerrando conexiones POP3...");
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(true);
                    logger.debug("✓ Carpeta cerrada");
                }
                if (store != null && store.isConnected()) {
                    store.close();
                    logger.debug("✓ Store cerrado");
                }
                logger.info("=== CONEXIÓN POP3 FINALIZADA ===");
            } catch (MessagingException e) {
                logger.error("✗ Error al cerrar conexiones: {}", e.getMessage());
                logger.debug("Stack trace:", e);
            }
        }

        return emailMessages;
    }

    /**
     * Extrae la información relevante de un mensaje de correo
     * @param message Mensaje a procesar
     * @return Objeto EmailMessage con la información extraída
     */
    private EmailMessage extractEmailMessage(Message message) throws MessagingException {
        EmailMessage emailMessage = new EmailMessage();

        try {
            // Extraer remitente
            Address[] fromAddresses = message.getFrom();
            if (fromAddresses != null && fromAddresses.length > 0) {
                emailMessage.setFrom(fromAddresses[0].toString());
            } else {
                emailMessage.setFrom("Desconocido");
            }
        } catch (Exception e) {
            logger.warn("No se pudo extraer el remitente: {}", e.getMessage());
            emailMessage.setFrom("Error al obtener remitente");
        }

        try {
            // Extraer asunto
            emailMessage.setSubject(message.getSubject() != null ? message.getSubject() : "Sin asunto");
        } catch (Exception e) {
            logger.warn("No se pudo extraer el asunto: {}", e.getMessage());
            emailMessage.setSubject("Error al obtener asunto");
        }

        try {
            // Extraer fecha
            emailMessage.setReceivedDate(message.getReceivedDate());
        } catch (Exception e) {
            logger.warn("No se pudo extraer la fecha: {}", e.getMessage());
            emailMessage.setReceivedDate(null);
        }

        logger.debug("Mensaje extraído - From: {}, Subject: {}", 
                    emailMessage.getFrom(), emailMessage.getSubject());

        return emailMessage;
    }

    /**
     * Obtiene un ID único para el mensaje
     * Usa combinación de From + Subject + Date para crear un identificador único
     * @param message Mensaje del que extraer el ID
     * @return ID único del mensaje
     */
    private String getMessageId(Message message) {
        try {
            // Intentar obtener el Message-ID del header
            String[] messageIds = message.getHeader("Message-ID");
            if (messageIds != null && messageIds.length > 0 && messageIds[0] != null) {
                return messageIds[0];
            }
            
            // Si no hay Message-ID, crear uno basado en From + Subject + Date
            String from = message.getFrom() != null && message.getFrom().length > 0 
                         ? message.getFrom()[0].toString() : "unknown";
            String subject = message.getSubject() != null ? message.getSubject() : "no-subject";
            String date = message.getReceivedDate() != null 
                         ? String.valueOf(message.getReceivedDate().getTime()) : "no-date";
            
            return String.format("%s|%s|%s", from, subject, date);
            
        } catch (Exception e) {
            logger.warn("No se pudo obtener ID del mensaje, generando uno temporal: {}", e.getMessage());
            return "temp-" + System.currentTimeMillis();
        }
    }

    /**
     * Limpia la caché de mensajes procesados
     * Útil para liberar memoria después de mucho tiempo
     */
    public void clearProcessedCache() {
        int size = processedMessageIds.size();
        processedMessageIds.clear();
        logger.info("Caché de mensajes procesados limpiada ({} entradas eliminadas)", size);
    }
    
    /**
     * Obtiene el tamaño actual de la caché
     * @return Número de mensajes en la caché
     */
    public int getProcessedCacheSize() {
        return processedMessageIds.size();
    }

    /**
     * Envía un correo de respuesta
     * @param to Destinatario
     * @param subject Asunto
     * @param body Cuerpo del mensaje
     */
    public void sendResponse(String to, String subject, String body) {
        try {
            logger.info("=== ENVIANDO RESPUESTA SMTP ===");
            logger.info("Destinatario: {}", to);
            logger.info("Asunto: {}", subject);
            
            // Asegurar que el remitente tenga el dominio completo
            String fromAddress = username.contains("@") ? username : username + "@tecnoweb.org.bo";
            logger.debug("Remitente: {}", fromAddress);
            logger.debug("Longitud del cuerpo: {} caracteres", body.length());

            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body, "utf-8", "plain");

            logger.debug("Mensaje SMTP creado, enviando...");
            mailSender.send(message);

            logger.info("✓ Respuesta enviada exitosamente a: {}", to);
            logger.info("=== ENVÍO SMTP COMPLETADO ===");

        } catch (MessagingException e) {
            logger.error("=== ERROR AL ENVIAR CORREO (MessagingException) ===");
            logger.error("Destinatario: {}", to);
            logger.error("Asunto: {}", subject);
            logger.error("Mensaje de error: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
            }
            logger.debug("Stack trace completo:", e);
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
            
        } catch (Exception e) {
            logger.error("=== ERROR AL ENVIAR CORREO (General) ===");
            logger.error("Tipo de error: {}", e.getClass().getSimpleName());
            logger.error("Destinatario: {}", to);
            logger.error("Mensaje de error: {}", e.getMessage());
            logger.error("Stack trace:", e);
        }
    }

    /**
     * Clase interna para representar un mensaje de correo electrónico
     */
    public static class EmailMessage {
        private String from;
        private String subject;
        private java.util.Date receivedDate;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public java.util.Date getReceivedDate() {
            return receivedDate;
        }

        public void setReceivedDate(java.util.Date receivedDate) {
            this.receivedDate = receivedDate;
        }

        @Override
        public String toString() {
            return "EmailMessage{" +
                    "from='" + from + '\'' +
                    ", subject='" + subject + '\'' +
                    ", receivedDate=" + receivedDate +
                    '}';
        }
    }
}

