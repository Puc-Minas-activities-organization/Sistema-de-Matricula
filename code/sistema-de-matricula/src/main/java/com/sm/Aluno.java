package com.sm;

import java.util.List;

public class Aluno extends Usuario{
    private List<Matricula> matriculas;
    private Curso curso;
    private int maxObrigatorias = 4;
    private int maxOptativas = 2;
    
    public Aluno(List<Matricula> matriculas, Curso curso, int maxObrigatorias, int maxOptativas, String email, String senha) {
        super(email, senha);
        this.matriculas = matriculas;
        this.curso = curso;
        this.maxObrigatorias = maxObrigatorias;
        this.maxOptativas = maxOptativas;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }
    public Curso getCurso() {
        return curso;
    }
    public void setCurso(Curso curso) {
        this.curso = curso;
    }
    public int getMaxObrigatorias() {
        return maxObrigatorias;
    }
    public void setMaxObrigatorias(int maxObrigatorias) {
        this.maxObrigatorias = maxObrigatorias;
    }
    public int getMaxOptativas() {
        return maxOptativas;
    }
    public void setMaxOptativas(int maxOptativas) {
        this.maxOptativas = maxOptativas;
    }

    
}
