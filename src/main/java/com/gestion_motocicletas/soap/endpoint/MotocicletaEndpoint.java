package com.gestion_motocicletas.soap.endpoint;

import com.gestion_motocicletas.shared.entity.Motocicleta;
import com.gestion_motocicletas.shared.service.MotocicletaService;
import com.gestion_motocicletas.soap.config.SoapNamespaces;
import com.gestion_motocicletas.soap.dto.ActualizarMotocicletaRequest;
import com.gestion_motocicletas.soap.dto.ActualizarMotocicletaResponse;
import com.gestion_motocicletas.soap.dto.CrearMotocicletaRequest;
import com.gestion_motocicletas.soap.dto.CrearMotocicletaResponse;
import com.gestion_motocicletas.soap.dto.EliminarMotocicletaRequest;
import com.gestion_motocicletas.soap.dto.EliminarMotocicletaResponse;
import com.gestion_motocicletas.soap.dto.ObtenerMotocicletaRequest;
import com.gestion_motocicletas.soap.dto.ObtenerMotocicletaResponse;
import com.gestion_motocicletas.soap.dto.ObtenerMotocicletasRequest;
import com.gestion_motocicletas.soap.dto.ObtenerMotocicletasResponse;
import com.gestion_motocicletas.soap.mapper.MotocicletaSoapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class MotocicletaEndpoint {

    private final MotocicletaService motocicletaService;
    private final MotocicletaSoapMapper motocicletaSoapMapper;

    @PayloadRoot(namespace = SoapNamespaces.MOTOCICLETAS, localPart = "obtenerMotocicletas")
    @ResponsePayload
    public ObtenerMotocicletasResponse obtenerMotocicletas(
            @RequestPayload ObtenerMotocicletasRequest request) {
        return new ObtenerMotocicletasResponse(
                motocicletaSoapMapper.toXml(motocicletaService.findAll()));
    }

    @PayloadRoot(namespace = SoapNamespaces.MOTOCICLETAS, localPart = "obtenerMotocicleta")
    @ResponsePayload
    public ObtenerMotocicletaResponse obtenerMotocicleta(
            @RequestPayload ObtenerMotocicletaRequest request) {
        Motocicleta motocicleta = motocicletaService.findById(request.getIdMotocicleta());
        return new ObtenerMotocicletaResponse(motocicletaSoapMapper.toXml(motocicleta));
    }

    @PayloadRoot(namespace = SoapNamespaces.MOTOCICLETAS, localPart = "crearMotocicleta")
    @ResponsePayload
    public CrearMotocicletaResponse crearMotocicleta(
            @RequestPayload CrearMotocicletaRequest request) {
        Motocicleta motocicleta = motocicletaService.save(
                motocicletaSoapMapper.toEntity(request));
        return new CrearMotocicletaResponse(
                "Motocicleta creada correctamente",
                motocicleta.getIdMotocicleta());
    }

    @PayloadRoot(namespace = SoapNamespaces.MOTOCICLETAS, localPart = "actualizarMotocicleta")
    @ResponsePayload
    public ActualizarMotocicletaResponse actualizarMotocicleta(
            @RequestPayload ActualizarMotocicletaRequest request) {
        motocicletaService.update(request.getIdMotocicleta(),
                motocicletaSoapMapper.toEntity(request));
        return new ActualizarMotocicletaResponse("Motocicleta actualizada correctamente");
    }

    @PayloadRoot(namespace = SoapNamespaces.MOTOCICLETAS, localPart = "eliminarMotocicleta")
    @ResponsePayload
    public EliminarMotocicletaResponse eliminarMotocicleta(
            @RequestPayload EliminarMotocicletaRequest request) {
        motocicletaService.delete(request.getIdMotocicleta());
        return new EliminarMotocicletaResponse("Motocicleta eliminada correctamente");
    }
}
