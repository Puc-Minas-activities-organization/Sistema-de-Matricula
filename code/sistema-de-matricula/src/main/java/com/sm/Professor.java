package com.sm;

import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario {
    private List<Disciplina> disciplinas;

    public Professor(List<Disciplina> disciplinas, String email, String senha) {
        super(email, senha);
        this.disciplinas = disciplinas != null ? disciplinas : new ArrayList<>();
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    //consulta os alunos somente das disciplina que ele leciona
    public void consultarAlunosDisciplina(String nomeDisciplina) {
        boolean encontrou = false;
        
        for (Disciplina disciplina : this.disciplinas) {
            if (disciplina.getNome().equalsIgnoreCase(nomeDisciplina)) {
                encontrou = true;
                break;
            }
        }    
        if (!encontrou) {
            System.out.println("Você não leciona esta disciplina.");
            return;
        }
        List<Disciplina> todasDisciplinas = SistemaArquivos.carregarDisciplinas();
        Disciplina disciplinaEncontrada = null;
        
        for (Disciplina d : todasDisciplinas) {
            if (d.getNome().equalsIgnoreCase(nomeDisciplina) && d.getStatus() == Status.ATIVA) {
                disciplinaEncontrada = d;
                break;
            }
        }
        
        if (disciplinaEncontrada == null) {
            System.out.println("Disciplina não encontrada ou não está ativa.");
            return;
        }
        
        List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
        System.out.println("\n=== ALUNOS DA DISCIPLINA: " + nomeDisciplina.toUpperCase() + " ===");
        
        boolean temAlunos = false;
        for (Matricula matricula : matriculas) {
            if (matricula.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina)) {
                System.out.println("Email: " + matricula.getAluno().getEmail());
                System.out.println("Data de Matrícula: " + matricula.getDataMatricula());
                System.out.println("------------------------");
                temAlunos = true;
            }
        }
        
        if (!temAlunos) {
            System.out.println("Nenhum aluno matriculado nesta disciplina.");
        }
    }
    
    // listar disciplinas que o professor leciona(interface dele)
    public void listarMinhasDisciplinas() {
        System.out.println("\n=== DISCIPLINAS QUE VOCÊ LECIONA ===");
        
        if (this.disciplinas == null || this.disciplinas.isEmpty()) {
            System.out.println("Você não está lecionando nenhuma disciplina.");
            return;
        }
        
        for (Disciplina disciplina : this.disciplinas) {
            System.out.println("Nome: " + disciplina.getNome());
            System.out.println("Carga Horária: " + disciplina.getCargaHoraria());
            System.out.println("Tipo: " + (disciplina.isObrigatoria() ? "Obrigatória" : "Optativa"));
            System.out.println("Status: " + disciplina.getStatus());
            System.out.println("Alunos matriculados: " + disciplina.getAlunos().size());
            System.out.println("------------------------");
        }
    }
    
    public void verificarStatusDisciplina(String nomeDisciplina) {
        boolean encontrou = false;

        for (Disciplina disciplina : this.disciplinas) {
            if (disciplina.getNome().equalsIgnoreCase(nomeDisciplina)) {
                encontrou = true;
                break;
            }
        }
        
        if (!encontrou) {
            System.out.println("Você não leciona esta disciplina.");
            return;
        }
        
        List<Matricula> matriculas = SistemaArquivos.carregarMatriculas();
        int contador = 0;
        
        for (Matricula matricula : matriculas) {
            if (matricula.getDisciplina().getNome().equalsIgnoreCase(nomeDisciplina)) {
                contador++;
            }
        }
        
        System.out.println("\n=== STATUS DA DISCIPLINA: " + nomeDisciplina.toUpperCase() + " ===");
        System.out.println("Alunos matriculados: " + contador);
        System.out.println("Mínimo necessário: " + Disciplina.MIN_ALUNOS);
        System.out.println("Máximo permitido: " + Disciplina.MAX_ALUNOS);
        
        if (contador < Disciplina.MIN_ALUNOS) {
            System.out.println("ATENÇÃO: Disciplina com inscrições insuficientes!");
        } else if (contador >= Disciplina.MAX_ALUNOS) {
            System.out.println("Disciplina lotada.");
        } else {
            System.out.println("Disciplina com número adequado de alunos.");
        }
    }
}
