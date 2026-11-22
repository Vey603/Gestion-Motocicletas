#!/usr/bin/env python3
"""Aplicación cliente de consola para consumir los servicios REST y SOAP
del Sistema de Gestión de Motocicletas.

Cumple con los requerimientos de la Sección 7 del proyecto:
- Lenguaje diferente al utilizado en las APIs (Python vs Java).
- Selección dinámica entre API REST (HTTP + JSON) y servicio SOAP (HTTP + XML).
- Menú interactivo con las cinco operaciones CRUD.
- Validación básica de datos antes de enviar solicitudes.
- Modo demostración automática (--demo) para pruebas de interoperabilidad.
"""

from __future__ import annotations

import argparse
from datetime import datetime
from decimal import Decimal, InvalidOperation
import os
import sys
import xml.etree.ElementTree as ET
from xml.sax.saxutils import escape

import requests


DEFAULT_REST_URL = os.getenv(
    "MOTOS_REST_URL", "http://localhost:8080/gestion_motocicletas/api/motocicletas"
)
DEFAULT_SOAP_URL = os.getenv(
    "MOTOS_SOAP_URL", "http://localhost:8080/gestion_motocicletas/ws"
)
SOAP_NAMESPACE = "urn:MotocicletasService"
SOAP_ENV_NS = "http://schemas.xmlsoap.org/soap/envelope/"
ESTADOS_VALIDOS = ("Disponible", "Agotada", "Inactiva")


class ServiceError(Exception):
    """Error controlado al comunicarse o recibir un fallo del servicio web."""


def formatear_precio(valor: object) -> str:
    """Formatea un valor numérico en estilo moneda local (ejemplo: $12.500.000)."""
    try:
        numero = Decimal(str(valor))
    except (InvalidOperation, ValueError):
        return f"${valor}"

    entero = int(numero)
    fraccion = numero - Decimal(entero)
    texto_entero = f"{entero:,}".replace(",", ".")
    if fraccion == 0:
        return f"${texto_entero}"
    decimales = f"{fraccion:.2f}".split(".")[1]
    return f"${texto_entero},{decimales}"


def local_name(tag: str) -> str:
    """Extrae el nombre local de una etiqueta XML ignorando el namespace."""
    if "}" in tag:
        return tag.split("}", 1)[1]
    return tag


def find_first_by_local(parent: ET.Element, name: str) -> ET.Element | None:
    """Busca el primer subelemento directo cuyo nombre local coincida."""
    for child in parent:
        if local_name(child.tag) == name:
            return child
    return None


def find_deep_by_local(root: ET.Element, name: str) -> ET.Element | None:
    """Busca el primer descendiente en cualquier nivel con el nombre local dado."""
    for elem in root.iter():
        if local_name(elem.tag) == name:
            return elem
    return None


def validar_datos_motocicleta(datos: dict) -> dict:
    """Valida los datos básicos antes de enviarlos al servicio REST o SOAP."""
    marca = str(datos.get("marca", "")).strip()
    modelo = str(datos.get("modelo", "")).strip()
    if not marca:
        raise ValueError("La marca no puede estar vacía.")
    if len(marca) > 60:
        raise ValueError("La marca no puede superar los 60 caracteres.")
    if not modelo:
        raise ValueError("El modelo no puede estar vacío.")
    if len(modelo) > 80:
        raise ValueError("El modelo no puede superar los 80 caracteres.")

    anio_actual = datetime.now().year
    try:
        anio = int(datos.get("anio"))
    except (TypeError, ValueError) as exc:
        raise ValueError("El año debe ser un número entero válido.") from exc
    if anio < 1900 or anio > anio_actual:
        raise ValueError(f"El año debe estar entre 1900 y {anio_actual}.")

    try:
        cilindraje = int(datos.get("cilindraje"))
    except (TypeError, ValueError) as exc:
        raise ValueError("El cilindraje debe ser un número entero.") from exc
    if cilindraje <= 0:
        raise ValueError("El cilindraje debe ser un entero mayor que cero.")

    try:
        precio = Decimal(str(datos.get("precio")))
    except (InvalidOperation, TypeError, ValueError) as exc:
        raise ValueError("El precio debe ser un valor numérico válido.") from exc
    if precio < 0:
        raise ValueError("El precio no puede ser negativo.")

    try:
        stock = int(datos.get("stock"))
    except (TypeError, ValueError) as exc:
        raise ValueError("El stock debe ser un número entero.") from exc
    if stock < 0:
        raise ValueError("El stock no puede ser negativo.")

    estado_raw = str(datos.get("estado", "")).strip()
    mapa_estados = {e.lower(): e for e in ESTADOS_VALIDOS}
    if estado_raw.lower() not in mapa_estados:
        raise ValueError(
            "Estado inválido. Valores permitidos: Disponible, Agotada, Inactiva."
        )
    estado = mapa_estados[estado_raw.lower()]

    if stock == 0 and estado == "Disponible":
        raise ValueError(
            "Coherencia inválida: si el stock es 0, la motocicleta no puede quedar como Disponible."
        )
    if stock > 0 and estado == "Agotada":
        raise ValueError(
            "Coherencia inválida: si el stock es mayor que 0, la motocicleta no puede quedar como Agotada."
        )

    return {
        "marca": marca,
        "modelo": modelo,
        "anio": anio,
        "cilindraje": cilindraje,
        "precio": float(precio) if precio % 1 != 0 else int(precio),
        "stock": stock,
        "estado": estado,
    }


class RestMotocicletaClient:
    """Cliente HTTP + JSON para consumir la API REST."""

    nombre = "REST"

    def __init__(self, base_url: str, timeout: int = 10) -> None:
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout
        self.headers = {
            "Content-Type": "application/json",
            "Accept": "application/json",
        }

    def _procesar_respuesta(self, resp: requests.Response) -> object:
        if resp.status_code == 204:
            return None
        try:
            payload = resp.json() if resp.text.strip() else {}
        except ValueError:
            payload = {"message": resp.text.strip()}

        if not (200 <= resp.status_code < 300):
            mensaje = (
                payload.get("message")
                or payload.get("mensaje")
                or f"Error HTTP {resp.status_code}"
            )
            raise ServiceError(f"HTTP {resp.status_code}: {mensaje}")
        return payload

    def listar(self) -> list[dict]:
        try:
            resp = requests.get(self.base_url, headers=self.headers, timeout=self.timeout)
        except requests.RequestException as exc:
            raise ServiceError(f"No se pudo conectar con el servicio REST: {exc}") from exc
        data = self._procesar_respuesta(resp)
        return data if isinstance(data, list) else []

    def obtener_por_id(self, id_moto: int) -> dict:
        try:
            resp = requests.get(
                f"{self.base_url}/{id_moto}", headers=self.headers, timeout=self.timeout
            )
        except requests.RequestException as exc:
            raise ServiceError(f"No se pudo conectar con el servicio REST: {exc}") from exc
        return self._procesar_respuesta(resp)  # type: ignore[return-value]

    def crear(self, datos: dict) -> dict:
        try:
            resp = requests.post(
                self.base_url, json=datos, headers=self.headers, timeout=self.timeout
            )
        except requests.RequestException as exc:
            raise ServiceError(f"No se pudo conectar con el servicio REST: {exc}") from exc
        return self._procesar_respuesta(resp)  # type: ignore[return-value]

    def actualizar(self, id_moto: int, datos: dict) -> str:
        try:
            resp = requests.put(
                f"{self.base_url}/{id_moto}",
                json=datos,
                headers=self.headers,
                timeout=self.timeout,
            )
        except requests.RequestException as exc:
            raise ServiceError(f"No se pudo conectar con el servicio REST: {exc}") from exc
        self._procesar_respuesta(resp)
        return "Motocicleta actualizada correctamente"

    def eliminar(self, id_moto: int) -> str:
        try:
            resp = requests.delete(
                f"{self.base_url}/{id_moto}", headers=self.headers, timeout=self.timeout
            )
        except requests.RequestException as exc:
            raise ServiceError(f"No se pudo conectar con el servicio REST: {exc}") from exc
        self._procesar_respuesta(resp)
        return "Motocicleta eliminada correctamente"


class SoapMotocicletaClient:
    """Cliente HTTP + XML para consumir el servicio web SOAP."""

    nombre = "SOAP"

    def __init__(self, endpoint_url: str, timeout: int = 10) -> None:
        self.endpoint_url = endpoint_url
        self.timeout = timeout
        self.headers = {
            "Content-Type": "text/xml; charset=utf-8",
            "Accept": "text/xml",
            "SOAPAction": "",
        }

    def _construir_sobre(self, operacion: str, cuerpo_interno: str = "") -> str:
        if cuerpo_interno:
            contenido = f"<ser:{operacion}>{cuerpo_interno}</ser:{operacion}>"
        else:
            contenido = f"<ser:{operacion}/>"
        return (
            '<?xml version="1.0" encoding="UTF-8"?>\n'
            f'<soap:Envelope xmlns:soap="{SOAP_ENV_NS}" xmlns:ser="{SOAP_NAMESPACE}">\n'
            "  <soap:Header/>\n"
            "  <soap:Body>\n"
            f"    {contenido}\n"
            "  </soap:Body>\n"
            "</soap:Envelope>"
        )

    def _enviar_soap(self, operacion: str, cuerpo_interno: str = "") -> ET.Element:
        xml_sobre = self._construir_sobre(operacion, cuerpo_interno)
        try:
            resp = requests.post(
                self.endpoint_url,
                data=xml_sobre.encode("utf-8"),
                headers=self.headers,
                timeout=self.timeout,
            )
        except requests.RequestException as exc:
            raise ServiceError(f"No se pudo conectar con el servicio SOAP: {exc}") from exc

        try:
            root = ET.fromstring(resp.text)
        except ET.ParseError as exc:
            raise ServiceError(
                f"Respuesta XML inválida (HTTP {resp.status_code})"
            ) from exc

        fault = find_deep_by_local(root, "Fault")
        if fault is not None:
            fault_code_elem = find_first_by_local(fault, "faultcode")
            fault_str_elem = find_first_by_local(fault, "faultstring")
            codigo = fault_code_elem.text if fault_code_elem is not None else "Fault"
            mensaje = (
                fault_str_elem.text
                if fault_str_elem is not None
                else "Error en servicio SOAP"
            )
            raise ServiceError(f"SOAP Fault ({codigo}): {mensaje}")

        if not (200 <= resp.status_code < 300):
            raise ServiceError(f"Error HTTP {resp.status_code} en servicio SOAP")

        return root

    @staticmethod
    def _xml_a_motocicleta(elem: ET.Element) -> dict:
        def texto(campo: str) -> str:
            sub = find_first_by_local(elem, campo)
            return sub.text.strip() if sub is not None and sub.text else ""

        id_txt = texto("id_motocicleta")
        anio_txt = texto("anio")
        cil_txt = texto("cilindraje")
        stock_txt = texto("stock")
        return {
            "id_motocicleta": int(id_txt) if id_txt else None,
            "marca": texto("marca"),
            "modelo": texto("modelo"),
            "anio": int(anio_txt) if anio_txt else 0,
            "cilindraje": int(cil_txt) if cil_txt else 0,
            "precio": texto("precio"),
            "stock": int(stock_txt) if stock_txt else 0,
            "estado": texto("estado"),
            "fecha_registro": texto("fecha_registro"),
        }

    def listar(self) -> list[dict]:
        root = self._enviar_soap("obtenerMotocicletas")
        lista_elem = find_deep_by_local(root, "motocicletas")
        if lista_elem is None:
            return []
        return [
            self._xml_a_motocicleta(child)
            for child in lista_elem
            if local_name(child.tag) == "motocicleta"
        ]

    def obtener_por_id(self, id_moto: int) -> dict:
        cuerpo = f"<id_motocicleta>{int(id_moto)}</id_motocicleta>"
        root = self._enviar_soap("obtenerMotocicleta", cuerpo)
        moto_elem = find_deep_by_local(root, "motocicleta")
        if moto_elem is None:
            raise ServiceError("La respuesta SOAP no incluyó los datos de la motocicleta.")
        return self._xml_a_motocicleta(moto_elem)

    def crear(self, datos: dict) -> dict:
        cuerpo = (
            f"<marca>{escape(str(datos['marca']))}</marca>"
            f"<modelo>{escape(str(datos['modelo']))}</modelo>"
            f"<anio>{int(datos['anio'])}</anio>"
            f"<cilindraje>{int(datos['cilindraje'])}</cilindraje>"
            f"<precio>{escape(str(datos['precio']))}</precio>"
            f"<stock>{int(datos['stock'])}</stock>"
            f"<estado>{escape(str(datos['estado']))}</estado>"
        )
        root = self._enviar_soap("crearMotocicleta", cuerpo)
        msg_elem = find_deep_by_local(root, "mensaje")
        id_elem = find_deep_by_local(root, "id_motocicleta")
        return {
            "mensaje": msg_elem.text if msg_elem is not None else "Motocicleta creada correctamente",
            "id_motocicleta": int(id_elem.text) if id_elem is not None and id_elem.text else None,
        }

    def actualizar(self, id_moto: int, datos: dict) -> str:
        cuerpo = (
            f"<id_motocicleta>{int(id_moto)}</id_motocicleta>"
            f"<marca>{escape(str(datos['marca']))}</marca>"
            f"<modelo>{escape(str(datos['modelo']))}</modelo>"
            f"<anio>{int(datos['anio'])}</anio>"
            f"<cilindraje>{int(datos['cilindraje'])}</cilindraje>"
            f"<precio>{escape(str(datos['precio']))}</precio>"
            f"<stock>{int(datos['stock'])}</stock>"
            f"<estado>{escape(str(datos['estado']))}</estado>"
        )
        root = self._enviar_soap("actualizarMotocicleta", cuerpo)
        msg_elem = find_deep_by_local(root, "mensaje")
        return msg_elem.text if msg_elem is not None and msg_elem.text else "Motocicleta actualizada correctamente"

    def eliminar(self, id_moto: int) -> str:
        cuerpo = f"<id_motocicleta>{int(id_moto)}</id_motocicleta>"
        root = self._enviar_soap("eliminarMotocicleta", cuerpo)
        msg_elem = find_deep_by_local(root, "mensaje")
        return msg_elem.text if msg_elem is not None and msg_elem.text else "Motocicleta eliminada correctamente"


def imprimir_encabezado(servicio_nombre: str) -> None:
    print("\n========================================")
    print("    INVENTARIO DE MOTOCICLETAS")
    print("========================================")
    print(f"Servicio seleccionado: {servicio_nombre}\n")


def imprimir_motocicleta(moto: dict) -> None:
    print(f"ID: {moto.get('id_motocicleta')}")
    print(f"Marca: {moto.get('marca')}")
    print(f"Modelo: {moto.get('modelo')}")
    print(f"Año: {moto.get('anio')}")
    print(f"Cilindraje: {moto.get('cilindraje')} cc")
    print(f"Precio: {formatear_precio(moto.get('precio'))}")
    print(f"Stock: {moto.get('stock')}")
    print(f"Estado: {moto.get('estado')}")
    if moto.get("fecha_registro"):
        print(f"Fecha registro: {moto.get('fecha_registro')}")


def solicitar_entero(mensaje: str) -> int:
    while True:
        valor = input(mensaje).strip()
        try:
            return int(valor)
        except ValueError:
            print("Error: ingrese un número entero válido.")


def solicitar_datos_motocicleta() -> dict:
    """Solicita por consola los campos de una motocicleta y los valida localmente."""
    while True:
        marca = input("Marca: ").strip()
        modelo = input("Modelo: ").strip()
        anio = input("Año de fabricación: ").strip()
        cilindraje = input("Cilindraje (cc): ").strip()
        precio = input("Precio: ").strip()
        stock = input("Stock: ").strip()
        estado = input("Estado (Disponible / Agotada / Inactiva): ").strip()
        try:
            return validar_datos_motocicleta(
                {
                    "marca": marca,
                    "modelo": modelo,
                    "anio": anio,
                    "cilindraje": cilindraje,
                    "precio": precio,
                    "stock": stock,
                    "estado": estado,
                }
            )
        except ValueError as exc:
            print(f"\n[Validación local] {exc} Intente nuevamente.\n")


def ejecutar_submenu(cliente: RestMotocicletaClient | SoapMotocicletaClient) -> None:
    while True:
        print(f"\n--- MENÚ DE OPERACIONES ({cliente.nombre}) ---")
        print("1. Listar motocicletas")
        print("2. Consultar motocicleta por ID")
        print("3. Registrar motocicleta")
        print("4. Actualizar motocicleta")
        print("5. Eliminar motocicleta")
        print("6. Volver al menú principal")

        opcion = input("Seleccione una opción: ").strip()
        if opcion in ("6", "0"):
            break

        try:
            if opcion == "1":
                motos = cliente.listar()
                imprimir_encabezado(cliente.nombre)
                if not motos:
                    print("No hay motocicletas registradas.")
                else:
                    for idx, moto in enumerate(motos):
                        if idx > 0:
                            print("----------------------------------------")
                        imprimir_motocicleta(moto)
                print("\nOperación ejecutada correctamente.")

            elif opcion == "2":
                id_moto = solicitar_entero("Ingrese el ID de la motocicleta: ")
                moto = cliente.obtener_por_id(id_moto)
                imprimir_encabezado(cliente.nombre)
                imprimir_motocicleta(moto)
                print("\nOperación ejecutada correctamente.")

            elif opcion == "3":
                datos = solicitar_datos_motocicleta()
                res = cliente.crear(datos)
                imprimir_encabezado(cliente.nombre)
                print(f"Mensaje: {res.get('mensaje')}")
                print(f"Nuevo ID asignado: {res.get('id_motocicleta')}")
                print("\nOperación ejecutada correctamente.")

            elif opcion == "4":
                id_moto = solicitar_entero("Ingrese el ID de la motocicleta a actualizar: ")
                datos = solicitar_datos_motocicleta()
                msg = cliente.actualizar(id_moto, datos)
                imprimir_encabezado(cliente.nombre)
                print(f"Mensaje: {msg}")
                print("\nOperación ejecutada correctamente.")

            elif opcion == "5":
                id_moto = solicitar_entero("Ingrese el ID de la motocicleta a eliminar: ")
                msg = cliente.eliminar(id_moto)
                imprimir_encabezado(cliente.nombre)
                print(f"Mensaje: {msg}")
                print("\nOperación ejecutada correctamente.")

            else:
                print("Opción no válida. Seleccione entre 1 y 6.")

        except ServiceError as exc:
            print(f"\n[Error del servicio {cliente.nombre}] {exc}")


def ejecutar_menu_interactivo(
    rest_client: RestMotocicletaClient, soap_client: SoapMotocicletaClient
) -> None:
    while True:
        print("\n========================================")
        print("SISTEMA DE INVENTARIO DE MOTOCICLETAS")
        print("========================================")
        print("Seleccione el servicio que desea utilizar:")
        print("1. Consumir API REST")
        print("2. Consumir servicio SOAP")
        print("3. Salir")

        opcion = input("Opción: ").strip()
        if opcion == "1":
            ejecutar_submenu(rest_client)
        elif opcion == "2":
            ejecutar_submenu(soap_client)
        elif opcion in ("3", "0"):
            print("Saliendo del sistema de inventario...")
            break
        else:
            print("Opción no válida. Seleccione 1, 2 o 3.")


def ejecutar_demostracion(
    rest_client: RestMotocicletaClient, soap_client: SoapMotocicletaClient
) -> int:
    """Ejecuta de forma automática la verificación de interoperabilidad (Sección 7.4 y 11)."""
    print("============================================================")
    print("DEMOSTRACIÓN DE INTEROPERABILIDAD DESDE CLIENTE PYTHON")
    print("============================================================")

    # 1. Consultar registro inicial (ID 2) por REST como en ejemplo 7.3
    imprimir_encabezado(rest_client.nombre)
    moto_inicial = rest_client.obtener_por_id(2)
    imprimir_motocicleta(moto_inicial)
    print("\nOperación ejecutada correctamente.")

    # 2. Registrar motocicleta usando REST y consultarla usando SOAP
    print("\n[Paso 1/6] Registrando motocicleta mediante API REST...")
    moto_nueva = validar_datos_motocicleta(
        {
            "marca": "Kawasaki",
            "modelo": "Z400",
            "anio": 2025,
            "cilindraje": 399,
            "precio": 28500000,
            "stock": 2,
            "estado": "Disponible",
        }
    )
    creada_rest = rest_client.crear(moto_nueva)
    id_creada_rest = int(creada_rest["id_motocicleta"])
    print(f"REST -> {creada_rest['mensaje']} (ID: {id_creada_rest})")

    print(f"\n[Paso 2/6] Consultando ID {id_creada_rest} desde servicio SOAP...")
    imprimir_encabezado(soap_client.nombre)
    imprimir_motocicleta(soap_client.obtener_por_id(id_creada_rest))
    print("\nOperación ejecutada correctamente.")

    # 3. Actualizar precio desde SOAP y consultar nuevo precio desde REST
    print(f"\n[Paso 3/6] Actualizando precio de ID {id_creada_rest} mediante SOAP...")
    datos_actualizados = validar_datos_motocicleta(
        {
            "marca": "Kawasaki",
            "modelo": "Z400 ABS",
            "anio": 2025,
            "cilindraje": 399,
            "precio": 29900000,
            "stock": 4,
            "estado": "Disponible",
        }
    )
    msg_upd = soap_client.actualizar(id_creada_rest, datos_actualizados)
    print(f"SOAP -> {msg_upd}")

    print(f"\n[Paso 4/6] Verificando nuevo precio de ID {id_creada_rest} desde REST...")
    imprimir_encabezado(rest_client.nombre)
    imprimir_motocicleta(rest_client.obtener_por_id(id_creada_rest))
    print("\nOperación ejecutada correctamente.")

    # 4. Registrar desde SOAP y consultar desde REST
    print("\n[Paso 5/6] Registrando motocicleta mediante SOAP y consultando desde REST...")
    moto_soap = validar_datos_motocicleta(
        {
            "marca": "KTM",
            "modelo": "Duke 200",
            "anio": 2025,
            "cilindraje": 200,
            "precio": 16800000,
            "stock": 3,
            "estado": "Disponible",
        }
    )
    creada_soap = soap_client.crear(moto_soap)
    id_creada_soap = int(creada_soap["id_motocicleta"])
    print(f"SOAP -> {creada_soap['mensaje']} (ID: {id_creada_soap})")
    consultada_rest = rest_client.obtener_por_id(id_creada_soap)
    print(
        f"REST -> Consultado ID {id_creada_soap}: {consultada_rest['marca']} "
        f"{consultada_rest['modelo']} ({formatear_precio(consultada_rest['precio'])})"
    )

    # 5. Eliminar ambos registros de prueba y comprobar en ambos servicios
    print("\n[Paso 6/6] Eliminando registros de prueba y comprobando inexistencia en ambos servicios...")
    print(f"REST eliminar ID {id_creada_rest} -> {rest_client.eliminar(id_creada_rest)}")
    print(f"SOAP eliminar ID {id_creada_soap} -> {soap_client.eliminar(id_creada_soap)}")

    for id_check in (id_creada_rest, id_creada_soap):
        for cli in (rest_client, soap_client):
            try:
                cli.obtener_por_id(id_check)
                print(f"ERROR: ID {id_check} todavía existe en {cli.nombre}")
                return 1
            except ServiceError as exc:
                print(f"Confirmado en {cli.nombre} que ID {id_check} ya no existe -> {exc}")

    print("\nINTEROPERABILIDAD VERIFICADA EXITOSAMENTE.")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Cliente de consola Python para API REST y SOAP de Motocicletas"
    )
    parser.add_argument("--rest-url", default=DEFAULT_REST_URL, help="URL base API REST")
    parser.add_argument("--soap-url", default=DEFAULT_SOAP_URL, help="URL endpoint SOAP")
    parser.add_argument(
        "--demo",
        action="store_true",
        help="Ejecuta la demostración automática de interoperabilidad REST <-> SOAP",
    )
    args = parser.parse_args()

    rest_client = RestMotocicletaClient(args.rest_url)
    soap_client = SoapMotocicletaClient(args.soap_url)

    if args.demo:
        return ejecutar_demostracion(rest_client, soap_client)

    ejecutar_menu_interactivo(rest_client, soap_client)
    return 0


if __name__ == "__main__":
    sys.exit(main())
