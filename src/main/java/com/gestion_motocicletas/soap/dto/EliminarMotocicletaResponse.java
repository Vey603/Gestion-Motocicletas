package com.gestion_motocicletas.soap.dto;

import com.gestion_motocicletas.soap.config.SoapNamespaces;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "eliminarMotocicletaResponse", namespace = SoapNamespaces.MOTOCICLETAS)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", namespace = SoapNamespaces.MOTOCICLETAS, propOrder = {"mensaje"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EliminarMotocicletaResponse {

    @XmlElement(required = true)
    private String mensaje;
}
