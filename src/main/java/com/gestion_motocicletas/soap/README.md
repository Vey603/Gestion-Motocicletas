# Servicio Web SOAP - Gestión de Motocicletas

Servicio web basado en el protocolo **SOAP 1.1** y mensajes **XML**, implementado con **Spring Web Services (JAXB + WSDL)** compartiendo exactamente la misma base de datos y lógica de validación que la API REST.

## URLs y Espacio de Nombres

- **Endpoint SOAP**: `http://localhost:8080/gestion_motocicletas/ws`
- **Contrato WSDL**: `http://localhost:8080/gestion_motocicletas/ws/motocicletas.wsdl` (archivo físico en `src/main/resources/wsdl/motocicletas.wsdl`)
- **Espacio de nombres (`targetNamespace`)**: `urn:MotocicletasService`

## Operaciones disponibles

| Operación SOAP | Parámetros principales | Función |
| :--- | :--- | :--- |
| `obtenerMotocicletas` | Ninguno | Consulta todos los registros del inventario |
| `obtenerMotocicleta` | `id_motocicleta` | Consulta una motocicleta específica por ID |
| `crearMotocicleta` | `marca`, `modelo`, `anio`, `cilindraje`, `precio`, `stock`, `estado` | Registra una nueva motocicleta y retorna `id_motocicleta` |
| `actualizarMotocicleta` | `id_motocicleta` y datos nuevos | Actualiza una motocicleta existente |
| `eliminarMotocicleta` | `id_motocicleta` | Elimina una motocicleta del inventario |

## Encabezados HTTP requeridos

- `Content-Type: text/xml; charset=utf-8`
- `Accept: text/xml`
- `SOAPAction: ""` (opcional / cadena vacía según el binding `document/literal` del WSDL)

## Ejemplos de solicitudes XML

### Consultar todas (`obtenerMotocicletas`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:ser="urn:MotocicletasService">
  <soap:Header/>
  <soap:Body>
    <ser:obtenerMotocicletas/>
  </soap:Body>
</soap:Envelope>
```

### Crear motocicleta (`crearMotocicleta`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:ser="urn:MotocicletasService">
  <soap:Header/>
  <soap:Body>
    <ser:crearMotocicleta>
      <marca>Kawasaki</marca>
      <modelo>Z400</modelo>
      <anio>2025</anio>
      <cilindraje>399</cilindraje>
      <precio>28500000</precio>
      <stock>2</stock>
      <estado>Disponible</estado>
    </ser:crearMotocicleta>
  </soap:Body>
</soap:Envelope>
```

## Manejo de errores (`SOAP Fault`)

Cuando ocurre un error de validación (`MotocicletaInvalidaException`), registro inexistente (`MotocicletaNoEncontradaException`) o datos XML mal formados (`UnmarshallingFailureException`), el servidor responde con un `SOAP Fault` de tipo `CLIENT` sin exponer detalles internos de SQL o clases del sistema.

## Estructura del código fuente

Implementado en `src/main/java/com/gestion_motocicletas/soap/`:
- `config/SoapConfig.java` y `config/SoapNamespaces.java`
- `endpoint/MotocicletaEndpoint.java`
- `dto/*Request.java`, `*Response.java`, `MotocicletaXml.java`
- `mapper/MotocicletaSoapMapper.java`

El contrato WSDL canónico se publica desde `src/main/resources/wsdl/motocicletas.wsdl` en:
`http://localhost:8080/gestion_motocicletas/ws/motocicletas.wsdl`
