package com.sm.interfaces;

import com.sm.Secretaria;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InterfaceSecretaria {
  private Secretaria secretaria;
  private Scanner scanner;

  public InterfaceSecretaria(Secretaria secretaria) {
    this.secretaria = secretaria;
    this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
  }

  public void exibirMenu() {
    int opcao;

 do {
      System.out.println("\n=== MENU DA SECRETARIA ===");
      System.out.println("Bem-vindo, " + secretaria.getEmail());
      System.out.println("1. Listar cursos disponíveis");
      System.out.println("2. Criar novo curso");
      System.out.println("3. Remover curso");
      System.out.println("4. Gerar currículo de curso");
      System.out.println("5. Cadastrar nova disciplina");
      System.out.println("6. Remover disciplina");
      System.out.println("7. Vincular disciplina a curso");
      System.out.println("8. Listar todas as disciplinas");
      System.out.println("9. Cancelar disciplina");
      System.out.println("10. Vincular professor a disciplina");
      System.out.println("11. Desvincular professor de disciplina");
      System.out.println("12. Matricular aluno em disciplina");
      System.out.println("13. Gerar relatório de matrículas");
      System.out.println("14. Iniciar período de matrícula");
      System.out.println("15. Finalizar período de matrícula");
      System.out.println("0. Sair");
      System.out.print("Escolha uma opção: ");

      opcao = scanner.nextInt();
      scanner.nextLine();

      switch (opcao) {
        case 1:
          listarCursos();
          break;
        case 2:
          criarCurso();
          break;
        case 3:
          removerCurso();
          break;
        case 4:
          gerarCurriculo();
          break;
        case 5:
          cadastrarDisciplina();
          break;
        case 6:
          removerDisciplina();
          break;
        case 7:
          vincularDisciplinaACurso();
          break;
        case 8:
          secretaria.listarTodasDisciplinas();
          break;
        case 9:
          cancelarDisciplina();
          break;
        case 10:
          vincularProfessorADisciplina();
          break;
        case 11:
          desvincularProfessorDeDisciplina();
          break;
        case 12:
          matricularAluno();
          break;
        case 13:
          secretaria.gerarRelatorioMatriculas();
          break;
        case 14:
          secretaria.iniciarPeriodoMatricula();
          break;
        case 15:
          secretaria.finalizarPeriodoMatricula();
          break;
        case 0:
          System.out.println("Saindo...");
          break;
        default:
          System.out.println("Opção inválida!");
      }
    } while (opcao != 0);
  }

  private void vincularDisciplinaACurso() {
    System.out.print("Digite o nome do curso: ");
    String nomeCurso = scanner.nextLine();
    System.out.print("Digite o nome da disciplina: ");
    String nomeDisciplina = scanner.nextLine();
    System.out.print("A disciplina é obrigatória? (true/false): ");
    boolean obrigatoria = Boolean.parseBoolean(scanner.nextLine());
    boolean sucesso =
        secretaria.vincularDisciplinaACurso(nomeCurso.trim(), nomeDisciplina.trim(), obrigatoria);
    if (!sucesso) {
      System.out.println("Falha ao vincular disciplina ao curso.");
    }
  }

  private void criarCurso() {
    System.out.print("Digite o nome do curso: ");
    String nome = scanner.nextLine();
    System.out.print("Digite o número de créditos: ");
    int creditos;
    try {
      creditos = Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      System.out.println("Créditos inválidos.");
      return;
    }
    boolean sucesso = secretaria.criarCurso(nome.trim(), creditos, new ArrayList<>(), new ArrayList<>());
    if (!sucesso) {
      System.out.println("Falha ao criar curso.");
    }
  }

  private void removerCurso() {
    System.out.print("Digite o nome do curso a ser removido: ");
    String nome = scanner.nextLine();
    boolean sucesso = secretaria.removerCurso(nome.trim());
    if (!sucesso) {
      System.out.println("Falha ao remover curso.");
    }
  }

  private void gerarCurriculo() {
    System.out.print("Digite o nome do curso: ");
    String nomeCurso = scanner.nextLine();
    String nomeCursoNormalizado = normalizarTexto(nomeCurso);
    secretaria.gerarCurriculo(nomeCursoNormalizado);
  }

  private void cadastrarDisciplina() {
    System.out.print("Digite o nome da disciplina: ");
    String nome = scanner.nextLine();

    System.out.print("Digite a carga horária: ");
    String cargaHorariaStr = scanner.nextLine();
    double cargaHoraria;

    try {
      cargaHoraria = Double.parseDouble(cargaHorariaStr.replace(",", "."));
    } catch (NumberFormatException e) {
      System.out.println("Carga horária inválida. Use apenas números (ex: 40 ou 40.5)");
      return;
    }

    System.out.print("É obrigatória? (true/false): ");
    boolean obrigatoria = scanner.nextBoolean();
    scanner.nextLine(); // Consumir quebra de linha

    try {
      secretaria.cadastrarDisciplina(nome, cargaHoraria, obrigatoria);
    } catch (IllegalArgumentException e) {
      System.out.println("Erro: " + e.getMessage());
    }
  }

  private void removerDisciplina() {
    System.out.print("Digite o nome da disciplina a ser removida: ");
    String nome = scanner.nextLine();
    secretaria.removerDisciplina(nome);
  }

  private void matricularAluno() {
    System.out.print("Digite o email do aluno: ");
    String emailAluno = scanner.nextLine();

    System.out.print("Digite o nome da disciplina: ");
    String nomeDisciplina = scanner.nextLine();

    secretaria.matricularAluno(emailAluno, nomeDisciplina);
  }

  private void cancelarDisciplina() {
    System.out.print("Digite o nome da disciplina a ser cancelada: ");
    String nome = scanner.nextLine();
    secretaria.cancelarDisciplina(nome);
  }

  private void vincularProfessorADisciplina() {
    System.out.print("Digite o nome da disciplina: ");
    String nomeDisciplina = scanner.nextLine();
    System.out.print("Digite o email do professor: ");
    String emailProfessor = scanner.nextLine();
    secretaria.vincularProfessorADisciplina(nomeDisciplina, emailProfessor);
  }

  private void desvincularProfessorDeDisciplina() {
    System.out.print("Digite o nome da disciplina: ");
    String nomeDisciplina = scanner.nextLine();
    secretaria.desvincularProfessorDeDisciplina(nomeDisciplina);
  }

  private void listarCursos() {
    List<com.sm.Curso> cursos = com.sm.SistemaArquivos.carregarCursos();
    System.out.println("\n=== CURSOS DISPONÍVEIS ===");

    if (cursos.isEmpty()) {
      System.out.println("Nenhum curso cadastrado.");
      return;
    }

    for (com.sm.Curso curso : cursos) {
      System.out.println("Nome: " + curso.getNome());
      System.out.println("Créditos: " + curso.getCreditos());
      System.out.println("Disciplinas Obrigatórias: " + curso.getDisciplinasObrigatorias().size());
      System.out.println("Disciplinas Optativas: " + curso.getDisciplinasOptativas().size());
      System.out.println("------------------------");
    }
  }

  private String normalizarTexto(String texto) { // muito feio
    if (texto == null) {
      return null;
    }

    return texto
        .toLowerCase()
        .replace("ã", "a")
        .replace("á", "a")
        .replace("à", "a")
        .replace("â", "a")
        .replace("ä", "a")
        .replace("é", "e")
        .replace("è", "e")
        .replace("ê", "e")
        .replace("ë", "e")
        .replace("í", "i")
        .replace("ì", "i")
        .replace("î", "i")
        .replace("ï", "i")
        .replace("ó", "o")
        .replace("ò", "o")
        .replace("ô", "o")
        .replace("õ", "o")
        .replace("ö", "o")
        .replace("ú", "u")
        .replace("ù", "u")
        .replace("û", "u")
        .replace("ü", "u")
        .replace("ç", "c")
        .trim();
  }
}
