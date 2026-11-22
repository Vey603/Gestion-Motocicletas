# Proyecto CRUD de Motocicletas mediante REST y SOAP

Solución tecnológica integral para administrar el inventario de motocicletas compartiendo una **única base de datos relacional PostgreSQL** y una **única capa de persistencia y lógica de negocio**, expuesta simultáneamente a través de una **API REST (HTTP + JSON)** y un **Servicio Web SOAP (XML + WSDL)**, junto con una **aplicación cliente de consola en Python**.

---

## Arquitectura y Tecnologías

- **Backend (REST + SOAP)**: Java 21, Spring Boot 4.1.2, Spring WebMVC, Spring Web Services (JAXB + WSDL 1.1), Spring Data JPA, Hibernate, Bean Validation, MapStruct y Lombok.
- **Base de datos**: PostgreSQL (puerto `5434`). El esquema y los 5 registros iniciales se cargan con `src/db/gestion_motocicletas.sql`.
- **Aplicación Cliente**: Python 3.9+ (`src/client/cliente.py`) utilizando únicamente peticiones HTTP (`requests`), sin conexión directa a la base de datos.

---

## Estructura del Proyecto

La estructura se adaptó al proyecto Gradle: todo el contenido del proyecto (código, cliente, base de datos, pruebas y documentación) vive bajo `src/`. En la raíz solo quedan el README principal y los archivos de configuración que Gradle y el repositorio necesitan.

```text
gestion_motocicletas/
├── README.md
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
├── .env.example
├── .gitattributes
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/gestion_motocicletas/
    │   │   ├── Application.java
    │   │   ├── rest/
    │   │   │   ├── README.md
    │   │   │   ├── controller/
    │   │   │   ├── dto/
    │   │   │   ├── exception/
    │   │   │   └── mapper/
    │   │   ├── soap/
    │   │   │   ├── README.md
    │   │   │   ├── config/
    │   │   │   ├── dto/
    │   │   │   ├── endpoint/
    │   │   │   └── mapper/
    │   │   └── shared/
    │   │       ├── entity/
    │   │       ├── exception/
    │   │       ├── repository/
    │   │       └── service/
    │   └── resources/
    │       ├── application.properties
    │       └── wsdl/
    │           └── motocicletas.wsdl
    ├── client/
    │   ├── cliente.py
    │   └── requirements.txt
    ├── db/
    │   ├── gestion_motocicletas.sql
    │   └── modelo_datos.png
    ├── pruebas/
    │   ├── coleccion_postman.json
    │   ├── matriz_pruebas.xlsx
    │   └── evidencias/
    └── documentacion/
        ├── manual_tecnico.pdf
        ├── manual_usuario.pdf
        └── comparativo_rest_soap.pdf
```

---

## Requisitos Previos

1. **JDK 21**.
2. **Python 3.9 o superior** y `pip`.
3. **PostgreSQL** escuchando en el puerto `5434`.

---

## Instalación y Ejecución desde Cero

### 1. Crear la base de datos PostgreSQL

```bash
createdb -h localhost -p 5434 -U tu_usuario gestion_motocicletas
psql -h localhost -p 5434 -U tu_usuario -d gestion_motocicletas \
  -f src/db/gestion_motocicletas.sql
```

### 2. Configurar las credenciales

Copie `.env.example` a `.env` (nunca suba `.env` al repositorio) y defina sus credenciales locales:

```ini
POSTGRES_USER=tu_usuario
POSTGRES_PASSWORD=tu_contrasena
```

La aplicación lee estas variables a través de `src/main/resources/application.properties`; no se usan perfiles de Spring ni bases de datos en memoria.

### 3. Ejecutar el servidor backend (REST + SOAP)

```bash
./gradlew bootRun
```

En Windows:

```powershell
.\gradlew.bat bootRun
```

Una vez iniciado el servidor, los servicios quedan disponibles en:
- **API REST**: `http://localhost:8080/gestion_motocicletas/api/motocicletas`
- **Servicio SOAP**: `http://localhost:8080/gestion_motocicletas/ws`
- **Contrato WSDL**: `http://localhost:8080/gestion_motocicletas/ws/motocicletas.wsdl`

### 4. Ejecutar la aplicación cliente (Python)

```bash
pip install -r src/client/requirements.txt
python3 src/client/cliente.py
```

Para ejecutar la demostración automatizada de interoperabilidad entre REST y SOAP:

```bash
python3 src/client/cliente.py --demo
```

---

## Pruebas Manuales

El proyecto no incluye pruebas automatizadas ni JUnit. Las pruebas se ejecutan manualmente con Postman y con el cliente Python:

- **Colección Postman**: `src/pruebas/coleccion_postman.json`. Los IDs de los registros creados se capturan automáticamente en las variables de colección `id_rest` e `id_soap`, por lo que la colección puede ejecutarse varias veces sin editar IDs fijos.
- **Matriz de pruebas**: `src/pruebas/matriz_pruebas.xlsx`, con los 14 escenarios obligatorios.
- **Evidencias**: `src/pruebas/evidencias/`, con capturas y logs de ejecución.

Orden recomendado de la colección: consultas iniciales, creación REST y SOAP, interoperabilidad cruzada, actualización, validaciones y errores, eliminación y verificación de inexistencia.

---

## Documentación

- Documentación de la API REST: `src/main/java/com/gestion_motocicletas/rest/README.md`
- Documentación del servicio SOAP: `src/main/java/com/gestion_motocicletas/soap/README.md`
- Manuales y comparativo: `src/documentacion/`
