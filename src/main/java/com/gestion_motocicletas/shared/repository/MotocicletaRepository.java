package com.gestion_motocicletas.shared.repository;

import com.gestion_motocicletas.shared.entity.Motocicleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotocicletaRepository extends JpaRepository<Motocicleta, Integer> {
}
