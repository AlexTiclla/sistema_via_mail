-- =====================================================
-- SCRIPT DE OPTIMIZACIÓN - SISTEMA TRANS COMARAPA
-- Índices para mejorar el rendimiento de consultas
-- =====================================================

-- TABLA: usuario
-- Índice para búsquedas por CI (usado frecuentemente en validaciones)
CREATE INDEX IF NOT EXISTS idx_usuario_ci ON usuario(ci);

-- Índice para búsquedas por tipo de usuario
CREATE INDEX IF NOT EXISTS idx_usuario_tipo ON usuario(tipo_usuario);

-- Índice para búsquedas por estado
CREATE INDEX IF NOT EXISTS idx_usuario_estado ON usuario(estado);

-- Índice compuesto para búsquedas por tipo y estado
CREATE INDEX IF NOT EXISTS idx_usuario_tipo_estado ON usuario(tipo_usuario, estado);

-- =====================================================
-- TABLA: vehiculo
-- Índice para búsquedas por placa (usado en comandos)
CREATE INDEX IF NOT EXISTS idx_vehiculo_placa ON vehiculo(placa);

-- Índice para búsquedas por propietario
CREATE INDEX IF NOT EXISTS idx_vehiculo_propietario ON vehiculo(id_propietario);

-- Índice para búsquedas por estado
CREATE INDEX IF NOT EXISTS idx_vehiculo_estado ON vehiculo(estado);

-- =====================================================
-- TABLA: ruta
-- Índice para búsquedas por estado
CREATE INDEX IF NOT EXISTS idx_ruta_estado ON ruta(estado);

-- Índice para búsquedas por origen y destino
CREATE INDEX IF NOT EXISTS idx_ruta_origen_destino ON ruta(origen, destino);

-- =====================================================
-- TABLA: boleto
-- Índice único para código de boleto (búsquedas frecuentes)
CREATE INDEX IF NOT EXISTS idx_boleto_codigo ON boleto(codigo_boleto);

-- Índice para búsquedas por fecha de viaje (reportes)
CREATE INDEX IF NOT EXISTS idx_boleto_fecha_viaje ON boleto(fecha_viaje);

-- Índice para búsquedas por estado
CREATE INDEX IF NOT EXISTS idx_boleto_estado ON boleto(estado);

-- Índice para búsquedas por ruta
CREATE INDEX IF NOT EXISTS idx_boleto_ruta ON boleto(id_ruta);

-- Índice para búsquedas por vehículo
CREATE INDEX IF NOT EXISTS idx_boleto_vehiculo ON boleto(id_vehiculo);

-- Índice compuesto para validar disponibilidad de asiento
CREATE INDEX IF NOT EXISTS idx_boleto_vehiculo_fecha_hora_asiento 
ON boleto(id_vehiculo, fecha_viaje, hora_salida, asiento, estado);

-- Índice para búsquedas por CI de pasajero
CREATE INDEX IF NOT EXISTS idx_boleto_ci_pasajero ON boleto(ci_pasajero);

-- Índice para fecha de emisión (reportes por período)
CREATE INDEX IF NOT EXISTS idx_boleto_fecha_emision ON boleto(fecha_emision);

-- =====================================================
-- TABLA: encomienda
-- Índice único para código de encomienda
CREATE INDEX IF NOT EXISTS idx_encomienda_codigo ON encomienda(codigo_encomienda);

-- Índice para búsquedas por estado (tracking)
CREATE INDEX IF NOT EXISTS idx_encomienda_estado ON encomienda(estado);

-- Índice para búsquedas por ruta
CREATE INDEX IF NOT EXISTS idx_encomienda_ruta ON encomienda(id_ruta);

-- Índice para búsquedas por vehículo
CREATE INDEX IF NOT EXISTS idx_encomienda_vehiculo ON encomienda(id_vehiculo);

-- Índice para búsquedas por fecha de envío
CREATE INDEX IF NOT EXISTS idx_encomienda_fecha_envio ON encomienda(fecha_envio);

-- Índice compuesto para reportes de encomiendas en tránsito
CREATE INDEX IF NOT EXISTS idx_encomienda_estado_fecha 
ON encomienda(estado, fecha_envio);

-- =====================================================
-- TABLA: venta
-- Índice único para código de venta
CREATE INDEX IF NOT EXISTS idx_venta_codigo ON venta(codigo_venta);

-- Índice para búsquedas por tipo de venta
CREATE INDEX IF NOT EXISTS idx_venta_tipo ON venta(tipo_venta);

-- Índice para búsquedas por vendedor
CREATE INDEX IF NOT EXISTS idx_venta_vendedor ON venta(id_usuario_vendedor);

-- Índice para búsquedas por fecha (reportes)
CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha_venta);

-- Índice compuesto para reportes por tipo y fecha
CREATE INDEX IF NOT EXISTS idx_venta_tipo_fecha 
ON venta(tipo_venta, fecha_venta);

-- Índice para referencia (join con boletos/encomiendas)
CREATE INDEX IF NOT EXISTS idx_venta_referencia ON venta(id_referencia, tipo_venta);

-- =====================================================
-- TABLA: pago
-- Índice para búsquedas por venta
CREATE INDEX IF NOT EXISTS idx_pago_venta ON pago(id_venta);

-- Índice para búsquedas por estado (pagos pendientes)
CREATE INDEX IF NOT EXISTS idx_pago_estado ON pago(estado);

-- Índice compuesto para búsqueda de cuotas por venta
CREATE INDEX IF NOT EXISTS idx_pago_venta_cuota 
ON pago(id_venta, numero_cuota);

-- Índice compuesto para reportes de pagos pendientes
CREATE INDEX IF NOT EXISTS idx_pago_estado_fecha 
ON pago(estado, fecha_pago);

-- =====================================================
-- TABLA: reporte
-- Índice para búsquedas por tipo de reporte
CREATE INDEX IF NOT EXISTS idx_reporte_tipo ON reporte(tipo_reporte);

-- Índice para búsquedas por fecha de generación
CREATE INDEX IF NOT EXISTS idx_reporte_fecha ON reporte(fecha_generacion);

-- =====================================================
-- ANÁLISIS Y VACUUMING (PostgreSQL)
-- Actualizar estadísticas de tablas para el optimizador
-- =====================================================

ANALYZE usuario;
ANALYZE vehiculo;
ANALYZE ruta;
ANALYZE boleto;
ANALYZE encomienda;
ANALYZE venta;
ANALYZE pago;
ANALYZE reporte;

-- =====================================================
-- COMENTARIOS SOBRE OPTIMIZACIÓN
-- =====================================================

-- Los índices creados mejoran el rendimiento en:
-- 1. Búsquedas frecuentes por códigos únicos (CI, placas, códigos de boleto/encomienda)
-- 2. Filtros por estado (usado en listar y reportes)
-- 3. Búsquedas por fecha (reportes y consultas temporales)
-- 4. Validaciones de disponibilidad (asientos)
-- 5. Joins entre tablas relacionadas

-- NOTAS IMPORTANTES:
-- - Los índices consumen espacio adicional en disco
-- - Los índices se actualizan automáticamente en INSERT/UPDATE/DELETE
-- - Monitorear el uso de índices con: 
--   SELECT * FROM pg_stat_user_indexes WHERE schemaname = 'public';
-- - Eliminar índices no utilizados si es necesario

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================

