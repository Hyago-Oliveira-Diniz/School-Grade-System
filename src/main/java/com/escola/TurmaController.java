package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin
public class TurmaController {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @GetMapping
    public List<Turma> listar() {
        return turmaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Turma buscarPorId(@PathVariable Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Turma não encontrada"));
    }

    @PostMapping
    public Turma cadastrar(@RequestBody Turma turma) {
        return turmaRepository.save(turma);
    }

    @PutMapping("/{id}")
    public Turma atualizar(@PathVariable Long id, @RequestBody Turma dados) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Turma não encontrada"));

        turma.setNome(dados.getNome());
        turma.setAno(dados.getAno());
        turma.setPeriodo(dados.getPeriodo());

        return turmaRepository.save(turma);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!turmaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada");
        }
        turmaRepository.deleteById(id);
    }

    // Adicionar aluno à turma: POST /api/turmas/{id}/alunos/{alunoId}
    @PostMapping("/{id}/alunos/{alunoId}")
    public Turma adicionarAluno(@PathVariable Long id, @PathVariable Long alunoId) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Turma não encontrada"));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        if (!turma.getAlunos().contains(aluno)) {
            turma.getAlunos().add(aluno);
        }

        return turmaRepository.save(turma);
    }

    // Remover aluno da turma: DELETE /api/turmas/{id}/alunos/{alunoId}
    @DeleteMapping("/{id}/alunos/{alunoId}")
    public Turma removerAluno(@PathVariable Long id, @PathVariable Long alunoId) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Turma não encontrada"));

        turma.getAlunos().removeIf(a -> a.getId().equals(alunoId));
        return turmaRepository.save(turma);
    }

    // Adicionar disciplina à turma: POST /api/turmas/{id}/disciplinas/{disciplinaId}
    @PostMapping("/{id}/disciplinas/{disciplinaId}")
    public Turma adicionarDisciplina(@PathVariable Long id, @PathVariable Long disciplinaId) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Turma não encontrada"));

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Disciplina não encontrada"));

        if (!turma.getDisciplinas().contains(disciplina)) {
            turma.getDisciplinas().add(disciplina);
        }

        return turmaRepository.save(turma);
    }

    // Remover disciplina da turma: DELETE /api/turmas/{id}/disciplinas/{disciplinaId}
    @DeleteMapping("/{id}/disciplinas/{disciplinaId}")
    public Turma removerDisciplina(@PathVariable Long id, @PathVariable Long disciplinaId) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Turma não encontrada"));

        turma.getDisciplinas().removeIf(d -> d.getId().equals(disciplinaId));
        return turmaRepository.save(turma);
    }
}
