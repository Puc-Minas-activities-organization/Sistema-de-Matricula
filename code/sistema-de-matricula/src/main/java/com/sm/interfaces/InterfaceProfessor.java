package com.sm.interfaces;

import java.util.Scanner;

import com.sm.Professor;

public class InterfaceProfessor {
    private Professor professor;
    private Scanner scanner;
    
    public InterfaceProfessor(Professor professor) {
        this.professor = professor;
        this.scanner = new Scanner(System.in);
    }
    
    public void exibirMenu() {
        int opcao;
        
        do {
            System.out.println("\n=== MENU DO PROFESSOR ===");
            System.out.println("Bem-vindo, " + professor.getEmail());
            System.out.println("1. Listar minhas disciplinas");
            System.out.println("2. Consultar alunos de uma disciplina");
            System.out.println("3. Verificar status de uma disciplina");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha
            
            switch (opcao) {
                case 1:
                    professor.listarMinhasDisciplinas();
                    break;
                case 2:
                    consultarAlunosDisciplina();
                    break;
                case 3:
                    verificarStatusDisciplina();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }
    
    private void consultarAlunosDisciplina() {
        System.out.print("Digite o nome da disciplina: ");
        String nomeDisciplina = scanner.nextLine();
        professor.consultarAlunosDisciplina(nomeDisciplina);
    }
    
    private void verificarStatusDisciplina() {
        System.out.print("Digite o nome da disciplina: ");
        String nomeDisciplina = scanner.nextLine();
        professor.verificarStatusDisciplina(nomeDisciplina);
    }
}
