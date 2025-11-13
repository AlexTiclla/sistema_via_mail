# CONFIGURACIÓN POP3 - Sistema Trans Comarapa

## 📅 Fecha: 13 de Noviembre de 2025

---

## 🔍 DIAGNÓSTICO INICIAL

### Test de Puertos Realizado

```
Puerto 25  (SMTP sin SSL)    ✅ ABIERTO
Puerto 110 (POP3 sin SSL)    ✅ ABIERTO
Puerto 143 (IMAP sin SSL)    ❌ CERRADO
Puerto 465 (SMTP con SSL)    ❌ CERRADO
Puerto 587 (SMTP STARTTLS)   ❌ CERRADO
Puerto 993 (IMAP con SSL)    ❌ CERRADO
Puerto 995 (POP3 con SSL)    ❌ CERRADO
```

### Conclusión
El servidor `mail.tecnoweb.org.bo` **SOLO permite conexiones sin cifrado SSL/TLS**.

---

## ⚙️ CAMBIOS REALIZADOS

### 1. Configuración SMTP (Envío de Correos)

**Antes:**
```properties
spring.mail.port=465              # Puerto SSL
mail.smtp.ssl.enable=true        # SSL habilitado
```

**Después:**
```properties
spring.mail.port=25               # Puerto sin SSL
mail.smtp.ssl.enable=false       # SSL deshabilitado
```

### 2. Configuración de Recepción de Correos

**Antes (IMAP con SSL):**
```properties
mail.store.protocol=imaps
mail.imap.host=mail.tecnoweb.org.bo
mail.imap.port=993
mail.imap.ssl.enable=true
```

**Después (POP3 sin SSL):**
```properties
mail.store.protocol=pop3
mail.pop3.host=mail.tecnoweb.org.bo
mail.pop3.port=110
mail.pop3.ssl.enable=false
mail.pop3.auth=true
```

### 3. Cambios en EmailService.java

```java
// Antes
@Value("${spring.mail.properties.mail.imap.host}")
private String imapHost;

@Value("${spring.mail.properties.mail.imap.port}")
private int imapPort;

// Después
@Value("${spring.mail.properties.mail.pop3.host}")
private String pop3Host;

@Value("${spring.mail.properties.mail.pop3.port}")
private int pop3Port;
```

```java
// Antes
logger.info("Conectando al servidor IMAP: {}:{}", imapHost, imapPort);
props.setProperty("mail.store.protocol", "imaps");
store = session.getStore("imaps");

// Después
logger.info("Conectando al servidor POP3: {}:{}", pop3Host, pop3Port);
props.setProperty("mail.store.protocol", "pop3");
store = session.getStore("pop3");
```

### 4. Scheduler Habilitado

```java
@EnableScheduling  // HABILITADO - usando POP3 puerto 110 sin SSL
```

---

## 🔄 DIFERENCIAS: POP3 vs IMAP

| Característica | POP3 | IMAP |
|----------------|------|------|
| **Descarga correos** | ✅ Sí, y los elimina del servidor* | ⚠️ Sincroniza, mantiene en servidor |
| **Acceso offline** | ✅ Sí | ❌ Limitado |
| **Múltiples dispositivos** | ❌ No recomendado | ✅ Ideal |
| **Puerto estándar** | 110 (sin SSL) / 995 (con SSL) | 143 (sin SSL) / 993 (con SSL) |
| **Uso de espacio** | 📱 En cliente | ☁️ En servidor |
| **Velocidad** | ⚡ Rápido (descarga todo) | 🐌 Más lento (sincroniza) |

**Para este proyecto**: POP3 es suficiente, ya que procesamos correos de forma automática.

---

## ✅ VERIFICACIÓN DE CONFIGURACIÓN

### application.properties - Configuración Final

```properties
# SMTP (Envío) - Puerto 25 sin SSL
spring.mail.port=25
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.ssl.enable=false
spring.mail.properties.mail.smtp.connectiontimeout=10000
spring.mail.properties.mail.smtp.timeout=10000

# POP3 (Recepción) - Puerto 110 sin SSL
spring.mail.properties.mail.store.protocol=pop3
spring.mail.properties.mail.pop3.host=mail.tecnoweb.org.bo
spring.mail.properties.mail.pop3.port=110
spring.mail.properties.mail.pop3.ssl.enable=false
spring.mail.properties.mail.pop3.auth=true
spring.mail.properties.mail.pop3.connectiontimeout=10000
spring.mail.properties.mail.pop3.timeout=10000
```

---

## 🚀 CÓMO PROBAR

### Paso 1: Compilar el proyecto
```bash
mvn clean compile
```

### Paso 2: Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### Paso 3: Verificar logs de inicio
Deberías ver:
```
=================================================
SISTEMA TRANS COMARAPA - Iniciado correctamente
=================================================
Correo monitoreado: grupo04sa@tecnoweb.org.bo
Protocolo: POP3 (Puerto 110 - Sin SSL)
Base de datos: db_grupo04sa
Fase de implementación: FASE 1 - Configuración y Base
=================================================
```

### Paso 4: Observar logs del scheduler
Cada 60 segundos verás:
```
===============================================
Iniciando revisión de correos #1
Fecha/Hora: 2025-11-13 15:30:00
===============================================
Conectando al servidor POP3: mail.tecnoweb.org.bo:110
Conexión POP3 establecida exitosamente
Mensajes no leídos encontrados: X
...
```

### Paso 5: Enviar correo de prueba
1. Envía un correo a: `grupo04sa@tecnoweb.org.bo`
2. Asunto: `PRUEBA-POP3`
3. Espera hasta 1 minuto
4. Verifica respuesta automática en tu bandeja de entrada

---

## 🔒 CONSIDERACIONES DE SEGURIDAD

### ⚠️ ADVERTENCIA: Conexión sin cifrado

La configuración actual usa puertos **sin SSL/TLS**:
- ❌ Las credenciales viajan en texto plano
- ❌ Los correos no están cifrados en tránsito
- ❌ Vulnerable a ataques man-in-the-middle

### ¿Por qué esta configuración?

El servidor `mail.tecnoweb.org.bo` **bloquea los puertos SSL/TLS** (465, 587, 993, 995) desde redes externas.

### Recomendaciones

1. **En producción**: Solicitar apertura de puertos SSL o usar VPN
2. **Para este proyecto académico**: La configuración actual es aceptable
3. **En el futuro**: Migrar a puertos seguros cuando estén disponibles

---

## 📝 NOTAS ADICIONALES

### Comportamiento de POP3 en este proyecto

**Configuración actual:**
```properties
email.mark.as.read=true
```

Esto significa que:
- ✅ Los correos se marcan como leídos después de procesarlos
- ⚠️ Con POP3, esto puede significar que se eliminan del servidor (depende del servidor)
- 💡 Si quieres mantenerlos: cambiar a `email.mark.as.read=false`

### Alternativas de configuración

**Si quieres mantener correos en el servidor:**
```properties
email.mark.as.read=false
```

**Si experimentas problemas con INBOX:**
```properties
email.inbox.folder=INBOX  # o probar: Inbox, inbox
```

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Error: "Connection refused"
- ✅ RESUELTO: Cambiando a puerto 110 (POP3 sin SSL)

### Error: "Authentication failed"
- Verificar credenciales en `application.properties`
- Confirmar que el usuario existe en el servidor

### Error: "Unknown host"
- Verificar conectividad: `ping mail.tecnoweb.org.bo`
- Confirmar DNS funcionando

### No se reciben correos
- Verificar que haya correos no leídos en la cuenta
- Revisar logs para ver mensajes de error
- Confirmar que el scheduler está habilitado

---

## 📊 ESTADO ACTUAL DEL PROYECTO

| Componente | Estado | Detalles |
|------------|--------|----------|
| Base de datos | ✅ | PostgreSQL conectado |
| SMTP (envío) | ✅ | Puerto 25 sin SSL |
| POP3 (recepción) | ✅ | Puerto 110 sin SSL |
| Scheduler | ✅ | Revisión cada 60 segundos |
| Entidades JPA | ✅ | 8 entidades creadas |
| Repositorios | ✅ | 8 repositorios funcionales |
| FASE 1 | ✅ | Completada al 100% |

---

## 🎯 PRÓXIMOS PASOS

Con la configuración POP3 funcionando, el siguiente paso es:

### FASE 2: Core del Sistema
1. Implementar CommandParserService
2. Implementar CommandValidator  
3. Implementar CommandExecutorService
4. Implementar ResponseFormatter
5. Crear DTOs (CommandRequest, CommandResponse)
6. Manejo de excepciones

Esto permitirá que el sistema **procese comandos reales** en lugar de solo enviar respuestas automáticas.

---

**Documento generado:** 13 de Noviembre de 2025  
**Versión:** 1.0  
**Estado:** ✅ POP3 Configurado y Funcionando

