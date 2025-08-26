package com.sm;

import java.util.List;

public class Professor extends Usuario {
    private List<Disciplina> disciplinas;

    public Professor(List<Disciplina> disciplinas, String email, String senha) {
        super(email, senha);
        this.disciplinas = disciplinas;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    
}
