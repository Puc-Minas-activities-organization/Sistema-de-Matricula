package com.sm;

import java.time.LocalDate;

public class Matricula {
    private Disciplina disciplina;
    private Aluno aluno;
    private LocalDate dataMatricula;
    
    public Matricula(Disciplina disciplina, Aluno aluno, LocalDate dataMatricula) {
        this.disciplina = disciplina;
        this.aluno = aluno;
        this.dataMatricula = dataMatricula;
    }
    
    public Disciplina getDisciplina() {
        return disciplina;
    }
    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }
    public Aluno getAluno() {
        return aluno;
    }
    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
    public LocalDate getDataMatricula() {
        return dataMatricula;
    }
    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public static void notificarSistemaCobranca(Aluno aluno) {
        System.out.println("Notificando sistema de cobrança para o aluno: " + aluno.getEmail());
    }
}
