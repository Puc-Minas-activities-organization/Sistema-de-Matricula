package com.sm;

import java.util.List;

public class Disciplina {
    public static final int MAX_ALUNOS = 60;
    public static final int MIN_ALUNOS = 3;
    private double cargaHoraria;
    private String nome;
    private boolean obrigatoria;
    private List<Aluno> alunos;
    private Status status;
    

    public String obterInformacoes(){
        return "";
    }

    public Status verificarStatus(){
        return status;
    }

}
