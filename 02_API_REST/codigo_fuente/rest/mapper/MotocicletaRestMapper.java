package com.gestion_motocicletas.rest.mapper;

import com.gestion_motocicletas.rest.dto.MotocicletaRequestDto;
import com.gestion_motocicletas.rest.dto.MotocicletaResponseDto;
import com.gestion_motocicletas.shared.entity.Motocicleta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MotocicletaRestMapper {

    @Mapping(target = "idMotocicleta", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    Motocicleta toEntity(MotocicletaRequestDto motocicletaRequestDto);

    MotocicletaResponseDto toDto(Motocicleta motocicleta);

    List<MotocicletaResponseDto> toDto(List<Motocicleta> motocicletas);
}
