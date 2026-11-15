package com.gestion_motocicletas.soap.dto;

import com.gestion_motocicletas.soap.config.SoapNamespaces;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "obtenerMotocicletas", namespace = SoapNamespaces.MOTOCICLETAS)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", namespace = SoapNamespaces.MOTOCICLETAS)
@NoArgsConstructor
public class ObtenerMotocicletasRequest {
}
