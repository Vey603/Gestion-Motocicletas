# Proyecto CRUD de Motocicletas mediante REST y SOAP

Solución tecnológica integral para administrar el inventario de motocicletas compartiendo una **única base de datos relacional** y una **única capa de persistencia y lógica de negocio**, expuesta simultáneamente a través de una **API REST (HTTP + JSON)** y un **Servicio Web SOAP (XML + WSDL)**, junto con una **aplicación cliente de consola en Python**.

---

## Arquitectura y Tecnologías

- **Backend (REST + SOAP)**: Java 21, Spring Boot 4.1.2, Spring WebMVC, Spring Web Services (JAXB + WSDL 1.1), Spring Data JPA, Hibernate, Bean Validation, MapStruct y Lombok.
- **Base de datos**:
  - **Modo inmediato / pruebas (`h2`, activo por defecto)**: Base de datos H2 en memoria configurada en modo PostgreSQL, precargada automáticamente con los 5 registros iniciales del proyecto al iniciar la aplicación (sin requerir instalación externa).
  - **Modo persistente (`postgres` / MySQL)**: Scripts SQL disponibles en `01_Base_Datos/gestion_motocicletas.sql` (PostgreSQL) y `01_Base_Datos/gestion_motocicletas_mysql.sql` (MySQL/MariaDB).
- **Aplicación Cliente**: Python 3.9+ (`04_Cliente/codigo_fuente/cliente.py`) utilizando únicamente peticiones HTTP (`requests`), sin conexión directa a la base de datos.

---

## Estructura de Entregables

```text
Gestion-Motocicletas/
├── 01_Base_Datos/
│   ├── gestion_motocicletas.sql          # Script SQL PostgreSQL
│   ├── gestion_motocicletas_mysql.sql    # Script SQL MySQL/MariaDB
│   └── modelo_datos.png                  # Diagrama del modelo de datos
├── 02_API_REST/
│   ├── codigo_fuente/                    # Controlador, DTOs, Mapper y Manejador de errores REST
│   └── README.md                         # Documentación de endpoints y códigos HTTP
├── 03_API_SOAP/
│   ├── codigo_fuente/                    # Endpoint, configuración, DTOs y Mapper SOAP
│   ├── contrato.wsdl                     # Contrato WSDL del servicio
│   └── README.md                         # Documentación de operaciones XML y SOAP Fault
├── 04_Cliente/
│   ├── codigo_fuente/
│   │   └── cliente.py                    # Aplicación cliente de consola en Python
│   └── requirements.txt                  # Dependencias Python (requests)
├── 05_Pruebas/
│   ├── coleccion_postman.json            # Colección Postman v2.1 (REST, SOAP e interoperabilidad)
│   ├── matriz_pruebas.xlsx               # Matriz con los 14 escenarios ejecutados
│   └── evidencias/                       # Capturas PNG, logs .txt y reporte de ejecución real
├── 06_Documentacion/
│   ├── Documento_Tecnico_Gestion_Motocicletas.docx  # Documento maestro consolidado en Word
│   ├── manual_tecnico.pdf
│   ├── manual_usuario.pdf
│   └── comparativo_rest_soap.pdf
├── src/                                  # Proyecto Gradle completo (compilable y ejecutable)
├── .env.example                          # Plantilla de variables de entorno (sin credenciales reales)
└── README.md                             # Esta guía de instalación y ejecución
```

---

## Requisitos Previos

1. **JDK 21 o superior** (verificado con Java 23).
2. **Python 3.9 o superior** y `pip`.
3. *(Opcional)* **PostgreSQL 18** o **MySQL/MariaDB** si se desea usar persistencia externa en lugar del perfil `h2` por defecto.

---

## Instalación y Ejecución desde Cero

### 1. Ejecutar el Servidor Backend (REST + SOAP)

#### Opción A: Modo inmediato con H2 en memoria (recomendado para pruebas rápidas)
No requiere instalar ni crear bases de datos externas; al arrancar se crean las tablas y se cargan las 5 motocicletas iniciales automáticamente:

```powershell
cd Gestion-Motocicletas
.\gradlew.bat bootRun
```

En Linux/macOS:
```bash
./gradlew bootRun
```

#### Opción B: Modo PostgreSQL (persistente)
1. Cree la base de datos y ejecute `01_Base_Datos/gestion_motocicletas.sql`:
   ```bash
   createdb -h localhost -p 5434 -U tu_usuario gestion_motocicletas
   psql -h localhost -p 5434 -U tu_usuario -d gestion_motocicletas -f 01_Base_Datos/gestion_motocicletas.sql
   ```
2. Copie `.env.example` a `.env` y defina sus credenciales locales (nunca suba `.env` al repositorio):
   ```ini
   POSTGRES_USER=tu_usuario
   POSTGRES_PASSWORD=tu_contrasena
   ```
3. Ejecute activando el perfil `postgres`:
   ```powershell
   $env:SPRING_PROFILES_ACTIVE="postgres"
   .\gradlew.bat bootRun
   ```

Una vez iniciado el servidor, los servicios quedan disponibles en:
- **API REST**: `http://localhost:8080/gestion_motocicletas/api/motocicletas`
- **Servicio SOAP**: `http://localhost:8080/gestion_motocicletas/ws`
- **Contrato WSDL**: `http://localhost:8080/gestion_motocicletas/ws/motocicletas.wsdl`

---

### 2. Ejecutar la Aplicación Cliente (Python)

1. Instale las dependencias del cliente:
   ```powershell
   pip install -r 04_Cliente/requirements.txt
   ```
2. Inicie el menú interactivo de consola:
   ```powershell
   python 04_Cliente/codigo_fuente/cliente.py
   ```
3. *(Opcional)* Para ejecutar la demostración automatizada de interoperabilidad entre REST y SOAP:
   ```powershell
   python 04_Cliente/codigo_fuente/cliente.py --demo
   ```

---

### 3. Ejecutar las Pruebas Automatizadas

Con Gradle (levanta contexto Spring Boot + H2 y valida los 14 escenarios obligatorios):
```powershell
.\gradlew.bat test
```

También puede importar `05_Pruebas/coleccion_postman.json` en Postman mientras el servidor está en ejecución para probar cada endpoint manualmente.
