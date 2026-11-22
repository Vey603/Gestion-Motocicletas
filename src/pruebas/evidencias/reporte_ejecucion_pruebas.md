# Evidencias de Ejecución Real - Sistema de Gestión de Motocicletas

Todas las solicitudes y respuestas fueron ejecutadas directamente contra el servidor en ejecución (`http://localhost:8080/gestion_motocicletas`) con base de datos PostgreSQL precargada.

## Escenario 1: Consultar todas las motocicletas
- **REST** (`GET`): HTTP 200: Lista JSON con 5 motocicletas iniciales — **APROBADO**
- **SOAP** (`POST (obtenerMotocicletas)`): HTTP 200: Sobre XML con 5 registros — **APROBADO**
- Archivo detalle: `evidencia_01.txt` | Captura visual: `evidencia_01.png`

## Escenario 2: Consultar un ID válido
- **REST** (`GET`): HTTP 200: Bajaj Pulsar NS200 (ID 2) — **APROBADO**
- **SOAP** (`POST (obtenerMotocicleta)`): HTTP 200: XML con Bajaj Pulsar NS200 (ID 2) — **APROBADO**
- Archivo detalle: `evidencia_02.txt` | Captura visual: `evidencia_02.png`

## Escenario 3: Consultar un ID inexistente
- **REST** (`GET`): HTTP 404: No existe una motocicleta con el ID 9999 — **APROBADO**
- **SOAP** (`POST (obtenerMotocicleta)`): HTTP 500 SOAP Fault (Client): No existe una motocicleta con el ID 9999 — **APROBADO**
- Archivo detalle: `evidencia_03.txt` | Captura visual: `evidencia_03.png`

## Escenario 4: Crear una motocicleta válida
- **REST** (`POST`): HTTP 201: Motocicleta creada correctamente (id_motocicleta=6) — **APROBADO**
- **SOAP** (`POST (crearMotocicleta)`): HTTP 200: Motocicleta creada correctamente (id_motocicleta=7) — **APROBADO**
- Archivo detalle: `evidencia_04.txt` | Captura visual: `evidencia_04.png`

## Escenario 5: Crear con marca vacía
- **REST** (`POST`): HTTP 400: La marca no puede estar vacía — **APROBADO**
- **SOAP** (`POST (crearMotocicleta)`): HTTP 500 SOAP Fault (Client): La marca no puede estar vacía — **APROBADO**
- Archivo detalle: `evidencia_05.txt` | Captura visual: `evidencia_05.png`

## Escenario 6: Crear con precio negativo
- **REST** (`POST`): HTTP 400: El precio no puede ser negativo — **APROBADO**
- **SOAP** (`POST (crearMotocicleta)`): HTTP 500 SOAP Fault (Client): El precio no puede ser negativo — **APROBADO**
- Archivo detalle: `evidencia_06.txt` | Captura visual: `evidencia_06.png`

## Escenario 7: Crear con stock negativo
- **REST** (`POST`): HTTP 400: El stock no puede ser negativo — **APROBADO**
- **SOAP** (`POST (crearMotocicleta)`): HTTP 500 SOAP Fault (Client): El stock no puede ser negativo — **APROBADO**
- Archivo detalle: `evidencia_07.txt` | Captura visual: `evidencia_07.png`

## Escenario 8: Actualizar una motocicleta existente
- **REST** (`PUT`): HTTP 200: Registro ID 6 actualizado en JSON — **APROBADO**
- **SOAP** (`POST (actualizarMotocicleta)`): HTTP 200: Motocicleta actualizada correctamente — **APROBADO**
- Archivo detalle: `evidencia_08.txt` | Captura visual: `evidencia_08.png`

## Escenario 9: Actualizar un ID inexistente
- **REST** (`PUT`): HTTP 404: No existe una motocicleta con el ID 9999 — **APROBADO**
- **SOAP** (`POST (actualizarMotocicleta)`): HTTP 500 SOAP Fault (Client): No existe una motocicleta con el ID 9999 — **APROBADO**
- Archivo detalle: `evidencia_09.txt` | Captura visual: `evidencia_09.png`

## Escenario 10: Eliminar una motocicleta existente
- **REST** (`DELETE`): HTTP 204: Registro eliminado sin cuerpo de respuesta — **APROBADO**
- **SOAP** (`POST (eliminarMotocicleta)`): HTTP 200: Motocicleta eliminada correctamente — **APROBADO**
- Archivo detalle: `evidencia_10.txt` | Captura visual: `evidencia_10.png`

## Escenario 11: Eliminar un ID inexistente
- **REST** (`DELETE`): HTTP 404: No existe una motocicleta con el ID 9999 — **APROBADO**
- **SOAP** (`POST (eliminarMotocicleta)`): HTTP 500 SOAP Fault (Client): No existe una motocicleta con el ID 9999 — **APROBADO**
- Archivo detalle: `evidencia_11.txt` | Captura visual: `evidencia_11.png`

## Escenario 12: Consultar desde REST un registro creado por SOAP
- **REST <- SOAP** (`GET`): HTTP 200: REST devolvió KTM Duke 200 (ID 7) — **APROBADO**
- Archivo detalle: `evidencia_12.txt` | Captura visual: `evidencia_12.png`

## Escenario 13: Consultar desde SOAP un registro creado por REST
- **SOAP <- REST** (`POST (obtenerMotocicleta)`): HTTP 200: SOAP devolvió Kawasaki Z400 (ID 6) — **APROBADO**
- Archivo detalle: `evidencia_13.txt` | Captura visual: `evidencia_13.png`

## Escenario 14: Ejecutar una petición con datos mal formados
- **REST** (`POST`): HTTP 400: El cuerpo de la solicitud no es válido o tiene un formato incorrecto — **APROBADO**
- **SOAP** (`POST (crearMotocicleta)`): HTTP 500 SOAP Fault (Client): Mensaje seguro sin traza SQL/JAXB interna — **APROBADO**
- Archivo detalle: `evidencia_14.txt` | Captura visual: `evidencia_14.png`

## Evidencia 15: Aplicación Cliente en Python
- Archivo salida completa: `evidencia_15_cliente_python.txt` | Captura: `evidencia_15_cliente_python.png`