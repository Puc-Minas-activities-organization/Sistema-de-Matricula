package com.sm;

import java.util.Scanner;

import com.sm.interfaces.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE MATRÍCULA ===");
        
        // Inicializar dados básicos (opcional - para testes)
        inicializarDadosBasicos();
        
        int opcao;
        
        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Login");
            System.out.println("2. Cadastrar novo usuário");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha
            
            switch (opcao) {
                case 1:
                    realizarLogin();
                    break;
                case 2:
                    cadastrarUsuario();
                    break;
                case 0:
                    System.out.println("Encerrando sistema...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
        
        scanner.close();
    }
    
    private static void realizarLogin() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        
        Usuario usuario = SistemaAutenticacao.autenticar(email, senha);
        
        if (usuario != null) {
            System.out.println("Login realizado com sucesso!");
            
            // Redirecionar para interface específica baseada no tipo do usuário
            if (usuario instanceof Aluno) {
                InterfaceAluno interfaceAluno = new InterfaceAluno((Aluno) usuario);
                interfaceAluno.exibirMenu();
            } else if (usuario instanceof Professor) {
                InterfaceProfessor interfaceProfessor = new InterfaceProfessor((Professor) usuario);
                interfaceProfessor.exibirMenu();
            } else if (usuario instanceof Secretaria) {
                InterfaceSecretaria interfaceSecretaria = new InterfaceSecretaria((Secretaria) usuario);
                interfaceSecretaria.exibirMenu();
            }
        } else {
            System.out.println("Email ou senha incorretos!");
        }
    }
    
    private static void cadastrarUsuario() {
        System.out.println("\n=== CADASTRO ===");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        
        System.out.println("Tipo de usuário:");
        System.out.println("1. Aluno");
        System.out.println("2. Professor");
        System.out.println("3. Secretária");
        System.out.print("Escolha: ");
        
        int tipoOpcao = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha
        
        String tipoUsuario = "";
        switch (tipoOpcao) {
            case 1:
                tipoUsuario = "aluno";
                break;
            case 2:
                tipoUsuario = "professor";
                break;
            case 3:
                tipoUsuario = "secretaria";
                break;
            default:
                System.out.println("Tipo inválido!");
                return;
        }
        
        if (SistemaAutenticacao.cadastrarUsuario(email, senha, tipoUsuario)) {
            System.out.println("Usuário cadastrado com sucesso!");
        } else {
            System.out.println("Erro: Não foi possível cadastrar o usuário.");
            System.out.println("Verifique se o email já não está sendo usado por outro usuário.");
        }
    }
    
    // Método para inicializar dados básicos para teste
    private static void inicializarDadosBasicos() {
        // Criar uma secretária padrão se não existir
        if (SistemaArquivos.carregarSecretarias().isEmpty()) {
            SistemaAutenticacao.cadastrarUsuario("admin@escola.com", "123456", "secretaria");
        }
        
        // Criar algumas disciplinas básicas se não existirem
        if (SistemaArquivos.carregarDisciplinas().isEmpty()) {
            Secretaria adminTemp = new Secretaria("temp", "temp");
            adminTemp.cadastrarDisciplina("Programação Orientada a Objetos", 60, true);
            adminTemp.cadastrarDisciplina("Estruturas de Dados", 60, true);
            adminTemp.cadastrarDisciplina("Banco de Dados", 60, true);
            adminTemp.cadastrarDisciplina("Desenvolvimento Web", 45, false);
            adminTemp.cadastrarDisciplina("Inteligência Artificial", 45, false);
        }
    }
}