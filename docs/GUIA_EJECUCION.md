# 🚀 GUÍA DE EJECUCIÓN
## Sistema Trans Comarapa - Vía Correo Electrónico

**Versión:** 1.0  
**Fecha:** Noviembre 2025  
**Grupo:** Grupo04 SA

---

## 📋 PRE-REQUISITOS

### Software Requerido

- ✅ **Java JDK 17** o superior
- ✅ **Maven 3.6+**
- ✅ **PostgreSQL 12+** (para producción)
- ✅ **Git** (opcional, para clonar)
- ✅ **IDE** recomendado: IntelliJ IDEA, Eclipse, VS Code

### Verificar Instalaciones

```bash
# Verificar Java
java -version
# Debe mostrar: openjdk version "17" o superior

# Verificar Maven
mvn -version
# Debe mostrar: Apache Maven 3.6.x o superior

# Verificar PostgreSQL
psql --version
# Debe mostrar: psql (PostgreSQL) 12.x o superior
```

---

## 🔧 CONFIGURACIÓN INICIAL

### 1. Clonar o Descargar el Proyecto

```bash
# Si tienes Git
git clone <url-del-repositorio>
cd sistema_via_mail

# O descomprimir el ZIP
unzip sistema_via_mail.zip
cd sistema_via_mail
```

### 2. Configurar Base de Datos (Producción)

La configuración actual apunta a:
- **Host:** www.tecnoweb.org.bo:5432
- **Base de Datos:** db_grupo04sa
- **Usuario:** grupo04sa
- **Password:** grup004grup004*

Si necesitas usar una base de datos local:

```bash
# Crear base de datos local
psql -U postgres
CREATE DATABASE sistema_via_mail;
CREATE USER sistema_user WITH PASSWORD 'sistema_pass';
GRANT ALL PRIVILEGES ON DATABASE sistema_via_mail TO sistema_user;
\q
```

Luego editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sistema_via_mail
spring.datasource.username=sistema_user
spring.datasource.password=sistema_pass
```

### 3. Crear Tablas (Primera Vez)

```bash
# Las tablas se crean automáticamente con JPA
# O ejecutar manualmente:
psql -U sistema_user -d sistema_via_mail -f src/main/resources/db/schema.sql
```

### 4. Cargar Datos de Prueba (Opcional)

```bash
psql -U sistema_user -d sistema_via_mail -f src/main/resources/db/data.sql
```

### 5. Aplicar Índices de Optimización

```bash
psql -U sistema_user -d sistema_via_mail -f src/main/resources/db/optimization.sql
```

---

## 🧪 EJECUTAR TESTS

### Tests Unitarios

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=CommandParserServiceTest
mvn test -Dtest=CommandValidatorTest
mvn test -Dtest=ResponseFormatterTest
```

### Tests de Integración

```bash
# Ejecutar tests de integración
mvn test -Dtest=CommandIntegrationTest

# Ejecutar con perfil de test
mvn test -Dspring.profiles.active=test
```

### Ver Reporte de Tests

```bash
# Generar reporte
mvn surefire-report:report

# El reporte estará en:
# target/site/surefire-report.html
```

---

## 🔨 COMPILAR EL PROYECTO

### Compilación Limpia

```bash
# Limpiar compilaciones anteriores y compilar
mvn clean install

# Sin tests (más rápido)
mvn clean install -DskipTests
```

### Verificar Compilación

```bash
# Debe mostrar: BUILD SUCCESS
# El JAR estará en: target/sistema_via_mail-0.0.1-SNAPSHOT.jar
```

---

## ▶️ EJECUTAR LA APLICACIÓN

### Modo Desarrollo (con Maven)

```bash
# Ejecutar directamente con Maven
mvn spring-boot:run

# Con perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Modo Producción (JAR)

```bash
# Compilar primero
mvn clean package -DskipTests

# Ejecutar JAR
java -jar target/sistema_via_mail-0.0.1-SNAPSHOT.jar

# Con perfil de producción
java -jar target/sistema_via_mail-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Ejecutar en Background (Linux/Mac)

```bash
nohup java -jar target/sistema_via_mail-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

# Ver logs en tiempo real
tail -f app.log
```

### Ejecutar en Background (Windows)

```powershell
# Usando PowerShell
Start-Process java -ArgumentList "-jar","target/sistema_via_mail-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden -RedirectStandardOutput "app.log" -RedirectStandardError "error.log"
```

---

## 📊 VERIFICAR QUE EL SISTEMA ESTÁ FUNCIONANDO

### 1. Verificar Logs

```bash
# Ver logs de inicio
tail -f logs/sistema_via_mail.log

# Buscar líneas importantes:
# "Started SistemaViaMailApplication"
# "Iniciando revisión de correos"
```

### 2. Verificar Scheduler

Deberías ver en los logs cada minuto:

```
===============================================
Iniciando revisión de correos #1
Fecha/Hora: 2025-11-13 10:30:00
===============================================
```

### 3. Enviar Correo de Prueba

```
Para: grupo04sa@tecnoweb.org.bo
Asunto: LISUSU["*"]
Cuerpo: (vacío)
```

Espera máximo 60 segundos y deberías recibir una respuesta.

---

## 🐛 DEBUGGING

### Habilitar Logs Detallados

Editar `application.properties`:

```properties
# Logs más detallados
logging.level.com.example.sistema_via_mail=DEBUG
logging.level.org.springframework.mail=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Desactivar Scheduler Temporalmente

Para testing sin correos, comentar en `SistemaViaMailApplication.java`:

```java
// @EnableScheduling  // <-- Comentar esta línea
public class SistemaViaMailApplication {
    // ...
}
```

### Probar Conexión a Base de Datos

```bash
# Desde línea de comandos
psql -h www.tecnoweb.org.bo -p 5432 -U grupo04sa -d db_grupo04sa

# Verificar tablas
\dt

# Verificar datos
SELECT COUNT(*) FROM usuario;
```

### Probar Conexión a Correo

Ejecutar: `test_puertos.ps1` (Windows) o crear script similar para Linux

---

## 📦 EMPAQUETAR PARA DISTRIBUCIÓN

### Crear JAR Ejecutable

```bash
mvn clean package -DskipTests

# El JAR estará en:
# target/sistema_via_mail-0.0.1-SNAPSHOT.jar
```

### Crear Distribución Completa

```bash
# Crear carpeta de distribución
mkdir -p dist
cp target/sistema_via_mail-0.0.1-SNAPSHOT.jar dist/
cp -r docs dist/
cp README.md dist/
cp application.properties.example dist/

# Crear ZIP
cd dist
zip -r sistema_via_mail_v1.0.zip *
```

---

## 🔄 ACTUALIZAR EL SISTEMA

### Sin Perder Datos

```bash
# 1. Detener aplicación
ps aux | grep sistema_via_mail
kill <PID>

# 2. Backup de base de datos
pg_dump -U sistema_user sistema_via_mail > backup_$(date +%Y%m%d).sql

# 3. Actualizar código
git pull  # o copiar nuevos archivos

# 4. Recompilar
mvn clean install

# 5. Aplicar migraciones si hay
psql -U sistema_user -d sistema_via_mail -f migrations/update.sql

# 6. Reiniciar
java -jar target/sistema_via_mail-0.0.1-SNAPSHOT.jar
```

---

## 🛡️ PRODUCCIÓN - MEJORES PRÁCTICAS

### 1. Variables de Entorno

En lugar de hardcodear credenciales en `application.properties`:

```bash
# Linux/Mac
export DB_PASSWORD="mi_password_seguro"
export MAIL_PASSWORD="mi_mail_password"

# Windows
set DB_PASSWORD=mi_password_seguro
set MAIL_PASSWORD=mi_mail_password
```

En `application.properties`:

```properties
spring.datasource.password=${DB_PASSWORD}
spring.mail.password=${MAIL_PASSWORD}
```

### 2. Usar Servicio Systemd (Linux)

Crear `/etc/systemd/system/sistema-via-mail.service`:

```ini
[Unit]
Description=Sistema Trans Comarapa
After=network.target postgresql.service

[Service]
Type=simple
User=sistema
WorkingDirectory=/opt/sistema_via_mail
ExecStart=/usr/bin/java -jar sistema_via_mail.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Habilitar e iniciar:

```bash
sudo systemctl daemon-reload
sudo systemctl enable sistema-via-mail
sudo systemctl start sistema-via-mail
sudo systemctl status sistema-via-mail
```

### 3. Rotación de Logs

Configurar en `application.properties`:

```properties
logging.file.name=logs/sistema_via_mail.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.pattern.rolling-file-name=logs/sistema_via_mail-%d{yyyy-MM-dd}.%i.log
```

### 4. Monitoreo

```bash
# Verificar que esté corriendo
ps aux | grep sistema_via_mail

# Ver uso de recursos
top -p <PID>

# Ver logs en tiempo real
tail -f logs/sistema_via_mail.log

# Verificar conectividad a BD
psql -h www.tecnoweb.org.bo -U grupo04sa -d db_grupo04sa -c "SELECT 1"
```

---

## 📋 CHECKLIST DE DESPLIEGUE

### Antes de Desplegar

- [ ] Tests pasando: `mvn test`
- [ ] Compilación exitosa: `mvn clean package`
- [ ] Base de datos accesible
- [ ] Credenciales de correo válidas
- [ ] IP habilitada en servidor de correo
- [ ] Backup de BD actual
- [ ] Logs configurados

### Después de Desplegar

- [ ] Aplicación iniciada correctamente
- [ ] Scheduler ejecutándose
- [ ] Primer correo de prueba exitoso
- [ ] Logs sin errores críticos
- [ ] Monitoreo configurado
- [ ] Usuarios notificados

---

## 🆘 SOLUCIÓN DE PROBLEMAS COMUNES

### Error: "Could not connect to database"

**Solución:**
1. Verificar que PostgreSQL esté corriendo
2. Verificar credenciales en `application.properties`
3. Verificar firewall/reglas de red
4. Probar conexión manual con `psql`

### Error: "Authentication failed for mail server"

**Solución:**
1. Verificar credenciales de correo
2. Verificar que la IP esté habilitada
3. Probar puerto correcto (110 para POP3, 25 para SMTP)
4. Verificar que SSL/TLS esté configurado correctamente

### Error: "Port already in use"

**Solución:**
```bash
# Ver qué proceso usa el puerto
lsof -i :8080  # Linux/Mac
netstat -ano | findstr :8080  # Windows

# Matar proceso
kill <PID>  # Linux/Mac
taskkill /PID <PID> /F  # Windows

# O cambiar puerto en application.properties
server.port=8081
```

### Error: "Out of Memory"

**Solución:**
```bash
# Aumentar memoria heap
java -Xms512m -Xmx2048m -jar sistema_via_mail.jar
```

---

## 📞 SOPORTE

**Email:** grupo04sa@tecnoweb.org.bo  
**Documentación:** `/docs`  
**Tests:** `mvn test`  
**Logs:** `logs/sistema_via_mail.log`

---

## ✅ COMANDOS RÁPIDOS

```bash
# Compilar
mvn clean install

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# Ver logs
tail -f logs/sistema_via_mail.log

# Verificar BD
psql -h www.tecnoweb.org.bo -U grupo04sa -d db_grupo04sa

# Detener aplicación
ps aux | grep sistema_via_mail
kill <PID>
```

---

**Sistema Trans Comarapa**  
**Grupo04 SA - Noviembre 2025**  
**¡Listo para producción!** 🚀

