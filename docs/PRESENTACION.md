# 🚌 SISTEMA DE GESTIÓN TRANS COMARAPA
## Vía Correo Electrónico

**Grupo04 SA**  
**Tecnología Web - INF513**  
**Noviembre 2025**

---

## 📋 AGENDA

1. Introducción al Proyecto
2. Problemática y Solución
3. Arquitectura del Sistema
4. Casos de Uso Implementados
5. Demostración en Vivo
6. Estadísticas y Resultados
7. Conclusiones y Trabajo Futuro

---

## 1️⃣ INTRODUCCIÓN AL PROYECTO

### Trans Comarapa

- **Empresa:** Transporte interprovincial
- **Servicios:** Pasajeros y encomiendas
- **Rutas:** Santa Cruz, Cochabamba, Comarapa, Samaipata

### Necesidad

Sistema de gestión **accesible desde cualquier lugar**, **sin necesidad de internet constante**, utilizando **tecnología universal**: **Correo Electrónico**

---

## 2️⃣ PROBLEMÁTICA Y SOLUCIÓN

### Problemática

- ❌ Falta de sistemas digitales en zonas rurales
- ❌ Acceso limitado a internet
- ❌ Personal no familiarizado con sistemas complejos
- ❌ Necesidad de acceso desde múltiples ubicaciones

### Nuestra Solución

- ✅ **100% vía correo electrónico**
- ✅ Funciona con cualquier cliente de correo
- ✅ No requiere instalación de software
- ✅ Respuestas automáticas en menos de 60 segundos
- ✅ Interfaz simple: comandos en el asunto del correo

---

## 3️⃣ ARQUITECTURA DEL SISTEMA

### Tecnologías Utilizadas

```
┌─────────────────────────────────────┐
│      Spring Boot 3.5.7              │
│      - Spring Data JPA              │
│      - JavaMail API                 │
│      - Spring Scheduler             │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│      PostgreSQL                     │
│      - 8 tablas normalizadas        │
│      - Índices optimizados          │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│   Servidor de Correo (POP3/SMTP)   │
│   mail.tecnoweb.org.bo              │
└─────────────────────────────────────┘
```

### Flujo de Procesamiento

```
Usuario → Email → Scheduler → Parser → Validator
                                           ↓
                                      Executor
                                           ↓
                                   Services (8)
                                           ↓
                                     Database
                                           ↓
                                    Formatter
                                           ↓
                                   Email Response
```

---

## 4️⃣ CASOS DE USO IMPLEMENTADOS

### CU1: Gestión de Usuarios ✅
- Propietarios, Secretarias, Conductores
- CRUD completo vía comandos
- Validaciones de CI, email, teléfono

### CU2: Gestión de Vehículos ✅
- Registro de vehículos con propietario
- Estados: DISPONIBLE, EN_RUTA, MANTENIMIENTO
- Validación de capacidad

### CU3: Gestión de Rutas ✅
- Rutas con coordenadas GPS
- Distancia, duración, precio
- Estados: ACTIVO, INACTIVO

### CU4: Gestión de Boletos ✅
- Venta de boletos con validación de asientos
- Prevención de doble reserva
- Generación de códigos QR
- Estados: VENDIDO, USADO, CANCELADO

---

## 4️⃣ CASOS DE USO (cont.)

### CU5: Gestión de Encomiendas ✅
- Registro con remitente y destinatario
- **Tracking GPS en tiempo real**
- Estados: REGISTRADA, EN_TRANSITO, ENTREGADA
- Soporte para fotos

### CU6: Gestión de Ventas ✅
- Registro de ventas de boletos y encomiendas
- Validación de vendedor autorizado
- Historial completo

### CU7: Gestión de Pagos ✅
- **Sistema de cuotas (hasta 2)**
- Métodos: EFECTIVO, TRANSFERENCIA, TARJETA
- Control de saldos pendientes

### CU8: Reportes y Estadísticas ✅
- 5 tipos de reportes
- Estadísticas en tiempo real
- Exportables vía email

---

## 5️⃣ DEMOSTRACIÓN EN VIVO

### Ejemplo 1: Registrar un Conductor

**Comando enviado:**
```
Para: grupo04sa@tecnoweb.org.bo
Asunto: INSUSU["1234567","Juan","Pérez","CONDUCTOR",
"71234567","juan@mail.com","foto.jpg"]
```

**Respuesta recibida (< 60 seg):**
```
COMANDO: INSUSU
ESTADO: EXITOSO
MENSAJE: Usuario registrado correctamente

DATOS:
- ID: 15
- CI: 1234567
- Nombre: Juan Pérez
- Tipo: CONDUCTOR
- Estado: ACTIVO
```

---

## 5️⃣ DEMOSTRACIÓN (cont.)

### Ejemplo 2: Vender un Boleto

**Comando:**
```
Asunto: INSBOL["BOL-100","1","ABC-123","María López",
"5544332","10","50.00","2025-11-20","14:00","qr_bol100.png"]
```

**El sistema valida:**
- ✅ Ruta existe (ID: 1)
- ✅ Vehículo existe (ABC-123)
- ✅ Asiento disponible (10)
- ✅ Asiento no excede capacidad
- ✅ Fecha y hora válidas

**Respuesta:**
```
ESTADO: EXITOSO
MENSAJE: Boleto registrado correctamente
DATOS: [Información completa del boleto]
```

---

## 5️⃣ DEMOSTRACIÓN (cont.)

### Ejemplo 3: Tracking de Encomienda

**Actualizar ubicación:**
```
Asunto: UPDENC["ENC-001","EN_TRANSITO","-17.8,-63.5"]
```

**Consultar ubicación:**
```
Asunto: LISENC["ENC-001"]
```

**Respuesta incluye:**
- Estado actual
- Coordenadas GPS
- Información de remitente y destinatario
- Ruta y vehículo asignado

---

## 5️⃣ DEMOSTRACIÓN (cont.)

### Ejemplo 4: Reporte de Ventas

**Comando:**
```
Asunto: REPREP["VENTAS_DIA","2025-11-20"]
```

**Respuesta incluye:**
- Total de ventas del día
- Desglose: boletos vs encomiendas
- Monto total recaudado
- Detalle de cada venta

---

## 6️⃣ ESTADÍSTICAS Y RESULTADOS

### Cobertura del Sistema

| Componente | Cantidad | Estado |
|------------|----------|--------|
| **Entidades** | 8 | ✅ 100% |
| **Repositorios** | 8 | ✅ 100% |
| **Servicios** | 10 | ✅ 100% |
| **Comandos** | 35+ | ✅ 100% |
| **Reportes** | 5 | ✅ 100% |

### Líneas de Código

- **Backend:** ~4,000 líneas Java
- **Tests:** ~600 líneas
- **Total:** 4,600+ líneas

---

## 6️⃣ ESTADÍSTICAS (cont.)

### Validaciones Implementadas

- ✅ **Sintaxis:** 35+ validaciones de formato
- ✅ **Datos:** CI, email, teléfono, GPS, fechas
- ✅ **Negocio:** Disponibilidad, capacidad, permisos
- ✅ **Integridad:** Referencias, estados, montos

### Optimizaciones

- ✅ **Índices:** 25+ índices en base de datos
- ✅ **Caché:** Conexiones reutilizadas
- ✅ **Transacciones:** ACID compliance
- ✅ **Performance:** Respuestas < 60 segundos

---

## 6️⃣ ESTADÍSTICAS (cont.)

### Tests Implementados

```
✅ Tests Unitarios:
   - CommandParserServiceTest (12 tests)
   - CommandValidatorTest (15 tests)
   - ResponseFormatterTest (10 tests)

✅ Tests de Integración:
   - CommandIntegrationTest (6 tests)
   - Flujos completos de usuario

✅ Cobertura: ~80% del código crítico
```

---

## 7️⃣ VENTAJAS DEL SISTEMA

### Para la Empresa

- ✅ **Costo:** Sin infraestructura web costosa
- ✅ **Mantenimiento:** Mínimo
- ✅ **Accesibilidad:** Desde cualquier lugar
- ✅ **Confiabilidad:** Email es protocolo estable
- ✅ **Trazabilidad:** Todos los correos quedan registrados

### Para los Usuarios

- ✅ **Facilidad:** Solo necesitan saber usar email
- ✅ **Rapidez:** Respuestas < 60 segundos
- ✅ **Disponibilidad:** 24/7
- ✅ **Comprobantes:** Correos como respaldo
- ✅ **Universal:** Funciona en cualquier dispositivo

---

## 8️⃣ DESAFÍOS Y SOLUCIONES

### Desafío 1: Parseo de Comandos

**Problema:** Interpretar comandos textuales  
**Solución:** Regex + validación estructurada

### Desafío 2: Validaciones Complejas

**Problema:** Validar múltiples reglas de negocio  
**Solución:** Validator centralizado con mensajes claros

### Desafío 3: Respuestas Legibles

**Problema:** Formatear datos para texto plano  
**Solución:** ResponseFormatter con estructura visual

### Desafío 4: Disponibilidad de Asientos

**Problema:** Prevenir doble reserva  
**Solución:** Validación con índices únicos en BD

---

## 9️⃣ COMPARACIÓN CON SISTEMAS TRADICIONALES

| Aspecto | Sistema Web | Nuestro Sistema |
|---------|-------------|-----------------|
| **Acceso** | Navegador + Internet | Email (cualquier dispositivo) |
| **Instalación** | No | No |
| **Curva de aprendizaje** | Media-Alta | Baja |
| **Disponibilidad offline** | ❌ No | ✅ Sí (envío diferido) |
| **Costo infraestructura** | Alto | Bajo |
| **Mantenimiento** | Continuo | Mínimo |
| **Registro de operaciones** | Logs | Correos permanentes |

---

## 🔟 CASOS DE USO REALES

### Escenario 1: Secretaria en Terminal

- 📍 Terminal de Santa Cruz
- 🕐 8:00 AM - mucha demanda
- 📱 Vende 5 boletos en 3 minutos
- ✅ Todo registrado automáticamente

### Escenario 2: Conductor en Ruta

- 🚌 Vehículo en tránsito
- 📦 Actualiza estado de encomiendas
- 📍 Envía coordenadas GPS
- ✅ Clientes ven ubicación en tiempo real

### Escenario 3: Propietario desde Casa

- 🏠 Desde cualquier ubicación
- 📊 Consulta reportes del día
- 💰 Revisa pagos pendientes
- ✅ Toma decisiones informadas

---

## 1️⃣1️⃣ SEGURIDAD

### Medidas Implementadas

- 🔐 **Autenticación:** Solo correos autorizados
- 🔐 **Validación:** Permisos por tipo de usuario
- 🔐 **Trazabilidad:** Registro de todas las operaciones
- 🔐 **Integridad:** Validaciones de negocio
- 🔐 **Backup:** Correos como respaldo permanente

### Futuras Mejoras de Seguridad

- 🔒 Firma digital de comandos
- 🔒 Encriptación de datos sensibles
- 🔒 2FA via código en correo
- 🔒 Rate limiting

---

## 1️⃣2️⃣ TRABAJO FUTURO

### Mejoras Planificadas - Corto Plazo

- 📱 **App móvil** con interfaz visual (complementaria)
- 🖼️ **Generación automática** de códigos QR
- 📧 **Notificaciones proactivas** (recordatorios)
- 📊 **Más reportes** (gráficos en HTML)

### Mejoras Planificadas - Mediano Plazo

- 🤖 **IA para predicción** de demanda
- 💳 **Integración con pasarelas** de pago
- 🗺️ **Mapa visual** de rutas
- 📞 **SMS** como canal alternativo

---

## 1️⃣3️⃣ LECCIONES APRENDIDAS

### Técnicas

- ✅ Email puede ser una interfaz poderosa
- ✅ La simplicidad es clave para la adopción
- ✅ Las validaciones son críticas
- ✅ Los tests previenen muchos problemas
- ✅ La documentación es tan importante como el código

### De Negocio

- ✅ Entender al usuario es fundamental
- ✅ La accesibilidad no siempre requiere web
- ✅ Los sistemas simples pueden ser muy efectivos
- ✅ El bajo costo de operación es un diferenciador

---

## 1️⃣4️⃣ CONCLUSIONES

### Objetivos Cumplidos

✅ **Sistema 100% funcional** vía correo electrónico  
✅ **8 casos de uso** completamente implementados  
✅ **35+ comandos** con validaciones completas  
✅ **Tests** unitarios y de integración  
✅ **Documentación** técnica y de usuario  
✅ **Performance** óptimo (< 60 seg respuesta)  

### Impacto del Proyecto

- 🎯 **Innovación:** Uso creativo de tecnología universal
- 🎯 **Accesibilidad:** Sistema usable por cualquier persona
- 🎯 **Escalabilidad:** Preparado para crecimiento
- 🎯 **Sostenibilidad:** Bajo costo de operación

---

## 1️⃣5️⃣ DEMOSTRACIÓN FINAL

### Demo en Vivo

1. **Crear usuario**
2. **Registrar vehículo**
3. **Vender boleto**
4. **Consultar disponibilidad**
5. **Generar reporte**

**¡Todo en menos de 5 minutos!** ⚡

---

## 📞 CONTACTO Y RECURSOS

### Equipo del Proyecto

**Grupo04 SA**  
**Email:** grupo04sa@tecnoweb.org.bo  

### Recursos Disponibles

- 📄 **Manual de Usuario Completo**
- 📄 **Documentación Técnica**
- 📄 **Guía de Comandos de Prueba**
- 💻 **Código Fuente** (GitHub)
- 🎥 **Videos de Demostración**

---

## ❓ PREGUNTAS Y RESPUESTAS

### ¿Preguntas?

Estamos listos para responder sus dudas sobre:

- 🔧 Aspectos técnicos
- 💼 Casos de uso
- 📊 Estadísticas
- 🚀 Implementación
- 📈 Escalabilidad

---

## 🙏 GRACIAS

### Agradecimientos

- **Universidad:** Por la oportunidad de desarrollar este proyecto
- **Docente:** Por la guía y retroalimentación
- **Trans Comarapa:** Por inspirar la solución
- **Equipo:** Por el compromiso y dedicación

---

## 🚀 ¡GRACIAS POR SU ATENCIÓN!

**Trans Comarapa**  
**Sistema de Gestión Vía Correo Electrónico**

**Grupo04 SA**  
**Tecnología Web - INF513**  
**Noviembre 2025**

---

**¿Listo para gestionar tu empresa desde tu correo?** 📧🚌

