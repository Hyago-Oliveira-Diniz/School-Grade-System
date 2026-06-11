package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByAlunoId(Long alunoId);
    List<Avaliacao> findByAlunoIdAndDisciplinaId(Long alunoId, Long disciplinaId);
    List<Avaliacao> findByAlunoIdAndBimestre(Long alunoId, Integer bimestre);

    @Query("SELECT a FROM Avaliacao a WHERE a.aluno.id = :alunoId AND a.disciplina.id = :disciplinaId AND a.bimestre = :bimestre")
    List<Avaliacao> findBoletim(@Param("alunoId") Long alunoId,
                                @Param("disciplinaId") Long disciplinaId,
                                @Param("bimestre") Integer bimestre);
}
