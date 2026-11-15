-- Base de datos: gestion_motocicletas
-- Motor: MySQL / MariaDB (script de referencia del documento de requerimientos)

CREATE DATABASE IF NOT EXISTS gestion_motocicletas
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gestion_motocicletas;

CREATE TABLE IF NOT EXISTS motocicletas (
    id_motocicleta INT AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(60) NOT NULL,
    modelo VARCHAR(80) NOT NULL,
    anio YEAR NOT NULL,
    cilindraje INT NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    estado ENUM('Disponible', 'Agotada', 'Inactiva')
        NOT NULL DEFAULT 'Disponible',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_cilindraje
        CHECK (cilindraje > 0),
    CONSTRAINT chk_precio
        CHECK (precio >= 0),
    CONSTRAINT chk_stock
        CHECK (stock >= 0)
);

INSERT INTO motocicletas
(marca, modelo, anio, cilindraje, precio, stock, estado)
VALUES
('Yamaha', 'FZ 2.0', 2024, 150, 10500000.00, 5, 'Disponible'),
('Bajaj', 'Pulsar NS200', 2025, 200, 12500000.00, 3, 'Disponible'),
('Honda', 'CB125F', 2023, 125, 8200000.00, 0, 'Agotada'),
('Suzuki', 'Gixxer 150', 2024, 150, 9900000.00, 2, 'Disponible'),
('AKT', 'NKD 125', 2022, 125, 5800000.00, 1, 'Inactiva');
