package com.gestion_motocicletas.shared.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EstadoMotocicleta {

    DISPONIBLE("Disponible"),
    AGOTADA("Agotada"),
    INACTIVA("Inactiva");

    private final String valor;

    EstadoMotocicleta(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static EstadoMotocicleta desdeValor(String valor) {
        if (valor == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(estado -> estado.valor.equalsIgnoreCase(valor.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estado no valido. Valores permitidos: Disponible, Agotada, Inactiva"));
    }
}
