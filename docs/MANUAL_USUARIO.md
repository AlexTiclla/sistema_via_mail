# 📖 MANUAL DE USUARIO
## Sistema de Gestión de Transporte Trans Comarapa
### Vía Correo Electrónico

**Versión:** 1.0  
**Fecha:** Noviembre 2025  
**Grupo:** Grupo04 SA

---

## 📋 TABLA DE CONTENIDOS

1. [Introducción](#introducción)
2. [Cómo Usar el Sistema](#cómo-usar-el-sistema)
3. [Formato de Comandos](#formato-de-comandos)
4. [Gestión de Usuarios](#gestión-de-usuarios)
5. [Gestión de Vehículos](#gestión-de-vehículos)
6. [Gestión de Rutas](#gestión-de-rutas)
7. [Gestión de Boletos](#gestión-de-boletos)
8. [Gestión de Encomiendas](#gestión-de-encomiendas)
9. [Gestión de Ventas](#gestión-de-ventas)
10. [Gestión de Pagos](#gestión-de-pagos)
11. [Reportes y Estadísticas](#reportes-y-estadísticas)
12. [Solución de Problemas](#solución-de-problemas)

---

## 1. INTRODUCCIÓN

### ¿Qué es el Sistema Trans Comarapa?

El Sistema Trans Comarapa es una plataforma de gestión de transporte interprovincial que opera completamente vía correo electrónico. Permite a propietarios, secretarias y conductores gestionar usuarios, vehículos, rutas, boletos, encomiendas, ventas y pagos sin necesidad de una interfaz web.

### Ventajas del Sistema

- ✅ **Accesible desde cualquier lugar**: Solo necesitas un cliente de correo
- ✅ **Sin instalaciones**: No requiere software adicional
- ✅ **Respuestas automáticas**: El sistema responde en menos de 1 minuto
- ✅ **Registro completo**: Todas las operaciones quedan registradas
- ✅ **Fácil de usar**: Comandos simples y respuestas claras

### Requisitos

- Cuenta de correo electrónico activa
- Acceso a internet
- Conocimiento básico de correo electrónico

---

## 2. CÓMO USAR EL SISTEMA

### Paso 1: Preparar el Correo

1. Abre tu cliente de correo (Gmail, Outlook, etc.)
2. Crea un nuevo correo
3. En **Para:** escribe `grupo04sa@tecnoweb.org.bo`

### Paso 2: Escribir el Comando

En el **ASUNTO** del correo, escribe el comando que deseas ejecutar.

**Ejemplo:**
```
Asunto: LISUSU["*"]
```

El **cuerpo del correo** puede estar vacío o contener notas personales (será ignorado).

### Paso 3: Enviar y Esperar Respuesta

1. Envía el correo
2. Espera la respuesta (máximo 60 segundos)
3. Revisa tu bandeja de entrada
4. Encontrarás un correo con el asunto: `RE: [Tu comando] - EXITOSO` o `RE: [Tu comando] - ERROR`

### Ejemplo de Respuesta

```
===========================================
SISTEMA TRANS COMARAPA - RESPUESTA
===========================================

COMANDO: LISUSU
ESTADO: EXITOSO
FECHA/HORA: 2025-11-13 10:30:00

MENSAJE: Consulta ejecutada correctamente

-------------------------------------------
DATOS:
-------------------------------------------
- Total: 3
- Usuarios:
  - CI: 1234567 | Juan Pérez | CONDUCTOR | ACTIVO
  - CI: 2345678 | María López | SECRETARIA | ACTIVO
  - CI: 3456789 | Pedro Díaz | PROPIETARIO | ACTIVO

===========================================
Trans Comarapa - Sistema de Gestión
Grupo04 SA
===========================================
```

---

## 3. FORMATO DE COMANDOS

### Estructura General

```
<OPERACION><ENTIDAD>[<parametros>]
```

**Componentes:**

1. **OPERACIÓN** (3 letras):
   - `LIS` = Listar/Consultar
   - `INS` = Insertar/Crear
   - `UPD` = Actualizar/Modificar
   - `DEL` = Eliminar/Desactivar
   - `REP` = Reporte (solo para reportes)

2. **ENTIDAD** (3 letras):
   - `USU` = Usuario
   - `VEH` = Vehículo
   - `RUT` = Ruta
   - `BOL` = Boleto
   - `ENC` = Encomienda
   - `VEN` = Venta
   - `PAG` = Pago
   - `REP` = Reporte

3. **PARÁMETROS**: Entre corchetes `[]`, separados por comas, entre comillas `"`

### Reglas Importantes

- ✅ Todo en MAYÚSCULAS
- ✅ Parámetros entre comillas dobles: `"valor"`
- ✅ Separados por comas: `","` 
- ✅ Sin espacios adicionales
- ❌ No usar tildes en comandos
- ❌ No omitir parámetros obligatorios

---

## 4. GESTIÓN DE USUARIOS

### Listar Usuarios

**Listar todos los usuarios:**
```
Asunto: LISUSU["*"]
```

**Buscar usuario por CI:**
```
Asunto: LISUSU["1234567"]
```

**Buscar por tipo de usuario:**
```
Asunto: LISUSU["CONDUCTOR"]
```

Tipos válidos: `PROPIETARIO`, `SECRETARIA`, `CONDUCTOR`

### Registrar Nuevo Usuario

```
Asunto: INSUSU["CI","Nombre","Apellido","Tipo","Telefono","Email","Foto"]
```

**Ejemplo:**
```
Asunto: INSUSU["1234567","Juan","Pérez","CONDUCTOR","71234567","juan@mail.com","foto.jpg"]
```

**Parámetros obligatorios:**
- CI (5-20 dígitos)
- Nombre
- Apellido
- Tipo (PROPIETARIO, SECRETARIA, CONDUCTOR)

**Parámetros opcionales:**
- Teléfono (7-20 dígitos)
- Email (formato válido)
- Foto (URL o nombre de archivo)

### Actualizar Usuario

```
Asunto: UPDUSU["CI","Nombre","Apellido","Tipo","Telefono","Email","Foto","Estado"]
```

**Ejemplo:**
```
Asunto: UPDUSU["1234567","Juan Carlos","Pérez López","CONDUCTOR","71234567","juan@mail.com","foto_nueva.jpg","ACTIVO"]
```

Estados válidos: `ACTIVO`, `INACTIVO`

### Eliminar Usuario (Desactivar)

```
Asunto: DELUSU["CI"]
```

**Ejemplo:**
```
Asunto: DELUSU["1234567"]
```

**Nota:** Los usuarios no se eliminan físicamente, solo se desactivan.

---

## 5. GESTIÓN DE VEHÍCULOS

### Listar Vehículos

**Listar todos:**
```
Asunto: LISVEH["*"]
```

**Buscar por placa:**
```
Asunto: LISVEH["ABC-123"]
```

### Registrar Nuevo Vehículo

```
Asunto: INSVEH["Placa","Marca","Modelo","Capacidad","CI_Propietario","Foto"]
```

**Ejemplo:**
```
Asunto: INSVEH["ABC-123","Toyota","Hiace","15","1234567","foto_vehiculo.jpg"]
```

**Nota:** El CI del propietario debe existir y ser de tipo PROPIETARIO.

### Actualizar Vehículo

```
Asunto: UPDVEH["Placa","Marca","Modelo","Capacidad","CI_Propietario","Foto","Estado"]
```

**Ejemplo:**
```
Asunto: UPDVEH["ABC-123","Toyota","Hiace","15","1234567","foto_nueva.jpg","EN_RUTA"]
```

Estados válidos: `DISPONIBLE`, `EN_RUTA`, `MANTENIMIENTO`

### Eliminar Vehículo

```
Asunto: DELVEH["Placa"]
```

---

## 6. GESTIÓN DE RUTAS

### Listar Rutas

**Listar todas:**
```
Asunto: LISRUT["*"]
```

**Buscar por ID:**
```
Asunto: LISRUT["1"]
```

### Registrar Nueva Ruta

```
Asunto: INSRUT["Nombre","Origen","Destino","Distancia_KM","Duracion_Horas","Precio","GPS_Origen","GPS_Destino"]
```

**Ejemplo:**
```
Asunto: INSRUT["Santa Cruz - Comarapa","Santa Cruz","Comarapa","150.5","3.5","50.00","-17.7833,-63.1821","-17.9167,-64.5167"]
```

**Formato GPS:** `"latitud,longitud"` (ej: `"-17.7833,-63.1821"`)

### Actualizar Ruta

```
Asunto: UPDRUT["ID","Nombre","Origen","Destino","Distancia","Duracion","Precio","GPS_Origen","GPS_Destino","Estado"]
```

### Eliminar Ruta

```
Asunto: DELRUT["ID"]
```

---

## 7. GESTIÓN DE BOLETOS

### Listar Boletos

**Listar todos:**
```
Asunto: LISBOL["*"]
```

**Buscar por código:**
```
Asunto: LISBOL["BOL-001"]
```

**Buscar por fecha:**
```
Asunto: LISBOL["2025-11-20"]
```

Formato de fecha: `yyyy-MM-dd`

### Vender Nuevo Boleto

```
Asunto: INSBOL["Codigo","ID_Ruta","Placa","Nombre_Pasajero","CI_Pasajero","Asiento","Precio","Fecha","Hora","QR"]
```

**Ejemplo:**
```
Asunto: INSBOL["BOL-001","1","ABC-123","Juan Pérez","1234567","15","50.00","2025-11-20","08:00","qr_bol001.png"]
```

**Importante:**
- El asiento debe estar disponible
- El número de asiento no debe exceder la capacidad del vehículo
- Formato de fecha: `yyyy-MM-dd`
- Formato de hora: `HH:mm`

### Actualizar Estado de Boleto

```
Asunto: UPDBOL["Codigo","Estado"]
```

**Ejemplo:**
```
Asunto: UPDBOL["BOL-001","USADO"]
```

Estados válidos: `VENDIDO`, `USADO`, `CANCELADO`

### Cancelar Boleto

```
Asunto: DELBOL["Codigo"]
```

---

## 8. GESTIÓN DE ENCOMIENDAS

### Listar Encomiendas

**Listar todas:**
```
Asunto: LISENC["*"]
```

**Buscar por código:**
```
Asunto: LISENC["ENC-001"]
```

**Buscar por estado:**
```
Asunto: LISENC["EN_TRANSITO"]
```

Estados: `REGISTRADA`, `EN_TRANSITO`, `ENTREGADA`

### Registrar Nueva Encomienda

```
Asunto: INSENC["Codigo","ID_Ruta","Placa","Remitente","Tel_Remitente","Destinatario","Tel_Destinatario","Descripcion","Peso_KG","Precio","Fecha","Foto","QR"]
```

**Ejemplo:**
```
Asunto: INSENC["ENC-001","1","ABC-123","Juan Pérez","71111111","María López","72222222","Paquete de ropa","5.5","25.00","2025-11-20","foto.jpg","qr.png"]
```

### Actualizar Encomienda (Estado y GPS)

```
Asunto: UPDENC["Codigo","Estado","GPS"]
```

**Ejemplos:**
```
# Actualizar estado y ubicación
Asunto: UPDENC["ENC-001","EN_TRANSITO","-17.8,-63.5"]

# Solo actualizar estado
Asunto: UPDENC["ENC-001","ENTREGADA",""]
```

### Tracking de Encomienda

Para conocer la ubicación actual de una encomienda:
```
Asunto: LISENC["ENC-001"]
```

La respuesta incluirá las coordenadas GPS actuales.

---

## 9. GESTIÓN DE VENTAS

### Listar Ventas

**Listar todas:**
```
Asunto: LISVEN["*"]
```

**Buscar por código:**
```
Asunto: LISVEN["VEN-001"]
```

### Registrar Venta de Boleto

```
Asunto: INSVEN["Codigo_Venta","BOLETO","Codigo_Boleto","CI_Vendedor","Monto"]
```

**Ejemplo:**
```
Asunto: INSVEN["VEN-001","BOLETO","BOL-001","1234567","50.00"]
```

### Registrar Venta de Encomienda

```
Asunto: INSVEN["Codigo_Venta","ENCOMIENDA","Codigo_Encomienda","CI_Vendedor","Monto"]
```

**Ejemplo:**
```
Asunto: INSVEN["VEN-002","ENCOMIENDA","ENC-001","1234567","25.00"]
```

**Nota:** El vendedor debe ser SECRETARIA o PROPIETARIO.

---

## 10. GESTIÓN DE PAGOS

El sistema soporta pagos en **hasta 2 cuotas**.

### Listar Pagos de una Venta

```
Asunto: LISPAG["Codigo_Venta"]
```

**Ejemplo:**
```
Asunto: LISPAG["VEN-001"]
```

### Registrar Pago (Cuota)

```
Asunto: INSPAG["Codigo_Venta","Numero_Cuota","Monto","Metodo_Pago"]
```

**Ejemplos:**

**Primera cuota:**
```
Asunto: INSPAG["VEN-001","1","25.00","EFECTIVO"]
```

**Segunda cuota:**
```
Asunto: INSPAG["VEN-001","2","25.00","TRANSFERENCIA"]
```

**Métodos de pago válidos:**
- `EFECTIVO`
- `TRANSFERENCIA`
- `TARJETA`

**Nota:** El sistema valida que el total de las cuotas no exceda el monto de la venta.

### Actualizar Estado de Pago

```
Asunto: UPDPAG["Codigo_Venta","Numero_Cuota","Estado"]
```

**Ejemplo:**
```
Asunto: UPDPAG["VEN-001","1","PAGADO"]
```

Estados válidos: `PENDIENTE`, `PAGADO`

---

## 11. REPORTES Y ESTADÍSTICAS

### Reporte de Ventas por Día

```
Asunto: REPREP["VENTAS_DIA","Fecha"]
```

**Ejemplo:**
```
Asunto: REPREP["VENTAS_DIA","2025-11-20"]
```

**Muestra:**
- Total de ventas
- Ventas de boletos
- Ventas de encomiendas
- Total general
- Detalle de cada venta

### Reporte de Boletos Vendidos

```
Asunto: REPREP["BOLETOS_VENDIDOS","Fecha_o_*"]
```

**Ejemplos:**
```
# Por fecha específica
Asunto: REPREP["BOLETOS_VENDIDOS","2025-11-20"]

# Todos los boletos
Asunto: REPREP["BOLETOS_VENDIDOS","*"]
```

**Muestra:**
- Total de boletos
- Total de ingresos
- Boletos por ruta

### Reporte de Encomiendas en Tránsito

```
Asunto: REPREP["ENCOMIENDAS_TRANSITO","*"]
```

**Muestra:**
- Encomiendas con estado EN_TRANSITO
- Ubicación GPS actual
- Información de remitente y destinatario

### Reporte de Pagos Pendientes

```
Asunto: REPREP["PAGOS_PENDIENTES","*"]
```

**Muestra:**
- Pagos con estado PENDIENTE
- Monto total pendiente
- Detalle por venta

### Reporte de Rutas Populares

```
Asunto: REPREP["RUTAS_POPULARES","Periodo"]
```

**Ejemplos:**
```
# Por mes específico
Asunto: REPREP["RUTAS_POPULARES","2025-11"]

# Todas las rutas
Asunto: REPREP["RUTAS_POPULARES","*"]
```

**Muestra:**
- Rutas ordenadas por cantidad de boletos
- Ingresos por ruta
- Estadísticas del período

---

## 12. SOLUCIÓN DE PROBLEMAS

### Problema: No recibo respuesta

**Soluciones:**
1. Verifica que el correo se envió correctamente
2. Revisa tu carpeta de SPAM
3. Espera hasta 60 segundos (el sistema revisa correos cada minuto)
4. Verifica que el destinatario sea: `grupo04sa@tecnoweb.org.bo`

### Problema: Recibo ERROR_SINTAXIS

**Causa:** El formato del comando es incorrecto

**Soluciones:**
1. Verifica que uses MAYÚSCULAS
2. Verifica que los parámetros estén entre comillas: `"valor"`
3. Verifica que uses corchetes: `[...]`
4. No uses espacios adicionales
5. Revisa los ejemplos en este manual

**Ejemplo incorrecto:**
```
INSUSU[1234567,Juan,Pérez,CONDUCTOR]  ❌ (faltan comillas)
```

**Ejemplo correcto:**
```
INSUSU["1234567","Juan","Pérez","CONDUCTOR"]  ✅
```

### Problema: Recibo ERROR_VALIDACION

**Causa:** Los parámetros son inválidos

**Soluciones:**
1. **CI inválido:** Debe tener 5-20 dígitos
2. **Email inválido:** Verifica formato (ejemplo@dominio.com)
3. **Teléfono inválido:** Debe tener 7-20 dígitos
4. **Fecha inválida:** Usa formato yyyy-MM-dd (ej: 2025-11-20)
5. **Hora inválida:** Usa formato HH:mm (ej: 08:00, 14:30)
6. **GPS inválido:** Usa formato lat,long (ej: -17.7833,-63.1821)

### Problema: Recibo ERROR_NEGOCIO

**Causas comunes:**
- Intentar crear un usuario/boleto/encomienda que ya existe
- Intentar actualizar/eliminar algo que no existe
- Asiento ya ocupado
- Usuario no autorizado para realizar la operación
- Pago excede el monto total de la venta

**Solución:**
Lee el mensaje de error que indica específicamente qué regla de negocio se violó.

### Problema: El comando funciona pero los datos son incorrectos

**Soluciones:**
1. Usa el comando UPDATE para corregir
2. Si es crítico, usa DELETE y luego INSERT nuevamente
3. Verifica que los parámetros estén en el orden correcto

---

## 📞 SOPORTE

Para asistencia adicional:

**Email del sistema:** grupo04sa@tecnoweb.org.bo  
**Grupo:** Grupo04 SA

**Horario de atención:** 24/7 (respuesta automática)

---

## 📝 NOTAS FINALES

1. **Todas las operaciones quedan registradas** en el sistema
2. **Los correos son procesados automáticamente** cada minuto
3. **Las respuestas siempre incluyen** el estado (EXITOSO/ERROR) y detalles
4. **Conserva los correos de respuesta** como comprobantes
5. **Usa códigos únicos** para boletos, encomiendas y ventas

---

**Manual de Usuario v1.0**  
**Trans Comarapa - Sistema de Gestión**  
**Grupo04 SA - Noviembre 2025**

