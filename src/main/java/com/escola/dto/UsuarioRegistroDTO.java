package com.escola.dto;

import com.escola.TipoUsuario;
import java.util.List;

public class UsuarioRegistroDTO {
    private String nome;
    private String usuario;
    private String senha;
    private TipoUsuario tipo;

    // Professor
    private String materia;
    private String registro;

    // Aluno
    private String matricula;

    // Responsável
    private String rg;
    private String telefone;
    private List<String> matriculasAlunos;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }
    public String getRegistro() { return registro; }
    public void setRegistro(String registro) { this.registro = registro; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public List<String> getMatriculasAlunos() { return matriculasAlunos; }
    public void setMatriculasAlunos(List<String> matriculasAlunos) { this.matriculasAlunos = matriculasAlunos; }
}