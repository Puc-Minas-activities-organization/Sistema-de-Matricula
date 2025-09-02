package com.sm;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SistemaArquivos {

  public static boolean atualizarDisciplinasDoCurso(
      String nomeCurso, List<String> obrigatorias, List<String> optativas) {
    List<Curso> cursos = carregarCursos();
    boolean encontrado = false;
    for (Curso curso : cursos) {
      if (curso.getNome().equalsIgnoreCase(nomeCurso)) {
        curso.setDisciplinasObrigatorias(obrigatorias);
        curso.setDisciplinasOptativas(optativas);
        encontrado = true;
        break;
      }
    }
    if (!encontrado) return false;
    try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_CURSOS))) {
      for (Curso curso : cursos) {
        writer.println(
            curso.getNome()
                + ";"
                + curso.getCreditos()
                + ";"
                + String.join(",", curso.getDisciplinasObrigatorias())
                + ";"
                + String.join(",", curso.getDisciplinasOptativas()));
      }
    } catch (IOException e) {
      System.err.println("Erro ao atualizar curso: " + e.getMessage());
      return false;
    }
    return true;
  }

  // reescreve todas as matrículas
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
  private static final String ARQUIVO_CURSOS =
      "code/sistema-de-matricula/src/main/java/com/sm/resources/cursos.txt";

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
          Professor professor = new Professor(new ArrayList<>(), email, senha);
          professores.add(professor);
        }
      }
    } catch (IOException e) {
    }
    // Após carregar professores, sincronizar com disciplinas para popular suas listas
    List<Disciplina> disciplinas = carregarDisciplinas();
    for (Disciplina d : disciplinas) {
      if (d.getProfessor() != null) {
        Professor prof = null;
        for (Professor p : professores) {
          if (p.getEmail().equalsIgnoreCase(d.getProfessor().getEmail())) {
            prof = p;
            break;
          }
        }
        if (prof != null) {
          d.setProfessor(prof); // garantir mesma instância
          boolean existe = false;
          for (Disciplina dp : prof.getDisciplinas()) {
            if (dp.getNome().equalsIgnoreCase(d.getNome())) {
              existe = true;
              break;
            }
          }
          if (!existe) prof.getDisciplinas().add(d);
        }
      }
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
          // professor será associado após carregarmos todos para evitar recursão/duplicação
          if (dados.length >= 6 && !dados[5].isEmpty()) {
            // Temporariamente armazenamos o email dentro do nome usando marcador (hack leve)
            // Depois resolveremos em sincronizarProfessoresComDisciplinas
            disciplina.setProfessor(
                new Professor(new ArrayList<>(), dados[5], "__SYNC_PLACEHOLDER__"));
          }
          disciplinas.add(disciplina);
        }
      }
    } catch (IOException e) {
    }
    sincronizarProfessoresComDisciplinas(disciplinas);
    return disciplinas;
  }

  private static void sincronizarProfessoresComDisciplinas(List<Disciplina> disciplinas) {
    List<Professor> professores = carregarProfessoresSemDisciplinas();
    for (Disciplina d : disciplinas) {
      if (d.getProfessor() != null && "__SYNC_PLACEHOLDER__".equals(d.getProfessor().getSenha())) {
        String email = d.getProfessor().getEmail();
        Professor real = null;
        for (Professor p : professores) {
          if (p.getEmail().equalsIgnoreCase(email)) {
            real = p;
            break;
          }
        }
        if (real != null) {
          d.setProfessor(real);
          if (real.getDisciplinas() == null) real.setDisciplinas(new ArrayList<>());
          boolean existe = false;
          for (Disciplina dp : real.getDisciplinas()) {
            if (dp.getNome().equalsIgnoreCase(d.getNome())) {
              existe = true;
              break;
            }
          }
          if (!existe) real.getDisciplinas().add(d);
        } else {
          d.setProfessor(null); // professor não existe mais
        }
      }
    }
  }

  // Carrega professores sem montar lista de disciplinas (usado para sincronização interna)
  private static List<Professor> carregarProfessoresSemDisciplinas() {
    List<Professor> professores = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 3 && "PROFESSOR".equals(dados[0])) {
          String email = dados[1];
          String senha = dados[2];
          professores.add(new Professor(new ArrayList<>(), email, senha));
        }
      }
    } catch (IOException e) {
    }
    return professores;
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

  public static void atualizarDisciplinasDoProfessor(
      String emailProfessor, List<String> nomesDisciplinas) {
    List<String> linhas = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] dados = linha.split(";");
        if (dados.length >= 3
            && "PROFESSOR".equals(dados[0])
            && dados[1].equalsIgnoreCase(emailProfessor)) {
          String disciplinasStr = String.join(",", nomesDisciplinas);
          linhas.add("PROFESSOR;" + dados[1] + ";" + dados[2] + ";" + disciplinasStr);
        } else {
          linhas.add(linha);
        }
      }
    } catch (IOException e) {
      System.err.println("Erro ao ler usuários: " + e.getMessage());
    }
    try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_USUARIOS))) {
      for (String l : linhas) {
        writer.println(l);
      }
    } catch (IOException e) {
      System.err.println("Erro ao atualizar disciplinas do professor: " + e.getMessage());
    }
  }

  // deixar para testar na sala (limpa todos os dados)
  public static void limparTodosDados() {
    try {
      new FileWriter(ARQUIVO_USUARIOS).close();
      new FileWriter(ARQUIVO_DISCIPLINAS).close();
      new FileWriter(ARQUIVO_MATRICULAS).close();
      new FileWriter(ARQUIVO_CURSOS).close();
      System.out.println("Todos os dados foram limpos.");
    } catch (IOException e) {
      System.err.println("Erro ao limpar dados: " + e.getMessage());
    }
  }

  public static void salvarCurso(Curso curso) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_CURSOS, true))) {
      writer.println(
          curso.getNome()
              + ";"
              + curso.getCreditos()
              + ";"
              + String.join(",", curso.getDisciplinasObrigatorias())
              + ";"
              + String.join(",", curso.getDisciplinasOptativas()));
    } catch (IOException e) {
      System.err.println("Erro ao salvar curso: " + e.getMessage());
    }
  }

  public static List<Curso> carregarCursos() {
    List<Curso> cursos = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_CURSOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        if (linha.trim().isEmpty()) continue;
        String[] dados = linha.split(";");
        if (dados.length < 2) continue; 
        String nome = dados[0].trim();
        int creditos = Integer.parseInt(dados[1].trim());
        List<String> disciplinasObrigatorias = new ArrayList<>();
        List<String> disciplinasOptativas = new ArrayList<>();
        if (dados.length > 2 && !dados[2].trim().isEmpty()) {
          String[] obrigatorias = dados[2].split(",");
          for (String disciplina : obrigatorias) {
            if (!disciplina.trim().isEmpty()) {
              disciplinasObrigatorias.add(disciplina.trim());
            }
          }
        }
        if (dados.length > 3 && !dados[3].trim().isEmpty()) {
          String[] optativas = dados[3].split(",");
          for (String disciplina : optativas) {
            if (!disciplina.trim().isEmpty()) {
              disciplinasOptativas.add(disciplina.trim());
            }
          }
        }
        cursos.add(new Curso(disciplinasObrigatorias, disciplinasOptativas, nome, creditos));
      }
    } catch (IOException e) {
    }
    return cursos;
  }

  public static Curso buscarCursoPorNome(String nome) {
    List<Curso> cursos = carregarCursos();
    for (Curso curso : cursos) {
      if (curso.getNome().equalsIgnoreCase(nome)) {
        return curso;
      }
    }
    return null;
  }

  public static void inicializarDadosPadrao() {
    List<Curso> cursos = carregarCursos();
    if (cursos.isEmpty()) {
      List<String> disciplinasEngSoftware = new ArrayList<>();
      disciplinasEngSoftware.add("Algoritmos e Estruturas de Dados");
      disciplinasEngSoftware.add("Engenharia de Software");
      disciplinasEngSoftware.add("Banco de Dados");
      disciplinasEngSoftware.add("Programação Orientada a Objetos");

      List<String> optativasEngSoftware = new ArrayList<>();
      optativasEngSoftware.add("Inteligência Artificial");
      optativasEngSoftware.add("Desenvolvimento Web");

      Curso engenhariaSoftware =
          new Curso(disciplinasEngSoftware, optativasEngSoftware, "Engenharia de Software", 240);
      salvarCurso(engenhariaSoftware);

      List<String> disciplinasCiencia = new ArrayList<>();
      disciplinasCiencia.add("Cálculo I");
      disciplinasCiencia.add("Programação I");
      disciplinasCiencia.add("Estruturas Discretas");
      disciplinasCiencia.add("Sistemas Operacionais");

      List<String> optativasCiencia = new ArrayList<>();
      optativasCiencia.add("Redes de Computadores");
      optativasCiencia.add("Computação Gráfica");

      Curso cienciaComputacao =
          new Curso(disciplinasCiencia, optativasCiencia, "Ciencia da Computacao", 240);
      salvarCurso(cienciaComputacao);

      System.out.println("Cursos padrão inicializados com sucesso!");
    }
  }
}
