package cl.untec.biblioteca.config;

import cl.untec.biblioteca.service.Passwords;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import javax.servlet.*;

public class Bootstrap implements ServletContextListener {
  public void contextInitialized(ServletContextEvent event) {
    try {
      initialize();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo inicializar la biblioteca", e);
    }
  }

  public static synchronized void initialize() throws Exception {
    try (Connection c = Database.getInstance().getConnection();
        InputStream in = Bootstrap.class.getResourceAsStream("/schema.sql")) {
      String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      for (String line : sql.split(";")) {
        if (!line.isBlank())
          try (Statement s = c.createStatement()) {
            s.execute(line);
          }
      }
      try (Statement s = c.createStatement();
          ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM usuario")) {
        rs.next();
        if (rs.getInt(1) > 0) return;
      }
      c.setAutoCommit(false);
      try {
        try (PreparedStatement s =
            c.prepareStatement(
                "INSERT INTO usuario(nombre,email,password_hash,rol) VALUES(?,?,?,?)")) {
          String[][] users = {
            {"Elena Torres", "admin@untec.test", "Biblioteca2026!", "ADMIN"},
            {"Ana Pérez", "ana@untec.test", "Lectura2026!", "ESTUDIANTE"},
            {"Diego Ríos", "diego@untec.test", "Lectura2026!", "ESTUDIANTE"}
          };
          for (String[] u : users) {
            s.setString(1, u[0]);
            s.setString(2, u[1]);
            s.setString(3, Passwords.hash(u[2]));
            s.setString(4, u[3]);
            s.executeUpdate();
          }
        }
        try (PreparedStatement s =
            c.prepareStatement("INSERT INTO libro(titulo,autor,isbn,ejemplares) VALUES(?,?,?,?)")) {
          String[][] books = {
            {"Cien años de soledad", "Gabriel García Márquez", "9780307474728"},
            {"La casa de los espíritus", "Isabel Allende", "9788401352836"},
            {"Clean Code", "Robert C. Martin", "9780132350884"},
            {"El principito", "Antoine de Saint-Exupéry", "9780156012195"},
            {"Introducción a los algoritmos", "Thomas H. Cormen", "9780262046305"},
            {"Ficciones", "Jorge Luis Borges", "9780307950925"}
          };
          for (String[] b : books) {
            s.setString(1, b[0]);
            s.setString(2, b[1]);
            s.setString(3, b[2]);
            s.setInt(4, 3);
            s.executeUpdate();
          }
        }
        c.commit();
      } catch (SQLException e) {
        c.rollback();
        throw e;
      }
    }
  }
}
