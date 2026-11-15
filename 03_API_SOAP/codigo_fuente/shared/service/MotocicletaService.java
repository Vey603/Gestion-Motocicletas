package com.gestion_motocicletas.shared.service;

import com.gestion_motocicletas.shared.entity.EstadoMotocicleta;
import com.gestion_motocicletas.shared.entity.Motocicleta;
import com.gestion_motocicletas.shared.exception.MotocicletaInvalidaException;
import com.gestion_motocicletas.shared.exception.MotocicletaNoEncontradaException;
import com.gestion_motocicletas.shared.repository.MotocicletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MotocicletaService {

    private final MotocicletaRepository motocicletaRepository;

    @Transactional(readOnly = true)
    public List<Motocicleta> findAll() {
        return motocicletaRepository.findAll(Sort.by(Sort.Direction.ASC, "idMotocicleta"));
    }

    @Transactional(readOnly = true)
    public Motocicleta findById(Integer id) {
        if (id == null) {
            throw new MotocicletaInvalidaException("El ID de la motocicleta es obligatorio");
        }
        return motocicletaRepository.findById(id)
                .orElseThrow(() -> new MotocicletaNoEncontradaException(
                        "No existe una motocicleta con el ID " + id));
    }

    @Transactional
    public Motocicleta save(Motocicleta motocicleta) {
        if (motocicleta == null) {
            throw new MotocicletaInvalidaException("Los datos de la motocicleta son obligatorios");
        }
        motocicleta.setIdMotocicleta(null);
        normalizar(motocicleta);
        validar(motocicleta);
        return motocicletaRepository.save(motocicleta);
    }

    @Transactional
    public Motocicleta update(Integer id, Motocicleta datos) {
        Motocicleta motocicleta = findById(id);
        if (datos == null) {
            throw new MotocicletaInvalidaException("Los datos de la motocicleta son obligatorios");
        }

        normalizar(datos);
        validar(datos);

        motocicleta.setMarca(datos.getMarca());
        motocicleta.setModelo(datos.getModelo());
        motocicleta.setAnio(datos.getAnio());
        motocicleta.setCilindraje(datos.getCilindraje());
        motocicleta.setPrecio(datos.getPrecio());
        motocicleta.setStock(datos.getStock());
        motocicleta.setEstado(datos.getEstado());

        return motocicletaRepository.save(motocicleta);
    }

    @Transactional
    public void delete(Integer id) {
        Motocicleta motocicleta = findById(id);
        motocicletaRepository.delete(motocicleta);
    }

    private void normalizar(Motocicleta motocicleta) {
        if (motocicleta.getMarca() != null) {
            motocicleta.setMarca(motocicleta.getMarca().trim());
        }
        if (motocicleta.getModelo() != null) {
            motocicleta.setModelo(motocicleta.getModelo().trim());
        }
    }

    private void validar(Motocicleta motocicleta) {
        if (motocicleta.getMarca() == null || motocicleta.getMarca().isBlank()) {
            throw new MotocicletaInvalidaException("La marca no puede estar vacía");
        }
        if (motocicleta.getMarca().length() > 60) {
            throw new MotocicletaInvalidaException("La marca no puede superar los 60 caracteres");
        }
        if (motocicleta.getModelo() == null || motocicleta.getModelo().isBlank()) {
            throw new MotocicletaInvalidaException("El modelo no puede estar vacío");
        }
        if (motocicleta.getModelo().length() > 80) {
            throw new MotocicletaInvalidaException("El modelo no puede superar los 80 caracteres");
        }

        int anioActual = Year.now().getValue();
        if (motocicleta.getAnio() == null) {
            throw new MotocicletaInvalidaException("El año es obligatorio");
        }
        if (motocicleta.getAnio() < 1900 || motocicleta.getAnio() > anioActual) {
            throw new MotocicletaInvalidaException(
                    "El año debe estar entre 1900 y " + anioActual);
        }
        if (motocicleta.getCilindraje() == null || motocicleta.getCilindraje() <= 0) {
            throw new MotocicletaInvalidaException("El cilindraje debe ser mayor que cero");
        }
        if (motocicleta.getPrecio() == null || motocicleta.getPrecio().signum() < 0) {
            throw new MotocicletaInvalidaException("El precio no puede ser negativo");
        }
        if (motocicleta.getStock() == null || motocicleta.getStock() < 0) {
            throw new MotocicletaInvalidaException("El stock no puede ser negativo");
        }
        if (motocicleta.getEstado() == null) {
            throw new MotocicletaInvalidaException("El estado es obligatorio");
        }

        if (motocicleta.getStock() == 0 && motocicleta.getEstado() == EstadoMotocicleta.DISPONIBLE) {
            throw new MotocicletaInvalidaException(
                    "Una motocicleta sin stock no puede estar en estado Disponible");
        }
        if (motocicleta.getStock() > 0 && motocicleta.getEstado() == EstadoMotocicleta.AGOTADA) {
            throw new MotocicletaInvalidaException(
                    "Una motocicleta con stock disponible no puede estar en estado Agotada");
        }
    }
}
