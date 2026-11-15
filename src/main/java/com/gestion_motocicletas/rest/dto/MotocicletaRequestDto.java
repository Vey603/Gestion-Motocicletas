package com.gestion_motocicletas.rest.dto;

import com.gestion_motocicletas.shared.entity.EstadoMotocicleta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MotocicletaRequestDto(

        @NotBlank(message = "La marca no puede estar vacía")
        @Size(max = 60, message = "La marca no puede superar los 60 caracteres")
        String marca,

        @NotBlank(message = "El modelo no puede estar vacío")
        @Size(max = 80, message = "El modelo no puede superar los 80 caracteres")
        String modelo,

        @NotNull(message = "El año es obligatorio")
        @Min(value = 1900, message = "El año debe ser mayor o igual a 1900")
        Integer anio,

        @NotNull(message = "El cilindraje es obligatorio")
        @Min(value = 1, message = "El cilindraje debe ser mayor que cero")
        Integer cilindraje,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
        @Digits(integer = 10, fraction = 2, message = "El precio admite máximo 10 enteros y 2 decimales")
        BigDecimal precio,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @NotNull(message = "El estado es obligatorio")
        EstadoMotocicleta estado
) {
}
