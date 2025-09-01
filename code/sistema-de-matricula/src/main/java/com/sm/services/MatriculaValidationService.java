package com.sm.services;

import com.sm.*;
import java.util.List;

public class MatriculaValidationService {
    
    public static boolean alunoJaMatriculado(String emailAluno, String nomeDisciplina, List<Matricula> matriculas) {
        if (emailAluno == null || nomeDisciplina == null || matriculas == null) {
            return false;
        }
        
        return matriculas.stream()
                .anyMatch(m -> m.getAluno().getEmail().equalsIgnoreCase(emailAluno) &&
                              m.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina));
    }
    
    public static int contarAlunosMatriculados(String nomeDisciplina, List<Matricula> matriculas) {
        if (nomeDisciplina == null || matriculas == null) {
            return 0;
        }
        
        return (int) matriculas.stream()
                .filter(m -> m.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina))
                .count();
    }
    
    public static int contarDisciplinasObrigatorias(String emailAluno, List<Matricula> matriculas) {
        if (emailAluno == null || matriculas == null) {
            return 0;
        }
        
        return (int) matriculas.stream()
                .filter(m -> m.getAluno().getEmail().equalsIgnoreCase(emailAluno) &&
                            m.getDisciplina().isObrigatoria())
                .count();
    }
    
    public static int contarDisciplinasOptativas(String emailAluno, List<Matricula> matriculas) {
        if (emailAluno == null || matriculas == null) {
            return 0;
        }
        
        return (int) matriculas.stream()
                .filter(m -> m.getAluno().getEmail().equalsIgnoreCase(emailAluno) &&
                            !m.getDisciplina().isObrigatoria())
                .count();
    }
    
    public static ValidationResult validarMatricula(Aluno aluno, Disciplina disciplina, List<Matricula> matriculas) {
        if (disciplina.getStatus() != Status.ATIVA) {
            return ValidationResult.erro("Disciplina não está ativa.");
        }
        
        int numAlunos = contarAlunosMatriculados(disciplina.getNome(), matriculas);
        if (numAlunos >= Disciplina.MAX_ALUNOS) {
            return ValidationResult.erro("Disciplina lotada.");
        }
        
        if (alunoJaMatriculado(aluno.getEmail(), disciplina.getNome(), matriculas)) {
            return ValidationResult.erro("Você já está matriculado nesta disciplina.");
        }
        
        int obrigatorias = contarDisciplinasObrigatorias(aluno.getEmail(), matriculas);
        int optativas = contarDisciplinasOptativas(aluno.getEmail(), matriculas);
        
        if (disciplina.isObrigatoria() && obrigatorias >= aluno.getMaxObrigatorias()) {
            return ValidationResult.erro("Limite de disciplinas obrigatórias atingido.");
        }
        
        if (!disciplina.isObrigatoria() && optativas >= aluno.getMaxOptativas()) {
            return ValidationResult.erro("Limite de disciplinas optativas atingido.");
        }
        
        return ValidationResult.sucesso();
    }
    
    public static class ValidationResult {
        private final boolean valido;
        private final String mensagemErro;
        
        private ValidationResult(boolean valido, String mensagemErro) {
            this.valido = valido;
            this.mensagemErro = mensagemErro;
        }
        
        public static ValidationResult sucesso() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult erro(String mensagem) {
            return new ValidationResult(false, mensagem);
        }
        
        public boolean isValido() {
            return valido;
        }
        
        public String getMensagemErro() {
            return mensagemErro;
        }
        
        public void imprimirErro() {
            if (!valido && mensagemErro != null) {
                System.out.println(mensagemErro);
            }
        }
    }
}
