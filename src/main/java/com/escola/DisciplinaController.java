package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/disciplinas")
@CrossOrigin
public class DisciplinaController {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @GetMapping
    public List<Disciplina> listar() {
        return disciplinaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Disciplina buscarPorId(@PathVariable Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Disciplina não encontrada"));
    }

    @PostMapping
    public Disciplina cadastrar(@RequestBody Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    @PutMapping("/{id}")
    public Disciplina atualizar(@PathVariable Long id, @RequestBody Disciplina dados) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Disciplina não encontrada"));

        disciplina.setNome(dados.getNome());
        disciplina.setCodigo(dados.getCodigo());
        disciplina.setProfessores(dados.getProfessores());

        return disciplinaRepository.save(disciplina);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!disciplinaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada");
        }
        disciplinaRepository.deleteById(id);
    }

    // Adicionar professor a uma disciplina: POST /api/disciplinas/{id}/professores/{professorId}
    @PostMapping("/{id}/professores/{professorId}")
    public Disciplina adicionarProfessor(@PathVariable Long id, @PathVariable Long professorId) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Disciplina não encontrada"));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Professor não encontrado"));

        if (!disciplina.getProfessores().contains(professor)) {
            disciplina.getProfessores().add(professor);
        }

        return disciplinaRepository.save(disciplina);
    }
}
