package com.escola;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Advertencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    private String descricao;

    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private GravidadeAdvertencia gravidade; // LEVE, MEDIA, GRAVE

    // GETTERS E SETTERS

    public Long getId() { return id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public GravidadeAdvertencia getGravidade() { return gravidade; }
    public void setGravidade(GravidadeAdvertencia gravidade) { this.gravidade = gravidade; }
}
