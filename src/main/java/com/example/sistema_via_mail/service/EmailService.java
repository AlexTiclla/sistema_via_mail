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

/**
 * Servicio de correo electrónico
 * Maneja el envío y recepción de correos electrónicos
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

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
            logger.info("Conectando al servidor POP3: {}:{}", pop3Host, pop3Port);

            // Configurar propiedades POP3
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "pop3");
            props.setProperty("mail.pop3.host", pop3Host);
            props.setProperty("mail.pop3.port", String.valueOf(pop3Port));
            props.setProperty("mail.pop3.ssl.enable", "false");
            props.setProperty("mail.pop3.auth", "true");

            // Crear sesión y conectar
            Session session = Session.getInstance(props);
            store = session.getStore("pop3");
            store.connect(pop3Host, username, password);

            logger.info("Conexión POP3 establecida exitosamente");

            // Abrir carpeta INBOX
            folder = store.getFolder(inboxFolder);
            folder.open(Folder.READ_WRITE);

            // Buscar mensajes no leídos
            Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            logger.info("Mensajes no leídos encontrados: {}", messages.length);

            // Procesar los mensajes (limitar a maxMessagesPerCycle)
            int processCount = Math.min(messages.length, maxMessagesPerCycle);
            for (int i = 0; i < processCount; i++) {
                Message message = messages[i];
                try {
                    EmailMessage emailMessage = extractEmailMessage(message);
                    emailMessages.add(emailMessage);

                    // Marcar como leído si está configurado
                    if (markAsRead) {
                        message.setFlag(Flags.Flag.SEEN, true);
                        logger.debug("Mensaje marcado como leído: {}", emailMessage.getSubject());
                    }
                } catch (Exception e) {
                    logger.error("Error al procesar mensaje {}: {}", i, e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("Error al revisar bandeja de entrada: {}", e.getMessage(), e);
        } finally {
            // Cerrar recursos
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(true);
                }
                if (store != null && store.isConnected()) {
                    store.close();
                }
            } catch (MessagingException e) {
                logger.error("Error al cerrar conexiones: {}", e.getMessage());
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

        // Extraer remitente
        Address[] fromAddresses = message.getFrom();
        if (fromAddresses != null && fromAddresses.length > 0) {
            emailMessage.setFrom(fromAddresses[0].toString());
        }

        // Extraer asunto
        emailMessage.setSubject(message.getSubject() != null ? message.getSubject() : "");

        // Extraer fecha
        emailMessage.setReceivedDate(message.getReceivedDate());

        logger.debug("Mensaje extraído - From: {}, Subject: {}", 
                    emailMessage.getFrom(), emailMessage.getSubject());

        return emailMessage;
    }

    /**
     * Envía un correo de respuesta
     * @param to Destinatario
     * @param subject Asunto
     * @param body Cuerpo del mensaje
     */
    public void sendResponse(String to, String subject, String body) {
        try {
            logger.info("Enviando respuesta a: {}", to);

            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body, "utf-8", "plain");

            mailSender.send(message);

            logger.info("Respuesta enviada exitosamente a: {}", to);

        } catch (Exception e) {
            logger.error("Error al enviar respuesta a {}: {}", to, e.getMessage(), e);
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

