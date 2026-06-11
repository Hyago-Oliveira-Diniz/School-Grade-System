package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/trabalhos")
@CrossOrigin
public class TrabalhoController {

    @Autowired
    private TrabalhoRepository trabalhoRepository;

    @GetMapping
    public List<Trabalho> listar() {
        return trabalhoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Trabalho buscar(@PathVariable Long id) {
        return trabalhoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabalho não encontrado"));
    }

    @GetMapping("/turma/{turmaId}")
    public List<Trabalho> porTurma(@PathVariable Long turmaId) {
        return trabalhoRepository.findByTurmaId(turmaId);
    }

    @GetMapping("/disciplina/{disciplinaId}")
    public List<Trabalho> porDisciplina(@PathVariable Long disciplinaId) {
        return trabalhoRepository.findByDisciplinaId(disciplinaId);
    }

    @PostMapping
    public Trabalho criar(@RequestBody Trabalho trabalho) {
        return trabalhoRepository.save(trabalho);
    }

    @PutMapping("/{id}")
    public Trabalho atualizar(@PathVariable Long id, @RequestBody Trabalho dados) {
        Trabalho t = trabalhoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabalho não encontrado"));
        t.setTitulo(dados.getTitulo());
        t.setDescricao(dados.getDescricao());
        t.setPrazo(dados.getPrazo());
        t.setBimestre(dados.getBimestre());
        t.setDisciplina(dados.getDisciplina());
        t.setTurma(dados.getTurma());
        return trabalhoRepository.save(t);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        trabalhoRepository.deleteById(id);
    }
}
