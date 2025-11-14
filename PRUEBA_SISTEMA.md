# 🧪 CÓMO PROBAR EL SISTEMA

## Estado Actual: FASE 1 - Monitoreo de Correos

### ¿Qué hace ahora?

✅ **Revisa correos automáticamente** cada 60 segundos
✅ **Responde automáticamente** a cualquier correo recibido
✅ **Tiene base de datos** con datos de prueba ya cargados

---

## 📧 PRUEBA 1: Enviar un Correo

### Desde tu correo personal:

**Para:** `grupo04sa@tecnoweb.org.bo`
**Asunto:** `PRUEBA-SISTEMA`
**Cuerpo:** Cualquier texto, por ejemplo: "Hola, probando el sistema"

### Qué esperar:

1. ⏰ **Espera máximo 60 segundos** (la app revisa cada minuto)
2. 📋 **Mira los logs** en la terminal donde corre la aplicación, verás:
   ```
   INFO: Correos nuevos encontrados: 1
   INFO: Procesando mensaje:
     - De: tu-correo@gmail.com
     - Asunto: PRUEBA-SISTEMA
   ```
3. 📨 **Revisa tu bandeja de entrada** - recibirás respuesta automática

---

## 🗄️ PRUEBA 2: Ver Datos en la Base de Datos

### Conectarse a PostgreSQL:

```powershell
# Desde PowerShell
psql -h www.tecnoweb.org.bo -U grupo04sa -d db_grupo04sa
# Password: grup004grup004*
```

### Consultas de Prueba:

```sql
-- Ver todos los usuarios
SELECT * FROM usuario;

-- Ver todos los vehículos
SELECT * FROM vehiculo;

-- Ver todas las rutas
SELECT * FROM ruta;

-- Ver boletos vendidos
SELECT b.*, r.origen, r.destino 
FROM boleto b 
JOIN ruta r ON b.id_ruta = r.id_ruta;

-- Ver encomiendas
SELECT * FROM encomienda;

-- Ver ventas con detalle
SELECT v.*, u.nombre_completo as vendedor
FROM venta v
JOIN usuario u ON v.id_usuario_vendedor = u.id_usuario;

-- Ver pagos pendientes
SELECT p.*, v.monto_total 
FROM pago p 
JOIN venta v ON p.id_venta = v.id_venta
WHERE p.estado = 'Pendiente';

-- Ver reportes generados
SELECT * FROM reporte;
```

---

## 📊 DATOS DE PRUEBA DISPONIBLES

### Usuarios (8):
- 2 Propietarios
- 2 Secretarias  
- 4 Conductores

### Vehículos (5):
- Minibuses con diferentes capacidades
- Asignados a propietarios

### Rutas (5):
- Santa Cruz - Comarapa
- Comarapa - Samaipata
- Santa Cruz - Samaipata
- Comarapa - Vallegrande
- Santa Cruz - Vallegrande

### Boletos (5):
- Con códigos QR
- Diferentes estados (Activo, Usado)

### Encomiendas (5):
- Con tracking GPS
- Fotos de encomiendas
- Códigos QR

### Ventas (10):
- Diferentes montos

### Pagos (14):
- Estados: Completado, Pendiente, Vencido
- Cuotas 1/2 y 2/2

### Reportes (3):
- Reporte de ventas diarias
- Reporte de ocupación de vehículos
- Reporte de pagos pendientes

---

## 🔍 VERIFICAR QUE TODO FUNCIONA

### 1. Ver logs de la aplicación

En la terminal donde corre Spring Boot verás cada 60 segundos:

```
INFO: ===============================================
INFO: Iniciando revisión de correos #X
INFO: Fecha/Hora: 2025-11-13 17:XX:XX
INFO: ===============================================
INFO: Conectando al servidor POP3: mail.tecnoweb.org.bo:110
INFO: No se encontraron nuevos correos para procesar
INFO: Revisión de correos finalizada. Próxima revisión en 60 segundos
```

### 2. Ver que la base de datos tiene datos

```sql
-- Contar registros
SELECT 'usuarios' as tabla, COUNT(*) FROM usuario
UNION ALL
SELECT 'vehiculos', COUNT(*) FROM vehiculo
UNION ALL
SELECT 'rutas', COUNT(*) FROM ruta
UNION ALL
SELECT 'boletos', COUNT(*) FROM boleto
UNION ALL
SELECT 'encomiendas', COUNT(*) FROM encomienda
UNION ALL
SELECT 'ventas', COUNT(*) FROM venta
UNION ALL
SELECT 'pagos', COUNT(*) FROM pago
UNION ALL
SELECT 'reportes', COUNT(*) FROM reporte;
```

Deberías ver:
- usuarios: 8
- vehiculos: 5
- rutas: 5
- boletos: 5
- encomiendas: 5
- ventas: 10
- pagos: 14
- reportes: 3

---

## ⚠️ NOTA IMPORTANTE

### FASE ACTUAL: FASE 1
- ✅ La aplicación **SÍ** recibe correos
- ✅ La aplicación **SÍ** responde automáticamente
- ✅ La base de datos **SÍ** tiene datos de prueba
- ❌ **NO** procesa comandos complejos todavía (LISUSU, INSVEH, etc.)
- ❌ **NO** genera QR, GPS ni fotos todavía

### PRÓXIMAS FASES:
- **FASE 2-7**: Implementar procesamiento de comandos (LISUSU, INSVEH, etc.)
- **FASE 8**: Funcionalidades avanzadas (QR, GPS, fotos)

---

## 🛑 DETENER LA APLICACIÓN

En la terminal donde corre Spring Boot:
- Presiona `Ctrl + C`

## ▶️ INICIAR LA APLICACIÓN

```powershell
.\mvnw.cmd spring-boot:run
```

---

**¡El sistema está funcionando correctamente!** 🎉
