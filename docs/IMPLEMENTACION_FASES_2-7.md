# IMPLEMENTACIÓN FASES 2-7 - SISTEMA VÍA CORREO ELECTRÓNICO
## Trans Comarapa - Sistema de Gestión de Transporte

**Fecha de Implementación:** 13/11/2025  
**Grupo:** Grupo04 SA  
**Estado:** ✅ **COMPLETADO**

---

## 📋 RESUMEN EJECUTIVO

Se han implementado exitosamente las **FASES 2 a 7** del plan de implementación, completando el sistema de gestión de transporte vía correo electrónico. El sistema ahora puede:

- ✅ Recibir y parsear comandos desde correos electrónicos
- ✅ Validar sintaxis y parámetros de comandos
- ✅ Ejecutar operaciones CRUD en todas las entidades
- ✅ Generar reportes y estadísticas
- ✅ Enviar respuestas formateadas automáticamente

---

## 🏗️ ARQUITECTURA IMPLEMENTADA

### Componentes Principales

```
┌─────────────────────────────────────────────────────┐
│              EmailScheduler (cada minuto)            │
└──────────────────┬──────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│              EmailService (POP3/SMTP)                │
│              - checkInbox()                          │
│              - sendResponse()                        │
└──────────────────┬──────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│            CommandParserService                      │
│            - parse(subject, email)                   │
└──────────────────┬──────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│             CommandValidator                         │
│             - validate(CommandRequest)               │
└──────────────────┬──────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│          CommandExecutorService                      │
│          - execute(CommandRequest)                   │
└──────────────────┬──────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
┌───────────────┐    ┌───────────────┐
│ Entity        │    │ Response      │
│ Services      │───▶│ Formatter     │
│ (8 servicios) │    │               │
└───────────────┘    └───────┬───────┘
                             │
                             ▼
                  ┌────────────────────┐
                  │  EmailService      │
                  │  sendResponse()    │
                  └────────────────────┘
```

---

## 📦 FASE 2: CORE DEL SISTEMA

### 1. DTOs Implementados

#### `CommandRequest.java`
- Representa un comando parseado del asunto del correo
- Propiedades: `operation`, `entity`, `parameters`, `senderEmail`
- Métodos útiles: `isListCommand()`, `isInsertCommand()`, etc.

#### `CommandResponse.java`
- Representa la respuesta de un comando ejecutado
- Estados: `EXITOSO`, `ERROR`, `ADVERTENCIA`
- Contiene: `command`, `status`, `message`, `details`, `data`, `timestamp`

### 2. Excepciones Personalizadas

#### `CommandException.java`
- Tipos de error: `ERROR_SINTAXIS`, `ERROR_VALIDACION`, `ERROR_NEGOCIO`, `ERROR_BASE_DATOS`, `ERROR_SISTEMA`
- Métodos factory: `syntaxError()`, `validationError()`, `businessError()`, etc.

#### `ValidationException.java`
- Maneja múltiples errores de validación
- Método: `getAllErrorsAsString()`

### 3. Servicios Core

#### `CommandParserService.java`
- Parsea comandos con formato: `<OPERACION><ENTIDAD>[<parametros>]`
- Ejemplo: `INSUSU["1234567","Juan","Pérez","CONDUCTOR"]`
- Operaciones válidas: `LIS`, `INS`, `UPD`, `DEL`, `REP`
- Entidades válidas: `USU`, `VEH`, `RUT`, `BOL`, `ENC`, `VEN`, `PAG`, `REP`

#### `CommandValidator.java`
- Valida sintaxis y parámetros según entidad y operación
- Validaciones específicas:
  - Emails, teléfonos, CIs
  - Fechas, horas, GPS
  - Números positivos, decimales
  - Estados, tipos de usuario, etc.

#### `CommandExecutorService.java`
- Orquestador principal
- Delega a servicios específicos según la entidad
- Maneja excepciones y genera respuestas

#### `ResponseFormatter.java` (Util)
- Formatea respuestas en texto plano para emails
- Métodos: `format()`, `formatError()`, `formatList()`

---

## 📊 FASE 3: USUARIOS Y VEHÍCULOS (CU1-CU2)

### UsuarioService.java
**Operaciones implementadas:**

#### LISUSU - Listar usuarios
```
LISUSU["*"]           # Todos los usuarios
LISUSU["1234567"]     # Por CI
LISUSU["CONDUCTOR"]   # Por tipo
```

#### INSUSU - Insertar usuario
```
INSUSU["1234567","Juan","Pérez","CONDUCTOR","71234567","juan@mail.com","foto.jpg"]
```

#### UPDUSU - Actualizar usuario
```
UPDUSU["1234567","Juan Carlos","Pérez López","CONDUCTOR","71234567","juan@mail.com","foto.jpg","ACTIVO"]
```

#### DELUSU - Eliminar usuario (desactivar)
```
DELUSU["1234567"]
```

### VehiculoService.java
**Operaciones implementadas:**

#### LISVEH - Listar vehículos
```
LISVEH["*"]         # Todos
LISVEH["ABC-123"]   # Por placa
```

#### INSVEH - Insertar vehículo
```
INSVEH["ABC-123","Toyota","Hiace","15","1234567","foto.jpg"]
```

#### UPDVEH - Actualizar vehículo
```
UPDVEH["ABC-123","Toyota","Hiace","15","1234567","foto.jpg","DISPONIBLE"]
```

#### DELVEH - Eliminar vehículo
```
DELVEH["ABC-123"]
```

---

## 🚌 FASE 4: RUTAS Y BOLETOS (CU3-CU4)

### RutaService.java
**Operaciones implementadas:**

#### LISRUT - Listar rutas
```
LISRUT["*"]   # Todas
LISRUT["1"]   # Por ID
```

#### INSRUT - Insertar ruta
```
INSRUT["Santa Cruz - Comarapa","Santa Cruz","Comarapa","150.5","3.5","50.00","-17.7833,-63.1821","-17.9167,-64.5167"]
```

#### UPDRUT - Actualizar ruta
```
UPDRUT["1","Santa Cruz - Comarapa","Santa Cruz","Comarapa","150.5","3.5","60.00","-17.7833,-63.1821","-17.9167,-64.5167","ACTIVO"]
```

#### DELRUT - Eliminar ruta
```
DELRUT["1"]
```

### BoletoService.java
**Operaciones implementadas:**

#### LISBOL - Listar boletos
```
LISBOL["*"]           # Todos
LISBOL["BOL-001"]     # Por código
LISBOL["2025-11-15"]  # Por fecha
```

#### INSBOL - Insertar boleto
```
INSBOL["BOL-001","1","ABC-123","Juan Pérez","1234567","15","50.00","2025-11-15","08:00","qr_001.png"]
```
**Validaciones:**
- Verifica disponibilidad de asiento
- Valida capacidad del vehículo
- Previene doble reserva

#### UPDBOL - Actualizar estado
```
UPDBOL["BOL-001","USADO"]
```

#### DELBOL - Eliminar (cancelar) boleto
```
DELBOL["BOL-001"]
```

---

## 📦 FASE 5: ENCOMIENDAS (CU5)

### EncomiendaService.java
**Operaciones implementadas:**

#### LISENC - Listar encomiendas
```
LISENC["*"]            # Todas
LISENC["ENC-001"]      # Por código
LISENC["EN_TRANSITO"]  # Por estado
```

#### INSENC - Insertar encomienda
```
INSENC["ENC-001","1","ABC-123","Juan Pérez","71111111","María López","72222222","Paquete de ropa","5.5","25.00","2025-11-15","foto.jpg","qr_001.png"]
```

#### UPDENC - Actualizar estado y GPS
```
UPDENC["ENC-001","EN_TRANSITO","-17.8,-63.5"]
```
**Características:**
- Actualiza estado (REGISTRADA, EN_TRANSITO, ENTREGADA)
- Actualiza coordenadas GPS para tracking

#### DELENC - Eliminar encomienda
```
DELENC["ENC-001"]
```

---

## 💰 FASE 6: VENTAS Y PAGOS (CU6-CU7)

### VentaService.java
**Operaciones implementadas:**

#### LISVEN - Listar ventas
```
LISVEN["*"]        # Todas
LISVEN["VEN-001"]  # Por código
```

#### INSVEN - Registrar venta
```
# Venta de boleto
INSVEN["VEN-001","BOLETO","BOL-001","1234567","50.00"]

# Venta de encomienda
INSVEN["VEN-002","ENCOMIENDA","ENC-001","1234567","25.00"]
```
**Validaciones:**
- Verifica existencia de boleto/encomienda
- Valida que el vendedor sea SECRETARIA o PROPIETARIO

### PagoService.java
**Operaciones implementadas:**

#### LISPAG - Listar pagos
```
LISPAG["VEN-001"]
```

#### INSPAG - Registrar pago (cuota)
```
INSPAG["VEN-001","1","25.00","EFECTIVO"]
```
**Características:**
- Sistema de cuotas (1 o 2)
- Validación de montos
- Cálculo automático de saldo pendiente
- Métodos: EFECTIVO, TRANSFERENCIA, TARJETA

#### UPDPAG - Actualizar estado de pago
```
UPDPAG["VEN-001","1","PAGADO"]
```

---

## 📊 FASE 7: REPORTES Y ESTADÍSTICAS (CU8)

### ReporteService.java
**Reportes implementados:**

#### 1. Reporte de Ventas por Día
```
REPREP["VENTAS_DIA","2025-11-15"]
```
**Muestra:**
- Total de ventas
- Ventas de boletos
- Ventas de encomiendas
- Total general
- Detalle de cada venta

#### 2. Reporte de Boletos Vendidos
```
REPREP["BOLETOS_VENDIDOS","2025-11-15"]
REPREP["BOLETOS_VENDIDOS","*"]
```
**Muestra:**
- Total de boletos
- Total de ingresos
- Boletos por ruta

#### 3. Reporte de Encomiendas en Tránsito
```
REPREP["ENCOMIENDAS_TRANSITO","*"]
```
**Muestra:**
- Encomiendas con estado EN_TRANSITO
- Ubicación GPS actual
- Información de remitente y destinatario

#### 4. Reporte de Pagos Pendientes
```
REPREP["PAGOS_PENDIENTES","*"]
```
**Muestra:**
- Pagos con estado PENDIENTE
- Monto total pendiente
- Detalle por venta

#### 5. Reporte de Rutas Populares
```
REPREP["RUTAS_POPULARES","2025-11"]
REPREP["RUTAS_POPULARES","*"]
```
**Muestra:**
- Rutas ordenadas por cantidad de boletos vendidos
- Ingresos por ruta
- Estadísticas del período

---

## 🔄 INTEGRACIÓN COMPLETA

### EmailScheduler (Actualizado)

El scheduler ahora:
1. **Revisa** bandeja de entrada cada minuto (configurable)
2. **Parsea** el asunto del correo para extraer el comando
3. **Valida** la sintaxis y parámetros
4. **Ejecuta** el comando delegando al servicio correspondiente
5. **Formatea** la respuesta en texto plano legible
6. **Envía** la respuesta automáticamente al remitente
7. **Maneja errores** y envía respuestas de error formateadas

### Flujo Completo de Procesamiento

```
1. Usuario envía email con comando
   ↓
2. EmailScheduler detecta nuevo email (cada 60 seg)
   ↓
3. EmailService.checkInbox() obtiene emails no leídos
   ↓
4. CommandParserService parsea el asunto
   ↓
5. CommandValidator valida sintaxis y parámetros
   ↓
6. CommandExecutorService delega a servicio específico
   ↓
7. Servicio ejecuta operación en BD
   ↓
8. ResponseFormatter genera respuesta legible
   ↓
9. EmailService.sendResponse() envía respuesta
   ↓
10. Usuario recibe respuesta con resultado
```

---

## 📝 EJEMPLOS DE USO COMPLETO

### Ejemplo 1: Registrar un conductor

**Email enviado:**
```
De: admin@transcomarapa.com
Para: grupo04sa@tecnoweb.org.bo
Asunto: INSUSU["7891234","Carlos","Ramírez","CONDUCTOR","71555666","carlos@mail.com","foto_carlos.jpg"]
```

**Respuesta recibida:**
```
===========================================
SISTEMA TRANS COMARAPA - RESPUESTA
===========================================

COMANDO: INSUSU
ESTADO: EXITOSO
FECHA/HORA: 2025-11-13 10:30:00

MENSAJE: Usuario registrado correctamente

-------------------------------------------
DATOS:
-------------------------------------------
- ID: 1
- CI: 7891234
- Nombre Completo: Carlos Ramírez
- Tipo: CONDUCTOR
- Estado: ACTIVO
- Fecha Registro: 2025-11-13T10:30:00

===========================================
Trans Comarapa - Sistema de Gestión
Grupo04 SA
===========================================
```

### Ejemplo 2: Vender un boleto

**Email enviado:**
```
Asunto: INSBOL["BOL-100","1","ABC-123","María López","5544332","10","50.00","2025-11-20","14:00","qr_bol100.png"]
```

**Respuesta:**
```
===========================================
SISTEMA TRANS COMARAPA - RESPUESTA
===========================================

COMANDO: INSBOL
ESTADO: EXITOSO
FECHA/HORA: 2025-11-13 11:15:00

MENSAJE: Boleto registrado correctamente

-------------------------------------------
DATOS:
-------------------------------------------
- ID: 15
- Código: BOL-100
- Pasajero: María López (CI: 5544332)
- Ruta: Santa Cruz - Comarapa
- Vehículo: ABC-123
- Asiento: 10
- Fecha Viaje: 2025-11-20 14:00
- Precio: Bs. 50.00
- Estado: VENDIDO

===========================================
Trans Comarapa - Sistema de Gestión
Grupo04 SA
===========================================
```

### Ejemplo 3: Error de validación

**Email enviado:**
```
Asunto: INSUSU["123","Juan","Pérez","INVALIDO"]
```

**Respuesta:**
```
===========================================
SISTEMA TRANS COMARAPA - RESPUESTA
===========================================

COMANDO: INSUSU
ESTADO: ERROR
FECHA/HORA: 2025-11-13 11:20:00

MENSAJE: Error de validación

DETALLE:
CI inválido: 123. Debe contener 5-20 dígitos; Tipo de usuario inválido: INVALIDO. Tipos válidos: [PROPIETARIO, SECRETARIA, CONDUCTOR]

===========================================
Trans Comarapa - Sistema de Gestión
Grupo04 SA
===========================================
```

---

## ⚙️ CONFIGURACIÓN

### application.properties

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://www.tecnoweb.org.bo:5432/db_grupo04sa
spring.datasource.username=grupo04sa
spring.datasource.password=grup004grup004*

# Correo
spring.mail.host=mail.tecnoweb.org.bo
spring.mail.username=grupo04sa@tecnoweb.org.bo
spring.mail.password=grup004grup004*

# SMTP (envío) - Puerto 25 SIN SSL
spring.mail.port=25
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=false

# POP3 (recepción) - Puerto 110 SIN SSL
spring.mail.properties.mail.pop3.host=mail.tecnoweb.org.bo
spring.mail.properties.mail.pop3.port=110
spring.mail.properties.mail.pop3.ssl.enable=false

# Scheduler - Revisar cada 60 segundos
scheduler.email.check.interval=60000
```

---

## 📊 ESTADÍSTICAS DE IMPLEMENTACIÓN

### Archivos Creados/Modificados

| Tipo | Cantidad | Archivos |
|------|----------|----------|
| **DTOs** | 2 | CommandRequest, CommandResponse |
| **Excepciones** | 2 | CommandException, ValidationException |
| **Servicios** | 10 | Parser, Validator, Executor, Email + 8 entidades |
| **Utilidades** | 2 | CommandValidator, ResponseFormatter |
| **Repositorios** | 1 | BoletoRepository (actualizado) |
| **Schedulers** | 1 | EmailScheduler (actualizado) |
| **Total** | **18** | archivos modificados/creados |

### Líneas de Código

- **Total aproximado:** ~3,500 líneas de código Java
- **Cobertura:** 8 entidades, 4 operaciones CRUD c/u, 5 tipos de reportes
- **Comandos soportados:** ~35 comandos diferentes

---

## ✅ VALIDACIONES IMPLEMENTADAS

### Por Entidad

#### Usuarios (USU)
- ✅ CI válido (5-20 dígitos)
- ✅ Email válido
- ✅ Teléfono válido (7-20 dígitos)
- ✅ Tipo de usuario (PROPIETARIO, SECRETARIA, CONDUCTOR)

#### Vehículos (VEH)
- ✅ Placa no duplicada
- ✅ Capacidad positiva
- ✅ Propietario existente y válido

#### Rutas (RUT)
- ✅ Distancia y duración positivas
- ✅ Precio base válido
- ✅ Coordenadas GPS formato "lat,long"

#### Boletos (BOL)
- ✅ Código único
- ✅ Disponibilidad de asiento
- ✅ Capacidad del vehículo
- ✅ Fecha y hora válidas
- ✅ Prevención de doble reserva

#### Encomiendas (ENC)
- ✅ Código único
- ✅ Teléfonos válidos
- ✅ Peso y precio positivos
- ✅ Estados válidos

#### Ventas (VEN)
- ✅ Referencia existente (boleto/encomienda)
- ✅ Vendedor autorizado (SECRETARIA/PROPIETARIO)
- ✅ Tipo de venta válido

#### Pagos (PAG)
- ✅ Número de cuota (1 o 2)
- ✅ Monto no excede total
- ✅ Pago no duplicado
- ✅ Método de pago válido

---

## 🚀 PRÓXIMOS PASOS (FASE 8)

1. **Pruebas exhaustivas de todos los comandos**
2. **Optimización de consultas a BD**
3. **Implementación de generación de códigos QR**
4. **Pruebas de carga**
5. **Documentación de usuario final**
6. **Preparación de presentación**

---

## 📞 SOPORTE

Para consultas sobre la implementación:
- **Grupo:** Grupo04 SA
- **Email:** grupo04sa@tecnoweb.org.bo
- **Base de Datos:** db_grupo04sa

---

**Estado del Proyecto:** ✅ **FASE 2-7 COMPLETADAS**  
**Listo para:** Pruebas de integración y Fase 8  
**Fecha:** 13/11/2025

