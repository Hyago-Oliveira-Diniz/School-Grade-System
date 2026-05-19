package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/professores")
@CrossOrigin
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @GetMapping
    public List<Professor> listar() {
        return professorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Professor buscarPorId(@PathVariable Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Professor não encontrado"));
    }

    @PostMapping
    public Professor cadastrar(@RequestBody Professor professor) {
        return professorRepository.save(professor);
    }

    @PutMapping("/{id}")
    public Professor atualizar(@PathVariable Long id, @RequestBody Professor dados) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Professor não encontrado"));

        professor.setNome(dados.getNome());
        professor.setMateria(dados.getMateria());
        professor.setRegistro(dados.getRegistro());
        professor.setUsuario(dados.getUsuario());

        return professorRepository.save(professor);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!professorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado");
        }
        professorRepository.deleteById(id);
    }
}
