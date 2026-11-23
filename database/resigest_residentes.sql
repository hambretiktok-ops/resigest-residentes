-- =====================================================================
-- ResiGest - Modulo de Gestion de Residentes
-- Script de base de datos (MySQL 8)
-- Evidencia: GA7-220501096-AA2-EV01
-- Tablas tomadas del modelo fisico de ResiGest (GA6-220501096-AA2-EV03):
-- TORRE, APARTAMENTO y RESIDENTE
-- =====================================================================

CREATE DATABASE IF NOT EXISTS resigest
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE resigest;

DROP TABLE IF EXISTS residente;
DROP TABLE IF EXISTS apartamento;
DROP TABLE IF EXISTS torre;

-- ---------------------------------------------------------------------
-- TORRE: el conjunto tiene 17 torres
-- ---------------------------------------------------------------------
CREATE TABLE torre (
    id_torre     INT          NOT NULL AUTO_INCREMENT,
    numero_torre INT          NOT NULL,
    nombre       VARCHAR(30)  NOT NULL,
    CONSTRAINT pk_torre PRIMARY KEY (id_torre),
    CONSTRAINT uq_torre_numero UNIQUE (numero_torre)
);

-- ---------------------------------------------------------------------
-- APARTAMENTO: 4 pisos x 4 apartamentos por torre (16 por torre)
-- ---------------------------------------------------------------------
CREATE TABLE apartamento (
    id_apartamento INT         NOT NULL AUTO_INCREMENT,
    id_torre       INT         NOT NULL,
    piso           TINYINT     NOT NULL,
    numero         VARCHAR(10) NOT NULL,
    CONSTRAINT pk_apartamento PRIMARY KEY (id_apartamento),
    CONSTRAINT uq_apartamento_torre_numero UNIQUE (id_torre, numero),
    CONSTRAINT fk_apartamento_torre FOREIGN KEY (id_torre)
        REFERENCES torre (id_torre)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_apartamento_piso CHECK (piso BETWEEN 1 AND 4)
);

-- ---------------------------------------------------------------------
-- RESIDENTE
-- ---------------------------------------------------------------------
CREATE TABLE residente (
    id_residente     INT          NOT NULL AUTO_INCREMENT,
    tipo_documento   VARCHAR(5)   NOT NULL,
    numero_documento VARCHAR(20)  NOT NULL,
    nombres          VARCHAR(60)  NOT NULL,
    apellidos        VARCHAR(60)  NOT NULL,
    telefono         VARCHAR(15)  NOT NULL,
    correo           VARCHAR(100) NULL,
    tipo_residente   ENUM('PROPIETARIO', 'ARRENDATARIO') NOT NULL,
    estado           ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    id_apartamento   INT          NOT NULL,
    fecha_registro   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_residente PRIMARY KEY (id_residente),
    CONSTRAINT uq_residente_documento UNIQUE (numero_documento),
    CONSTRAINT fk_residente_apartamento FOREIGN KEY (id_apartamento)
        REFERENCES apartamento (id_apartamento)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_residente_tipo_documento CHECK (tipo_documento IN ('CC', 'CE', 'TI', 'PA'))
);

-- ---------------------------------------------------------------------
-- Datos iniciales: 17 torres
-- ---------------------------------------------------------------------
INSERT INTO torre (numero_torre, nombre) VALUES
(1, 'Torre 1'), (2, 'Torre 2'), (3, 'Torre 3'), (4, 'Torre 4'),
(5, 'Torre 5'), (6, 'Torre 6'), (7, 'Torre 7'), (8, 'Torre 8'),
(9, 'Torre 9'), (10, 'Torre 10'), (11, 'Torre 11'), (12, 'Torre 12'),
(13, 'Torre 13'), (14, 'Torre 14'), (15, 'Torre 15'), (16, 'Torre 16'),
(17, 'Torre 17');

-- 272 apartamentos: numeros 101-104, 201-204, 301-304, 401-404 en cada torre
INSERT INTO apartamento (id_torre, piso, numero)
SELECT t.id_torre, p.n, CONCAT(p.n, '0', a.n)
FROM torre t
CROSS JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) p
CROSS JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) a
ORDER BY t.id_torre, p.n, a.n;

-- Residentes de ejemplo
INSERT INTO residente (tipo_documento, numero_documento, nombres, apellidos, telefono, correo, tipo_residente, estado, id_apartamento)
VALUES
('CC', '1090456789', 'Laura Marcela', 'Pabon Rincon', '3104567890', 'laura.pabon@correo.com', 'PROPIETARIO', 'ACTIVO',
    (SELECT a.id_apartamento FROM apartamento a JOIN torre t ON t.id_torre = a.id_torre WHERE t.numero_torre = 1 AND a.numero = '101')),
('CC', '88234567', 'Jorge Enrique', 'Villamizar Ortega', '3157894561', 'jorge.villamizar@correo.com', 'ARRENDATARIO', 'ACTIVO',
    (SELECT a.id_apartamento FROM apartamento a JOIN torre t ON t.id_torre = a.id_torre WHERE t.numero_torre = 3 AND a.numero = '204')),
('CE', 'E7654321', 'Maria Gabriela', 'Contreras Lugo', '3201239876', NULL, 'ARRENDATARIO', 'INACTIVO',
    (SELECT a.id_apartamento FROM apartamento a JOIN torre t ON t.id_torre = a.id_torre WHERE t.numero_torre = 7 AND a.numero = '302'));
