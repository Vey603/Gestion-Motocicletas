package com.gestion_motocicletas.shared.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoMotocicletaConverter implements AttributeConverter<EstadoMotocicleta, String> {

    @Override
    public String convertToDatabaseColumn(EstadoMotocicleta estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoMotocicleta convertToEntityAttribute(String valor) {
        return valor == null ? null : EstadoMotocicleta.desdeValor(valor);
    }
}
