package com.sm.services;

import com.sm.*;
import java.util.List;

public class BuscaService {
    
    public static Disciplina buscarDisciplinaPorNome(String nome, List<Disciplina> disciplinas) {
        if (nome == null || disciplinas == null) {
            return null;
        }
        
        return disciplinas.stream()
                .filter(d -> d.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }
    
    public static Disciplina buscarDisciplinaPorNome(String nome) {
        return buscarDisciplinaPorNome(nome, SistemaArquivos.carregarDisciplinas());
    }
    
    public static Aluno buscarAlunoPorEmail(String email, List<Aluno> alunos) {
        if (email == null || alunos == null) {
            return null;
        }
        
        return alunos.stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
    
    public static Aluno buscarAlunoPorEmail(String email) {
        return buscarAlunoPorEmail(email, SistemaArquivos.carregarAlunos());
    }
    
    public static Professor buscarProfessorPorEmail(String email, List<Professor> professores) {
        if (email == null || professores == null) {
            return null;
        }
        
        return professores.stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
    
    public static Professor buscarProfessorPorEmail(String email) {
        return buscarProfessorPorEmail(email, SistemaArquivos.carregarProfessores());
    }
    
    public static boolean professorLecionaDisciplina(Professor professor, String nomeDisciplina) {
        if (professor == null || professor.getDisciplinas() == null || nomeDisciplina == null) {
            return false;
        }
        
        return professor.getDisciplinas().stream()
                .anyMatch(d -> d.getNome().equalsIgnoreCase(nomeDisciplina));
    }
}
