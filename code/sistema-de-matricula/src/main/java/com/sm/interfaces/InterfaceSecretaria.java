package com.sm.interfaces;

import com.sm.Secretaria;
import java.util.Scanner;

public class InterfaceSecretaria {
  private Secretaria secretaria;
  private Scanner scanner;

  public InterfaceSecretaria(Secretaria secretaria) {
    this.secretaria = secretaria;
    this.scanner = new Scanner(System.in);
  }

  public void exibirMenu() {
    int opcao;

    do {
      System.out.println("\n=== MENU DA SECRETARIA ===");
      System.out.println("Bem-vindo, " + secretaria.getEmail());
      System.out.println("1. Gerar currículo de curso");
      System.out.println("2. Cadastrar nova disciplina");
      System.out.println("3. Remover disciplina");
      System.out.println("4. Matricular aluno em disciplina");
      System.out.println("5. Listar todas as disciplinas");
      System.out.println("6. Cancelar disciplina");
      System.out.println("7. Gerar relatório de matrículas");
      System.out.println("8. Iniciar período de matrícula");
      System.out.println("9. Finalizar período de matrícula");
      System.out.println("0. Sair");
      System.out.print("Escolha uma opção: ");

      opcao = scanner.nextInt();
      scanner.nextLine(); // Consumir quebra de linha

      switch (opcao) {
        case 1:
          gerarCurriculo();
          break;
        case 2:
          cadastrarDisciplina();
          break;
        case 3:
          removerDisciplina();
          break;
        case 4:
          matricularAluno();
          break;
        case 5:
          secretaria.listarTodasDisciplinas();
          break;
        case 6:
          cancelarDisciplina();
          break;
        case 7:
          secretaria.gerarRelatorioMatriculas();
          break;
        case 8:
          secretaria.iniciarPeriodoMatricula();
          break;
        case 9:
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

  private void gerarCurriculo() {
    System.out.print("Digite o nome do curso: ");
    String nomeCurso = scanner.nextLine();
    secretaria.gerarCurriculo(nomeCurso);
  }

  private void cadastrarDisciplina() {
    System.out.print("Digite o nome da disciplina: ");
    String nome = scanner.nextLine();

    System.out.print("Digite a carga horária: ");
    double cargaHoraria = scanner.nextDouble();

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
}
