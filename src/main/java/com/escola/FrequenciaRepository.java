package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {

    // Todas as frequências de um aluno
    List<Frequencia> findByAlunoId(Long alunoId);

    // Frequências de um aluno em uma disciplina
    List<Frequencia> findByAlunoIdAndDisciplinaId(Long alunoId, Long disciplinaId);

    // Frequências de uma disciplina em uma data específica
    List<Frequencia> findByDisciplinaIdAndData(Long disciplinaId, LocalDate data);

    // Percentual de presença de um aluno em uma disciplina
    @Query("""
        SELECT
            COUNT(f) AS total,
            SUM(CASE WHEN f.presente = true THEN 1 ELSE 0 END) AS presencas
        FROM Frequencia f
        WHERE f.aluno.id = :alunoId AND f.disciplina.id = :disciplinaId
    """)
    Object[] resumoFrequencia(@Param("alunoId") Long alunoId,
                              @Param("disciplinaId") Long disciplinaId);
}
