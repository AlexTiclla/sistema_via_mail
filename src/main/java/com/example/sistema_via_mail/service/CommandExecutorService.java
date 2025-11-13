package com.example.sistema_via_mail.service;

import com.example.sistema_via_mail.dto.CommandRequest;
import com.example.sistema_via_mail.dto.CommandResponse;
import com.example.sistema_via_mail.exception.CommandException;
import com.example.sistema_via_mail.exception.ValidationException;
import com.example.sistema_via_mail.util.CommandValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio ejecutor de comandos
 * Orquesta el procesamiento de comandos delegando a los servicios específicos
 */
@Service
public class CommandExecutorService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutorService.class);
    
    private final CommandValidator commandValidator;
    private final UsuarioService usuarioService;
    private final VehiculoService vehiculoService;
    private final RutaService rutaService;
    private final BoletoService boletoService;
    private final EncomiendaService encomiendaService;
    private final VentaService ventaService;
    private final PagoService pagoService;
    private final ReporteService reporteService;
    
    public CommandExecutorService(CommandValidator commandValidator,
                                  UsuarioService usuarioService,
                                  VehiculoService vehiculoService,
                                  RutaService rutaService,
                                  BoletoService boletoService,
                                  EncomiendaService encomiendaService,
                                  VentaService ventaService,
                                  PagoService pagoService,
                                  ReporteService reporteService) {
        this.commandValidator = commandValidator;
        this.usuarioService = usuarioService;
        this.vehiculoService = vehiculoService;
        this.rutaService = rutaService;
        this.boletoService = boletoService;
        this.encomiendaService = encomiendaService;
        this.ventaService = ventaService;
        this.pagoService = pagoService;
        this.reporteService = reporteService;
    }
    
    /**
     * Ejecuta un comando validado
     * @param request Comando a ejecutar
     * @return Respuesta del comando
     * @throws CommandException Si ocurre un error durante la ejecución
     */
    public CommandResponse execute(CommandRequest request) {
        logger.info("Ejecutando comando: {}", request.getFullCommand());
        
        try {
            // Validar comando
            commandValidator.validate(request);
            
            // Delegar a servicio específico según la entidad
            CommandResponse response = switch (request.getEntity()) {
                case "USU" -> usuarioService.processCommand(request);
                case "VEH" -> vehiculoService.processCommand(request);
                case "RUT" -> rutaService.processCommand(request);
                case "BOL" -> boletoService.processCommand(request);
                case "ENC" -> encomiendaService.processCommand(request);
                case "VEN" -> ventaService.processCommand(request);
                case "PAG" -> pagoService.processCommand(request);
                case "REP" -> reporteService.processCommand(request);
                default -> CommandResponse.error(request.getFullCommand(), 
                    "Entidad no soportada: " + request.getEntity());
            };
            
            logger.info("Comando ejecutado exitosamente: {} - {}", 
                request.getFullCommand(), response.getStatus());
            
            return response;
            
        } catch (ValidationException e) {
            logger.error("Error de validación: {}", e.getMessage());
            CommandResponse response = CommandResponse.error(request.getFullCommand(), 
                "Error de validación");
            response.setDetails(e.getAllErrorsAsString());
            return response;
            
        } catch (CommandException e) {
            logger.error("Error de comando: {}", e.getMessage());
            CommandResponse response = CommandResponse.error(request.getFullCommand(), 
                e.getMessage());
            if (e.getErrorType() != null) {
                response.addData("Tipo de Error", e.getErrorType());
            }
            return response;
            
        } catch (Exception e) {
            logger.error("Error inesperado al ejecutar comando: {}", e.getMessage(), e);
            CommandResponse response = CommandResponse.error(request.getFullCommand(), 
                "Error inesperado del sistema");
            response.setDetails(e.getMessage());
            response.addData("Tipo de Error", "ERROR_SISTEMA");
            return response;
        }
    }
}

