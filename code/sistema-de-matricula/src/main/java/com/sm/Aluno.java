package com.sm;

import com.sm.services.MatriculaService;
import com.sm.services.MatriculaValidationService;
import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {
  private List<Matricula> matriculas;
  private Curso curso;
  private int maxObrigatorias = 4;
  private int maxOptativas = 2;

  public Aluno(
      List<Matricula> matriculas,
      Curso curso,
      int maxObrigatorias,
      int maxOptativas,
      String email,
      String senha) {
    super(email, senha);
    this.matriculas = matriculas != null ? matriculas : new ArrayList<>();
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

  public boolean matricular(String nomeDisciplina) {
    return MatriculaService.matricularAluno(this, nomeDisciplina);
  }

  public boolean cancelarMatricula(String nomeDisciplina) {
    return MatriculaService.cancelarMatricula(this, nomeDisciplina);
  }

  public void listarDisciplinasDisponiveis() {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
    System.out.println("\n=== DISCIPLINAS DISPONÍVEIS ===");

    boolean temDisponiveis = false;
    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getStatus() == Status.ATIVA) {
        int alunosMatriculados = MatriculaValidationService.contarAlunosMatriculados(
            disciplina.getNome(), matriculas);
        
        if (alunosMatriculados < Disciplina.MAX_ALUNOS) {
          System.out.println("Nome: " + disciplina.getNome());
          System.out.println("Carga Horária: " + disciplina.getCargaHoraria());
          System.out.println("Tipo: " + (disciplina.isObrigatoria() ? "Obrigatória" : "Optativa"));
          System.out.println("Vagas disponíveis: " + (Disciplina.MAX_ALUNOS - alunosMatriculados));
          if (disciplina.getProfessor() != null) {
            System.out.println("Professor: " + disciplina.getProfessor().getEmail());
          } else {
            System.out.println("Professor: (não vinculado)");
          }
          System.out.println("------------------------");
          temDisponiveis = true;
        }
      }
    }
    
    if (!temDisponiveis) {
      System.out.println("Não há disciplinas disponíveis no momento.");
    }
  }

  public void visualizarMatriculas() {
    List<Matricula> todasMatriculas = SistemaArquivos.carregarMatriculas();
    System.out.println("\n=== SUAS MATRÍCULAS ===");

    boolean temMatricula = false;
    for (Matricula matricula : todasMatriculas) {
      if (matricula.getAluno().getEmail().equalsIgnoreCase(this.getEmail())) {
        System.out.println("Disciplina: " + matricula.getDisciplina().getNome());
        System.out.println("Data da Matrícula: " + matricula.getDataMatricula());
        System.out.println("Tipo: " + (matricula.getDisciplina().isObrigatoria() ? "Obrigatória" : "Optativa"));
        System.out.println("------------------------");
        temMatricula = true;
      }
    }

    if (!temMatricula) {
      System.out.println("Você não possui matrículas ativas.");
    }
  }
}
