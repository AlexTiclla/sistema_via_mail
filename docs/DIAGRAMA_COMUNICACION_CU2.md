# Diagrama de Comunicación - CU2: Gestión de Vehículos

## Diagrama Resumido - Flujo General CU2

```mermaid
sequenceDiagram
    participant U as 👤 Usuario
    participant S as 📧 Sistema
    participant VS as 🚐 VehiculoService
    participant DB as 💾 PostgreSQL
    
    Note over U,DB: Comandos: LISVEH | INSVEH | UPDVEH | DELVEH
    
    U->>+S: 1: Email con comando
    S->>S: 2: Parse comando
    S->>+VS: 3: processCommand()
    
    alt LISVEH - Listar
        VS->>+DB: 4a: SELECT * FROM vehiculo
        DB-->>-VS: Lista vehículos
    else INSVEH - Insertar
        VS->>+DB: 4b: INSERT INTO vehiculo
        DB-->>-VS: Vehículo creado
    else UPDVEH - Actualizar
        VS->>+DB: 4c: UPDATE vehiculo
        DB-->>-VS: Vehículo actualizado
    else DELVEH - Eliminar
        VS->>+DB: 4d: DELETE FROM vehiculo
        DB-->>-VS: Vehículo eliminado
    end
    
    VS-->>-S: 5: CommandResponse
    S->>S: 6: Format respuesta
    S-->>U: 7: Email con resultado
```

## Diagrama Detallado - LISVEH (Listar Vehículos)

```mermaid
sequenceDiagram
    participant U as 👤 Propietario
    participant E as 📧 Email System
    participant VS as 🚐 VehiculoService
    participant DB as 💾 DB
    
    U->>E: LISVEH["*"]
    E->>VS: processCommand()
    VS->>DB: findAll()
    DB-->>VS: List<Vehiculo>
    VS-->>E: CommandResponse
    E-->>U: Lista de 5 vehículos
```

## Diagrama Detallado - INSVEH (Insertar Vehículo)

```mermaid
sequenceDiagram
    participant U as 👤 Propietario
    participant E as 📧 Email System
    participant VS as 🚐 VehiculoService
    participant DB as 💾 DB
    
    U->>E: INSVEH["placa","marca","modelo","capacidad","ci_propietario"]
    E->>VS: processCommand()
    VS->>DB: findByPlaca() - Verificar duplicado
    DB-->>VS: Optional.empty()
    VS->>DB: findPropietario(ci)
    DB-->>VS: Usuario propietario
    VS->>DB: save(vehiculo)
    DB-->>VS: Vehículo (id=6)
    VS-->>E: SUCCESS
    E-->>U: "Vehículo registrado"
```

## Diagrama Detallado - UPDVEH (Actualizar Vehículo)

```mermaid
sequenceDiagram
    participant U as 👤 Propietario
    participant E as 📧 Email System
    participant VS as 🚐 VehiculoService
    participant DB as 💾 DB
    
    U->>E: UPDVEH["ABC-123","estado","MANTENIMIENTO"]
    E->>VS: processCommand()
    VS->>DB: findByPlaca("ABC-123")
    DB-->>VS: Vehiculo
    VS->>VS: vehiculo.setEstado()
    VS->>DB: save(vehiculo)
    DB-->>VS: Vehículo actualizado
    VS-->>E: SUCCESS
    E-->>U: "Vehículo actualizado"
```

## Diagrama Detallado - DELVEH (Eliminar Vehículo)

```mermaid
sequenceDiagram
    participant U as 👤 Propietario
    participant E as 📧 Email System
    participant VS as 🚐 VehiculoService
    participant DB as 💾 DB
    
    U->>E: DELVEH["ABC-123"]
    E->>VS: processCommand()
    VS->>DB: findByPlaca("ABC-123")
    DB-->>VS: Vehiculo
    VS->>DB: delete(vehiculo)
    DB-->>VS: OK
    VS-->>E: SUCCESS
    E-->>U: "Vehículo eliminado"
```

## Comandos CU2 Implementados

| Comando | Operación | Parámetros | Ejemplo |
|---------|-----------|------------|---------|
| **LISVEH** | Listar | `["*"]` o `["placa"]` | `LISVEH["*"]` |
| **INSVEH** | Insertar | `["placa","marca","modelo","capacidad","ci_propietario"]` | `INSVEH["XYZ-999","Toyota","Hiace","15","1234567"]` |
| **UPDVEH** | Actualizar | `["placa","campo","valor"]` | `UPDVEH["ABC-123","estado","MANTENIMIENTO"]` |
| **DELVEH** | Eliminar | `["placa"]` | `DELVEH["ABC-123"]` |
