package com.sm;

import java.time.LocalDate;
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

  // Método para matricular em uma disciplina
  public boolean matricular(String nomeDisciplina) {
    if (!Secretaria.isPeriodoMatriculaAtivo()) {
      System.out.println("Não é possível se matricular fora do período de matrícula.");
      return false;
    }
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getNome().equalsIgnoreCase(nomeDisciplina)) {
        // Verificar se a disciplina está ativa
        if (disciplina.getStatus() != Status.ATIVA) {
          System.out.println("Disciplina não está ativa.");
          return false;
        }
        // Verificar se há vagas (mín 3, máx 60)
        int numAlunos = disciplina.getAlunos().size();
        if (numAlunos >= Disciplina.MAX_ALUNOS) {
          System.out.println("Disciplina lotada.");
          return false;
        }
        // Verificar se o aluno já está matriculado
        List<Matricula> todasMatriculas = SistemaArquivos.carregarMatriculas();
        for (Matricula m : todasMatriculas) {
          if (m.getAluno().getEmail().equalsIgnoreCase(this.getEmail())
              && m.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina)) {
            System.out.println("Você já está matriculado nesta disciplina.");
            return false;
          }
        }
        // Verificar limite de disciplinas obrigatórias/optativas
        int obrigatoriasAtual = 0;
        int optativasAtual = 0;
        for (Matricula m : todasMatriculas) {
          if (m.getAluno().getEmail().equalsIgnoreCase(this.getEmail())) {
            if (m.getDisciplina().isObrigatoria()) {
              obrigatoriasAtual++;
            } else {
              optativasAtual++;
            }
          }
        }
        if (disciplina.isObrigatoria() && obrigatoriasAtual >= maxObrigatorias) {
          System.out.println("Limite de disciplinas obrigatórias atingido.");
          return false;
        }
        if (!disciplina.isObrigatoria() && optativasAtual >= maxOptativas) {
          System.out.println("Limite de disciplinas optativas atingido.");
          return false;
        }
        // Realizar matrícula
        Matricula matricula = new Matricula(disciplina, this, LocalDate.now());
        SistemaArquivos.salvarMatricula(matricula);
        this.matriculas.add(matricula);
        disciplina.getAlunos().add(this);
        // Atualiza persistência da disciplina com nova contagem de alunos
        List<Disciplina> todasDisciplinas = SistemaArquivos.carregarDisciplinas();
        for (Disciplina d : todasDisciplinas) {
          if (d.getNome().equalsIgnoreCase(disciplina.getNome())) {
            d.setAlunos(disciplina.getAlunos());
          }
        }
        SistemaArquivos.reescreverDisciplinas(todasDisciplinas);
        System.out.println("Matrícula realizada com sucesso!");
        return true;
      }
    }
    System.out.println("Disciplina não encontrada.");
    return false;
  }

  // Método para listar disciplinas disponíveis
  public void listarDisciplinasDisponiveis() {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    System.out.println("\n=== DISCIPLINAS DISPONÍVEIS ===");

    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getStatus() == Status.ATIVA
          && disciplina.getAlunos().size() < Disciplina.MAX_ALUNOS) {
        System.out.println("Nome: " + disciplina.getNome());
        System.out.println("Carga Horária: " + disciplina.getCargaHoraria());
        System.out.println("Tipo: " + (disciplina.isObrigatoria() ? "Obrigatória" : "Optativa"));
        System.out.println(
            "Vagas disponíveis: " + (Disciplina.MAX_ALUNOS - disciplina.getAlunos().size()));
        if (disciplina.getProfessor() != null) {
          System.out.println("Professor: " + disciplina.getProfessor().getEmail());
        } else {
          System.out.println("Professor: (não vinculado)");
        }
        System.out.println("------------------------");
      }
    }
  }

  // Método para visualizar suas matrículas
  public void visualizarMatriculas() {
    List<Matricula> todasMatriculas = SistemaArquivos.carregarMatriculas();
    System.out.println("\n=== SUAS MATRÍCULAS ===");

    boolean temMatricula = false;
    for (Matricula matricula : todasMatriculas) {
      if (matricula.getAluno().getEmail().equalsIgnoreCase(this.getEmail())) {
        System.out.println("Disciplina: " + matricula.getDisciplina().getNome());
        System.out.println("Data da Matrícula: " + matricula.getDataMatricula());
        System.out.println(
            "Tipo: " + (matricula.getDisciplina().isObrigatoria() ? "Obrigatória" : "Optativa"));
        System.out.println("------------------------");
        temMatricula = true;
      }
    }

    if (!temMatricula) {
      System.out.println("Você não possui matrículas ativas.");
    }
  }

  // Método para cancelar matrícula em uma disciplina
  public boolean cancelarMatricula(String nomeDisciplina) {
    if (!Secretaria.isPeriodoMatriculaAtivo()) {
      System.out.println("Não é possível cancelar matrícula fora do período de matrícula.");
      return false;
    }
    List<Matricula> todasMatriculas = SistemaArquivos.carregarMatriculas();
    boolean encontrou = false;
    Matricula matriculaParaRemover = null;
    for (Matricula m : todasMatriculas) {
      if (m.getAluno().getEmail().equalsIgnoreCase(this.getEmail())
          && m.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina)) {
        matriculaParaRemover = m;
        encontrou = true;
        break;
      }
    }
    if (!encontrou) {
      System.out.println("Você não está matriculado nesta disciplina.");
      return false;
    }

    todasMatriculas.remove(matriculaParaRemover);
    SistemaArquivos.reescreverMatriculas(todasMatriculas);
    // Remove aluno da lista de alunos da disciplina
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    for (Disciplina d : disciplinas) {
      if (d.getNome().equalsIgnoreCase(nomeDisciplina)) {
        d.getAlunos().removeIf(a -> a.getEmail().equalsIgnoreCase(this.getEmail()));
      }
    }
    SistemaArquivos.reescreverDisciplinas(disciplinas);
    System.out.println("Matrícula cancelada com sucesso!");
    return true;
  }
}
