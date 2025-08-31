package com.sm;

import java.util.List;

public class Disciplina {
  public static final int MAX_ALUNOS = 60;
  public static final int MIN_ALUNOS = 3;
  private double cargaHoraria;
  private String nome;
  private boolean obrigatoria;
  private List<Aluno> alunos;
  private Status status;
  private Professor professor;

  public Disciplina(
      double cargaHoraria, String nome, boolean obrigatoria, List<Aluno> alunos, Status status) {
    this.cargaHoraria = cargaHoraria;
    this.nome = nome;
    this.obrigatoria = obrigatoria;
    this.alunos = alunos;
    this.status = status;
    this.professor = null;
  }

  public Professor getProfessor() {
    return professor;
  }

  public void setProfessor(Professor professor) {
    this.professor = professor;
  }

  public static int getMaxAlunos() {
    return MAX_ALUNOS;
  }

  public static int getMinAlunos() {
    return MIN_ALUNOS;
  }

  public double getCargaHoraria() {
    return cargaHoraria;
  }

  public void setCargaHoraria(double cargaHoraria) {
    if (cargaHoraria <= 0) {
      throw new IllegalArgumentException("Carga horária deve ser maior que zero.");
    }
    this.cargaHoraria = cargaHoraria;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public boolean isObrigatoria() {
    return obrigatoria;
  }

  public void setObrigatoria(boolean obrigatoria) {
    this.obrigatoria = obrigatoria;
  }

  public List<Aluno> getAlunos() {
    return alunos;
  }

  public void setAlunos(List<Aluno> alunos) {
    this.alunos = alunos;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
