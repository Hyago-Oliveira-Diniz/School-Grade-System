package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    List<Turma> findByAno(String ano);
    List<Turma> findByPeriodo(String periodo);
}
