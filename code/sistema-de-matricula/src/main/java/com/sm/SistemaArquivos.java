package com.sm;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SistemaArquivos {
//reescreve todas as matrículas
  public static void reescreverMatriculas(List<Matricula> matriculas) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_MATRICULAS))) {
      for (Matricula m : matriculas) {
        writer.println(
            m.getAluno().getEmail()
                + ";"
                + m.getDisciplina().getNome()
                + ";"
                + m.getDataMatricula());
      }
    } catch (IOException e) {
      System.out.println("Erro ao reescrever arquivo de matrículas: " + e.getMessage());
    }
  }

  private static final String ARQUIVO_USUARIOS =
      "code/sistema-de-matricula/src/main/java/com/sm/resources/usuarios.txt";
  private static final String ARQUIVO_DISCIPLINAS =
      "code/sistema-de-matricula/src/main/java/com/sm/resources/disciplinas.txt";
  private static final String ARQUIVO_MATRICULAS =
      "code/sistema-de-matricula/src/main/java/com/sm/resources/matriculas.txt";

  // salva qualquer usuário no arquivo
  public static void salvarUsuario(Usuario usuario) {
    String tipoUsuario = "";
    String dadosExtra = "";

    if (usuario instanceof Aluno) {
      tipoUsuario = "ALUNO";
      Aluno aluno = (Aluno) usuario;
      dadosExtra = (aluno.getCurso() != null ? aluno.getCurso().getNome() : "");
    } else if (usuario instanceof Professor) {
      tipoUsuario = "PROFESSOR";
      Professor professor = (Professor) usuario;
      StringBuilder disciplinas = new StringBuilder();
      if (professor.getDisciplinas() != null) {
        for (Disciplina d : professor.getDisciplinas()) {
          disciplinas.append(d.getNome()).append(",");
        }
      }
      dadosExtra = disciplinas.toString();
    } else if (usuario instanceof Secretaria) {
      tipoUsuario = "SECRETARIA";
      dadosExtra = "";
    }

    try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_USUARIOS, true))) {
      writer.println(
          tipoUsuario + ";" + usuario.getEmail() + ";" + usuario.getSenha() + ";" + dadosExtra);
    } catch (IOException e) {
      System.err.println("Erro ao salvar usuário: " + e.getMessage());
    }
  }

  public static void salvarDisciplina(Disciplina disciplina) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_DISCIPLINAS, true))) {
      String emailProfessor =
          disciplina.getProfessor() != null ? disciplina.getProfessor().getEmail() : "";
      writer.println(
          disciplina.getNome()
              + ";"
              + disciplina.getCargaHoraria()
              + ";"
              + disciplina.isObrigatoria()
              + ";"
              + disciplina.getStatus()
              + ";"
              + (disciplina.getAlunos() != null ? disciplina.getAlunos().size() : 0)
              + ";"
              + emailProfessor);
    } catch (IOException e) {
      System.err.println("Erro ao salvar disciplina: " + e.getMessage());
    }
  }

  public static void salvarMatricula(Matricula matricula) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_MATRICULAS, true))) {
      writer.println(
          matricula.getAluno().getEmail()
              + ";"
              + matricula.getDisciplina().getNome()
              + ";"
              + matricula.getDataMatricula().toString());
    } catch (IOException e) {
      System.err.println("Erro ao salvar matrícula: " + e.getMessage());
    }
  }

  public static List<Aluno> carregarAlunos() {
    List<Aluno> alunos = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 3 && "ALUNO".equals(dados[0])) {
          String email = dados[1];
          String senha = dados[2];
          Curso curso = null;
          if (dados.length > 3 && !dados[3].isEmpty()) {
            curso = buscarCursoPorNome(dados[3]);
          }
          Aluno aluno = new Aluno(new ArrayList<>(), curso, 4, 2, email, senha);
          alunos.add(aluno);
        }
      }
    } catch (IOException e) {
    }
    return alunos;
  }

  public static List<Professor> carregarProfessores() {
    List<Professor> professores = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 3 && "PROFESSOR".equals(dados[0])) {
          String email = dados[1];
          String senha = dados[2];
          List<Disciplina> disciplinas = new ArrayList<>();
          if (dados.length > 3 && !dados[3].isEmpty()) {
            String[] nomesDisciplinas = dados[3].split(",");
            for (String nome : nomesDisciplinas) {
              if (!nome.trim().isEmpty()) {
                Disciplina disciplina = buscarDisciplinaPorNome(nome.trim());
                if (disciplina != null) {
                  disciplinas.add(disciplina);
                }
              }
            }
          }
          Professor professor = new Professor(disciplinas, email, senha);
          professores.add(professor);
        }
      }
    } catch (IOException e) {
    }
    return professores;
  }

  public static List<Secretaria> carregarSecretarias() {
    List<Secretaria> secretarias = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 3 && "SECRETARIA".equals(dados[0])) {
          String email = dados[1];
          String senha = dados[2];
          Secretaria secretaria = new Secretaria(email, senha);
          secretarias.add(secretaria);
        }
      }
    } catch (IOException e) {
    }
    return secretarias;
  }

  public static List<Disciplina> carregarDisciplinas() {
    List<Disciplina> disciplinas = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_DISCIPLINAS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 5) {
          String nome = dados[0];
          double cargaHoraria = Double.parseDouble(dados[1]);
          boolean obrigatoria = Boolean.parseBoolean(dados[2]);
          Status status = Status.valueOf(dados[3]);
          List<Aluno> alunos = new ArrayList<>();
          Disciplina disciplina = new Disciplina(cargaHoraria, nome, obrigatoria, alunos, status);
          if (dados.length >= 6 && !dados[5].isEmpty()) {
            String emailProfessor = dados[5];
            List<Professor> professores = carregarProfessores();
            for (Professor prof : professores) {
              if (prof.getEmail().equalsIgnoreCase(emailProfessor)) {
                disciplina.setProfessor(prof);
                break;
              }
            }
          }
          disciplinas.add(disciplina);
        }
      }
    } catch (IOException e) {
    }
    return disciplinas;
  }

  public static List<Matricula> carregarMatriculas() {
    List<Matricula> matriculas = new ArrayList<>();
    List<Aluno> alunos = carregarAlunos();
    List<Disciplina> disciplinas = carregarDisciplinas();

    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_MATRICULAS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 3) {
          Aluno aluno = buscarAlunoPorEmail(dados[0], alunos);
          Disciplina disciplina = buscarDisciplinaPorNome(dados[1], disciplinas);
          LocalDate data = LocalDate.parse(dados[2]);

          if (aluno != null && disciplina != null) {
            Matricula matricula = new Matricula(disciplina, aluno, data);
            matriculas.add(matricula);
          }
        }
      }
    } catch (IOException e) {
    }
    return matriculas;
  }

  private static Curso buscarCursoPorNome(String nome) {
    return new Curso(new ArrayList<>(), nome, 180);
  }

  private static Disciplina buscarDisciplinaPorNome(String nome) {
    List<Disciplina> disciplinas = carregarDisciplinas();
    return buscarDisciplinaPorNome(nome, disciplinas);
  }

  private static Disciplina buscarDisciplinaPorNome(String nome, List<Disciplina> disciplinas) {
    for (Disciplina d : disciplinas) {
      if (d.getNome().equalsIgnoreCase(nome)) {
        return d;
      }
    }
    return null;
  }

  private static Aluno buscarAlunoPorEmail(String email, List<Aluno> alunos) {
    for (Aluno a : alunos) {
      if (a.getEmail().equalsIgnoreCase(email)) {
        return a;
      }
    }
    return null;
  }

  public static void reescreverDisciplinas(List<Disciplina> disciplinas) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_DISCIPLINAS))) {
      for (Disciplina disciplina : disciplinas) {
        String emailProfessor =
            disciplina.getProfessor() != null ? disciplina.getProfessor().getEmail() : "";
        writer.println(
            disciplina.getNome()
                + ";"
                + disciplina.getCargaHoraria()
                + ";"
                + disciplina.isObrigatoria()
                + ";"
                + disciplina.getStatus()
                + ";"
                + (disciplina.getAlunos() != null ? disciplina.getAlunos().size() : 0)
                + ";"
                + emailProfessor);
      }
    } catch (IOException e) {
      System.err.println("Erro ao reescrever disciplinas: " + e.getMessage());
    }
  }

  public static boolean verificarUsuarioExiste(String email, String tipoUsuario) {
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 3) {
          String emailArquivo = dados[1];

          if ("qualquer".equalsIgnoreCase(tipoUsuario)) {
            if (emailArquivo.equalsIgnoreCase(email)) {
              return true;
            }
          } else {
            String tipoArquivo = dados[0];
            if (tipoArquivo.equalsIgnoreCase(tipoUsuario) && emailArquivo.equalsIgnoreCase(email)) {
              return true;
            }
          }
        }
      }
    } catch (IOException e) {
    }
    return false;
  }

  public static boolean emailJaExiste(String email) {
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 3) {
          String emailArquivo = dados[1];
          if (emailArquivo.equalsIgnoreCase(email)) {
            return true;
          }
        }
      }
    } catch (IOException e) {
    }
    return false;
  }

  public static List<Usuario> carregarTodosUsuarios() {
    List<Usuario> usuarios = new ArrayList<>();
    usuarios.addAll(carregarAlunos());
    usuarios.addAll(carregarProfessores());
    usuarios.addAll(carregarSecretarias());
    return usuarios;
  }

  // deixar para testar na sala (limpa todos os dados)
  public static void limparTodosDados() {
    try {
      new FileWriter(ARQUIVO_USUARIOS).close();
      new FileWriter(ARQUIVO_DISCIPLINAS).close();
      new FileWriter(ARQUIVO_MATRICULAS).close();
      System.out.println("Todos os dados foram limpos.");
    } catch (IOException e) {
      System.err.println("Erro ao limpar dados: " + e.getMessage());
    }
  }
}
