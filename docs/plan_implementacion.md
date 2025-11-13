# PLAN DE IMPLEMENTACIÓN - SISTEMA VÍA CORREO ELECTRÓNICO
## Trans Comarapa - Sistema de Gestión de Transporte

---

## 1. INFORMACIÓN DEL PROYECTO

**Empresa:** Trans Comarapa  
**Grupo:** Grupo04 SA  
**Correo del Sistema:** grupo04sa@tecnoweb.org.bo  
**Base de Datos:** db_grupo04sa  
**Tecnología:** Spring Boot 3.5.7 + PostgreSQL + JavaMail

---

## 2. DISEÑO DE BASE DE DATOS

### 2.1. Modelo Entidad-Relación Simplificado

#### Tabla: `usuario`
Gestiona propietarios, secretarias y conductores (CU1)

```sql
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    ci VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL, -- PROPIETARIO, SECRETARIA, CONDUCTOR
    telefono VARCHAR(20),
    email VARCHAR(100),
    foto_url VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO', -- ACTIVO, INACTIVO
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Tabla: `vehiculo`
Gestiona los vehículos de la empresa (CU2)

```sql
CREATE TABLE vehiculo (
    id_vehiculo SERIAL PRIMARY KEY,
    placa VARCHAR(20) UNIQUE NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    capacidad_pasajeros INTEGER NOT NULL,
    id_propietario INTEGER REFERENCES usuario(id_usuario),
    foto_url VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'DISPONIBLE', -- DISPONIBLE, EN_RUTA, MANTENIMIENTO
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Tabla: `ruta`
Gestiona las rutas disponibles (CU3)

```sql
CREATE TABLE ruta (
    id_ruta SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    origen VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    distancia_km DECIMAL(10,2),
    duracion_horas DECIMAL(5,2),
    precio_base DECIMAL(10,2) NOT NULL,
    gps_origen VARCHAR(100), -- Coordenadas: "lat,long"
    gps_destino VARCHAR(100), -- Coordenadas: "lat,long"
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Tabla: `boleto`
Gestiona los boletos de pasajeros (CU4)

```sql
CREATE TABLE boleto (
    id_boleto SERIAL PRIMARY KEY,
    codigo_boleto VARCHAR(50) UNIQUE NOT NULL,
    id_ruta INTEGER REFERENCES ruta(id_ruta),
    id_vehiculo INTEGER REFERENCES vehiculo(id_vehiculo),
    nombre_pasajero VARCHAR(200) NOT NULL,
    ci_pasajero VARCHAR(20) NOT NULL,
    asiento INTEGER NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    fecha_viaje DATE NOT NULL,
    hora_salida TIME NOT NULL,
    qr_code VARCHAR(255), -- URL o código QR
    estado VARCHAR(20) DEFAULT 'VENDIDO', -- VENDIDO, USADO, CANCELADO
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Tabla: `encomienda`
Gestiona el envío de encomiendas (CU5)

```sql
CREATE TABLE encomienda (
    id_encomienda SERIAL PRIMARY KEY,
    codigo_encomienda VARCHAR(50) UNIQUE NOT NULL,
    id_ruta INTEGER REFERENCES ruta(id_ruta),
    id_vehiculo INTEGER REFERENCES vehiculo(id_vehiculo),
    nombre_remitente VARCHAR(200) NOT NULL,
    telefono_remitente VARCHAR(20),
    nombre_destinatario VARCHAR(200) NOT NULL,
    telefono_destinatario VARCHAR(20),
    descripcion TEXT,
    peso_kg DECIMAL(10,2),
    precio DECIMAL(10,2) NOT NULL,
    fecha_envio DATE NOT NULL,
    foto_url VARCHAR(255),
    qr_code VARCHAR(255),
    gps_actual VARCHAR(100), -- Tracking GPS: "lat,long"
    estado VARCHAR(20) DEFAULT 'REGISTRADA', -- REGISTRADA, EN_TRANSITO, ENTREGADA
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Tabla: `venta`
Gestiona las ventas realizadas (CU6)

```sql
CREATE TABLE venta (
    id_venta SERIAL PRIMARY KEY,
    codigo_venta VARCHAR(50) UNIQUE NOT NULL,
    tipo_venta VARCHAR(20) NOT NULL, -- BOLETO, ENCOMIENDA
    id_referencia INTEGER NOT NULL, -- id_boleto o id_encomienda
    id_usuario_vendedor INTEGER REFERENCES usuario(id_usuario),
    monto_total DECIMAL(10,2) NOT NULL,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Tabla: `pago`
Gestiona los pagos en cuotas (CU7)

```sql
CREATE TABLE pago (
    id_pago SERIAL PRIMARY KEY,
    id_venta INTEGER REFERENCES venta(id_venta),
    numero_cuota INTEGER NOT NULL, -- 1 o 2
    monto_cuota DECIMAL(10,2) NOT NULL,
    fecha_pago TIMESTAMP,
    metodo_pago VARCHAR(50), -- EFECTIVO, TRANSFERENCIA, TARJETA
    estado VARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, PAGADO
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Tabla: `reporte`
Almacena reportes y estadísticas generados (CU8)

```sql
CREATE TABLE reporte (
    id_reporte SERIAL PRIMARY KEY,
    tipo_reporte VARCHAR(50) NOT NULL,
    descripcion TEXT,
    parametros TEXT, -- JSON con parámetros del reporte
    resultado TEXT, -- JSON con resultado del reporte
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 3. DEFINICIÓN DE COMANDOS VÍA CORREO

### 3.1. Formato General de Comandos

**Sintaxis:**
```
<OPERACION><ENTIDAD>[<parametros>]
```

**Operaciones:**
- `LIS` - Listar/Consultar (SELECT)
- `INS` - Insertar (INSERT)
- `UPD` - Actualizar (UPDATE)
- `DEL` - Eliminar (DELETE)

**Entidades:**
- `USU` - Usuario
- `VEH` - Vehículo
- `RUT` - Ruta
- `BOL` - Boleto
- `ENC` - Encomienda
- `VEN` - Venta
- `PAG` - Pago
- `REP` - Reporte

### 3.2. Comandos por Entidad

#### A. USUARIO (CU1)

**Listar todos:**
```
Subject: LISUSU["*"]
```

**Listar por CI:**
```
Subject: LISUSU["1234567"]
```

**Listar por tipo:**
```
Subject: LISUSU["CONDUCTOR"]
```

**Insertar:**
```
Subject: INSUSU["1234567","Juan","Pérez","CONDUCTOR","71234567","juan@mail.com","foto.jpg"]
```

**Actualizar:**
```
Subject: UPDUSU["1234567","Juan Carlos","Pérez López","CONDUCTOR","71234567","juan@mail.com","foto.jpg","ACTIVO"]
```

**Eliminar (desactivar):**
```
Subject: DELUSU["1234567"]
```

---

#### B. VEHÍCULO (CU2)

**Listar todos:**
```
Subject: LISVEH["*"]
```

**Listar por placa:**
```
Subject: LISVEH["ABC-123"]
```

**Insertar:**
```
Subject: INSVEH["ABC-123","Toyota","Hiace","15","1234567","foto.jpg"]
```
*(último parámetro es CI del propietario)*

**Actualizar:**
```
Subject: UPDVEH["ABC-123","Toyota","Hiace","15","1234567","foto.jpg","DISPONIBLE"]
```

**Eliminar:**
```
Subject: DELVEH["ABC-123"]
```

---

#### C. RUTA (CU3)

**Listar todas:**
```
Subject: LISRUT["*"]
```

**Listar por ID:**
```
Subject: LISRUT["1"]
```

**Insertar:**
```
Subject: INSRUT["Santa Cruz - Comarapa","Santa Cruz","Comarapa","150.5","3.5","50.00","-17.7833,-63.1821","-17.9167,-64.5167"]
```

**Actualizar:**
```
Subject: UPDRUT["1","Santa Cruz - Comarapa","Santa Cruz","Comarapa","150.5","3.5","60.00","-17.7833,-63.1821","-17.9167,-64.5167","ACTIVO"]
```

**Eliminar:**
```
Subject: DELRUT["1"]
```

---

#### D. BOLETO (CU4)

**Listar todos:**
```
Subject: LISBOL["*"]
```

**Listar por código:**
```
Subject: LISBOL["BOL-001"]
```

**Listar por fecha:**
```
Subject: LISBOL["2025-11-15"]
```

**Insertar:**
```
Subject: INSBOL["BOL-001","1","ABC-123","Juan Pérez","1234567","15","50.00","2025-11-15","08:00","qr_001.png"]
```
*(parámetros: codigo, id_ruta, placa_vehiculo, nombre_pasajero, ci, asiento, precio, fecha, hora, qr)*

**Actualizar estado:**
```
Subject: UPDBOL["BOL-001","USADO"]
```

**Eliminar:**
```
Subject: DELBOL["BOL-001"]
```

---

#### E. ENCOMIENDA (CU5)

**Listar todas:**
```
Subject: LISENC["*"]
```

**Listar por código:**
```
Subject: LISENC["ENC-001"]
```

**Insertar:**
```
Subject: INSENC["ENC-001","1","ABC-123","Juan Pérez","71111111","María López","72222222","Paquete de ropa","5.5","25.00","2025-11-15","foto.jpg","qr_001.png"]
```

**Actualizar estado y GPS:**
```
Subject: UPDENC["ENC-001","EN_TRANSITO","-17.8,-63.5"]
```

**Eliminar:**
```
Subject: DELENC["ENC-001"]
```

---

#### F. VENTA (CU6)

**Listar todas:**
```
Subject: LISVEN["*"]
```

**Listar por código:**
```
Subject: LISVEN["VEN-001"]
```

**Registrar venta de boleto:**
```
Subject: INSVEN["VEN-001","BOLETO","BOL-001","1234567","50.00"]
```
*(parámetros: codigo_venta, tipo, codigo_boleto/encomienda, ci_vendedor, monto)*

**Registrar venta de encomienda:**
```
Subject: INSVEN["VEN-002","ENCOMIENDA","ENC-001","1234567","25.00"]
```

---

#### G. PAGO (CU7)

**Listar pagos de una venta:**
```
Subject: LISPAG["VEN-001"]
```

**Registrar pago (cuota):**
```
Subject: INSPAG["VEN-001","1","25.00","EFECTIVO"]
```
*(parámetros: codigo_venta, numero_cuota, monto, metodo_pago)*

**Actualizar estado de pago:**
```
Subject: UPDPAG["VEN-001","1","PAGADO"]
```

---

#### H. REPORTES (CU8)

**Reporte de ventas por fecha:**
```
Subject: REPREP["VENTAS_DIA","2025-11-15"]
```

**Reporte de boletos vendidos:**
```
Subject: REPREP["BOLETOS_VENDIDOS","2025-11-15"]
```

**Reporte de encomiendas en tránsito:**
```
Subject: REPREP["ENCOMIENDAS_TRANSITO","*"]
```

**Reporte de pagos pendientes:**
```
Subject: REPREP["PAGOS_PENDIENTES","*"]
```

**Estadísticas de rutas más usadas:**
```
Subject: REPREP["RUTAS_POPULARES","2025-11"]
```

---

## 4. ARQUITECTURA DEL SISTEMA SPRING BOOT

### 4.1. Estructura de Paquetes

```
com.example.sistema_via_mail/
├── config/              # Configuraciones (Mail, JPA, etc.)
├── model/               # Entidades JPA
│   ├── Usuario.java
│   ├── Vehiculo.java
│   ├── Ruta.java
│   ├── Boleto.java
│   ├── Encomienda.java
│   ├── Venta.java
│   ├── Pago.java
│   └── Reporte.java
├── repository/          # Interfaces JPA Repository
│   ├── UsuarioRepository.java
│   ├── VehiculoRepository.java
│   ├── RutaRepository.java
│   ├── BoletoRepository.java
│   ├── EncomiendaRepository.java
│   ├── VentaRepository.java
│   ├── PagoRepository.java
│   └── ReporteRepository.java
├── service/             # Lógica de negocio
│   ├── EmailService.java
│   ├── CommandParserService.java
│   ├── CommandExecutorService.java
│   ├── UsuarioService.java
│   ├── VehiculoService.java
│   ├── RutaService.java
│   ├── BoletoService.java
│   ├── EncomiendaService.java
│   ├── VentaService.java
│   ├── PagoService.java
│   └── ReporteService.java
├── scheduler/           # Tareas programadas
│   └── EmailScheduler.java
├── dto/                 # Data Transfer Objects
│   ├── CommandRequest.java
│   └── CommandResponse.java
├── exception/           # Manejo de excepciones
│   ├── CommandException.java
│   └── ValidationException.java
└── util/                # Utilidades
    ├── CommandValidator.java
    └── ResponseFormatter.java
```

### 4.2. Flujo de Procesamiento

```
1. EmailScheduler (cada X minutos)
   ↓
2. EmailService.checkInbox()
   ↓
3. CommandParserService.parse(subject)
   ↓
4. CommandValidator.validate(command)
   ↓
5. CommandExecutorService.execute(command)
   ↓
6. [Service específico] (ej: UsuarioService)
   ↓
7. [Repository] (ej: UsuarioRepository)
   ↓
8. Database (PostgreSQL)
   ↓
9. ResponseFormatter.format(result)
   ↓
10. EmailService.sendResponse()
```

---

## 5. FASES DE IMPLEMENTACIÓN

### FASE 1: Configuración y Base (Semana 1)

**Tareas:**
1. ✅ Configurar Spring Boot + PostgreSQL
2. ✅ Configurar JavaMail (IMAP/SMTP)
3. ✅ Crear script de creación de tablas
4. ✅ Crear entidades JPA
5. ✅ Crear repositorios JPA
6. ✅ Configurar Scheduler básico

**Entregables:**
- Base de datos creada y poblada con datos de prueba
- Conexión a correo funcionando
- Lectura básica de correos

---

### FASE 2: Core del Sistema (Semana 2)

**Tareas:**
1. Implementar CommandParserService
2. Implementar CommandValidator
3. Implementar CommandExecutorService
4. Implementar ResponseFormatter
5. Crear DTOs necesarios
6. Manejo de excepciones

**Entregables:**
- Sistema capaz de parsear comandos
- Validación de sintaxis
- Respuestas formateadas

---

### FASE 3: CU1 y CU2 - Usuarios y Vehículos (Semana 3)

**Tareas:**
1. Implementar UsuarioService (CRUD completo)
2. Implementar VehiculoService (CRUD completo)
3. Validaciones de negocio
4. Pruebas unitarias
5. Pruebas de integración vía correo

**Comandos implementados:**
- LISUSU, INSUSU, UPDUSU, DELUSU
- LISVEH, INSVEH, UPDVEH, DELVEH

---

### FASE 4: CU3 y CU4 - Rutas y Boletos (Semana 4)

**Tareas:**
1. Implementar RutaService (CRUD + GPS)
2. Implementar BoletoService (CRUD + QR)
3. Generar códigos QR
4. Validar disponibilidad de asientos
5. Pruebas de integración

**Comandos implementados:**
- LISRUT, INSRUT, UPDRUT, DELRUT
- LISBOL, INSBOL, UPDBOL, DELBOL

---

### FASE 5: CU5 - Encomiendas (Semana 5)

**Tareas:**
1. Implementar EncomiendaService (CRUD + GPS + QR + Foto)
2. Tracking GPS
3. Estados de encomienda
4. Manejo de fotos
5. Pruebas de integración

**Comandos implementados:**
- LISENC, INSENC, UPDENC, DELENC

---

### FASE 6: CU6 y CU7 - Ventas y Pagos (Semana 6)

**Tareas:**
1. Implementar VentaService
2. Implementar PagoService (sistema de cuotas)
3. Validar integridad de ventas
4. Control de pagos en cuotas
5. Pruebas de integración

**Comandos implementados:**
- LISVEN, INSVEN
- LISPAG, INSPAG, UPDPAG

---

### FASE 7: CU8 - Reportes y Estadísticas (Semana 7)

**Tareas:**
1. Implementar ReporteService
2. Crear queries para estadísticas
3. Formatear reportes para email
4. Implementar diferentes tipos de reportes
5. Pruebas de integración

**Comandos implementados:**
- REPREP (con diferentes parámetros)

**Tipos de reportes:**
- Ventas por día/mes
- Boletos vendidos por ruta
- Encomiendas en tránsito
- Pagos pendientes
- Rutas más populares
- Conductores más activos
- Vehículos más utilizados

---

### FASE 8: Pruebas y Documentación (Semana 8)

**Tareas:**
1. Pruebas completas de todos los comandos
2. Pruebas de casos extremos
3. Optimización de consultas
4. Documentación técnica
5. Manual de usuario
6. Preparar presentación

**Entregables:**
- Sistema completamente funcional
- Documentación completa
- Manual de comandos
- Casos de prueba documentados

---

## 6. EJEMPLOS DE USO COMPLETO

### Ejemplo 1: Registrar un nuevo conductor

**Correo enviado:**
```
From: admin@transcomarapa.com
To: grupo04sa@tecnoweb.org.bo
Subject: INSUSU["7891234","Carlos","Ramírez","CONDUCTOR","71555666","carlos@mail.com","foto_carlos.jpg"]
```

**Respuesta del sistema:**
```
From: grupo04sa@tecnoweb.org.bo
To: admin@transcomarapa.com
Subject: RE: INSUSU - Exitoso

COMANDO: INSUSU
ESTADO: EXITOSO
MENSAJE: Usuario registrado correctamente

DATOS:
- ID: 1
- CI: 7891234
- Nombre: Carlos Ramírez
- Tipo: CONDUCTOR
- Estado: ACTIVO
- Fecha: 2025-11-13 10:30:00
```

### Ejemplo 2: Vender un boleto

**Correo enviado:**
```
Subject: INSBOL["BOL-100","1","ABC-123","María López","5544332","10","50.00","2025-11-20","14:00","qr_bol100.png"]
```

**Respuesta:**
```
COMANDO: INSBOL
ESTADO: EXITOSO
MENSAJE: Boleto registrado correctamente

DATOS:
- Código: BOL-100
- Pasajero: María López (CI: 5544332)
- Ruta: Santa Cruz - Comarapa
- Vehículo: ABC-123
- Asiento: 10
- Fecha viaje: 2025-11-20 14:00
- Precio: Bs. 50.00
- QR: qr_bol100.png
```

### Ejemplo 3: Consultar encomiendas en tránsito

**Correo enviado:**
```
Subject: LISENC["EN_TRANSITO"]
```

**Respuesta:**
```
COMANDO: LISENC
ESTADO: EXITOSO
REGISTROS ENCONTRADOS: 3

1. ENC-001
   - Remitente: Juan Pérez (71111111)
   - Destinatario: Ana García (72222222)
   - Ruta: Santa Cruz - Comarapa
   - Vehículo: ABC-123
   - Estado: EN_TRANSITO
   - GPS Actual: -17.8,-63.5

2. ENC-002
   - Remitente: Pedro Sánchez (73333333)
   - Destinatario: Lucía Morales (74444444)
   - Ruta: Cochabamba - Comarapa
   - Vehículo: XYZ-456
   - Estado: EN_TRANSITO
   - GPS Actual: -17.5,-64.0

3. ENC-003
   - Remitente: Rosa Díaz (75555555)
   - Destinatario: Mario Castro (76666666)
   - Ruta: Santa Cruz - Samaipata
   - Vehículo: DEF-789
   - Estado: EN_TRANSITO
   - GPS Actual: -18.0,-63.8
```

---

## 7. CONSIDERACIONES TÉCNICAS

### 7.1. Configuración de Correo

**application.properties:**
```properties
# Configuración IMAP (recepción)
spring.mail.imap.host=mail.tecnoweb.org.bo
spring.mail.imap.port=993
spring.mail.imap.username=grupo04sa@tecnoweb.org.bo
spring.mail.imap.password=grup004grup004*
spring.mail.imap.protocol=imaps

# Configuración SMTP (envío)
spring.mail.smtp.host=mail.tecnoweb.org.bo
spring.mail.smtp.port=465
spring.mail.smtp.username=grupo04sa@tecnoweb.org.bo
spring.mail.smtp.password=grup004grup004*
spring.mail.smtp.auth=true
spring.mail.smtp.starttls.enable=true

# Scheduler
scheduler.email.check.interval=60000
```

### 7.2. Validaciones Importantes

1. **Validación de sintaxis de comandos**
2. **Validación de tipos de datos**
3. **Validación de existencia de referencias (FK)**
4. **Validación de reglas de negocio**
5. **Validación de permisos (según tipo de usuario)**

### 7.3. Manejo de Errores

**Tipos de error:**
- `ERROR_SINTAXIS`: Comando mal formado
- `ERROR_VALIDACION`: Datos inválidos
- `ERROR_NEGOCIO`: Regla de negocio violada
- `ERROR_BASE_DATOS`: Error en BD
- `ERROR_SISTEMA`: Error interno

**Formato de respuesta de error:**
```
COMANDO: [comando]
ESTADO: ERROR
TIPO: [tipo de error]
MENSAJE: [descripción del error]
DETALLE: [información adicional]
```

---

## 8. CRONOGRAMA RESUMIDO

| Semana | Fase | Actividades Principales |
|--------|------|------------------------|
| 1 | Configuración | Base de datos, entidades, correo |
| 2 | Core | Parser, validator, executor |
| 3 | CU1-CU2 | Usuarios y vehículos |
| 4 | CU3-CU4 | Rutas y boletos |
| 5 | CU5 | Encomiendas |
| 6 | CU6-CU7 | Ventas y pagos |
| 7 | CU8 | Reportes y estadísticas |
| 8 | Finalización | Pruebas y documentación |

---

## 9. CRITERIOS DE ACEPTACIÓN

### Para cada Caso de Uso:

✅ Todos los comandos CRUD funcionan correctamente  
✅ Validaciones implementadas y funcionando  
✅ Respuestas correctamente formateadas  
✅ Manejo de errores implementado  
✅ Pruebas unitarias pasando  
✅ Pruebas de integración vía correo exitosas  
✅ Documentación actualizada  

### Para el Sistema Completo:

✅ Todos los CU implementados  
✅ Base de datos normalizada y funcionando  
✅ Sistema de correo robusto (procesa correos cada minuto)  
✅ Todas las validaciones implementadas  
✅ Reportes generando información útil  
✅ Sistema desplegado y accesible  
✅ Documentación completa entregada  

---

## 10. PRÓXIMOS PASOS INMEDIATOS

1. **Revisar y aprobar este plan**
2. **Crear script SQL de base de datos**
3. **Generar datos de prueba**
4. **Configurar conexión de correo**
5. **Implementar EmailScheduler básico**
6. **Crear todas las entidades JPA**
7. **Crear todos los repositorios**
8. **Hacer commit inicial del proyecto**

---

**Fecha de creación:** 13/11/2025  
**Última actualización:** 13/11/2025  
**Estado:** En planificación  
**Versión:** 1.0

