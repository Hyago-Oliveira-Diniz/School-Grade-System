package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trabalhos")
@CrossOrigin
public class TrabalhoController {

    @Autowired
    private TrabalhoRepository trabalhoRepository;

    @Autowired
    private NotaTrabalhoRepository notaTrabalhoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    // ─────────────────────────────────────────────────
    // CRIAR TRABALHO (Professor/Admin)
    // POST /api/trabalhos
    // Body: { "titulo": "Redação", "descricao": "...", "disciplinaId": 1,
    //         "dataEntrega": "2025-06-20", "notaMinima": 0.0, "notaMaxima": 10.0 }
    // ─────────────────────────────────────────────────
    @PostMapping
    public Trabalho criar(@RequestBody Map<String, Object> body) {
        Long disciplinaId = Long.valueOf(body.get("disciplinaId").toString());

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Disciplina não encontrada"));

        Double notaMinima = body.containsKey("notaMinima")
                ? Double.valueOf(body.get("notaMinima").toString()) : 0.0;
        Double notaMaxima = body.containsKey("notaMaxima")
                ? Double.valueOf(body.get("notaMaxima").toString()) : 10.0;

        if (notaMinima < 0 || notaMaxima > 10 || notaMinima >= notaMaxima) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nota mínima e máxima inválidas (devem estar entre 0 e 10, mínima < máxima)");
        }

        Trabalho t = new Trabalho();
        t.setTitulo(body.get("titulo").toString());
        t.setDescricao(body.containsKey("descricao") ? body.get("descricao").toString() : "");
        t.setDisciplina(disciplina);
        t.setDataEntrega(LocalDate.parse(body.get("dataEntrega").toString()));
        t.setNotaMinima(notaMinima);
        t.setNotaMaxima(notaMaxima);

        return trabalhoRepository.save(t);
    }

    // ─────────────────────────────────────────────────
    // LISTAR TODOS OS TRABALHOS
    // GET /api/trabalhos
    // ─────────────────────────────────────────────────
    @GetMapping
    public List<Trabalho> listar() {
        return trabalhoRepository.findAllByOrderByDataEntregaAsc();
    }

    // ─────────────────────────────────────────────────
    // LISTAR TRABALHOS DE UMA DISCIPLINA
    // GET /api/trabalhos/disciplina/{disciplinaId}
    // ─────────────────────────────────────────────────
    @GetMapping("/disciplina/{disciplinaId}")
    public List<Trabalho> porDisciplina(@PathVariable Long disciplinaId) {
        return trabalhoRepository.findByDisciplinaId(disciplinaId);
    }

    // ─────────────────────────────────────────────────
    // LANÇAR NOTA DE UM ALUNO EM UM TRABALHO (Professor/Admin)
    // POST /api/trabalhos/nota
    // Body: { "trabalhoId": 1, "alunoId": 2, "notaAluno": 8.5 }
    // ─────────────────────────────────────────────────
    @PostMapping("/nota")
    public NotaTrabalho lancarNota(@RequestBody Map<String, Object> body) {
        Long trabalhoId = Long.valueOf(body.get("trabalhoId").toString());
        Long alunoId    = Long.valueOf(body.get("alunoId").toString());
        Double nota     = Double.valueOf(body.get("notaAluno").toString());

        Trabalho trabalho = trabalhoRepository.findById(trabalhoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Trabalho não encontrado"));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        if (nota < trabalho.getNotaMinima() || nota > trabalho.getNotaMaxima()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Nota deve estar entre %.1f e %.1f",
                            trabalho.getNotaMinima(), trabalho.getNotaMaxima()));
        }

        // Atualiza se já existe, senão cria
        NotaTrabalho nt = notaTrabalhoRepository
                .findByTrabalhoIdAndAlunoId(trabalhoId, alunoId)
                .orElse(new NotaTrabalho());

        nt.setTrabalho(trabalho);
        nt.setAluno(aluno);
        nt.setNotaAluno(nota);

        return notaTrabalhoRepository.save(nt);
    }

    // ─────────────────────────────────────────────────
    // VER TRABALHOS DO ALUNO (com notas mín, máx e nota dele)
    // GET /api/trabalhos/aluno/{alunoId}
    // Retorna lista de { trabalho, notaMinima, notaMaxima, notaAluno }
    // ─────────────────────────────────────────────────
    @GetMapping("/aluno/{alunoId}")
    public List<Map<String, Object>> porAluno(@PathVariable Long alunoId) {
        List<NotaTrabalho> notas = notaTrabalhoRepository.findByAlunoId(alunoId);

        return notas.stream().map(nt -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", nt.getId());
            item.put("titulo", nt.getTrabalho().getTitulo());
            item.put("descricao", nt.getTrabalho().getDescricao());
            item.put("disciplina", nt.getTrabalho().getDisciplina().getNome());
            item.put("dataEntrega", nt.getTrabalho().getDataEntrega().toString());
            item.put("notaMinima", nt.getTrabalho().getNotaMinima());
            item.put("notaMaxima", nt.getTrabalho().getNotaMaxima());
            item.put("notaAluno", nt.getNotaAluno());
            return item;
        }).toList();
    }

    // ─────────────────────────────────────────────────
    // VER NOTAS DE TODOS OS ALUNOS NUM TRABALHO (Professor)
    // GET /api/trabalhos/{trabalhoId}/notas
    // ─────────────────────────────────────────────────
    @GetMapping("/{trabalhoId}/notas")
    public List<Map<String, Object>> notasDoTrabalho(@PathVariable Long trabalhoId) {
        List<NotaTrabalho> notas = notaTrabalhoRepository.findByTrabalhoId(trabalhoId);

        return notas.stream().map(nt -> {
            Map<String, Object> item = new HashMap<>();
            item.put("alunoId", nt.getAluno().getId());
            item.put("alunoNome", nt.getAluno().getNome());
            item.put("notaAluno", nt.getNotaAluno());
            item.put("notaMinima", nt.getTrabalho().getNotaMinima());
            item.put("notaMaxima", nt.getTrabalho().getNotaMaxima());
            return item;
        }).toList();
    }

    // ─────────────────────────────────────────────────
    // DELETAR TRABALHO (Admin)
    // DELETE /api/trabalhos/{id}
    // ─────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!trabalhoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabalho não encontrado");
        }
        trabalhoRepository.deleteById(id);
    }
}
