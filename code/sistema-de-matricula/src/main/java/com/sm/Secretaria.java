package com.sm;

import com.sm.services.BuscaService;
import com.sm.services.MatriculaValidationService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Secretaria extends Usuario {
  private static boolean periodoMatriculaAtivo = false;

  public Secretaria(String email, String senha) {
    super(email, senha);
  }

  // 
  public void gerarCurriculo(String nomeCurso) {
    Curso curso = SistemaArquivos.buscarCursoPorNome(nomeCurso);
    
    if (curso == null) {
      System.out.println("Curso não encontrado: " + nomeCurso);
      return;
    }
    
    System.out.println("\n=== CURRÍCULO DO CURSO: " + nomeCurso.toUpperCase() + " ===");
    System.out.println("Créditos necessários: " + curso.getCreditos());
    
    double totalCargaHoraria = 0;
    List<Disciplina> todasDisciplinas = SistemaArquivos.carregarDisciplinas();
    
    System.out.println("\nDisciplinas Obrigatórias:");
    for (String nomeDisciplina : curso.getDisciplinasObrigatorias()) {
      for (Disciplina disciplina : todasDisciplinas) {
        if (disciplina.getNome().equalsIgnoreCase(nomeDisciplina)) {
          System.out.println("- " + disciplina.getNome() + " (" + disciplina.getCargaHoraria() + "h)");
          totalCargaHoraria += disciplina.getCargaHoraria();
          break;
        }
      }
    }
    
    System.out.println("\nDisciplinas Optativas:");
    for (String nomeDisciplina : curso.getDisciplinasOptativas()) {
      for (Disciplina disciplina : todasDisciplinas) {
        if (disciplina.getNome().equalsIgnoreCase(nomeDisciplina)) {
          System.out.println("- " + disciplina.getNome() + " (" + disciplina.getCargaHoraria() + "h)");
          break;
        }
      }
    }
    
    System.out.println("\n=== RESUMO ===");
    System.out.println("Total de disciplinas obrigatórias: " + curso.getDisciplinasObrigatorias().size());
    System.out.println("Total de disciplinas optativas: " + curso.getDisciplinasOptativas().size());
    System.out.println("Carga horária total das obrigatórias: " + totalCargaHoraria + "h");
  }

  // secretária que decide quando o período de matricula inicia
  public void iniciarPeriodoMatricula() {
    periodoMatriculaAtivo = true;
    System.out.println(
        "Período de matrícula iniciado. Os alunos podem se inscrever ou cancelar matrículas.");
  }

  // decide também quando acaba
  public void finalizarPeriodoMatricula() {
    periodoMatriculaAtivo = false;
    System.out.println(
        "Período de matrícula encerrado. Verificando disciplinas com menos de 3 alunos...");
    cancelarDisciplinasComPoucosAlunos();
  }

  public static boolean isPeriodoMatriculaAtivo() {
    return periodoMatriculaAtivo;
  }

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

  public boolean cadastrarDisciplina(String nome, double cargaHoraria, boolean obrigatoria) {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();

    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getNome().equalsIgnoreCase(nome)) {
        System.out.println("Disciplina já cadastrada.");
        return false;
      }
    }

    Disciplina novaDisciplina =
        new Disciplina(cargaHoraria, nome, obrigatoria, new ArrayList<>(), Status.ATIVA);
    SistemaArquivos.salvarDisciplina(novaDisciplina);

    System.out.println("Disciplina cadastrada com sucesso!");
    return true;
  }

  public boolean removerDisciplina(String nome) {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    boolean encontrou = false;


    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
    for (Matricula matricula : matriculas) {
      if (matricula.getDisciplina().getNome().equalsIgnoreCase(nome)) {
        System.out.println("Não é possível remover disciplina com alunos matriculados.");
        return false;
      }
    }

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

  public boolean matricularAluno(String emailAluno, String nomeDisciplina) {
    List<Aluno> alunos = SistemaArquivos.carregarAlunos();
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();

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

    for (Matricula m : matriculas) {
      if (m.getAluno().getEmail().equalsIgnoreCase(emailAluno)
          && m.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina)) {
        System.out.println("Aluno já está matriculado nesta disciplina.");
        return false;
      }
    }

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
    Matricula matricula = new Matricula(disciplina, aluno, LocalDate.now());
    SistemaArquivos.salvarMatricula(matricula);

    System.out.println("Aluno matriculado com sucesso!");
    return true;
  }

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
      if (disciplina.getProfessor() != null) {
        System.out.println("Professor: " + disciplina.getProfessor().getEmail());
      } else {
        System.out.println("Professor: (não vinculado)");
      }
      System.out.println("------------------------");
    }
  }

  public boolean cancelarDisciplina(String nomeDisciplina) {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();

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

  public boolean vincularProfessorADisciplina(String nomeDisciplina, String emailProfessor) {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
    List<Professor> professores = SistemaArquivos.carregarProfessores();
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
    Professor professor = null;
    for (Professor p : professores) {
      if (p.getEmail().equalsIgnoreCase(emailProfessor)) {
        professor = p;
        break;
      }
    }
    if (professor == null) {
      System.out.println("Professor não encontrado.");
      return false;
    }
    // Atualiza objeto disciplina
    disciplina.setProfessor(professor);
    // Garante que a lista de disciplinas do professor em memória seja atualizada
    if (professor.getDisciplinas() == null) {
      professor.setDisciplinas(new java.util.ArrayList<>());
    }
    boolean jaExiste = false;
    for (Disciplina dProf : professor.getDisciplinas()) {
      if (dProf.getNome().equalsIgnoreCase(disciplina.getNome())) {
        jaExiste = true;
        break;
      }
    }
    if (!jaExiste) {
      professor.getDisciplinas().add(disciplina);
    }
    SistemaArquivos.reescreverDisciplinas(disciplinas);
    // Atualiza disciplinas do professor no arquivo de usuários
    List<String> nomesDisciplinas = new ArrayList<>();
    for (Disciplina d : disciplinas) {
      if (d.getProfessor() != null && d.getProfessor().getEmail().equalsIgnoreCase(emailProfessor)) {
        nomesDisciplinas.add(d.getNome());
      }
    }
    SistemaArquivos.atualizarDisciplinasDoProfessor(emailProfessor, nomesDisciplinas);
    System.out.println("Professor vinculado à disciplina com sucesso!");
    return true;
  }

  public boolean desvincularProfessorDeDisciplina(String nomeDisciplina) {
    List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
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
    Professor professorAnterior = disciplina.getProfessor();
    disciplina.setProfessor(null);
    SistemaArquivos.reescreverDisciplinas(disciplinas);
    if (professorAnterior != null) {
      // Remove da lista em memória
      if (professorAnterior.getDisciplinas() != null) {
        professorAnterior.getDisciplinas().removeIf(d -> d.getNome().equalsIgnoreCase(nomeDisciplina));
      }
      // Recalcula lista restante para persistir
      List<String> nomesRestantes = new ArrayList<>();
      for (Disciplina d : disciplinas) {
        if (d.getProfessor() != null && d.getProfessor().getEmail().equalsIgnoreCase(professorAnterior.getEmail())) {
          nomesRestantes.add(d.getNome());
        }
      }
      SistemaArquivos.atualizarDisciplinasDoProfessor(professorAnterior.getEmail(), nomesRestantes);
    }
    System.out.println("Professor desvinculado da disciplina com sucesso!");
    return true;
  }
}
