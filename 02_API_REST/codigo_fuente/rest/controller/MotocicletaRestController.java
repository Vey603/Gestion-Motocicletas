package com.gestion_motocicletas.rest.controller;

import com.gestion_motocicletas.rest.dto.MotocicletaCreadaResponseDto;
import com.gestion_motocicletas.rest.dto.MotocicletaRequestDto;
import com.gestion_motocicletas.rest.dto.MotocicletaResponseDto;
import com.gestion_motocicletas.rest.mapper.MotocicletaRestMapper;
import com.gestion_motocicletas.shared.entity.Motocicleta;
import com.gestion_motocicletas.shared.service.MotocicletaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/motocicletas")
@RequiredArgsConstructor
public class MotocicletaRestController {

    private final MotocicletaService motocicletaService;
    private final MotocicletaRestMapper motocicletaRestMapper;

    @GetMapping
    public ResponseEntity<List<MotocicletaResponseDto>> findAll() {
        return ResponseEntity.ok(motocicletaRestMapper.toDto(motocicletaService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotocicletaResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(motocicletaRestMapper.toDto(motocicletaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<MotocicletaCreadaResponseDto> save(
            @Valid @RequestBody MotocicletaRequestDto motocicletaRequestDto) {

        Motocicleta motocicleta = motocicletaService.save(
                motocicletaRestMapper.toEntity(motocicletaRequestDto));

        MotocicletaCreadaResponseDto response = new MotocicletaCreadaResponseDto(
                "Motocicleta creada correctamente",
                motocicleta.getIdMotocicleta());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotocicletaResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody MotocicletaRequestDto motocicletaRequestDto) {

        Motocicleta motocicleta = motocicletaService.update(
                id,
                motocicletaRestMapper.toEntity(motocicletaRequestDto));

        return ResponseEntity.ok(motocicletaRestMapper.toDto(motocicleta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        motocicletaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
