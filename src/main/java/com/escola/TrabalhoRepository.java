package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrabalhoRepository extends JpaRepository<Trabalho, Long> {
    List<Trabalho> findByDisciplinaId(Long disciplinaId);
    List<Trabalho> findByTurmaId(Long turmaId);
    List<Trabalho> findByBimestre(Integer bimestre);
}
