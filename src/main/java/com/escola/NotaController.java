package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notas")
@CrossOrigin
public class NotaController {

    @Autowired
    private NotaService notaService;

    // Lançar ou atualizar nota: POST /api/notas
    // Body: { "alunoId": 1, "disciplinaId": 2, "bimestre": 1, "valor": 8.5 }
    @PostMapping
    public Nota lancar(@RequestBody Map<String, Object> body) {
        Long alunoId      = Long.valueOf(body.get("alunoId").toString());
        Long disciplinaId = Long.valueOf(body.get("disciplinaId").toString());
        Integer bimestre  = Integer.valueOf(body.get("bimestre").toString());
        Double valor      = Double.valueOf(body.get("valor").toString());

        return notaService.lancarNota(alunoId, disciplinaId, bimestre, valor);
    }

    // Todas as notas de um aluno: GET /api/notas/aluno/{alunoId}
    @GetMapping("/aluno/{alunoId}")
    public List<Nota> porAluno(@PathVariable Long alunoId) {
        return notaService.buscarPorAluno(alunoId);
    }

    // Notas de um aluno em uma disciplina: GET /api/notas/aluno/{alunoId}/disciplina/{disciplinaId}
    @GetMapping("/aluno/{alunoId}/disciplina/{disciplinaId}")
    public List<Nota> porAlunoEDisciplina(@PathVariable Long alunoId,
                                          @PathVariable Long disciplinaId) {
        return notaService.buscarPorAlunoEDisciplina(alunoId, disciplinaId);
    }
}
