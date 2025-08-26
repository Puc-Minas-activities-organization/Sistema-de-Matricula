package com.sm;

import java.util.List;

public class Curso {

    private List<Disciplina> disciplinas;
    private String nome;
    private int creditos;

    public Curso(List<Disciplina> disciplinas, String nome, int creditos) {
        this.disciplinas = disciplinas;
        this.nome = nome;
        this.creditos = creditos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public int getCreditos() {
        return creditos;
    }
    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }


}
