# Diagrama de Comunicación - CU4: Gestión de Boletos

## Diagrama Resumido - Flujo General CU4

```mermaid
sequenceDiagram
    participant U as 👤 Usuario
    participant S as 📧 Sistema
    participant BS as 🎫 BoletoService
    participant DB as 💾 PostgreSQL
    
    Note over U,DB: Comandos: LISBOL | INSBOL | UPDBOL | DELBOL
    
    U->>+S: 1: Email con comando
    S->>S: 2: Parse comando
    S->>+BS: 3: processCommand()
    
    alt LISBOL - Listar
        BS->>+DB: 4a: SELECT * FROM boleto
        DB-->>-BS: Lista boletos
    else INSBOL - Insertar (Emitir)
        BS->>+DB: 4b: INSERT INTO boleto + QR
        DB-->>-BS: Boleto creado
    else UPDBOL - Actualizar Estado
        BS->>+DB: 4c: UPDATE boleto SET estado
        DB-->>-BS: Boleto actualizado
    else DELBOL - Cancelar
        BS->>+DB: 4d: DELETE FROM boleto
        DB-->>-BS: Boleto cancelado
    end
    
    BS-->>-S: 5: CommandResponse
    S->>S: 6: Format respuesta
    S-->>U: 7: Email con resultado
```

## Diagrama Detallado - LISBOL (Listar Boletos)

```mermaid
sequenceDiagram
    participant U as 👤 Secretaria/Conductor
    participant E as 📧 Email System
    participant BS as 🎫 BoletoService
    participant DB as 💾 DB
    
    U->>E: LISBOL["2025-11-13"]
    E->>BS: processCommand()
    BS->>DB: findByFechaViaje("2025-11-13")
    DB-->>BS: List<Boleto>
    BS-->>E: CommandResponse + códigos QR
    E-->>U: Lista de boletos del día
```

## Diagrama Detallado - INSBOL (Emitir Boleto con QR)

```mermaid
sequenceDiagram
    participant U as 👤 Secretaria
    participant E as 📧 Email System
    participant BS as 🎫 BoletoService
    participant DB as 💾 DB
    
    U->>E: INSBOL["codigo","id_ruta","id_vehiculo","pasajero","ci","asiento","precio","fecha","hora"]
    E->>BS: processCommand()
    BS->>DB: Validar ruta exists
    DB-->>BS: Ruta OK
    BS->>DB: Validar vehículo disponible
    DB-->>BS: Vehículo OK
    BS->>BS: Generar código QR
    BS->>DB: save(boleto)
    DB-->>BS: Boleto (id=6)
    BS-->>E: SUCCESS + código QR
    E-->>U: "Boleto emitido + QR: BOL-XXX"
```

## Diagrama Detallado - UPDBOL (Actualizar Estado)

```mermaid
sequenceDiagram
    participant U as 👤 Conductor
    participant E as 📧 Email System
    participant BS as 🎫 BoletoService
    participant DB as 💾 DB
    
    U->>E: UPDBOL["BOL-001","estado","USADO"]
    E->>BS: processCommand()
    BS->>DB: findByCodigo("BOL-001")
    DB-->>BS: Boleto
    BS->>BS: Verificar QR válido
    BS->>BS: boleto.setEstado("USADO")
    BS->>DB: save(boleto)
    DB-->>BS: Boleto actualizado
    BS-->>E: SUCCESS
    E-->>U: "Boleto marcado como usado"
```

## Diagrama Detallado - DELBOL (Cancelar Boleto)

```mermaid
sequenceDiagram
    participant U as 👤 Secretaria
    participant E as 📧 Email System
    participant BS as 🎫 BoletoService
    participant DB as 💾 DB
    
    U->>E: DELBOL["BOL-001"]
    E->>BS: processCommand()
    BS->>DB: findByCodigo("BOL-001")
    DB-->>BS: Boleto
    BS->>DB: delete(boleto)
    DB-->>BS: OK
    BS-->>E: SUCCESS
    E-->>U: "Boleto cancelado"
```

## Comandos CU4 Implementados

| Comando | Operación | Parámetros | Ejemplo |
|---------|-----------|------------|---------|
| **LISBOL** | Listar | `["*"]` o `["codigo"]` o `["fecha"]` | `LISBOL["2025-11-13"]` |
| **INSBOL** | Emitir | `["codigo","id_ruta","id_vehiculo","pasajero","ci","asiento","precio","fecha","hora"]` | `INSBOL["BOL-006","1","1","Juan Perez","9876543","5","50.00","2025-11-15","08:00"]` |
| **UPDBOL** | Actualizar | `["codigo","estado","nuevo_estado"]` | `UPDBOL["BOL-001","estado","USADO"]` |
| **DELBOL** | Cancelar | `["codigo"]` | `DELBOL["BOL-001"]` |

## Características Especiales CU4

- ✅ **Generación de códigos QR** para cada boleto
- ✅ **Validación de asientos** disponibles
- ✅ **Validación de ruta y vehículo** activos
- ✅ **Control de estados:** VENDIDO → USADO → CANCELADO
