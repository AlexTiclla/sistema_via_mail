# COMANDOS DE PRUEBA - SISTEMA VÍA CORREO ELECTRÓNICO
## Trans Comarapa - Guía de Pruebas

**Fecha:** 13/11/2025  
**Destinatario de todos los comandos:** grupo04sa@tecnoweb.org.bo

---

## 📧 INSTRUCCIONES DE USO

Para probar el sistema, envía un correo electrónico con el comando en el **ASUNTO** del correo.

**Formato general:** `<OPERACION><ENTIDAD>[<parametros>]`

El sistema responderá automáticamente con el resultado de la operación.

---

## 👥 CU1 - USUARIOS (USU)

### Insertar Usuarios

```
Asunto: INSUSU["8765432","Ana","García","PROPIETARIO","71234567","ana@mail.com","foto_ana.jpg"]

Asunto: INSUSU["9876543","Pedro","Martínez","SECRETARIA","72345678","pedro@mail.com","foto_pedro.jpg"]

Asunto: INSUSU["5432109","Luis","Sánchez","CONDUCTOR","73456789","luis@mail.com","foto_luis.jpg"]

Asunto: INSUSU["6543210","María","Flores","CONDUCTOR","74567890","maria@mail.com","foto_maria.jpg"]
```

### Listar Usuarios

```
Asunto: LISUSU["*"]

Asunto: LISUSU["8765432"]

Asunto: LISUSU["CONDUCTOR"]
```

### Actualizar Usuario

```
Asunto: UPDUSU["8765432","Ana María","García López","PROPIETARIO","71234567","ana.garcia@mail.com","foto_ana_2.jpg","ACTIVO"]
```

### Eliminar Usuario

```
Asunto: DELUSU["5432109"]
```

---

## 🚗 CU2 - VEHÍCULOS (VEH)

### Insertar Vehículos

```
Asunto: INSVEH["ABC-123","Toyota","Hiace","15","8765432","foto_abc123.jpg"]

Asunto: INSVEH["XYZ-456","Mercedes","Sprinter","20","8765432","foto_xyz456.jpg"]

Asunto: INSVEH["DEF-789","Hyundai","County","25","8765432","foto_def789.jpg"]
```

### Listar Vehículos

```
Asunto: LISVEH["*"]

Asunto: LISVEH["ABC-123"]
```

### Actualizar Vehículo

```
Asunto: UPDVEH["ABC-123","Toyota","Hiace","15","8765432","foto_abc123_nuevo.jpg","EN_RUTA"]
```

### Eliminar Vehículo

```
Asunto: DELVEH["DEF-789"]
```

---

## 🛣️ CU3 - RUTAS (RUT)

### Insertar Rutas

```
Asunto: INSRUT["Santa Cruz - Comarapa","Santa Cruz","Comarapa","150.5","3.5","50.00","-17.7833,-63.1821","-17.9167,-64.5167"]

Asunto: INSRUT["Cochabamba - Comarapa","Cochabamba","Comarapa","180.0","4.0","60.00","-17.3935,-66.1570","-17.9167,-64.5167"]

Asunto: INSRUT["Santa Cruz - Samaipata","Santa Cruz","Samaipata","120.0","2.5","40.00","-17.7833,-63.1821","-18.1792,-63.8731"]
```

### Listar Rutas

```
Asunto: LISRUT["*"]

Asunto: LISRUT["1"]
```

### Actualizar Ruta

```
Asunto: UPDRUT["1","Santa Cruz - Comarapa","Santa Cruz","Comarapa","150.5","3.5","55.00","-17.7833,-63.1821","-17.9167,-64.5167","ACTIVO"]
```

### Eliminar Ruta

```
Asunto: DELRUT["3"]
```

---

## 🎫 CU4 - BOLETOS (BOL)

### Insertar Boletos

```
Asunto: INSBOL["BOL-001","1","ABC-123","Juan Pérez","1234567","1","50.00","2025-11-20","08:00","qr_bol001.png"]

Asunto: INSBOL["BOL-002","1","ABC-123","María López","2345678","2","50.00","2025-11-20","08:00","qr_bol002.png"]

Asunto: INSBOL["BOL-003","1","ABC-123","Carlos Díaz","3456789","3","50.00","2025-11-20","08:00","qr_bol003.png"]

Asunto: INSBOL["BOL-004","2","XYZ-456","Rosa Morales","4567890","1","60.00","2025-11-21","10:00","qr_bol004.png"]
```

### Listar Boletos

```
Asunto: LISBOL["*"]

Asunto: LISBOL["BOL-001"]

Asunto: LISBOL["2025-11-20"]
```

### Actualizar Estado de Boleto

```
Asunto: UPDBOL["BOL-001","USADO"]
```

### Eliminar (Cancelar) Boleto

```
Asunto: DELBOL["BOL-003"]
```

---

## 📦 CU5 - ENCOMIENDAS (ENC)

### Insertar Encomiendas

```
Asunto: INSENC["ENC-001","1","ABC-123","Juan Pérez","71111111","Ana García","72222222","Paquete de ropa","5.5","25.00","2025-11-20","foto_enc001.jpg","qr_enc001.png"]

Asunto: INSENC["ENC-002","2","XYZ-456","Pedro Sánchez","73333333","Lucía Morales","74444444","Documentos importantes","1.0","15.00","2025-11-21","foto_enc002.jpg","qr_enc002.png"]

Asunto: INSENC["ENC-003","1","ABC-123","Rosa Díaz","75555555","Mario Castro","76666666","Electrodomésticos","15.0","45.00","2025-11-20","foto_enc003.jpg","qr_enc003.png"]
```

### Listar Encomiendas

```
Asunto: LISENC["*"]

Asunto: LISENC["ENC-001"]

Asunto: LISENC["EN_TRANSITO"]
```

### Actualizar Encomienda (Estado y GPS)

```
Asunto: UPDENC["ENC-001","EN_TRANSITO","-17.8,-63.5"]

Asunto: UPDENC["ENC-001","ENTREGADA",""]
```

### Eliminar Encomienda

```
Asunto: DELENC["ENC-003"]
```

---

## 💰 CU6 - VENTAS (VEN)

### Registrar Ventas

**Venta de Boleto:**
```
Asunto: INSVEN["VEN-001","BOLETO","BOL-001","9876543","50.00"]

Asunto: INSVEN["VEN-002","BOLETO","BOL-002","9876543","50.00"]
```

**Venta de Encomienda:**
```
Asunto: INSVEN["VEN-003","ENCOMIENDA","ENC-001","9876543","25.00"]

Asunto: INSVEN["VEN-004","ENCOMIENDA","ENC-002","9876543","15.00"]
```

### Listar Ventas

```
Asunto: LISVEN["*"]

Asunto: LISVEN["VEN-001"]
```

---

## 💳 CU7 - PAGOS (PAG)

### Registrar Pagos (Cuotas)

**Primera cuota:**
```
Asunto: INSPAG["VEN-001","1","25.00","EFECTIVO"]

Asunto: INSPAG["VEN-002","1","30.00","TRANSFERENCIA"]

Asunto: INSPAG["VEN-003","1","15.00","TARJETA"]
```

**Segunda cuota:**
```
Asunto: INSPAG["VEN-001","2","25.00","EFECTIVO"]

Asunto: INSPAG["VEN-002","2","20.00","EFECTIVO"]
```

### Listar Pagos de una Venta

```
Asunto: LISPAG["VEN-001"]

Asunto: LISPAG["VEN-002"]
```

### Actualizar Estado de Pago

```
Asunto: UPDPAG["VEN-001","1","PAGADO"]
```

---

## 📊 CU8 - REPORTES (REP)

### Reporte de Ventas por Día

```
Asunto: REPREP["VENTAS_DIA","2025-11-20"]
```

### Reporte de Boletos Vendidos

**Por fecha específica:**
```
Asunto: REPREP["BOLETOS_VENDIDOS","2025-11-20"]
```

**Todos:**
```
Asunto: REPREP["BOLETOS_VENDIDOS","*"]
```

### Reporte de Encomiendas en Tránsito

```
Asunto: REPREP["ENCOMIENDAS_TRANSITO","*"]
```

### Reporte de Pagos Pendientes

```
Asunto: REPREP["PAGOS_PENDIENTES","*"]
```

### Reporte de Rutas Populares

**Por mes:**
```
Asunto: REPREP["RUTAS_POPULARES","2025-11"]
```

**Todas:**
```
Asunto: REPREP["RUTAS_POPULARES","*"]
```

---

## 🧪 CASOS DE PRUEBA ESPECÍFICOS

### Validación de Disponibilidad de Asiento

**Intentar reservar un asiento ya ocupado:**
```
1. Primero:
   Asunto: INSBOL["BOL-100","1","ABC-123","Persona 1","1111111","5","50.00","2025-12-01","14:00","qr_100.png"]

2. Luego (debería dar error):
   Asunto: INSBOL["BOL-101","1","ABC-123","Persona 2","2222222","5","50.00","2025-12-01","14:00","qr_101.png"]
```

### Validación de Capacidad del Vehículo

**Intentar reservar asiento que excede la capacidad:**
```
Asunto: INSBOL["BOL-200","1","ABC-123","Persona 3","3333333","30","50.00","2025-12-01","14:00","qr_200.png"]
```
(Debería dar error si el vehículo ABC-123 tiene capacidad de 15)

### Validación de Monto de Cuotas

**Intentar pagar más del total:**
```
1. Primero crear venta:
   Asunto: INSVEN["VEN-100","BOLETO","BOL-002","9876543","50.00"]

2. Intentar pagar más (debería dar error):
   Asunto: INSPAG["VEN-100","1","60.00","EFECTIVO"]
```

### Validación de CI

**CI inválido (muy corto):**
```
Asunto: INSUSU["123","Juan","Pérez","CONDUCTOR","71234567","juan@mail.com","foto.jpg"]
```

### Validación de Email

**Email inválido:**
```
Asunto: INSUSU["1234567","Juan","Pérez","CONDUCTOR","71234567","email_invalido","foto.jpg"]
```

### Validación de Tipo de Usuario

**Tipo inválido:**
```
Asunto: INSUSU["1234567","Juan","Pérez","ADMINISTRADOR","71234567","juan@mail.com","foto.jpg"]
```

---

## 📋 SECUENCIA DE PRUEBA COMPLETA

### Paso 1: Crear Datos Maestros

```
1. INSUSU["8765432","Ana","García","PROPIETARIO","71234567","ana@mail.com","foto_ana.jpg"]
2. INSUSU["9876543","Pedro","Martínez","SECRETARIA","72345678","pedro@mail.com","foto_pedro.jpg"]
3. INSUSU["5432109","Luis","Sánchez","CONDUCTOR","73456789","luis@mail.com","foto_luis.jpg"]
4. INSVEH["ABC-123","Toyota","Hiace","15","8765432","foto_abc123.jpg"]
5. INSRUT["Santa Cruz - Comarapa","Santa Cruz","Comarapa","150.5","3.5","50.00","-17.7833,-63.1821","-17.9167,-64.5167"]
```

### Paso 2: Crear Boletos

```
6. INSBOL["BOL-001","1","ABC-123","Juan Pérez","1234567","1","50.00","2025-11-20","08:00","qr_bol001.png"]
7. INSBOL["BOL-002","1","ABC-123","María López","2345678","2","50.00","2025-11-20","08:00","qr_bol002.png"]
```

### Paso 3: Crear Encomiendas

```
8. INSENC["ENC-001","1","ABC-123","Juan Pérez","71111111","Ana García","72222222","Paquete","5.5","25.00","2025-11-20","foto.jpg","qr.png"]
```

### Paso 4: Registrar Ventas

```
9. INSVEN["VEN-001","BOLETO","BOL-001","9876543","50.00"]
10. INSVEN["VEN-002","ENCOMIENDA","ENC-001","9876543","25.00"]
```

### Paso 5: Registrar Pagos

```
11. INSPAG["VEN-001","1","25.00","EFECTIVO"]
12. INSPAG["VEN-001","2","25.00","EFECTIVO"]
```

### Paso 6: Actualizar Estados

```
13. UPDENC["ENC-001","EN_TRANSITO","-17.8,-63.5"]
14. UPDBOL["BOL-001","USADO"]
```

### Paso 7: Generar Reportes

```
15. REPREP["VENTAS_DIA","2025-11-20"]
16. REPREP["BOLETOS_VENDIDOS","*"]
17. REPREP["ENCOMIENDAS_TRANSITO","*"]
18. REPREP["PAGOS_PENDIENTES","*"]
19. REPREP["RUTAS_POPULARES","*"]
```

### Paso 8: Listar Todo

```
20. LISUSU["*"]
21. LISVEH["*"]
22. LISRUT["*"]
23. LISBOL["*"]
24. LISENC["*"]
25. LISVEN["*"]
```

---

## ⚠️ COMANDOS INVÁLIDOS (Para Probar Validaciones)

### Sintaxis Incorrecta

```
Asunto: INSUSU[1234567,Juan,Pérez,CONDUCTOR]
(Falta comillas)

Asunto: INSERTAR_USUARIO["1234567","Juan","Pérez","CONDUCTOR"]
(Comando no existe)

Asunto: INSUSU1234567JuanPérezCONDUCTOR
(Sin corchetes ni parámetros)
```

### Parámetros Insuficientes

```
Asunto: INSUSU["1234567","Juan"]
(Faltan parámetros obligatorios)

Asunto: INSBOL["BOL-001"]
(Faltan parámetros)
```

### Datos Inválidos

```
Asunto: INSUSU["123","Juan","Pérez","CONDUCTOR","71234567","juan@mail.com","foto.jpg"]
(CI muy corto)

Asunto: INSVEH["ABC-123","Toyota","Hiace","-5","8765432","foto.jpg"]
(Capacidad negativa)

Asunto: INSBOL["BOL-001","1","ABC-123","Juan","1234567","15","50.00","2025-13-40","08:00","qr.png"]
(Fecha inválida)
```

---

## 📝 NOTAS IMPORTANTES

1. **Esperar respuesta:** Cada comando será respondido automáticamente. Espera la respuesta antes de enviar el siguiente.

2. **IDs y Códigos:** Asegúrate de usar códigos únicos para cada entidad (códigos de boleto, encomienda, venta, etc.).

3. **Referencias:** Para ventas y pagos, debes usar códigos que existan previamente (boletos, encomiendas).

4. **Fechas:** Usa formato `yyyy-MM-dd` (ej: 2025-11-20).

5. **Horas:** Usa formato `HH:mm` (ej: 08:00, 14:30).

6. **GPS:** Usa formato `lat,long` (ej: -17.7833,-63.1821).

7. **Scheduler:** El sistema revisa correos cada 60 segundos, así que puede tomar hasta 1 minuto recibir la respuesta.

---

## 🎯 CRITERIOS DE ÉXITO

Una prueba es exitosa si:
- ✅ El sistema responde automáticamente
- ✅ La respuesta tiene formato correcto
- ✅ El estado es EXITOSO para comandos válidos
- ✅ El estado es ERROR para comandos inválidos con mensaje descriptivo
- ✅ Los datos se guardan correctamente en la BD
- ✅ Las validaciones funcionan correctamente

---

**Preparado por:** Grupo04 SA  
**Fecha:** 13/11/2025  
**Para uso en:** Fase 8 - Pruebas y Validación

