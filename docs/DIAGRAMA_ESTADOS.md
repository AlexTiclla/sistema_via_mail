# Diagramas de Estados - Sistema Trans Comarapa

## Diagrama de Estados - Usuario

```mermaid
stateDiagram-v2
    [*] --> ACTIVO: Registro exitoso
    
    ACTIVO --> INACTIVO: Desactivar usuario
    INACTIVO --> ACTIVO: Reactivar usuario
    
    ACTIVO --> [*]: Eliminar usuario
    INACTIVO --> [*]: Eliminar usuario
    
    note right of ACTIVO
        Usuario puede operar
        en el sistema
    end note
    
    note right of INACTIVO
        Usuario suspendido
        temporalmente
    end note
```

## Diagrama de Estados - Vehículo

```mermaid
stateDiagram-v2
    [*] --> DISPONIBLE: Registro de vehículo
    
    DISPONIBLE --> EN_RUTA: Asignar a viaje
    EN_RUTA --> DISPONIBLE: Terminar viaje
    
    DISPONIBLE --> MANTENIMIENTO: Enviar a taller
    MANTENIMIENTO --> DISPONIBLE: Completar reparación
    
    EN_RUTA --> MANTENIMIENTO: Problema en ruta
    
    DISPONIBLE --> [*]: Dar de baja
    MANTENIMIENTO --> [*]: Dar de baja
    
    note right of DISPONIBLE
        Listo para ser asignado
        a una ruta
    end note
    
    note right of EN_RUTA
        Transportando pasajeros
        o encomiendas
    end note
    
    note right of MANTENIMIENTO
        Reparación o
        mantenimiento preventivo
    end note
```

## Diagrama de Estados - Ruta

```mermaid
stateDiagram-v2
    [*] --> ACTIVO: Crear ruta
    
    ACTIVO --> INACTIVO: Suspender temporalmente
    INACTIVO --> ACTIVO: Reactivar
    
    ACTIVO --> [*]: Eliminar ruta
    INACTIVO --> [*]: Eliminar ruta
    
    note right of ACTIVO
        Ruta disponible para
        venta de boletos
    end note
    
    note right of INACTIVO
        Ruta suspendida
        (temporada, clima, etc)
    end note
```

## Diagrama de Estados - Boleto

```mermaid
stateDiagram-v2
    [*] --> VENDIDO: Emitir boleto
    
    VENDIDO --> USADO: Validar al abordar
    VENDIDO --> CANCELADO: Cancelar venta
    
    USADO --> [*]: Viaje completado
    CANCELADO --> [*]: Procesar reembolso
    
    note right of VENDIDO
        Boleto pagado
        QR generado
        Listo para uso
    end note
    
    note right of USADO
        Pasajero abordó
        QR validado
    end note
    
    note right of CANCELADO
        Boleto anulado
        Pendiente de reembolso
    end note
```

## Diagrama de Estados - Encomienda

```mermaid
stateDiagram-v2
    [*] --> REGISTRADA: Recibir encomienda
    
    REGISTRADA --> EN_TRANSITO: Iniciar transporte
    EN_TRANSITO --> ENTREGADA: Confirmar entrega
    
    REGISTRADA --> CANCELADA: Cancelar antes de envío
    EN_TRANSITO --> CANCELADA: Problema en transporte
    
    ENTREGADA --> [*]: Proceso completado
    CANCELADA --> [*]: Devolver o reembolsar
    
    note right of REGISTRADA
        Encomienda registrada
        QR generado
        Esperando transporte
    end note
    
    note right of EN_TRANSITO
        En camino
        GPS actualizable
        Rastreable
    end note
    
    note right of ENTREGADA
        Entregada al destinatario
        Firma confirmada
    end note
    
    note right of CANCELADA
        Anulada por problema
        o solicitud cliente
    end note
```

## Diagrama de Estados - Pago

```mermaid
stateDiagram-v2
    [*] --> PENDIENTE: Registrar pago/cuota
    
    PENDIENTE --> PAGADO: Confirmar pago
    PENDIENTE --> VENCIDO: Superar fecha límite
    
    VENCIDO --> PAGADO: Pago tardío
    
    PAGADO --> [*]: Finalizado
    
    note right of PENDIENTE
        Cuota 1/2 o 2/2
        En espera de pago
    end note
    
    note right of VENCIDO
        Pago no realizado
        a tiempo
    end note
    
    note right of PAGADO
        Pago confirmado
        QR validado
    end note
```

## Diagrama de Estados Integrado - Venta → Boleto/Encomienda

```mermaid
stateDiagram-v2
    [*] --> RegistrarVenta: Cliente solicita
    
    state RegistrarVenta {
        [*] --> ValidarDatos
        ValidarDatos --> CrearBoleto: Tipo: BOLETO
        ValidarDatos --> CrearEncomienda: Tipo: ENCOMIENDA
    }
    
    CrearBoleto --> GenerarPagos
    CrearEncomienda --> GenerarPagos
    
    state GenerarPagos {
        [*] --> Cuota1
        Cuota1 --> Cuota2: Si pago en 2 cuotas
        Cuota1 --> Completado: Si pago único
        Cuota2 --> Completado
    }
    
    Completado --> [*]
```

## Diagrama de Estados - Flujo Completo del Sistema

```mermaid
stateDiagram-v2
    [*] --> RecepcionEmail: Email recibido
    
    RecepcionEmail --> ParseComando: Leer asunto
    ParseComando --> ValidarComando: Extraer operación
    
    ValidarComando --> EjecutarCU: Comando válido
    ValidarComando --> EnviarError: Comando inválido
    
    state EjecutarCU {
        [*] --> CU_Gestion
        CU_Gestion --> CU_Operacion
        CU_Operacion --> CU_Finanzas
    }
    
    EjecutarCU --> FormatearRespuesta
    FormatearRespuesta --> EnviarRespuesta
    
    EnviarError --> EnviarRespuesta
    EnviarRespuesta --> [*]
```

## Matriz de Transiciones de Estado

### Usuario
| Estado Actual | Evento | Estado Siguiente | Comando |
|---------------|--------|------------------|---------|
| - | Registro | ACTIVO | INSUSU |
| ACTIVO | Desactivar | INACTIVO | UPDUSU[ci,"estado","INACTIVO"] |
| INACTIVO | Reactivar | ACTIVO | UPDUSU[ci,"estado","ACTIVO"] |
| ACTIVO/INACTIVO | Eliminar | [fin] | DELUSU |

### Vehículo
| Estado Actual | Evento | Estado Siguiente | Comando |
|---------------|--------|------------------|---------|
| - | Registro | DISPONIBLE | INSVEH |
| DISPONIBLE | Asignar viaje | EN_RUTA | UPDVEH[placa,"estado","EN_RUTA"] |
| EN_RUTA | Terminar | DISPONIBLE | UPDVEH[placa,"estado","DISPONIBLE"] |
| DISPONIBLE | A taller | MANTENIMIENTO | UPDVEH[placa,"estado","MANTENIMIENTO"] |
| MANTENIMIENTO | Reparado | DISPONIBLE | UPDVEH[placa,"estado","DISPONIBLE"] |

### Boleto
| Estado Actual | Evento | Estado Siguiente | Comando |
|---------------|--------|------------------|---------|
| - | Emitir | VENDIDO | INSBOL |
| VENDIDO | Validar QR | USADO | UPDBOL[codigo,"estado","USADO"] |
| VENDIDO | Cancelar | CANCELADO | UPDBOL[codigo,"estado","CANCELADO"] |

### Encomienda
| Estado Actual | Evento | Estado Siguiente | Comando |
|---------------|--------|------------------|---------|
| - | Registrar | REGISTRADA | INSENC |
| REGISTRADA | Iniciar envío | EN_TRANSITO | UPDENC[codigo,"estado","EN_TRANSITO"] |
| EN_TRANSITO | Entregar | ENTREGADA | UPDENC[codigo,"estado","ENTREGADA"] |
| REGISTRADA/EN_TRANSITO | Cancelar | CANCELADA | UPDENC[codigo,"estado","CANCELADA"] |

### Pago
| Estado Actual | Evento | Estado Siguiente | Comando |
|---------------|--------|------------------|---------|
| - | Registrar | PENDIENTE | INSPAG |
| PENDIENTE | Pagar | PAGADO | UPDPAG[id,"estado","PAGADO"] |
| PENDIENTE | Vencer | VENCIDO | (Automático por fecha) |
| VENCIDO | Pago tardío | PAGADO | UPDPAG[id,"estado","PAGADO"] |

## Reglas de Negocio por Estado

### Vehículo
- ✅ Solo vehículos **DISPONIBLE** pueden asignarse a rutas
- ✅ Vehículos en **MANTENIMIENTO** no aparecen en búsquedas de disponibilidad
- ✅ Cambio a **EN_RUTA** requiere boleto o encomienda asociada

### Boleto
- ✅ Solo boletos **VENDIDO** pueden validarse (cambiar a USADO)
- ✅ Boletos **CANCELADO** no pueden cambiar de estado
- ✅ Generación de QR solo en estado **VENDIDO**

### Encomienda
- ✅ GPS solo actualizable en estado **EN_TRANSITO**
- ✅ Estado **ENTREGADA** es final (no reversible)
- ✅ Código QR generado al crear (estado **REGISTRADA**)

### Pago
- ✅ Solo pagos **PENDIENTE** o **VENCIDO** pueden marcarse como PAGADO
- ✅ Estado **PAGADO** es final
- ✅ Verificación QR solo para pagos **PENDIENTE**
