# Diagrama de Comunicación - CU1: Gestión de Usuarios

## Diagrama Resumido - Flujo General CU1

```mermaid
sequenceDiagram
    participant U as 👤 Usuario
    participant S as 📧 Sistema
    participant US as 👥 UsuarioService
    participant DB as 💾 PostgreSQL
    
    Note over U,DB: Comandos: LISUSU | INSUSU | UPDUSU | DELUSU
    
    U->>+S: 1: Email con comando
    S->>S: 2: Parse comando
    S->>+US: 3: processCommand()
    
    alt LISUSU - Listar
        US->>+DB: 4a: SELECT * FROM usuario
        DB-->>-US: Lista usuarios
    else INSUSU - Insertar
        US->>+DB: 4b: INSERT INTO usuario
        DB-->>-US: Usuario creado
    else UPDUSU - Actualizar
        US->>+DB: 4c: UPDATE usuario
        DB-->>-US: Usuario actualizado
    else DELUSU - Eliminar
        US->>+DB: 4d: DELETE FROM usuario
        DB-->>-US: Usuario eliminado
    end
    
    US-->>-S: 5: CommandResponse
    S->>S: 6: Format respuesta
    S-->>U: 7: Email con resultado
```

## Diagrama Detallado - LISUSU (Listar Usuarios)

```mermaid
sequenceDiagram
    participant U as 👤 Usuario
    participant E as 📧 Email System
    participant P as 🔍 Parser
    participant US as 👥 UsuarioService
    participant DB as 💾 DB
    
    U->>E: LISUSU["*"]
    E->>P: parse()
    P-->>E: CommandRequest
    E->>US: processCommand()
    US->>DB: findAll()
    DB-->>US: List<Usuario>
    US-->>E: CommandResponse
    E-->>U: Lista de 8 usuarios
```

## Diagrama Detallado - INSUSU (Insertar Usuario)

```mermaid
sequenceDiagram
    participant U as 👤 Usuario
    participant E as 📧 Email System
    participant US as 👥 UsuarioService
    participant DB as 💾 DB
    
    U->>E: INSUSU["CI","nombre","apellido","tipo"]
    E->>US: processCommand()
    US->>DB: findByCi() - Verificar duplicado
    DB-->>US: Optional.empty()
    US->>DB: save(usuario)
    DB-->>US: Usuario (id=9)
    US-->>E: SUCCESS
    E-->>U: "Usuario registrado"
```

## Diagrama Detallado - UPDUSU (Actualizar)

```mermaid
sequenceDiagram
    participant U as 👤 Usuario
    participant E as 📧 Email System
    participant US as 👥 UsuarioService
    participant DB as 💾 DB
    
    U->>E: UPDUSU["CI","campo","valor"]
    E->>US: processCommand()
    US->>DB: findByCi()
    DB-->>US: Usuario
    US->>US: usuario.set(campo, valor)
    US->>DB: save(usuario)
    DB-->>US: Usuario actualizado
    US-->>E: SUCCESS
    E-->>U: "Usuario actualizado"
```

## Diagrama Detallado - DELUSU (Eliminar)

```mermaid
sequenceDiagram
    participant U as 👤 Usuario
    participant E as 📧 Email System
    participant US as 👥 UsuarioService
    participant DB as 💾 DB
    
    U->>E: DELUSU["CI"]
    E->>US: processCommand()
    US->>DB: findByCi()
    DB-->>US: Usuario
    US->>DB: delete(usuario)
    DB-->>US: OK
    US-->>E: SUCCESS
    E-->>U: "Usuario eliminado"
```

## Flujo de Objetos y Mensajes

```mermaid
graph LR
    subgraph "1. Entrada"
        A[Email Subject:<br/>LISUSU/INSUSU/UPDUSU/DELUSU]
    end
    
    subgraph "2. Parsing"
        B[CommandRequest<br/>operation: LIS/INS/UPD/DEL<br/>entity: USU<br/>params: Array]
    end
    
    subgraph "3. Ejecución"
        C[UsuarioService]
        D[Usuario<br/>Entity JPA]
    end
    
    subgraph "4. Respuesta"
        E[CommandResponse<br/>status: SUCCESS/ERROR<br/>data: Map<br/>message: String]
        F[Email Response<br/>Body formateado]
    end
    
    A --> B
    B --> C
    C --> D
    D --> E
    E --> F
    
    classDef input fill:#e3f2fd,stroke:#1976d2
    classDef process fill:#f3e5f5,stroke:#7b1fa2
    classDef data fill:#e8f5e9,stroke:#388e3c
    classDef output fill:#fff3e0,stroke:#f57c00
    
    class A input
    class B,C process
    class D,E data
    class F output
```

## Matriz de Responsabilidades CU1

| Componente | Responsabilidad | Datos de Entrada | Datos de Salida |
|------------|----------------|------------------|-----------------|
| **EmailScheduler** | Coordinar flujo | Email Message | - |
| **EmailService** | Recibir/Enviar emails | - | List<EmailMessage> |
| **CommandParser** | Parsear comando | Subject String | CommandRequest |
| **CommandExecutor** | Delegar a servicio | CommandRequest | CommandResponse |
| **UsuarioService** | Lógica de negocio CU1 | CommandRequest | CommandResponse |
| **UsuarioRepository** | Persistencia | Usuario Entity | Usuario / List / void |
| **ResponseFormatter** | Formatear respuesta | CommandResponse | String (email body) |

## Comandos CU1 Implementados

| Comando | Operación | Parámetros | Ejemplo |
|---------|-----------|------------|---------|
| **LISUSU** | Listar | `["*"]` o `["CI"]` o `["TIPO"]` | `LISUSU["*"]` |
| **INSUSU** | Insertar | `["ci","nombre","apellido","tipo",...]` | `INSUSU["9876543","Pedro","Gomez","CONDUCTOR"]` |
| **UPDUSU** | Actualizar | `["ci","campo","valor"]` | `UPDUSU["9876543","estado","INACTIVO"]` |
| **DELUSU** | Eliminar | `["ci"]` | `DELUSU["9876543"]` |
