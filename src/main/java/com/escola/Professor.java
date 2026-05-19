package com.escola;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Professor extends Pessoa {

    private String materia;

    @Column(name = "registro", unique = true)
    private String registro;

    @ManyToMany(mappedBy = "professores")
    @JsonIgnoreProperties("professores")
    private List<Disciplina> disciplinas;

    // GETTERS E SETTERS

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }
}
