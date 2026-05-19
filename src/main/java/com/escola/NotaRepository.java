package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    // Todas as notas de um aluno
    List<Nota> findByAlunoId(Long alunoId);

    // Notas de um aluno em uma disciplina específica
    List<Nota> findByAlunoIdAndDisciplinaId(Long alunoId, Long disciplinaId);

    // Notas de um aluno em uma disciplina por bimestre
    Nota findByAlunoIdAndDisciplinaIdAndBimestre(Long alunoId, Long disciplinaId, Integer bimestre);

    // Média geral de uma disciplina
    @Query("SELECT AVG(n.valor) FROM Nota n WHERE n.disciplina.id = :disciplinaId")
    Double mediaGeralDisciplina(@Param("disciplinaId") Long disciplinaId);
}
