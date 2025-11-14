# Diagrama de Comunicación - CU8: Reportes y Estadísticas

## Diagrama Resumido - Flujo General CU8

```mermaid
sequenceDiagram
    participant U as 👤 Usuario
    participant S as 📧 Sistema
    participant RS as 📊 ReporteService
    participant DB as 💾 PostgreSQL
    
    Note over U,DB: Comando: REPREP["tipo","parametro"]
    
    U->>+S: 1: Email con solicitud de reporte
    S->>S: 2: Parse comando
    S->>+RS: 3: processCommand()
    
    RS->>RS: 4: Identificar tipo de reporte
    
    alt VENTAS_DIA
        RS->>+DB: 5a: SELECT ventas WHERE fecha = ?
        DB-->>-RS: Ventas del día
    else BOLETOS_VENDIDOS
        RS->>+DB: 5b: SELECT boletos WHERE mes = ?
        DB-->>-RS: Boletos del mes
    else ENCOMIENDAS_TRANSITO
        RS->>+DB: 5c: SELECT encomiendas WHERE estado = 'EN_TRANSITO'
        DB-->>-RS: Encomiendas activas
    else PAGOS_PENDIENTES
        RS->>+DB: 5d: SELECT pagos WHERE estado = 'PENDIENTE'
        DB-->>-RS: Pagos pendientes
    else RUTAS_POPULARES
        RS->>+DB: 5e: SELECT COUNT(*) GROUP BY ruta
        DB-->>-RS: Estadísticas de rutas
    end
    
    RS->>RS: 6: Formatear datos en tabla/gráfico
    RS-->>-S: 7: CommandResponse con reporte
    S->>S: 8: Format respuesta HTML/texto
    S-->>U: 9: Email con reporte generado
```

## Diagrama Detallado - REPREP VENTAS_DIA

```mermaid
sequenceDiagram
    participant U as 👤 Propietario/Secretaria
    participant E as 📧 Email System
    participant RS as 📊 ReporteService
    participant DB as 💾 DB
    
    U->>E: REPREP["VENTAS_DIA","2025-11-13"]
    E->>RS: processCommand()
    RS->>DB: findVentasByFecha("2025-11-13")
    DB-->>RS: List<Venta>
    RS->>RS: Calcular totales y estadísticas
    RS->>DB: save(reporte) - Guardar histórico
    DB-->>RS: Reporte guardado
    RS-->>E: CommandResponse + datos formateados
    E-->>U: "Reporte: 10 ventas - Total: Bs. 500"
```

## Diagrama Detallado - REPREP BOLETOS_VENDIDOS

```mermaid
sequenceDiagram
    participant U as 👤 Propietario/Secretaria
    participant E as 📧 Email System
    participant RS as 📊 ReporteService
    participant DB as 💾 DB
    
    U->>E: REPREP["BOLETOS_VENDIDOS","2025-11"]
    E->>RS: processCommand()
    RS->>DB: findBoletosByMes("2025-11")
    DB-->>RS: List<Boleto>
    RS->>RS: Agrupar por ruta y fecha
    RS->>RS: Calcular ocupación por vehículo
    RS-->>E: CommandResponse + estadísticas
    E-->>U: "150 boletos vendidos en noviembre"
```

## Diagrama Detallado - REPREP ENCOMIENDAS_TRANSITO

```mermaid
sequenceDiagram
    participant U as 👤 Secretaria
    participant E as 📧 Email System
    participant RS as 📊 ReporteService
    participant DB as 💾 DB
    
    U->>E: REPREP["ENCOMIENDAS_TRANSITO","*"]
    E->>RS: processCommand()
    RS->>DB: findByEstado("EN_TRANSITO")
    DB-->>RS: List<Encomienda>
    RS->>RS: Incluir datos GPS actual
    RS-->>E: CommandResponse + ubicaciones
    E-->>U: "3 encomiendas en tránsito + GPS"
```

## Diagrama Detallado - REPREP PAGOS_PENDIENTES

```mermaid
sequenceDiagram
    participant U as 👤 Secretaria
    participant E as 📧 Email System
    participant RS as 📊 ReporteService
    participant DB as 💾 DB
    
    U->>E: REPREP["PAGOS_PENDIENTES","*"]
    E->>RS: processCommand()
    RS->>DB: findByEstado("PENDIENTE")
    DB-->>RS: List<Pago>
    RS->>RS: Calcular montos y vencimientos
    RS->>RS: Agrupar por cliente
    RS-->>E: CommandResponse + análisis
    E-->>U: "5 pagos pendientes - Total: Bs. 250"
```

## Diagrama Detallado - REPREP RUTAS_POPULARES

```mermaid
sequenceDiagram
    participant U as 👤 Propietario
    participant E as 📧 Email System
    participant RS as 📊 ReporteService
    participant DB as 💾 DB
    
    U->>E: REPREP["RUTAS_POPULARES","2025-11"]
    E->>RS: processCommand()
    RS->>DB: SELECT COUNT(*) FROM boleto GROUP BY ruta
    DB-->>RS: Estadísticas por ruta
    RS->>DB: JOIN con datos de rutas
    DB-->>RS: Rutas completas
    RS->>RS: Ordenar por popularidad
    RS-->>E: CommandResponse + ranking
    E-->>U: "Top 3: SCZ-Comarapa (50), SCZ-Samaipata (30)..."
```

## Tipos de Reportes Disponibles

| Tipo de Reporte | Parámetro | Descripción | Ejemplo |
|----------------|-----------|-------------|---------|
| **VENTAS_DIA** | Fecha (YYYY-MM-DD) | Ventas de un día específico | `REPREP["VENTAS_DIA","2025-11-13"]` |
| **BOLETOS_VENDIDOS** | Mes (YYYY-MM) | Boletos vendidos en el mes | `REPREP["BOLETOS_VENDIDOS","2025-11"]` |
| **ENCOMIENDAS_TRANSITO** | `*` | Encomiendas actualmente en tránsito | `REPREP["ENCOMIENDAS_TRANSITO","*"]` |
| **PAGOS_PENDIENTES** | `*` | Pagos que están pendientes | `REPREP["PAGOS_PENDIENTES","*"]` |
| **RUTAS_POPULARES** | Mes (YYYY-MM) | Rutas más utilizadas | `REPREP["RUTAS_POPULARES","2025-11"]` |

## Flujo de Generación de Reportes

```mermaid
graph LR
    A[Solicitud Email] --> B[Parse REPREP]
    B --> C{Tipo?}
    
    C -->|VENTAS| D1[Query Ventas]
    C -->|BOLETOS| D2[Query Boletos]
    C -->|ENCOMIENDAS| D3[Query Encomiendas]
    C -->|PAGOS| D4[Query Pagos]
    C -->|RUTAS| D5[Query Estadísticas]
    
    D1 --> E[Procesar Datos]
    D2 --> E
    D3 --> E
    D4 --> E
    D5 --> E
    
    E --> F[Formatear Tabla]
    F --> G[Guardar Reporte]
    G --> H[Enviar por Email]
    
    classDef query fill:#e3f2fd,stroke:#1976d2
    classDef process fill:#f3e5f5,stroke:#7b1fa2
    classDef output fill:#e8f5e9,stroke:#388e3c
    
    class D1,D2,D3,D4,D5 query
    class E,F,G process
    class H output
```

## Características Especiales CU8

- ✅ **Reportes históricos** guardados en tabla `reporte`
- ✅ **Datos estadísticos** con totales, promedios y agrupaciones
- ✅ **Formato tabular** fácil de leer en email
- ✅ **Parámetros flexibles** (fecha, mes, todos)
- ✅ **Múltiples tipos** de análisis disponibles
