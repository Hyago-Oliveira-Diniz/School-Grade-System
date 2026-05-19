package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    Disciplina findByCodigo(String codigo);
    List<Disciplina> findByNomeContainingIgnoreCase(String nome);
}
