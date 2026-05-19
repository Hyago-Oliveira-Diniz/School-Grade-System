package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/frequencias")
@CrossOrigin
public class FrequenciaController {

    @Autowired
    private FrequenciaService frequenciaService;

    // Registrar presença: POST /api/frequencias
    // Body: { "alunoId": 1, "disciplinaId": 2, "data": "2025-04-10", "presente": true, "observacao": "" }
    @PostMapping
    public Frequencia registrar(@RequestBody Map<String, Object> body) {
        Long alunoId      = Long.valueOf(body.get("alunoId").toString());
        Long disciplinaId = Long.valueOf(body.get("disciplinaId").toString());
        LocalDate data    = LocalDate.parse(body.get("data").toString());
        Boolean presente  = Boolean.valueOf(body.get("presente").toString());
        String observacao = body.containsKey("observacao") ? body.get("observacao").toString() : null;

        return frequenciaService.registrar(alunoId, disciplinaId, data, presente, observacao);
    }

    // Frequências de um aluno: GET /api/frequencias/aluno/{alunoId}
    @GetMapping("/aluno/{alunoId}")
    public List<Frequencia> porAluno(@PathVariable Long alunoId) {
        return frequenciaService.buscarPorAluno(alunoId);
    }

    // Frequências de um aluno em uma disciplina: GET /api/frequencias/aluno/{alunoId}/disciplina/{disciplinaId}
    @GetMapping("/aluno/{alunoId}/disciplina/{disciplinaId}")
    public List<Frequencia> porAlunoEDisciplina(@PathVariable Long alunoId,
                                                @PathVariable Long disciplinaId) {
        return frequenciaService.buscarPorAlunoEDisciplina(alunoId, disciplinaId);
    }

    // Resumo de frequência (percentual + situação): GET /api/frequencias/resumo/{alunoId}/{disciplinaId}
    @GetMapping("/resumo/{alunoId}/{disciplinaId}")
    public Map<String, Object> resumo(@PathVariable Long alunoId,
                                      @PathVariable Long disciplinaId) {
        return frequenciaService.resumo(alunoId, disciplinaId);
    }

    // Chamada do dia por disciplina: GET /api/frequencias/disciplina/{disciplinaId}/data/{data}
    @GetMapping("/disciplina/{disciplinaId}/data/{data}")
    public List<Frequencia> chamadaDoDia(@PathVariable Long disciplinaId,
                                         @PathVariable String data) {
        return frequenciaService.buscarPorDisciplinaEData(disciplinaId, LocalDate.parse(data));
    }
}
