# Sistema Trans Comarapa - Gestión vía Correo Electrónico

Sistema de gestión de transporte que opera completamente vía correo electrónico, desarrollado para la empresa Trans Comarapa.

## 📋 Información del Proyecto

- **Empresa:** Trans Comarapa
- **Grupo:** Grupo04 SA
- **Correo del Sistema:** grupo04sa@tecnoweb.org.bo
- **Base de Datos:** db_grupo04sa
- **Tecnología:** Spring Boot 3.5.7 + PostgreSQL + JavaMail

## 🎯 Casos de Uso

1. **CU1:** Gestión de Usuarios (Propietarios, Secretarias, Conductor) - Fotos
2. **CU2:** Gestión de Vehículos - Fotos
3. **CU3:** Gestión de Rutas (Origen, Destino) - GPS
4. **CU4:** Gestión de Boletos (Rutas, Vehículos) - QR
5. **CU5:** Gestión de Encomienda - Foto, QR, GPS
6. **CU6:** Gestión de Ventas
7. **CU7:** Gestión de Pagos (dos cuotas)
8. **CU8:** Reportes y Estadísticas

## 🚀 Estado Actual

### ✅ FASE 1 - Configuración y Base (COMPLETADA)

- ✅ Base de datos diseñada e implementada (8 tablas)
- ✅ Entidades JPA creadas (8 entidades)
- ✅ Repositorios JPA implementados (8 repositorios)
- ✅ Configuración de correo electrónico (IMAP/SMTP)
- ✅ Servicio de correo electrónico funcional
- ✅ Programador de tareas implementado
- ✅ Datos de prueba cargados

### 🔄 FASE 2 - Core del Sistema (PENDIENTE)

- Parser de comandos
- Validador de comandos
- Ejecutor de comandos
- Formateador de respuestas
- DTOs y manejo de excepciones

## 📦 Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- PostgreSQL (acceso a db_grupo04sa)
- Credenciales de correo (grupo04sa@tecnoweb.org.bo)

## 🔧 Instalación

### 1. Clonar el repositorio

```bash
git clone <url-repositorio>
cd sistema_via_mail
```

### 2. Configurar base de datos

```bash
# Conectar a PostgreSQL
psql -h www.tecnoweb.org.bo -U grupo04sa -d db_grupo04sa

# Ejecutar scripts
\i src/main/resources/db/schema.sql
\i src/main/resources/db/data.sql
```

### 3. Verificar configuración

Revisar `src/main/resources/application.properties` y confirmar:
- Credenciales de base de datos
- Credenciales de correo electrónico
- Configuraciones IMAP/SMTP

### 4. Compilar el proyecto

```bash
mvn clean compile
```

### 5. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

## 📧 Probar el Sistema

1. Enviar un correo a: `grupo04sa@tecnoweb.org.bo`
2. Asunto: `PRUEBA-FASE1`
3. Esperar hasta 1 minuto (el sistema revisa cada 60 segundos)
4. Verificar respuesta automática en tu bandeja de entrada

## 📁 Estructura del Proyecto

```
sistema_via_mail/
├── docs/
│   ├── enunciado_proyecto.md
│   ├── plan_implementacion.md
│   └── fase1_resultados.md
├── src/
│   ├── main/
│   │   ├── java/com/example/sistema_via_mail/
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositorios
│   │   │   ├── service/         # Servicios
│   │   │   └── scheduler/       # Tareas programadas
│   │   └── resources/
│   │       ├── db/              # Scripts SQL
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## 🗄️ Modelo de Base de Datos

### Tablas Principales

- **usuario** - Gestión de usuarios (propietarios, secretarias, conductores)
- **vehiculo** - Gestión de vehículos
- **ruta** - Rutas disponibles con coordenadas GPS
- **boleto** - Boletos de pasajeros con código QR
- **encomienda** - Envío de encomiendas con tracking GPS
- **venta** - Registro de ventas
- **pago** - Gestión de pagos en cuotas
- **reporte** - Almacenamiento de reportes generados

## 📝 Formato de Comandos (Próximamente en Fase 2)

### Sintaxis General
```
<OPERACION><ENTIDAD>[<parametros>]
```

### Operaciones
- `LIS` - Listar/Consultar
- `INS` - Insertar
- `UPD` - Actualizar
- `DEL` - Eliminar

### Entidades
- `USU` - Usuario
- `VEH` - Vehículo
- `RUT` - Ruta
- `BOL` - Boleto
- `ENC` - Encomienda
- `VEN` - Venta
- `PAG` - Pago
- `REP` - Reporte

### Ejemplo
```
Subject: LISUSU["*"]
```
(Listará todos los usuarios una vez implementada la Fase 2)

## 🔍 Logs

El sistema genera logs detallados:

```bash
# Ver logs en tiempo real
tail -f logs/sistema-via-mail.log
```

Nivel de logs: DEBUG (configurable en `application.properties`)

## ⚙️ Configuración

### Intervalo de revisión de correos

Modificar en `application.properties`:
```properties
scheduler.email.check.interval=60000  # 60 segundos
```

### Máximo de correos por ciclo

```properties
email.max.messages.per.cycle=10
```

## 🐛 Solución de Problemas

### Error de conexión a base de datos
- Verificar que el servidor PostgreSQL esté accesible
- Confirmar credenciales en `application.properties`
- Verificar firewall y permisos

### Error de conexión de correo
- Verificar credenciales de correo
- Confirmar que los puertos 993 (IMAP) y 465 (SMTP) estén abiertos
- Revisar configuración SSL

### No se reciben correos
- Verificar que el scheduler esté habilitado (`@EnableScheduling`)
- Revisar logs para ver si hay errores de conexión
- Confirmar que el correo no esté en spam

## 📚 Documentación

- [Plan de Implementación](docs/plan_implementacion.md) - Plan completo del proyecto
- [Resultados Fase 1](docs/fase1_resultados.md) - Documentación detallada de la Fase 1
- [Enunciado del Proyecto](docs/enunciado_proyecto.md) - Especificaciones originales

## 👥 Equipo

- **Grupo:** Grupo04 SA
- **Materia:** INF-513 Tecnología Web
- **Gestión:** 2025
- **Universidad:** UAGRM

## 📄 Licencia

Proyecto académico - Universidad Autónoma Gabriel René Moreno (UAGRM)

---

**Última actualización:** 13 de Noviembre de 2025  
**Versión:** 1.0.0 (Fase 1)  
**Estado:** ✅ Fase 1 Completada - 🔄 Fase 2 en Preparación

