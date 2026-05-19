package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FrequenciaService {

    @Autowired
    private FrequenciaRepository frequenciaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public Frequencia registrar(Long alunoId, Long disciplinaId,
                                LocalDate data, Boolean presente, String observacao) {

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Disciplina não encontrada"));

        Frequencia frequencia = new Frequencia();
        frequencia.setAluno(aluno);
        frequencia.setDisciplina(disciplina);
        frequencia.setData(data);
        frequencia.setPresente(presente);
        frequencia.setObservacao(observacao);

        return frequenciaRepository.save(frequencia);
    }

    public Map<String, Object> resumo(Long alunoId, Long disciplinaId) {
        Object[] resultado = frequenciaRepository.resumoFrequencia(alunoId, disciplinaId);

        long total     = resultado[0] != null ? ((Number) resultado[0]).longValue() : 0L;
        long presencas = resultado[1] != null ? ((Number) resultado[1]).longValue() : 0L;
        long faltas    = total - presencas;
        double percentual = total > 0 ? Math.round((presencas * 100.0 / total) * 10.0) / 10.0 : 0.0;

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("total", total);
        resumo.put("presencas", presencas);
        resumo.put("faltas", faltas);
        resumo.put("percentualPresenca", percentual);
        resumo.put("situacao", percentual >= 75 ? "APROVADO" : "REPROVADO POR FALTA");

        return resumo;
    }

    public List<Frequencia> buscarPorAluno(Long alunoId) {
        return frequenciaRepository.findByAlunoId(alunoId);
    }

    public List<Frequencia> buscarPorAlunoEDisciplina(Long alunoId, Long disciplinaId) {
        return frequenciaRepository.findByAlunoIdAndDisciplinaId(alunoId, disciplinaId);
    }

    public List<Frequencia> buscarPorDisciplinaEData(Long disciplinaId, LocalDate data) {
        return frequenciaRepository.findByDisciplinaIdAndData(disciplinaId, data);
    }
}
