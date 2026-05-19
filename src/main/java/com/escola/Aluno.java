package com.escola;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Aluno extends Pessoa {

    private double nota;

    @Column(name = "matricula", unique = true)
    private String matricula;

    @ManyToMany(mappedBy = "alunos")
    @JsonIgnoreProperties("alunos")
    private List<Turma> turmas;

    // GETTERS E SETTERS

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public List<Turma> getTurmas() {
        return turmas;
    }

    public void setTurmas(List<Turma> turmas) {
        this.turmas = turmas;
    }
}
