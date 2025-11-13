-- ============================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS
-- Sistema Trans Comarapa
-- Base de datos: db_grupo04sa
-- ============================================

-- Eliminar tablas si existen (orden inverso por dependencias)
DROP TABLE IF EXISTS reporte CASCADE;
DROP TABLE IF EXISTS pago CASCADE;
DROP TABLE IF EXISTS venta CASCADE;
DROP TABLE IF EXISTS encomienda CASCADE;
DROP TABLE IF EXISTS boleto CASCADE;
DROP TABLE IF EXISTS ruta CASCADE;
DROP TABLE IF EXISTS vehiculo CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- ============================================
-- TABLA: usuario
-- Gestiona propietarios, secretarias y conductores (CU1)
-- ============================================
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    ci VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL CHECK (tipo_usuario IN ('PROPIETARIO', 'SECRETARIA', 'CONDUCTOR')),
    telefono VARCHAR(20),
    email VARCHAR(100),
    foto_url VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_usuario_ci ON usuario(ci);
CREATE INDEX idx_usuario_tipo ON usuario(tipo_usuario);
CREATE INDEX idx_usuario_estado ON usuario(estado);

-- ============================================
-- TABLA: vehiculo
-- Gestiona los vehículos de la empresa (CU2)
-- ============================================
CREATE TABLE vehiculo (
    id_vehiculo SERIAL PRIMARY KEY,
    placa VARCHAR(20) UNIQUE NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    capacidad_pasajeros INTEGER NOT NULL CHECK (capacidad_pasajeros > 0),
    id_propietario INTEGER REFERENCES usuario(id_usuario),
    foto_url VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'DISPONIBLE' CHECK (estado IN ('DISPONIBLE', 'EN_RUTA', 'MANTENIMIENTO')),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_vehiculo_placa ON vehiculo(placa);
CREATE INDEX idx_vehiculo_estado ON vehiculo(estado);
CREATE INDEX idx_vehiculo_propietario ON vehiculo(id_propietario);

-- ============================================
-- TABLA: ruta
-- Gestiona las rutas disponibles (CU3)
-- ============================================
CREATE TABLE ruta (
    id_ruta SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    origen VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    distancia_km DECIMAL(10,2),
    duracion_horas DECIMAL(5,2),
    precio_base DECIMAL(10,2) NOT NULL CHECK (precio_base > 0),
    gps_origen VARCHAR(100),
    gps_destino VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_ruta_origen ON ruta(origen);
CREATE INDEX idx_ruta_destino ON ruta(destino);
CREATE INDEX idx_ruta_estado ON ruta(estado);

-- ============================================
-- TABLA: boleto
-- Gestiona los boletos de pasajeros (CU4)
-- ============================================
CREATE TABLE boleto (
    id_boleto SERIAL PRIMARY KEY,
    codigo_boleto VARCHAR(50) UNIQUE NOT NULL,
    id_ruta INTEGER REFERENCES ruta(id_ruta),
    id_vehiculo INTEGER REFERENCES vehiculo(id_vehiculo),
    nombre_pasajero VARCHAR(200) NOT NULL,
    ci_pasajero VARCHAR(20) NOT NULL,
    asiento INTEGER NOT NULL CHECK (asiento > 0),
    precio DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    fecha_viaje DATE NOT NULL,
    hora_salida TIME NOT NULL,
    qr_code VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'VENDIDO' CHECK (estado IN ('VENDIDO', 'USADO', 'CANCELADO')),
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_boleto_codigo ON boleto(codigo_boleto);
CREATE INDEX idx_boleto_fecha ON boleto(fecha_viaje);
CREATE INDEX idx_boleto_estado ON boleto(estado);
CREATE INDEX idx_boleto_ruta ON boleto(id_ruta);
CREATE INDEX idx_boleto_vehiculo ON boleto(id_vehiculo);

-- ============================================
-- TABLA: encomienda
-- Gestiona el envío de encomiendas (CU5)
-- ============================================
CREATE TABLE encomienda (
    id_encomienda SERIAL PRIMARY KEY,
    codigo_encomienda VARCHAR(50) UNIQUE NOT NULL,
    id_ruta INTEGER REFERENCES ruta(id_ruta),
    id_vehiculo INTEGER REFERENCES vehiculo(id_vehiculo),
    nombre_remitente VARCHAR(200) NOT NULL,
    telefono_remitente VARCHAR(20),
    nombre_destinatario VARCHAR(200) NOT NULL,
    telefono_destinatario VARCHAR(20),
    descripcion TEXT,
    peso_kg DECIMAL(10,2),
    precio DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    fecha_envio DATE NOT NULL,
    foto_url VARCHAR(255),
    qr_code VARCHAR(255),
    gps_actual VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'REGISTRADA' CHECK (estado IN ('REGISTRADA', 'EN_TRANSITO', 'ENTREGADA', 'CANCELADA')),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_encomienda_codigo ON encomienda(codigo_encomienda);
CREATE INDEX idx_encomienda_estado ON encomienda(estado);
CREATE INDEX idx_encomienda_fecha ON encomienda(fecha_envio);
CREATE INDEX idx_encomienda_ruta ON encomienda(id_ruta);
CREATE INDEX idx_encomienda_vehiculo ON encomienda(id_vehiculo);

-- ============================================
-- TABLA: venta
-- Gestiona las ventas realizadas (CU6)
-- ============================================
CREATE TABLE venta (
    id_venta SERIAL PRIMARY KEY,
    codigo_venta VARCHAR(50) UNIQUE NOT NULL,
    tipo_venta VARCHAR(20) NOT NULL CHECK (tipo_venta IN ('BOLETO', 'ENCOMIENDA')),
    id_referencia INTEGER NOT NULL,
    id_usuario_vendedor INTEGER REFERENCES usuario(id_usuario),
    monto_total DECIMAL(10,2) NOT NULL CHECK (monto_total > 0),
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_venta_codigo ON venta(codigo_venta);
CREATE INDEX idx_venta_tipo ON venta(tipo_venta);
CREATE INDEX idx_venta_fecha ON venta(fecha_venta);
CREATE INDEX idx_venta_vendedor ON venta(id_usuario_vendedor);

-- ============================================
-- TABLA: pago
-- Gestiona los pagos en cuotas (CU7)
-- ============================================
CREATE TABLE pago (
    id_pago SERIAL PRIMARY KEY,
    id_venta INTEGER REFERENCES venta(id_venta),
    numero_cuota INTEGER NOT NULL CHECK (numero_cuota IN (1, 2)),
    monto_cuota DECIMAL(10,2) NOT NULL CHECK (monto_cuota > 0),
    fecha_pago TIMESTAMP,
    metodo_pago VARCHAR(50) CHECK (metodo_pago IN ('EFECTIVO', 'TRANSFERENCIA', 'TARJETA')),
    estado VARCHAR(20) DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'PAGADO')),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_venta, numero_cuota)
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_pago_venta ON pago(id_venta);
CREATE INDEX idx_pago_estado ON pago(estado);

-- ============================================
-- TABLA: reporte
-- Almacena reportes y estadísticas generados (CU8)
-- ============================================
CREATE TABLE reporte (
    id_reporte SERIAL PRIMARY KEY,
    tipo_reporte VARCHAR(50) NOT NULL,
    descripcion TEXT,
    parametros TEXT,
    resultado TEXT,
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_reporte_tipo ON reporte(tipo_reporte);
CREATE INDEX idx_reporte_fecha ON reporte(fecha_generacion);

-- ============================================
-- COMENTARIOS EN LAS TABLAS
-- ============================================
COMMENT ON TABLE usuario IS 'Gestiona usuarios: propietarios, secretarias y conductores';
COMMENT ON TABLE vehiculo IS 'Gestiona los vehículos de la empresa';
COMMENT ON TABLE ruta IS 'Gestiona las rutas disponibles con GPS';
COMMENT ON TABLE boleto IS 'Gestiona los boletos de pasajeros con QR';
COMMENT ON TABLE encomienda IS 'Gestiona el envío de encomiendas con foto, QR y GPS';
COMMENT ON TABLE venta IS 'Gestiona las ventas de boletos y encomiendas';
COMMENT ON TABLE pago IS 'Gestiona los pagos en cuotas (máximo 2)';
COMMENT ON TABLE reporte IS 'Almacena reportes y estadísticas generados';

-- ============================================
-- FIN DEL SCRIPT
-- ============================================

