package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrabalhoRepository extends JpaRepository<Trabalho, Long> {

    // Todos os trabalhos de uma disciplina
    List<Trabalho> findByDisciplinaId(Long disciplinaId);

    // Todos os trabalhos (listagem geral)
    List<Trabalho> findAllByOrderByDataEntregaAsc();
}
