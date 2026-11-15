package com.gestion_motocicletas;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationTests {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String restUrl(String suffix) {
        return "http://localhost:" + port + "/gestion_motocicletas/api/motocicletas" + suffix;
    }

    private String soapUrl() {
        return "http://localhost:" + port + "/gestion_motocicletas/ws";
    }

    private String wsdlUrl() {
        return "http://localhost:" + port + "/gestion_motocicletas/ws/motocicletas.wsdl";
    }

    private HttpResponse<String> sendRest(String method, String suffix, String jsonBody) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(restUrl(suffix)))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
        if (jsonBody != null) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(jsonBody));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendSoap(String innerXml) throws Exception {
        String envelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                               xmlns:ser="urn:MotocicletasService">
                  <soap:Header/>
                  <soap:Body>
                    %s
                  </soap:Body>
                </soap:Envelope>
                """.formatted(innerXml);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(soapUrl()))
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("Accept", "text/xml")
                .POST(HttpRequest.BodyPublishers.ofString(envelope))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private int extractInt(String body, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(body);
        assertTrue(matcher.find(), "No se encontró el patrón " + regex + " en: " + body);
        return Integer.parseInt(matcher.group(1));
    }

    @Test
    @Order(1)
    void contextLoadsAndWsdlAvailable() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(wsdlUrl())).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("urn:MotocicletasService"));
        assertTrue(response.body().contains("obtenerMotocicletas"));
    }

    @Test
    @Order(2)
    void escenario01_02_03_consultasRestYSoap() throws Exception {
        // Escenario 1: Consultar todas las motocicletas (REST y SOAP)
        HttpResponse<String> restAll = sendRest("GET", "", null);
        assertEquals(200, restAll.statusCode());
        assertTrue(restAll.body().contains("Yamaha"));
        assertTrue(restAll.body().contains("Pulsar NS200"));

        HttpResponse<String> soapAll = sendSoap("<ser:obtenerMotocicletas/>");
        assertEquals(200, soapAll.statusCode());
        assertTrue(soapAll.body().contains("Yamaha"));
        assertTrue(soapAll.body().contains("Pulsar NS200"));

        // Escenario 2: Consultar ID válido (2 = Bajaj Pulsar NS200)
        HttpResponse<String> restById = sendRest("GET", "/2", null);
        assertEquals(200, restById.statusCode());
        assertTrue(restById.body().contains("Pulsar NS200"));

        HttpResponse<String> soapById = sendSoap("<ser:obtenerMotocicleta><id_motocicleta>2</id_motocicleta></ser:obtenerMotocicleta>");
        assertEquals(200, soapById.statusCode());
        assertTrue(soapById.body().contains("Pulsar NS200"));

        // Escenario 3: Consultar ID inexistente (9999)
        HttpResponse<String> restNotFound = sendRest("GET", "/9999", null);
        assertEquals(404, restNotFound.statusCode());

        HttpResponse<String> soapNotFound = sendSoap("<ser:obtenerMotocicleta><id_motocicleta>9999</id_motocicleta></ser:obtenerMotocicleta>");
        assertEquals(500, soapNotFound.statusCode());
        assertTrue(soapNotFound.body().contains("Fault"));
        assertTrue(soapNotFound.body().contains("No existe una motocicleta"));
    }

    @Test
    @Order(3)
    void escenario04_08_10_12_13_crudEInteroperabilidadRestYSoap() throws Exception {
        // Escenario 4 + 13: Crear por REST y consultar desde SOAP
        String jsonCrear = """
                {
                  "marca": "Kawasaki",
                  "modelo": "Z400",
                  "anio": 2025,
                  "cilindraje": 399,
                  "precio": 28500000,
                  "stock": 2,
                  "estado": "Disponible"
                }
                """;
        HttpResponse<String> resCrearRest = sendRest("POST", "", jsonCrear);
        assertEquals(201, resCrearRest.statusCode());
        int idRest = extractInt(resCrearRest.body(), "\"id_motocicleta\"\\s*:\\s*(\\d+)");

        HttpResponse<String> resConsultaSoap = sendSoap(
                "<ser:obtenerMotocicleta><id_motocicleta>" + idRest + "</id_motocicleta></ser:obtenerMotocicleta>");
        assertEquals(200, resConsultaSoap.statusCode());
        assertTrue(resConsultaSoap.body().contains("Kawasaki"));
        assertTrue(resConsultaSoap.body().contains("Z400"));

        // Escenario 8: Actualizar por SOAP y verificar reflejo en REST
        HttpResponse<String> resUpdSoap = sendSoap("""
                <ser:actualizarMotocicleta>
                  <id_motocicleta>%d</id_motocicleta>
                  <marca>Kawasaki</marca>
                  <modelo>Z400 ABS</modelo>
                  <anio>2025</anio>
                  <cilindraje>399</cilindraje>
                  <precio>29900000</precio>
                  <stock>4</stock>
                  <estado>Disponible</estado>
                </ser:actualizarMotocicleta>
                """.formatted(idRest));
        assertEquals(200, resUpdSoap.statusCode());

        HttpResponse<String> resVerRest = sendRest("GET", "/" + idRest, null);
        assertEquals(200, resVerRest.statusCode());
        assertTrue(resVerRest.body().contains("Z400 ABS"));
        assertTrue(resVerRest.body().contains("29900000"));

        // Escenario 4 + 12: Crear por SOAP y consultar desde REST
        HttpResponse<String> resCrearSoap = sendSoap("""
                <ser:crearMotocicleta>
                  <marca>KTM</marca>
                  <modelo>Duke 200</modelo>
                  <anio>2025</anio>
                  <cilindraje>200</cilindraje>
                  <precio>16800000</precio>
                  <stock>3</stock>
                  <estado>Disponible</estado>
                </ser:crearMotocicleta>
                """);
        assertEquals(200, resCrearSoap.statusCode());
        int idSoap = extractInt(resCrearSoap.body(), "<id_motocicleta>(\\d+)</id_motocicleta>");

        HttpResponse<String> resConsultaRest = sendRest("GET", "/" + idSoap, null);
        assertEquals(200, resConsultaRest.statusCode());
        assertTrue(resConsultaRest.body().contains("Duke 200"));

        // Escenario 8 en REST: Actualizar por REST y verificar reflejo en SOAP
        String jsonUpd = """
                {
                  "marca": "KTM",
                  "modelo": "Duke 200 NG",
                  "anio": 2025,
                  "cilindraje": 200,
                  "precio": 17200000,
                  "stock": 0,
                  "estado": "Agotada"
                }
                """;
        HttpResponse<String> resUpdRest = sendRest("PUT", "/" + idSoap, jsonUpd);
        assertEquals(200, resUpdRest.statusCode());

        HttpResponse<String> resVerSoap = sendSoap(
                "<ser:obtenerMotocicleta><id_motocicleta>" + idSoap + "</id_motocicleta></ser:obtenerMotocicleta>");
        assertEquals(200, resVerSoap.statusCode());
        assertTrue(resVerSoap.body().contains("Duke 200 NG"));
        assertTrue(resVerSoap.body().contains("Agotada"));

        // Escenario 10: Eliminar registros existentes en REST y SOAP y comprobar ausencia en ambos
        HttpResponse<String> resDelRest = sendRest("DELETE", "/" + idRest, null);
        assertEquals(204, resDelRest.statusCode());
        assertEquals(404, sendRest("GET", "/" + idRest, null).statusCode());
        assertTrue(sendSoap("<ser:obtenerMotocicleta><id_motocicleta>" + idRest + "</id_motocicleta></ser:obtenerMotocicleta>")
                .body().contains("Fault"));

        HttpResponse<String> resDelSoap = sendSoap(
                "<ser:eliminarMotocicleta><id_motocicleta>" + idSoap + "</id_motocicleta></ser:eliminarMotocicleta>");
        assertEquals(200, resDelSoap.statusCode());
        assertEquals(404, sendRest("GET", "/" + idSoap, null).statusCode());
    }

    @Test
    @Order(4)
    void escenario05_06_07_09_11_14_validacionesYErroresRestYSoap() throws Exception {
        // Escenario 5: Crear con marca vacía
        String marcaVaciaJson = """
                {"marca":"","modelo":"X","anio":2024,"cilindraje":150,"precio":1000,"stock":1,"estado":"Disponible"}
                """;
        assertEquals(400, sendRest("POST", "", marcaVaciaJson).statusCode());
        HttpResponse<String> soapMarcaVacia = sendSoap("""
                <ser:crearMotocicleta>
                  <marca></marca><modelo>X</modelo><anio>2024</anio><cilindraje>150</cilindraje>
                  <precio>1000</precio><stock>1</stock><estado>Disponible</estado>
                </ser:crearMotocicleta>
                """);
        assertTrue(soapMarcaVacia.body().contains("Fault"));

        // Escenario 6: Crear con precio negativo
        String precioNegJson = """
                {"marca":"Yamaha","modelo":"R15","anio":2024,"cilindraje":155,"precio":-500,"stock":1,"estado":"Disponible"}
                """;
        assertEquals(400, sendRest("POST", "", precioNegJson).statusCode());
        HttpResponse<String> soapPrecioNeg = sendSoap("""
                <ser:crearMotocicleta>
                  <marca>Yamaha</marca><modelo>R15</modelo><anio>2024</anio><cilindraje>155</cilindraje>
                  <precio>-500</precio><stock>1</stock><estado>Disponible</estado>
                </ser:crearMotocicleta>
                """);
        assertTrue(soapPrecioNeg.body().contains("Fault"));

        // Escenario 7: Crear con stock negativo
        String stockNegJson = """
                {"marca":"Yamaha","modelo":"R15","anio":2024,"cilindraje":155,"precio":12000000,"stock":-2,"estado":"Disponible"}
                """;
        assertEquals(400, sendRest("POST", "", stockNegJson).statusCode());
        HttpResponse<String> soapStockNeg = sendSoap("""
                <ser:crearMotocicleta>
                  <marca>Yamaha</marca><modelo>R15</modelo><anio>2024</anio><cilindraje>155</cilindraje>
                  <precio>12000000</precio><stock>-2</stock><estado>Disponible</estado>
                </ser:crearMotocicleta>
                """);
        assertTrue(soapStockNeg.body().contains("Fault"));

        // Escenario 9: Actualizar ID inexistente
        String validaJson = """
                {"marca":"Yamaha","modelo":"R15","anio":2024,"cilindraje":155,"precio":12000000,"stock":1,"estado":"Disponible"}
                """;
        assertEquals(404, sendRest("PUT", "/9999", validaJson).statusCode());
        assertTrue(sendSoap("""
                <ser:actualizarMotocicleta>
                  <id_motocicleta>9999</id_motocicleta><marca>Yamaha</marca><modelo>R15</modelo>
                  <anio>2024</anio><cilindraje>155</cilindraje><precio>12000000</precio><stock>1</stock><estado>Disponible</estado>
                </ser:actualizarMotocicleta>
                """).body().contains("Fault"));

        // Escenario 11: Eliminar ID inexistente
        assertEquals(404, sendRest("DELETE", "/9999", null).statusCode());
        assertTrue(sendSoap("<ser:eliminarMotocicleta><id_motocicleta>9999</id_motocicleta></ser:eliminarMotocicleta>")
                .body().contains("Fault"));

        // Escenario 14: Petición con datos mal formados sin exponer datos internos
        HttpResponse<String> restMalFormado = sendRest("POST", "", "{ \"marca\": \"Yamaha\", \"anio\": \"no-numero\" ");
        assertEquals(400, restMalFormado.statusCode());
        assertNotNull(restMalFormado.body());
        assertFalse(restMalFormado.body().toLowerCase().contains("hibernate"));
        assertFalse(restMalFormado.body().toLowerCase().contains("sql"));

        HttpResponse<String> soapMalFormado = sendSoap("""
                <ser:crearMotocicleta>
                  <marca>Yamaha</marca><modelo>R15</modelo><anio>no-numero</anio><cilindraje>155</cilindraje>
                  <precio>12000000</precio><stock>1</stock><estado>Disponible</estado>
                </ser:crearMotocicleta>
                """);
        assertTrue(soapMalFormado.body().contains("Fault"));
        assertFalse(soapMalFormado.body().toLowerCase().contains("hibernate"));
        assertFalse(soapMalFormado.body().toLowerCase().contains("sql"));
    }
}
