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
            // Manejar comando especial HELP
            if ("HLP".equals(request.getOperation()) && "SYS".equals(request.getEntity())) {
                return generateHelpResponse(request);
            }
            
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
    
    /**
     * Genera la respuesta de ayuda con todos los comandos disponibles
     * @param request Comando HELP
     * @return Respuesta con la guía de comandos
     */
    private CommandResponse generateHelpResponse(CommandRequest request) {
        CommandResponse response = CommandResponse.success("HELP", "Guía de Comandos - Sistema Trans Comarapa");
        
        StringBuilder help = new StringBuilder();
        help.append("=".repeat(70)).append("\n");
        help.append("           SISTEMA TRANS COMARAPA - GUÍA DE COMANDOS\n");
        help.append("=".repeat(70)).append("\n\n");
        
        help.append("FORMATO: <OPERACION><ENTIDAD>[<parametros>]\n");
        help.append("OPERACIONES: LIS (Listar), INS (Insertar), UPD (Actualizar), DEL (Eliminar)\n\n");
        
        // ========== USUARIOS (USU) ==========
        help.append("─".repeat(70)).append("\n");
        help.append("1. USUARIOS (USU)\n");
        help.append("─".repeat(70)).append("\n");
        help.append("  • LISTAR TODOS:\n");
        help.append("    Asunto: LISUSU[\"*\"]\n\n");
        help.append("  • LISTAR POR ID:\n");
        help.append("    Asunto: LISUSU[1]\n\n");
        help.append("  • INSERTAR:\n");
        help.append("    Asunto: INSUSU[\"Juan\",\"Perez\",\"12345678\",\"Conductor\",\"555-1234\",\"juan@mail.com\"]\n\n");
        help.append("  • ACTUALIZAR:\n");
        help.append("    Asunto: UPDUSU[1,\"Juan Carlos\",\"Perez Lopez\",\"87654321\"]\n\n");
        help.append("  • ELIMINAR:\n");
        help.append("    Asunto: DELUSU[1]\n\n");
        
        // ========== VEHÍCULOS (VEH) ==========
        help.append("─".repeat(70)).append("\n");
        help.append("2. VEHÍCULOS (VEH)\n");
        help.append("─".repeat(70)).append("\n");
        help.append("  • LISTAR TODOS:\n");
        help.append("    Asunto: LISVEH[\"*\"]\n\n");
        help.append("  • LISTAR POR ID:\n");
        help.append("    Asunto: LISVEH[1]\n\n");
        help.append("  • INSERTAR:\n");
        help.append("    Asunto: INSVEH[\"ABC-123\",\"Bus\",50,\"Activo\"]\n\n");
        help.append("  • ACTUALIZAR:\n");
        help.append("    Asunto: UPDVEH[1,\"XYZ-789\",\"Minibus\",30]\n\n");
        help.append("  • ELIMINAR:\n");
        help.append("    Asunto: DELVEH[1]\n\n");
        
        // ========== RUTAS (RUT) ==========
        help.append("─".repeat(70)).append("\n");
        help.append("3. RUTAS (RUT)\n");
        help.append("─".repeat(70)).append("\n");
        help.append("  • LISTAR TODAS:\n");
        help.append("    Asunto: LISRUT[\"*\"]\n\n");
        help.append("  • LISTAR POR ID:\n");
        help.append("    Asunto: LISRUT[1]\n\n");
        help.append("  • INSERTAR:\n");
        help.append("    Asunto: INSRUT[\"Santa Cruz\",\"Cochabamba\",450.5,\"08:00\",\"16:00\"]\n\n");
        help.append("  • ACTUALIZAR:\n");
        help.append("    Asunto: UPDRUT[1,\"La Paz\",\"Sucre\",380.0]\n\n");
        help.append("  • ELIMINAR:\n");
        help.append("    Asunto: DELRUT[1]\n\n");
        
        // ========== BOLETOS (BOL) ==========
        help.append("─".repeat(70)).append("\n");
        help.append("4. BOLETOS (BOL)\n");
        help.append("─".repeat(70)).append("\n");
        help.append("  • LISTAR TODOS:\n");
        help.append("    Asunto: LISBOL[\"*\"]\n\n");
        help.append("  • LISTAR POR ID:\n");
        help.append("    Asunto: LISBOL[1]\n\n");
        help.append("  • INSERTAR:\n");
        help.append("    Asunto: INSBOL[1,1,\"2025-11-15\",15,120.50,\"Reservado\"]\n\n");
        help.append("  • ACTUALIZAR:\n");
        help.append("    Asunto: UPDBOL[1,\"Confirmado\",150.00]\n\n");
        help.append("  • ELIMINAR:\n");
        help.append("    Asunto: DELBOL[1]\n\n");
        
        // ========== ENCOMIENDAS (ENC) ==========
        help.append("─".repeat(70)).append("\n");
        help.append("5. ENCOMIENDAS (ENC)\n");
        help.append("─".repeat(70)).append("\n");
        help.append("  • LISTAR TODAS:\n");
        help.append("    Asunto: LISENC[\"*\"]\n\n");
        help.append("  • LISTAR POR ID:\n");
        help.append("    Asunto: LISENC[1]\n\n");
        help.append("  • INSERTAR:\n");
        help.append("    Asunto: INSENC[\"Juan Perez\",\"555-1111\",\"Ana Lopez\",\"555-2222\",1,5.5,\"Documentos\"]\n\n");
        help.append("  • ACTUALIZAR:\n");
        help.append("    Asunto: UPDENC[1,\"Entregado\",8.0]\n\n");
        help.append("  • ELIMINAR:\n");
        help.append("    Asunto: DELENC[1]\n\n");
        
        // ========== VENTAS (VEN) ==========
        help.append("─".repeat(70)).append("\n");
        help.append("6. VENTAS (VEN)\n");
        help.append("─".repeat(70)).append("\n");
        help.append("  • LISTAR TODAS:\n");
        help.append("    Asunto: LISVEN[\"*\"]\n\n");
        help.append("  • LISTAR POR ID:\n");
        help.append("    Asunto: LISVEN[1]\n\n");
        help.append("  • INSERTAR:\n");
        help.append("    Asunto: INSVEN[1,\"2025-11-15\",250.75,\"Efectivo\"]\n\n");
        help.append("  • ACTUALIZAR:\n");
        help.append("    Asunto: UPDVEN[1,300.00,\"Tarjeta\"]\n\n");
        help.append("  • ELIMINAR:\n");
        help.append("    Asunto: DELVEN[1]\n\n");
        
        // ========== PAGOS (PAG) ==========
        help.append("─".repeat(70)).append("\n");
        help.append("7. PAGOS (PAG)\n");
        help.append("─".repeat(70)).append("\n");
        help.append("  • LISTAR TODOS:\n");
        help.append("    Asunto: LISPAG[\"*\"]\n\n");
        help.append("  • LISTAR POR ID:\n");
        help.append("    Asunto: LISPAG[1]\n\n");
        help.append("  • INSERTAR:\n");
        help.append("    Asunto: INSPAG[1,\"2025-11-15\",150.00,\"Transferencia\",\"Confirmado\"]\n\n");
        help.append("  • ACTUALIZAR:\n");
        help.append("    Asunto: UPDPAG[1,\"Procesado\",200.00]\n\n");
        help.append("  • ELIMINAR:\n");
        help.append("    Asunto: DELPAG[1]\n\n");
        
        // ========== REPORTES (REP) ==========
        help.append("─".repeat(70)).append("\n");
        help.append("8. REPORTES (REP)\n");
        help.append("─".repeat(70)).append("\n");
        help.append("  • LISTAR TODOS:\n");
        help.append("    Asunto: LISREP[\"*\"]\n\n");
        help.append("  • LISTAR POR ID:\n");
        help.append("    Asunto: LISREP[1]\n\n");
        help.append("  • INSERTAR:\n");
        help.append("    Asunto: INSREP[\"Ventas Mensuales\",\"2025-11\",\"Administrativo\"]\n\n");
        help.append("  • ACTUALIZAR:\n");
        help.append("    Asunto: UPDREP[1,\"Ventas Anuales\",\"2025\"]\n\n");
        help.append("  • ELIMINAR:\n");
        help.append("    Asunto: DELREP[1]\n\n");
        
        // ========== NOTAS IMPORTANTES ==========
        help.append("=".repeat(70)).append("\n");
        help.append("NOTAS IMPORTANTES:\n");
        help.append("=".repeat(70)).append("\n");
        help.append("  ✓ Solo importa el ASUNTO del correo (el cuerpo se ignora)\n");
        help.append("  ✓ Textos van entre comillas dobles: \"texto\"\n");
        help.append("  ✓ Números SIN comillas: 123 o 45.50\n");
        help.append("  ✓ El asterisco \"*\" lista todos los registros\n");
        help.append("  ✓ Respeta MAYÚSCULAS en operaciones y entidades\n");
        help.append("  ✓ Los parámetros se separan con comas\n\n");
        
        help.append("TIEMPO DE RESPUESTA:\n");
        help.append("  • El sistema revisa correos cada 60 segundos\n");
        help.append("  • Recibirás respuesta en máximo 1-2 minutos\n\n");
        
        help.append("COMANDOS ESPECIALES:\n");
        help.append("  • HELP o AYUDA - Muestra esta guía de comandos\n\n");
        
        help.append("=".repeat(70)).append("\n");
        help.append("Sistema Trans Comarapa - Gestión vía Email\n");
        help.append("Contacto: grupo04sa@tecnoweb.org.bo\n");
        help.append("=".repeat(70)).append("\n");
        
        response.setDetails(help.toString());
        response.addData("Total Comandos Documentados", "32 ejemplos (4 operaciones × 8 entidades)");
        response.addData("Total Operaciones", "4 (LIS, INS, UPD, DEL)");
        response.addData("Total Entidades", "8 (USU, VEH, RUT, BOL, ENC, VEN, PAG, REP)");
        
        logger.info("Generada respuesta HELP para: {}", request.getSenderEmail());
        
        return response;
    }
}

