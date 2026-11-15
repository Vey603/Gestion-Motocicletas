package com.gestion_motocicletas.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gestion_motocicletas.shared.entity.EstadoMotocicleta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MotocicletaResponseDto(
        @JsonProperty("id_motocicleta") Integer idMotocicleta,
        String marca,
        String modelo,
        Integer anio,
        Integer cilindraje,
        BigDecimal precio,
        Integer stock,
        EstadoMotocicleta estado,
        @JsonProperty("fecha_registro") LocalDateTime fechaRegistro
) {
}
