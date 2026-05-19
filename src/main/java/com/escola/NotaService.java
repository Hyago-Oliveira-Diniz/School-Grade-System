package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotaService {

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public Nota lancarNota(Long alunoId, Long disciplinaId, Integer bimestre, Double valor) {
        if (bimestre < 1 || bimestre > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bimestre deve ser entre 1 e 4");
        }
        if (valor < 0 || valor > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nota deve ser entre 0 e 10");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Disciplina não encontrada"));

        // Atualiza se já existe nota para esse bimestre
        Nota nota = notaRepository.findByAlunoIdAndDisciplinaIdAndBimestre(
                alunoId, disciplinaId, bimestre);

        if (nota == null) {
            nota = new Nota();
            nota.setAluno(aluno);
            nota.setDisciplina(disciplina);
            nota.setBimestre(bimestre);
        }

        nota.setValor(valor);

        // Recalcula a nota final (média dos bimestres lançados)
        Nota salva = notaRepository.save(nota);
        recalcularNotaFinal(alunoId, disciplinaId);

        return salva;
    }

    public void recalcularNotaFinal(Long alunoId, Long disciplinaId) {
        List<Nota> notas = notaRepository.findByAlunoIdAndDisciplinaId(alunoId, disciplinaId);

        if (notas.isEmpty()) return;

        double media = notas.stream()
                .mapToDouble(Nota::getValor)
                .average()
                .orElse(0.0);

        // Atualiza a nota final em todos os registros da disciplina para esse aluno
        notas.forEach(n -> n.setNotaFinal(Math.round(media * 10.0) / 10.0));
        notaRepository.saveAll(notas);
    }

    public List<Nota> buscarPorAluno(Long alunoId) {
        return notaRepository.findByAlunoId(alunoId);
    }

    public List<Nota> buscarPorAlunoEDisciplina(Long alunoId, Long disciplinaId) {
        return notaRepository.findByAlunoIdAndDisciplinaId(alunoId, disciplinaId);
    }
}
