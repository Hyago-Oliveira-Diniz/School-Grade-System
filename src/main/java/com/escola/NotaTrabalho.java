package com.escola;

import jakarta.persistence.*;

/**
 * Armazena a nota que cada aluno tirou em um trabalho.
 * A nota do aluno deve estar entre notaMinima e notaMaxima do Trabalho.
 */
@Entity
public class NotaTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trabalho_id", nullable = false)
    private Trabalho trabalho;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    private Double notaAluno;

    // GETTERS E SETTERS

    public Long getId() { return id; }

    public Trabalho getTrabalho() { return trabalho; }
    public void setTrabalho(Trabalho trabalho) { this.trabalho = trabalho; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Double getNotaAluno() { return notaAluno; }
    public void setNotaAluno(Double notaAluno) { this.notaAluno = notaAluno; }
}
