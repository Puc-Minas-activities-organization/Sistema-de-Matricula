package com.sm.interfaces;

import java.util.Scanner;

import com.sm.Aluno;

public class InterfaceAluno {
    private Aluno aluno;
    private Scanner scanner;
    
    public InterfaceAluno(Aluno aluno) {
        this.aluno = aluno;
        this.scanner = new Scanner(System.in);
    }
    
    public void exibirMenu() {
        int opcao;
        
        do {
            System.out.println("\n=== MENU DO ALUNO ===");
            System.out.println("Bem-vindo, " + aluno.getEmail());
            System.out.println("1. Listar disciplinas disponíveis");
            System.out.println("2. Matricular-se em disciplina");
            System.out.println("3. Visualizar minhas matrículas");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha
            
            switch (opcao) {
                case 1:
                    aluno.listarDisciplinasDisponiveis();
                    break;
                case 2:
                    matricularEmDisciplina();
                    break;
                case 3:
                    aluno.visualizarMatriculas();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }
    
    private void matricularEmDisciplina() {
        System.out.print("Digite o nome da disciplina: ");
        String nomeDisciplina = scanner.nextLine();
        aluno.matricular(nomeDisciplina);
    }
}
