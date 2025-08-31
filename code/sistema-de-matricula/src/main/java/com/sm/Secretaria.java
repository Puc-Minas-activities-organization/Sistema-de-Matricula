package com.sm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Secretaria extends Usuario {
  private static boolean periodoMatriculaAtivo = false;

  public Secretaria(String email, String senha) {
    super(email, senha);
  }

  // Método para gerar currículo de um curso
  public void gerarCurriculo(String nomeCurso) {
    System.out.println("\n=== CURRÍCULO DO CURSO: " + nomeCurso.toUpperCase() + " ===");

    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    double totalCargaHoraria = 0;
    int totalObrigatorias = 0;
    int totalOptativas = 0;

    System.out.println("\nDisciplinas Obrigatórias:");
    for (Disciplina disciplina : disciplinas) {
      if (disciplina.isObrigatoria()) {
        System.out.println(
            "- " + disciplina.getNome() + " (" + disciplina.getCargaHoraria() + "h)");
        totalCargaHoraria += disciplina.getCargaHoraria();
        totalObrigatorias++;
      }
    }

    System.out.println("\nDisciplinas Optativas:");
    for (Disciplina disciplina : disciplinas) {
      if (!disciplina.isObrigatoria()) {
        System.out.println(
            "- " + disciplina.getNome() + " (" + disciplina.getCargaHoraria() + "h)");
        totalOptativas++;
      }
    }

    System.out.println("\n=== RESUMO ===");
    System.out.println("Total de disciplinas obrigatórias: " + totalObrigatorias);
    System.out.println("Total de disciplinas optativas: " + totalOptativas);
    System.out.println("Carga horária total das obrigatórias: " + totalCargaHoraria + "h");
  }

  // Iniciar período de matrícula
  public void iniciarPeriodoMatricula() {
    periodoMatriculaAtivo = true;
    System.out.println(
        "Período de matrícula iniciado. Os alunos podem se inscrever ou cancelar matrículas.");
  }

  // Finalizar período de matrícula
  public void finalizarPeriodoMatricula() {
    periodoMatriculaAtivo = false;
    System.out.println(
        "Período de matrícula encerrado. Verificando disciplinas com menos de 3 alunos...");
    cancelarDisciplinasComPoucosAlunos();
  }

  // Verifica se o período está ativo
  public static boolean isPeriodoMatriculaAtivo() {
    return periodoMatriculaAtivo;
  }

  // Cancela disciplinas com menos de 3 alunos
  private void cancelarDisciplinasComPoucosAlunos() {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
    boolean houveCancelamento = false;
    for (Disciplina disciplina : disciplinas) {
      int contador = 0;
      for (Matricula m : matriculas) {
        if (m.getDisciplina().getNome().equalsIgnoreCase(disciplina.getNome())) {
          contador++;
        }
      }
      if (contador < Disciplina.MIN_ALUNOS && disciplina.getStatus() == Status.ATIVA) {
        disciplina.setStatus(Status.CANCELADA);
        System.out.println(
            "Disciplina '" + disciplina.getNome() + "' cancelada por falta de alunos.");
        houveCancelamento = true;
      }
    }
    SistemaArquivos.reescreverDisciplinas(disciplinas);
    if (!houveCancelamento) {
      System.out.println("Nenhuma disciplina precisou ser cancelada.");
    }
  }

  // Método para cadastrar nova disciplina
  public boolean cadastrarDisciplina(String nome, double cargaHoraria, boolean obrigatoria) {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();

    // Verificar se a disciplina já existe
    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getNome().equalsIgnoreCase(nome)) {
        System.out.println("Disciplina já cadastrada.");
        return false;
      }
    }

    // Criar nova disciplina
    Disciplina novaDisciplina =
        new Disciplina(cargaHoraria, nome, obrigatoria, new ArrayList<>(), Status.ATIVA);
    SistemaArquivos.salvarDisciplina(novaDisciplina);

    System.out.println("Disciplina cadastrada com sucesso!");
    return true;
  }

  // Método para remover disciplina
  public boolean removerDisciplina(String nome) {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    boolean encontrou = false;

    // Verificar se há alunos matriculados
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
    for (Matricula matricula : matriculas) {
      if (matricula.getDisciplina().getNome().equalsIgnoreCase(nome)) {
        System.out.println("Não é possível remover disciplina com alunos matriculados.");
        return false;
      }
    }

    // Remover disciplina
    for (int i = 0; i < disciplinas.size(); i++) {
      if (disciplinas.get(i).getNome().equalsIgnoreCase(nome)) {
        disciplinas.remove(i);
        encontrou = true;
        break;
      }
    }

    if (encontrou) {
      SistemaArquivos.reescreverDisciplinas(disciplinas);
      System.out.println("Disciplina removida com sucesso!");
      return true;
    } else {
      System.out.println("Disciplina não encontrada.");
      return false;
    }
  }

  // Método para matricular aluno em disciplina
  public boolean matricularAluno(String emailAluno, String nomeDisciplina) {
    List<Aluno> alunos = SistemaArquivos.carregarAlunos();
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();

    // Buscar aluno
    Aluno aluno = null;
    for (Aluno a : alunos) {
      if (a.getEmail().equalsIgnoreCase(emailAluno)) {
        aluno = a;
        break;
      }
    }

    if (aluno == null) {
      System.out.println("Aluno não encontrado.");
      return false;
    }

    // Buscar disciplina
    Disciplina disciplina = null;
    for (Disciplina d : disciplinas) {
      if (d.getNome().equalsIgnoreCase(nomeDisciplina)) {
        disciplina = d;
        break;
      }
    }

    if (disciplina == null) {
      System.out.println("Disciplina não encontrada.");
      return false;
    }

    if (disciplina.getStatus() != Status.ATIVA) {
      System.out.println("Disciplina não está ativa.");
      return false;
    }

    // Verificar se já está matriculado
    for (Matricula m : matriculas) {
      if (m.getAluno().getEmail().equalsIgnoreCase(emailAluno)
          && m.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina)) {
        System.out.println("Aluno já está matriculado nesta disciplina.");
        return false;
      }
    }

    // Contar alunos na disciplina
    int contador = 0;
    for (Matricula m : matriculas) {
      if (m.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina)) {
        contador++;
      }
    }

    if (contador >= Disciplina.MAX_ALUNOS) {
      System.out.println("Disciplina lotada.");
      return false;
    }

    // Realizar matrícula
    Matricula matricula = new Matricula(disciplina, aluno, LocalDate.now());
    SistemaArquivos.salvarMatricula(matricula);

    System.out.println("Aluno matriculado com sucesso!");
    return true;
  }

  // Método para listar todas as disciplinas
  public void listarTodasDisciplinas() {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
    System.out.println("\n=== TODAS AS DISCIPLINAS ===");

    if (disciplinas.isEmpty()) {
      System.out.println("Nenhuma disciplina cadastrada.");
      return;
    }

    for (Disciplina disciplina : disciplinas) {
      int alunosCount = 0;
      for (Matricula matricula : matriculas) {
        if (matricula.getDisciplina().getNome().equalsIgnoreCase(disciplina.getNome())) {
          alunosCount++;
        }
      }
      System.out.println("Nome: " + disciplina.getNome());
      System.out.println("Carga Horária: " + disciplina.getCargaHoraria());
      System.out.println("Tipo: " + (disciplina.isObrigatoria() ? "Obrigatória" : "Optativa"));
      System.out.println("Status: " + disciplina.getStatus());
      System.out.println("Alunos: " + alunosCount);
      System.out.println("------------------------");
    }
  }

  // Método para cancelar disciplina (se tiver menos que o mínimo de alunos)
  public boolean cancelarDisciplina(String nomeDisciplina) {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();

    // Contar matrículas
    int contador = 0;
    for (Matricula matricula : matriculas) {
      if (matricula.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina)) {
        contador++;
      }
    }

    if (contador >= Disciplina.MIN_ALUNOS) {
      System.out.println("Disciplina não pode ser cancelada - possui número suficiente de alunos.");
      return false;
    }

    // Alterar status da disciplina
    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getNome().equalsIgnoreCase(nomeDisciplina)) {
        disciplina.setStatus(Status.CANCELADA);
        SistemaArquivos.reescreverDisciplinas(disciplinas);
        System.out.println("Disciplina cancelada com sucesso!");
        return true;
      }
    }

    System.out.println("Disciplina não encontrada.");
    return false;
  }

  // Método para gerar relatório de matrículas
  public void gerarRelatorioMatriculas() {
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
    System.out.println("\n=== RELATÓRIO DE MATRÍCULAS ===");

    if (matriculas.isEmpty()) {
      System.out.println("Nenhuma matrícula encontrada.");
      return;
    }

    for (Matricula matricula : matriculas) {
      System.out.println("Aluno: " + matricula.getAluno().getEmail());
      System.out.println("Disciplina: " + matricula.getDisciplina().getNome());
      System.out.println("Data: " + matricula.getDataMatricula());
      System.out.println("------------------------");
    }
  }
}
