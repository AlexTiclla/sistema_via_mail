-- ============================================
-- SCRIPT DE DATOS DE PRUEBA
-- Sistema Trans Comarapa
-- ============================================

-- ============================================
-- INSERTAR USUARIOS
-- ============================================

-- Propietarios
INSERT INTO usuario (ci, nombre, apellido, tipo_usuario, telefono, email, foto_url, estado) VALUES
('1234567', 'Juan', 'Perez Gonzalez', 'PROPIETARIO', '71234567', 'juan.perez@transcomarapa.com', 'foto_juan.jpg', 'ACTIVO'),
('2345678', 'Maria', 'Lopez Fernandez', 'PROPIETARIO', '71345678', 'maria.lopez@transcomarapa.com', 'foto_maria.jpg', 'ACTIVO');

-- Secretarias
INSERT INTO usuario (ci, nombre, apellido, tipo_usuario, telefono, email, foto_url, estado) VALUES
('3456789', 'Ana', 'Garcia Rojas', 'SECRETARIA', '72456789', 'ana.garcia@transcomarapa.com', 'foto_ana.jpg', 'ACTIVO'),
('4567890', 'Lucia', 'Morales Castro', 'SECRETARIA', '72567890', 'lucia.morales@transcomarapa.com', 'foto_lucia.jpg', 'ACTIVO');

-- Conductores
INSERT INTO usuario (ci, nombre, apellido, tipo_usuario, telefono, email, foto_url, estado) VALUES
('5678901', 'Carlos', 'Ramirez Sanchez', 'CONDUCTOR', '73678901', 'carlos.ramirez@transcomarapa.com', 'foto_carlos.jpg', 'ACTIVO'),
('6789012', 'Pedro', 'Sanchez Diaz', 'CONDUCTOR', '73789012', 'pedro.sanchez@transcomarapa.com', 'foto_pedro.jpg', 'ACTIVO'),
('7890123', 'Jose', 'Martinez Torres', 'CONDUCTOR', '74890123', 'jose.martinez@transcomarapa.com', 'foto_jose.jpg', 'ACTIVO'),
('8901234', 'Luis', 'Torres Flores', 'CONDUCTOR', '74901234', 'luis.torres@transcomarapa.com', 'foto_luis.jpg', 'ACTIVO');

-- ============================================
-- INSERTAR VEHICULOS
-- ============================================
INSERT INTO vehiculo (placa, marca, modelo, capacidad_pasajeros, id_propietario, foto_url, estado) VALUES
('ABC-123', 'Toyota', 'Hiace', 15, 1, 'foto_abc123.jpg', 'DISPONIBLE'),
('DEF-456', 'Mercedes-Benz', 'Sprinter', 18, 1, 'foto_def456.jpg', 'DISPONIBLE'),
('GHI-789', 'Hyundai', 'County', 20, 2, 'foto_ghi789.jpg', 'DISPONIBLE'),
('JKL-012', 'Toyota', 'Coaster', 22, 2, 'foto_jkl012.jpg', 'DISPONIBLE'),
('MNO-345', 'Chevrolet', 'N300', 12, 1, 'foto_mno345.jpg', 'MANTENIMIENTO');

-- ============================================
-- INSERTAR RUTAS
-- ============================================
INSERT INTO ruta (nombre, origen, destino, distancia_km, duracion_horas, precio_base, gps_origen, gps_destino, estado) VALUES
('Santa Cruz - Comarapa', 'Santa Cruz de la Sierra', 'Comarapa', 150.5, 3.5, 50.00, '-17.7833,-63.1821', '-17.9167,-64.5167', 'ACTIVO'),
('Comarapa - Santa Cruz', 'Comarapa', 'Santa Cruz de la Sierra', 150.5, 3.5, 50.00, '-17.9167,-64.5167', '-17.7833,-63.1821', 'ACTIVO'),
('Santa Cruz - Samaipata', 'Santa Cruz de la Sierra', 'Samaipata', 120.0, 2.5, 40.00, '-17.7833,-63.1821', '-18.1800,-63.8700', 'ACTIVO'),
('Samaipata - Santa Cruz', 'Samaipata', 'Santa Cruz de la Sierra', 120.0, 2.5, 40.00, '-18.1800,-63.8700', '-17.7833,-63.1821', 'ACTIVO'),
('Santa Cruz - Vallegrande', 'Santa Cruz de la Sierra', 'Vallegrande', 250.0, 5.0, 80.00, '-17.7833,-63.1821', '-18.4833,-64.1000', 'ACTIVO');

-- ============================================
-- INSERTAR BOLETOS
-- ============================================
INSERT INTO boleto (codigo_boleto, id_ruta, id_vehiculo, nombre_pasajero, ci_pasajero, asiento, precio, fecha_viaje, hora_salida, qr_code, estado) VALUES
('BOL-001', 1, 1, 'Roberto Garcia Lopez', '9876543', 1, 50.00, '2025-11-20', '08:00:00', 'qr_bol001.png', 'VENDIDO'),
('BOL-002', 1, 1, 'Carmen Flores Ruiz', '8765432', 2, 50.00, '2025-11-20', '08:00:00', 'qr_bol002.png', 'VENDIDO'),
('BOL-003', 1, 1, 'Diego Moreno Silva', '7654321', 3, 50.00, '2025-11-20', '08:00:00', 'qr_bol003.png', 'VENDIDO'),
('BOL-004', 3, 2, 'Patricia Vega Ortiz', '6543210', 1, 40.00, '2025-11-21', '09:00:00', 'qr_bol004.png', 'VENDIDO'),
('BOL-005', 3, 2, 'Fernando Castro Rios', '5432109', 2, 40.00, '2025-11-21', '09:00:00', 'qr_bol005.png', 'VENDIDO');

-- ============================================
-- INSERTAR ENCOMIENDAS
-- ============================================
INSERT INTO encomienda (codigo_encomienda, id_ruta, id_vehiculo, nombre_remitente, telefono_remitente, nombre_destinatario, telefono_destinatario, descripcion, peso_kg, precio, fecha_envio, foto_url, qr_code, gps_actual, estado) VALUES
('ENC-001', 1, 1, 'Jorge Mendez', '75111111', 'Silvia Rojas', '76111111', 'Paquete de ropa', 5.5, 25.00, '2025-11-20', 'foto_enc001.jpg', 'qr_enc001.png', '-17.8500,-63.5000', 'EN_TRANSITO'),
('ENC-002', 1, 1, 'Rosa Diaz', '75222222', 'Miguel Castro', '76222222', 'Documentos importantes', 0.5, 15.00, '2025-11-20', 'foto_enc002.jpg', 'qr_enc002.png', '-17.8500,-63.5000', 'EN_TRANSITO'),
('ENC-003', 2, 3, 'Alberto Nunez', '75333333', 'Carla Mendoza', '76333333', 'Artesanias', 8.0, 35.00, '2025-11-19', 'foto_enc003.jpg', 'qr_enc003.png', '', 'ENTREGADA'),
('ENC-004', 3, 2, 'Monica Reyes', '75444444', 'Raul Vargas', '76444444', 'Alimentos no perecederos', 12.0, 40.00, '2025-11-21', 'foto_enc004.jpg', 'qr_enc004.png', '', 'REGISTRADA'),
('ENC-005', 5, 4, 'Elena Guzman', '75555555', 'Ricardo Paz', '76555555', 'Herramientas', 15.0, 50.00, '2025-11-22', 'foto_enc005.jpg', 'qr_enc005.png', '', 'REGISTRADA');

-- ============================================
-- INSERTAR VENTAS
-- ============================================
INSERT INTO venta (codigo_venta, tipo_venta, id_referencia, id_usuario_vendedor, monto_total) VALUES
('VEN-001', 'BOLETO', 1, 3, 50.00),
('VEN-002', 'BOLETO', 2, 3, 50.00),
('VEN-003', 'BOLETO', 3, 4, 50.00),
('VEN-004', 'BOLETO', 4, 3, 40.00),
('VEN-005', 'BOLETO', 5, 4, 40.00),
('VEN-006', 'ENCOMIENDA', 1, 3, 25.00),
('VEN-007', 'ENCOMIENDA', 2, 3, 15.00),
('VEN-008', 'ENCOMIENDA', 3, 4, 35.00),
('VEN-009', 'ENCOMIENDA', 4, 3, 40.00),
('VEN-010', 'ENCOMIENDA', 5, 4, 50.00);

-- ============================================
-- INSERTAR PAGOS
-- ============================================

-- Pagos completados (ambas cuotas pagadas)
INSERT INTO pago (id_venta, numero_cuota, monto_cuota, fecha_pago, metodo_pago, estado) VALUES
(1, 1, 25.00, '2025-11-13 10:00:00', 'EFECTIVO', 'PAGADO'),
(1, 2, 25.00, '2025-11-13 10:00:00', 'EFECTIVO', 'PAGADO'),
(2, 1, 25.00, '2025-11-13 11:00:00', 'TRANSFERENCIA', 'PAGADO'),
(2, 2, 25.00, '2025-11-13 11:00:00', 'TRANSFERENCIA', 'PAGADO');

-- Pagos parciales (solo primera cuota)
INSERT INTO pago (id_venta, numero_cuota, monto_cuota, fecha_pago, metodo_pago, estado) VALUES
(3, 1, 25.00, '2025-11-13 12:00:00', 'EFECTIVO', 'PAGADO'),
(3, 2, 25.00, NULL, NULL, 'PENDIENTE'),
(4, 1, 20.00, '2025-11-13 13:00:00', 'TARJETA', 'PAGADO'),
(4, 2, 20.00, NULL, NULL, 'PENDIENTE');

-- Pagos pendientes (ambas cuotas pendientes)
INSERT INTO pago (id_venta, numero_cuota, monto_cuota, fecha_pago, metodo_pago, estado) VALUES
(5, 1, 20.00, NULL, NULL, 'PENDIENTE'),
(5, 2, 20.00, NULL, NULL, 'PENDIENTE'),
(6, 1, 12.50, NULL, NULL, 'PENDIENTE'),
(6, 2, 12.50, NULL, NULL, 'PENDIENTE');

-- ============================================
-- INSERTAR REPORTES (Ejemplos)
-- ============================================
INSERT INTO reporte (tipo_reporte, descripcion, parametros, resultado, id_usuario_generador) VALUES
('VENTAS_DIA', 'Reporte de ventas del dia 13/11/2025', '{"fecha":"2025-11-13"}', '{"total_ventas":10,"monto_total":395.00}', 1),
('BOLETOS_VENDIDOS', 'Boletos vendidos para el 20/11/2025', '{"fecha":"2025-11-20"}', '{"total_boletos":3,"ruta":"Santa Cruz - Comarapa"}', 1),
('ENCOMIENDAS_TRANSITO', 'Encomiendas en transito', '{"estado":"EN_TRANSITO"}', '{"total":2}', 3);

-- ============================================
-- VERIFICACION DE DATOS INSERTADOS
-- ============================================

-- Mostrar resumen de datos insertados
SELECT 'USUARIOS' as tabla, COUNT(*) as registros FROM usuario
UNION ALL
SELECT 'VEHICULOS', COUNT(*) FROM vehiculo
UNION ALL
SELECT 'RUTAS', COUNT(*) FROM ruta
UNION ALL
SELECT 'BOLETOS', COUNT(*) FROM boleto
UNION ALL
SELECT 'ENCOMIENDAS', COUNT(*) FROM encomienda
UNION ALL
SELECT 'VENTAS', COUNT(*) FROM venta
UNION ALL
SELECT 'PAGOS', COUNT(*) FROM pago
UNION ALL
SELECT 'REPORTES', COUNT(*) FROM reporte;

-- ============================================
-- FIN DEL SCRIPT
-- ============================================