package com.gestion_motocicletas.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MotocicletaCreadaResponseDto(
        String mensaje,
        @JsonProperty("id_motocicleta") Integer idMotocicleta
) {
}
