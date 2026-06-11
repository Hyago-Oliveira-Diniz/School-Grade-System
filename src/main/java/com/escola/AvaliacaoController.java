package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin
public class AvaliacaoController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @GetMapping("/aluno/{alunoId}")
    public List<Avaliacao> porAluno(@PathVariable Long alunoId) {
        return avaliacaoRepository.findByAlunoId(alunoId);
    }

    @GetMapping("/aluno/{alunoId}/disciplina/{disciplinaId}")
    public List<Avaliacao> porAlunoEDisciplina(@PathVariable Long alunoId,
                                               @PathVariable Long disciplinaId) {
        return avaliacaoRepository.findByAlunoIdAndDisciplinaId(alunoId, disciplinaId);
    }

    @GetMapping("/aluno/{alunoId}/bimestre/{bimestre}")
    public List<Avaliacao> porAlunoEBimestre(@PathVariable Long alunoId,
                                             @PathVariable Integer bimestre) {
        return avaliacaoRepository.findByAlunoIdAndBimestre(alunoId, bimestre);
    }

    @PostMapping
    public Avaliacao lancar(@RequestBody Map<String, Object> body) {
        Long alunoId      = Long.valueOf(body.get("alunoId").toString());
        Long disciplinaId = Long.valueOf(body.get("disciplinaId").toString());
        Integer bimestre  = Integer.valueOf(body.get("bimestre").toString());
        Double valor      = Double.valueOf(body.get("valor").toString());
        String tipo       = body.get("tipo").toString();
        String descricao  = body.containsKey("descricao") ? body.get("descricao").toString() : "";

        if (valor < 0 || valor > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nota deve ser entre 0 e 10");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada"));

        Avaliacao av = new Avaliacao();
        av.setAluno(aluno);
        av.setDisciplina(disciplina);
        av.setBimestre(bimestre);
        av.setValor(valor);
        av.setTipo(TipoAvaliacao.valueOf(tipo.toUpperCase()));
        av.setDescricao(descricao);

        return avaliacaoRepository.save(av);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        avaliacaoRepository.deleteById(id);
    }
}
