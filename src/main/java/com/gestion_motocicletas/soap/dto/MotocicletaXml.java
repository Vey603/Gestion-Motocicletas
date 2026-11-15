package com.gestion_motocicletas.soap.dto;

import com.gestion_motocicletas.soap.config.SoapNamespaces;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Motocicleta", propOrder = {
        "idMotocicleta", "marca", "modelo", "anio", "cilindraje",
        "precio", "stock", "estado", "fechaRegistro"
})
@Getter
@Setter
@NoArgsConstructor
public class MotocicletaXml {

    @XmlElement(name = "id_motocicleta")
    private Integer idMotocicleta;

    @XmlElement(required = true)
    private String marca;

    @XmlElement(required = true)
    private String modelo;

    @XmlElement(required = true)
    private Integer anio;

    @XmlElement(required = true)
    private Integer cilindraje;

    @XmlElement(required = true)
    private BigDecimal precio;

    @XmlElement(required = true)
    private Integer stock;

    @XmlElement(required = true)
    private String estado;

    @XmlElement(name = "fecha_registro")
    private String fechaRegistro;
}
