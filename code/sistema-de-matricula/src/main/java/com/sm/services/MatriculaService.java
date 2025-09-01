package com.sm.services;

import com.sm.*;
import java.time.LocalDate;
import java.util.List;

public class MatriculaService {
    
    public static boolean matricularAluno(Aluno aluno, String nomeDisciplina) {
        if (!Secretaria.isPeriodoMatriculaAtivo()) {
            System.out.println("Não é possível se matricular fora do período de matrícula.");
            return false;
        }
        
        List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
        List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
        
        Disciplina disciplina = BuscaService.buscarDisciplinaPorNome(nomeDisciplina, disciplinas);
        if (disciplina == null) {
            System.out.println("Disciplina não encontrada.");
            return false;
        }
        
        MatriculaValidationService.ValidationResult resultado = 
            MatriculaValidationService.validarMatricula(aluno, disciplina, matriculas);
        
        if (!resultado.isValido()) {
            resultado.imprimirErro();
            return false;
        }
        
        Matricula matricula = new Matricula(disciplina, aluno, LocalDate.now());
        SistemaArquivos.salvarMatricula(matricula);
        
        disciplina.getAlunos().add(aluno);
        SistemaArquivos.reescreverDisciplinas(disciplinas);
        
        System.out.println("Matrícula realizada com sucesso!");
        return true;
    }
    
    public static boolean cancelarMatricula(Aluno aluno, String nomeDisciplina) {
        if (!Secretaria.isPeriodoMatriculaAtivo()) {
            System.out.println("Não é possível cancelar matrícula fora do período de matrícula.");
            return false;
        }
        
        List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
        
        Matricula matriculaParaRemover = matriculas.stream()
                .filter(m -> m.getAluno().getEmail().equalsIgnoreCase(aluno.getEmail()) &&
                            m.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina))
                .findFirst()
                .orElse(null);
        
        if (matriculaParaRemover == null) {
            System.out.println("Você não está matriculado nesta disciplina.");
            return false;
        }
        
        matriculas.remove(matriculaParaRemover);
        SistemaArquivos.reescreverMatriculas(matriculas);
        
        List<Disciplina> disciplinas = SistemaArquivos.carregarDisciplinas();
        disciplinas.stream()
                .filter(d -> d.getNome().equalsIgnoreCase(nomeDisciplina))
                .forEach(d -> d.getAlunos().removeIf(a -> a.getEmail().equalsIgnoreCase(aluno.getEmail())));
        
        SistemaArquivos.reescreverDisciplinas(disciplinas);
        
        System.out.println("Matrícula cancelada com sucesso!");
        return true;
    }
}
