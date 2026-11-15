package com.gestion_motocicletas.soap.mapper;

import com.gestion_motocicletas.shared.entity.EstadoMotocicleta;
import com.gestion_motocicletas.shared.entity.Motocicleta;
import com.gestion_motocicletas.shared.exception.MotocicletaInvalidaException;
import com.gestion_motocicletas.soap.dto.ActualizarMotocicletaRequest;
import com.gestion_motocicletas.soap.dto.CrearMotocicletaRequest;
import com.gestion_motocicletas.soap.dto.MotocicletaXml;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MotocicletaSoapMapper {

    private static final DateTimeFormatter FECHA_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public Motocicleta toEntity(CrearMotocicletaRequest request) {
        Motocicleta motocicleta = new Motocicleta();
        llenarDatos(motocicleta, request.getMarca(), request.getModelo(), request.getAnio(),
                request.getCilindraje(), request.getPrecio(), request.getStock(), request.getEstado());
        return motocicleta;
    }

    public Motocicleta toEntity(ActualizarMotocicletaRequest request) {
        Motocicleta motocicleta = new Motocicleta();
        llenarDatos(motocicleta, request.getMarca(), request.getModelo(), request.getAnio(),
                request.getCilindraje(), request.getPrecio(), request.getStock(), request.getEstado());
        return motocicleta;
    }

    public MotocicletaXml toXml(Motocicleta motocicleta) {
        MotocicletaXml xml = new MotocicletaXml();
        xml.setIdMotocicleta(motocicleta.getIdMotocicleta());
        xml.setMarca(motocicleta.getMarca());
        xml.setModelo(motocicleta.getModelo());
        xml.setAnio(motocicleta.getAnio());
        xml.setCilindraje(motocicleta.getCilindraje());
        xml.setPrecio(motocicleta.getPrecio());
        xml.setStock(motocicleta.getStock());
        xml.setEstado(motocicleta.getEstado() == null ? null : motocicleta.getEstado().getValor());
        xml.setFechaRegistro(motocicleta.getFechaRegistro() == null
                ? null
                : FECHA_FORMATTER.format(motocicleta.getFechaRegistro()));
        return xml;
    }

    public List<MotocicletaXml> toXml(List<Motocicleta> motocicletas) {
        return motocicletas.stream().map(this::toXml).toList();
    }

    private void llenarDatos(Motocicleta motocicleta, String marca, String modelo, Integer anio,
                             Integer cilindraje, BigDecimal precio, Integer stock, String estado) {

        motocicleta.setMarca(marca);
        motocicleta.setModelo(modelo);
        motocicleta.setAnio(anio);
        motocicleta.setCilindraje(cilindraje);
        motocicleta.setPrecio(precio);
        motocicleta.setStock(stock);
        motocicleta.setEstado(convertirEstado(estado));
    }

    private EstadoMotocicleta convertirEstado(String estado) {
        try {
            return EstadoMotocicleta.desdeValor(estado);
        } catch (IllegalArgumentException ex) {
            throw new MotocicletaInvalidaException(ex.getMessage());
        }
    }
}
