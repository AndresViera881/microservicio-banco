-- ============================================================
--  BaseDatos.sql - Banco Microservicios
--  Cada microservicio usa su propio schema (independencia de datos)
-- ============================================================

-- ── SCHEMAS ──────────────────────────────────────────────────
CREATE SCHEMA IF NOT EXISTS cliente_schema;
CREATE SCHEMA IF NOT EXISTS cuenta_schema;
CREATE SCHEMA IF NOT EXISTS movimiento_schema;

-- ============================================================
--  SCHEMA: cliente_schema  →  cliente-service
-- ============================================================
DROP TABLE IF EXISTS cliente_schema.clientes CASCADE;
DROP TABLE IF EXISTS cliente_schema.personas CASCADE;

CREATE TABLE cliente_schema.personas (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    genero          VARCHAR(20),
    edad            INTEGER         CHECK (edad > 0),
    identificacion  VARCHAR(20)     NOT NULL UNIQUE,
    direccion       VARCHAR(200),
    telefono        VARCHAR(20)
);

CREATE TABLE cliente_schema.clientes (
    persona_id      BIGINT          PRIMARY KEY REFERENCES cliente_schema.personas(id) ON DELETE CASCADE,
    cliente_id      VARCHAR(50)     NOT NULL UNIQUE,
    contrasena      VARCHAR(255)    NOT NULL,
    estado          BOOLEAN         NOT NULL DEFAULT TRUE
);

-- ============================================================
--  SCHEMA: cuenta_schema  →  cuenta-service
-- ============================================================
DROP TABLE IF EXISTS cuenta_schema.cuentas CASCADE;

CREATE TABLE cuenta_schema.cuentas (
    id              BIGSERIAL       PRIMARY KEY,
    numero_cuenta   VARCHAR(20)     NOT NULL UNIQUE,
    tipo_cuenta     VARCHAR(20)     NOT NULL,
    saldo_inicial   NUMERIC(15,2)   NOT NULL DEFAULT 0 CHECK (saldo_inicial >= 0),
    estado          BOOLEAN         NOT NULL DEFAULT TRUE,
    -- Referencia lógica cross-service (no FK física)
    cliente_id      VARCHAR(50)     NOT NULL
);

CREATE INDEX idx_cuentas_cliente ON cuenta_schema.cuentas(cliente_id);

-- ============================================================
--  SCHEMA: movimiento_schema  →  movimiento-service
-- ============================================================
DROP TABLE IF EXISTS movimiento_schema.movimientos CASCADE;

CREATE TABLE movimiento_schema.movimientos (
    id              BIGSERIAL       PRIMARY KEY,
    fecha           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(20)     NOT NULL CHECK (tipo_movimiento IN ('CREDITO','DEBITO')),
    valor           NUMERIC(15,2)   NOT NULL,
    saldo           NUMERIC(15,2)   NOT NULL,
    -- Referencia lógica cross-service (no FK física)
    numero_cuenta   VARCHAR(20)     NOT NULL
);

CREATE INDEX idx_movimientos_cuenta ON movimiento_schema.movimientos(numero_cuenta);
CREATE INDEX idx_movimientos_fecha  ON movimiento_schema.movimientos(fecha);

-- ============================================================
--  DATOS DE PRUEBA (Casos de uso del documento)
-- ============================================================

-- 1. Clientes
INSERT INTO cliente_schema.personas (nombre, genero, edad, identificacion, direccion, telefono) VALUES
    ('Jose Lema',          'Masculino', 35, '1000000001', 'Otavalo sn y principal',   '098254785'),
    ('Marianela Montalvo', 'Femenino',  30, '1000000002', 'Amazonas y NNUU',          '097548965'),
    ('Juan Osorio',        'Masculino', 28, '1000000003', '13 junio y Equinoccial',   '098874587');

INSERT INTO cliente_schema.clientes (persona_id, cliente_id, contrasena, estado) VALUES
    (1, 'CL001', '1234', TRUE),
    (2, 'CL002', '5678', TRUE),
    (3, 'CL003', '1245', TRUE);

-- 2. Cuentas
INSERT INTO cuenta_schema.cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) VALUES
    ('478758', 'Ahorro',    2000.00, TRUE, 'CL001'),
    ('225487', 'Corriente',  100.00, TRUE, 'CL002'),
    ('495878', 'Ahorros',      0.00, TRUE, 'CL003'),
    ('496825', 'Ahorros',    540.00, TRUE, 'CL002'),
    ('585545', 'Corriente', 1000.00, TRUE, 'CL001');

-- 3. Movimientos (casos de uso del documento)
INSERT INTO movimiento_schema.movimientos (fecha, tipo_movimiento, valor, saldo, numero_cuenta) VALUES
    ('2022-02-08 10:00:00', 'DEBITO',  -575.00, 1425.00, '478758'),
    ('2022-02-10 10:00:00', 'CREDITO',  600.00,  700.00, '225487'),
    ('2022-02-08 11:00:00', 'CREDITO',  150.00,  150.00, '495878'),
    ('2022-02-08 12:00:00', 'DEBITO',  -540.00,    0.00, '496825');

-- ============================================================
--  Verificación rápida
-- ============================================================
SELECT
    p.nombre            AS "Cliente",
    c.numero_cuenta     AS "Número Cuenta",
    c.tipo_cuenta       AS "Tipo",
    m.tipo_movimiento   AS "Movimiento",
    m.valor             AS "Valor",
    m.saldo             AS "Saldo Disponible",
    m.fecha             AS "Fecha"
FROM movimiento_schema.movimientos m
JOIN cuenta_schema.cuentas c   ON m.numero_cuenta = c.numero_cuenta
JOIN cliente_schema.clientes cl ON c.cliente_id = cl.cliente_id
JOIN cliente_schema.personas p  ON cl.persona_id = p.id
ORDER BY m.fecha;
