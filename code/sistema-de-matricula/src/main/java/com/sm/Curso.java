package com.sm;

import java.util.List;

public class Curso {
    private List<String> disciplinasObrigatorias;
    private List<String> disciplinasOptativas;
    private String nome;
    private int creditos;

    public Curso(List<String> disciplinasObrigatorias, List<String> disciplinasOptativas, String nome, int creditos) {
        this.disciplinasObrigatorias = disciplinasObrigatorias;
        this.disciplinasOptativas = disciplinasOptativas;
        this.nome = nome;
        this.creditos = creditos;
    }

    public Curso(List<String> disciplinasObrigatorias, String nome, int creditos) {
        this.disciplinasObrigatorias = disciplinasObrigatorias;
        this.disciplinasOptativas = new java.util.ArrayList<>();
        this.nome = nome;
        this.creditos = creditos;
    }

    public List<String> getDisciplinasObrigatorias() {
        return disciplinasObrigatorias;
    }
    
    public void setDisciplinasObrigatorias(List<String> disciplinasObrigatorias) {
        this.disciplinasObrigatorias = disciplinasObrigatorias;
    }
    
    public List<String> getDisciplinasOptativas() {
        return disciplinasOptativas;
    }
    
    public void setDisciplinasOptativas(List<String> disciplinasOptativas) {
        this.disciplinasOptativas = disciplinasOptativas;
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
