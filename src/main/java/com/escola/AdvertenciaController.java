package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advertencias")
@CrossOrigin
public class AdvertenciaController {

    @Autowired
    private AdvertenciaRepository advertenciaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @GetMapping("/aluno/{alunoId}")
    public List<Advertencia> porAluno(@PathVariable Long alunoId) {
        return advertenciaRepository.findByAlunoIdOrderByDataDesc(alunoId);
    }

    @PostMapping
    public Advertencia registrar(@RequestBody Map<String, Object> body) {
        Long alunoId     = Long.valueOf(body.get("alunoId").toString());
        String descricao = body.get("descricao").toString();
        String gravidade = body.get("gravidade").toString();
        String dataStr   = body.get("data").toString();

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        Advertencia adv = new Advertencia();
        adv.setAluno(aluno);
        adv.setDescricao(descricao);
        adv.setData(LocalDate.parse(dataStr));
        adv.setGravidade(GravidadeAdvertencia.valueOf(gravidade.toUpperCase()));

        if (body.containsKey("professorId")) {
            Long professorId = Long.valueOf(body.get("professorId").toString());
            professorRepository.findById(professorId).ifPresent(adv::setProfessor);
        }

        return advertenciaRepository.save(adv);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        advertenciaRepository.deleteById(id);
    }
}
