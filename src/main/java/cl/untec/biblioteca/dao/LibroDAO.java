package cl.untec.biblioteca.dao;

import cl.untec.biblioteca.config.Database;
import cl.untec.biblioteca.model.Libro;
import java.sql.*;
import java.util.*;

/** Acceso JDBC al catálogo y validación transaccional de ejemplares. */
public class LibroDAO {
  public List<Libro> listar(String query) throws SQLException {
    List<Libro> result = new ArrayList<>();
    String q = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
    String sql =
        "SELECT l.*,l.ejemplares-(SELECT COUNT(*) FROM prestamo p WHERE p.libro_id=l.id AND"
            + " p.devolucion IS NULL) disponibles FROM libro l WHERE LOWER(l.titulo) LIKE ? OR"
            + " LOWER(l.autor) LIKE ? ORDER BY l.titulo";
    try (Connection c = Database.getInstance().getConnection();
        PreparedStatement s = c.prepareStatement(sql)) {
      s.setString(1, "%" + q + "%");
      s.setString(2, "%" + q + "%");
      try (ResultSet rs = s.executeQuery()) {
        while (rs.next()) {
          Libro l = new Libro();
          l.setId(rs.getLong("id"));
          l.setTitulo(rs.getString("titulo"));
          l.setAutor(rs.getString("autor"));
          l.setIsbn(rs.getString("isbn"));
          l.setEjemplares(rs.getInt("ejemplares"));
          l.setDisponibles(rs.getInt("disponibles"));
          result.add(l);
        }
      }
    }
    return result;
  }

  public void guardar(long id, String titulo, String autor, String isbn, int ejemplares)
      throws SQLException {
    if (titulo == null
        || titulo.isBlank()
        || titulo.length() > 160
        || autor == null
        || autor.isBlank()
        || autor.length() > 120
        || isbn == null
        || isbn.isBlank()
        || isbn.length() > 30
        || ejemplares < 1
        || ejemplares > 1000)
      throw new IllegalArgumentException("Completa los campos y usa entre 1 y 1000 ejemplares.");
    try (Connection c = Database.getInstance().getConnection()) {
      c.setAutoCommit(false);
      try {
        if (id > 0) {
          try (PreparedStatement lock =
              c.prepareStatement("SELECT id FROM libro WHERE id=? FOR UPDATE")) {
            lock.setLong(1, id);
            try (ResultSet r = lock.executeQuery()) {
              if (!r.next()) throw new IllegalArgumentException("El libro no existe.");
            }
          }
          try (PreparedStatement s =
              c.prepareStatement(
                  "SELECT COUNT(*) FROM prestamo WHERE libro_id=? AND devolucion IS NULL")) {
            s.setLong(1, id);
            try (ResultSet rs = s.executeQuery()) {
              rs.next();
              if (ejemplares < rs.getInt(1))
                throw new IllegalArgumentException(
                    "No puedes reducir los ejemplares por debajo de los préstamos activos.");
            }
          }
        }
        try (PreparedStatement s =
            c.prepareStatement(
                id == 0
                    ? "INSERT INTO libro(titulo,autor,isbn,ejemplares) VALUES(?,?,?,?)"
                    : "UPDATE libro SET titulo=?,autor=?,isbn=?,ejemplares=? WHERE id=?")) {
          s.setString(1, titulo.trim());
          s.setString(2, autor.trim());
          s.setString(3, isbn.trim());
          s.setInt(4, ejemplares);
          if (id > 0) s.setLong(5, id);
          s.executeUpdate();
        }
        c.commit();
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    }
  }

  public void eliminar(long id) throws SQLException {
    try (Connection c = Database.getInstance().getConnection();
        PreparedStatement s = c.prepareStatement("DELETE FROM libro WHERE id=?")) {
      s.setLong(1, id);
      if (s.executeUpdate() != 1) throw new IllegalArgumentException("El libro no existe.");
    }
  }
}
