package com.escola;

import jakarta.persistence.*;

@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    private Integer bimestre; // 1, 2, 3, 4

    @Enumerated(EnumType.STRING)
    private TipoAvaliacao tipo; // PROVA, TRABALHO, PARTICIPACAO

    private Double valor;

    private String descricao; // Ex: "Prova bimestral", "Trabalho em grupo"

    // GETTERS E SETTERS

    public Long getId() { return id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }

    public Integer getBimestre() { return bimestre; }
    public void setBimestre(Integer bimestre) { this.bimestre = bimestre; }

    public TipoAvaliacao getTipo() { return tipo; }
    public void setTipo(TipoAvaliacao tipo) { this.tipo = tipo; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
