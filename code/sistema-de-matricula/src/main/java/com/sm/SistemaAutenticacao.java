package com.sm;

import java.util.List;

public class SistemaAutenticacao {
    
    public static Usuario autenticar(String email, String senha) {
        // Verificar alunos
        List<Aluno> alunos = SistemaArquivos.carregarAlunos();
        for (Aluno aluno : alunos) {
            if (aluno.getEmail().equalsIgnoreCase(email) && aluno.getSenha().equals(senha)) {
                return aluno;
            }
        }
        
        // Verificar professores
        List<Professor> professores = SistemaArquivos.carregarProfessores();
        for (Professor professor : professores) {
            if (professor.getEmail().equalsIgnoreCase(email) && professor.getSenha().equals(senha)) {
                return professor;
            }
        }
        
        // Verificar secretárias
        List<Secretaria> secretarias = SistemaArquivos.carregarSecretarias();
        for (Secretaria secretaria : secretarias) {
            if (secretaria.getEmail().equalsIgnoreCase(email) && secretaria.getSenha().equals(senha)) {
                return secretaria;
            }
        }
        
        return null; // Usuário não encontrado ou senha incorreta
    }
    
    public static boolean cadastrarUsuario(String email, String senha, String tipoUsuario) {
        // Verificar se o email já existe no sistema (independente do tipo)
        if (SistemaArquivos.emailJaExiste(email)) {
            System.out.println("Erro: Este email já está cadastrado no sistema.");
            return false; // Email já existe
        }
        
        switch (tipoUsuario.toLowerCase()) {
            case "aluno":
                Aluno novoAluno = new Aluno(null, null, 4, 2, email, senha);
                SistemaArquivos.salvarUsuario(novoAluno);
                return true;
            case "professor":
                Professor novoProfessor = new Professor(null, email, senha);
                SistemaArquivos.salvarUsuario(novoProfessor);
                return true;
            case "secretaria":
                Secretaria novaSecretaria = new Secretaria(email, senha);
                SistemaArquivos.salvarUsuario(novaSecretaria);
                return true;
            default:
                return false;
        }
    }
}
