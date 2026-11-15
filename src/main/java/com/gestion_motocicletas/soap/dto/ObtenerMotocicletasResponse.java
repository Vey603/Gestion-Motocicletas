package com.gestion_motocicletas.soap.dto;

import com.gestion_motocicletas.soap.config.SoapNamespaces;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "obtenerMotocicletasResponse", namespace = SoapNamespaces.MOTOCICLETAS)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", namespace = SoapNamespaces.MOTOCICLETAS, propOrder = {"motocicletas"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ObtenerMotocicletasResponse {

    @XmlElementWrapper(name = "motocicletas")
    @XmlElement(name = "motocicleta")
    private List<MotocicletaXml> motocicletas = new ArrayList<>();
}
