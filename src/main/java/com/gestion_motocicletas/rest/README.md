# API REST - Gestión de Motocicletas

Servicio web RESTful implementado en **Java 21 + Spring Boot**, que expone el CRUD completo sobre el inventario de motocicletas utilizando el protocolo HTTP y formato **JSON**.

## Ruta base

- Localhost: `http://localhost:8080/gestion_motocicletas/api/motocicletas`

## Endpoints obligatorios

| Operación | Método HTTP | Endpoint | Resultado esperado |
| :--- | :--- | :--- | :--- |
| Consultar todas | `GET` | `/api/motocicletas` | Lista JSON (`200 OK`) |
| Consultar por ID | `GET` | `/api/motocicletas/{id}` | Objeto JSON (`200 OK` / `404 Not Found`) |
| Crear | `POST` | `/api/motocicletas` | Confirmación y nuevo ID (`201 Created`) |
| Actualizar | `PUT` | `/api/motocicletas/{id}` | Registro actualizado (`200 OK` / `404 Not Found`) |
| Eliminar | `DELETE` | `/api/motocicletas/{id}` | Sin cuerpo de respuesta (`204 No Content`) |

## Encabezados HTTP

- `Content-Type: application/json` (en peticiones `POST` y `PUT`)
- `Accept: application/json`

## Ejemplo de petición (`POST /api/motocicletas`)

```json
{
  "marca": "Kawasaki",
  "modelo": "Z400",
  "anio": 2025,
  "cilindraje": 399,
  "precio": 28500000,
  "stock": 2,
  "estado": "Disponible"
}
```

### Respuesta exitosa (`201 Created`)

```json
{
  "mensaje": "Motocicleta creada correctamente",
  "id_motocicleta": 6
}
```

## Códigos de estado HTTP manejados

- `200 OK`: Consulta o actualización satisfactoria.
- `201 Created`: Registro creado correctamente.
- `204 No Content`: Eliminación satisfactoria sin cuerpo de respuesta.
- `400 Bad Request`: Datos incorrectos, faltantes, violación de validaciones o JSON mal formado.
- `404 Not Found`: Motocicleta o recurso inexistente.
- `405 Method Not Allowed`: Método HTTP no permitido para la ruta.
- `415 Unsupported Media Type`: Tipo de contenido distinto a `application/json`.
- `500 Internal Server Error`: Error interno controlado sin exponer detalles de la base de datos.

## Estructura del código fuente

El código fuente del servicio REST se encuentra en `src/main/java/com/gestion_motocicletas/rest/`, compartiendo la capa de negocio y persistencia con el servicio SOAP en `src/main/java/com/gestion_motocicletas/shared/`:
- `controller/MotocicletaRestController.java`
- `dto/MotocicletaRequestDto.java`
- `dto/MotocicletaResponseDto.java`
- `dto/MotocicletaCreadaResponseDto.java`
- `mapper/MotocicletaRestMapper.java`
- `exception/RestExceptionHandler.java`
- `exception/ErrorMessage.java`
