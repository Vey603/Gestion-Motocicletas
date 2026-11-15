-- Datos iniciales equivalentes al script oficial del proyecto (para perfil H2 en memoria)
INSERT INTO motocicletas (marca, modelo, anio, cilindraje, precio, stock, estado, fecha_registro)
SELECT marca, modelo, anio, cilindraje, precio, stock, estado, CURRENT_TIMESTAMP
FROM (
    SELECT 'Yamaha' AS marca, 'FZ 2.0' AS modelo, 2024 AS anio, 150 AS cilindraje, 10500000.00 AS precio, 5 AS stock, 'Disponible' AS estado
    UNION ALL SELECT 'Bajaj', 'Pulsar NS200', 2025, 200, 12500000.00, 3, 'Disponible'
    UNION ALL SELECT 'Honda', 'CB125F', 2023, 125, 8200000.00, 0, 'Agotada'
    UNION ALL SELECT 'Suzuki', 'Gixxer 150', 2024, 150, 9900000.00, 2, 'Disponible'
    UNION ALL SELECT 'AKT', 'NKD 125', 2022, 125, 5800000.00, 1, 'Inactiva'
) AS datos
WHERE NOT EXISTS (SELECT 1 FROM motocicletas);
