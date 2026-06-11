package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotaTrabalhoRepository extends JpaRepository<NotaTrabalho, Long> {

    // Todas as notas de trabalhos de um aluno
    List<NotaTrabalho> findByAlunoId(Long alunoId);

    // Notas de um aluno em trabalhos de uma disciplina específica
    List<NotaTrabalho> findByAlunoIdAndTrabalho_DisciplinaId(Long alunoId, Long disciplinaId);

    // Busca nota de um aluno num trabalho específico (para não duplicar ao lançar)
    Optional<NotaTrabalho> findByTrabalhoIdAndAlunoId(Long trabalhoId, Long alunoId);

    // Todas as notas de um trabalho (para ver o resultado da turma)
    List<NotaTrabalho> findByTrabalhoId(Long trabalhoId);
}
