package cl.untec.biblioteca.dao;

import cl.untec.biblioteca.config.Database;
import cl.untec.biblioteca.model.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Operaciones de préstamos con integridad referencial y control de disponibilidad. */
public class PrestamoDAO {
  public List<Prestamo> listar(Long usuario) throws SQLException {
    List<Prestamo> list = new ArrayList<>();
    String sql =
        "SELECT p.*,u.nombre,l.titulo FROM prestamo p JOIN usuario u ON u.id=p.usuario_id JOIN"
            + " libro l ON l.id=p.libro_id"
            + (usuario == null ? "" : " WHERE p.usuario_id=?")
            + " ORDER BY p.id DESC";
    try (Connection c = Database.getInstance().getConnection();
        PreparedStatement s = c.prepareStatement(sql)) {
      if (usuario != null) s.setLong(1, usuario);
      try (ResultSet r = s.executeQuery()) {
        while (r.next()) {
          Prestamo p = new Prestamo();
          p.setId(r.getLong("id"));
          p.setUsuarioId(r.getLong("usuario_id"));
          p.setLibroId(r.getLong("libro_id"));
          p.setUsuario(r.getString("nombre"));
          p.setLibro(r.getString("titulo"));
          p.setFecha(r.getString("fecha"));
          p.setVence(r.getString("vence"));
          p.setDevolucion(r.getString("devolucion"));
          list.add(p);
        }
      }
    }
    return list;
  }

  /** Bloquea el libro antes de contar reservas para evitar sobrepréstamos. */
  public void crear(long usuario, long libro) throws SQLException {
    try (Connection c = Database.getInstance().getConnection()) {
      c.setAutoCommit(false);
      try {
        int stock;
        try (PreparedStatement s =
            c.prepareStatement("SELECT ejemplares FROM libro WHERE id=? FOR UPDATE")) {
          s.setLong(1, libro);
          try (ResultSet r = s.executeQuery()) {
            if (!r.next()) throw new IllegalArgumentException("El libro no existe.");
            stock = r.getInt(1);
          }
        }
        try (PreparedStatement s =
            c.prepareStatement(
                "SELECT COUNT(*) FROM prestamo WHERE libro_id=? AND devolucion IS NULL")) {
          s.setLong(1, libro);
          try (ResultSet r = s.executeQuery()) {
            r.next();
            if (r.getInt(1) >= stock)
              throw new IllegalArgumentException("No quedan ejemplares disponibles.");
          }
        }
        try (PreparedStatement s =
            c.prepareStatement(
                "SELECT COUNT(*) FROM prestamo WHERE usuario_id=? AND libro_id=? AND devolucion IS"
                    + " NULL")) {
          s.setLong(1, usuario);
          s.setLong(2, libro);
          try (ResultSet r = s.executeQuery()) {
            r.next();
            if (r.getInt(1) > 0)
              throw new IllegalArgumentException("Ya tienes un préstamo activo de este libro.");
          }
        }
        try (PreparedStatement s =
            c.prepareStatement(
                "INSERT INTO prestamo(usuario_id,libro_id,fecha,vence) VALUES(?,?,?,?)")) {
          s.setLong(1, usuario);
          s.setLong(2, libro);
          s.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
          s.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusDays(14)));
          s.executeUpdate();
        }
        c.commit();
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    }
  }

  public void devolver(long id, long usuario, boolean admin) throws SQLException {
    try (Connection c = Database.getInstance().getConnection();
        PreparedStatement s =
            c.prepareStatement(
                "UPDATE prestamo SET devolucion=CURRENT_DATE WHERE id=? AND devolucion IS NULL"
                    + (admin ? "" : " AND usuario_id=?"))) {
      s.setLong(1, id);
      if (!admin) s.setLong(2, usuario);
      if (s.executeUpdate() != 1)
        throw new IllegalArgumentException("Préstamo no disponible para devolución.");
    }
  }

  public void renovar(long id, LocalDate vence) throws SQLException {
    if (vence == null
        || vence.isBefore(LocalDate.now())
        || vence.isAfter(LocalDate.now().plusDays(90)))
      throw new IllegalArgumentException(
          "El vencimiento debe estar entre hoy y los próximos 90 días.");
    try (Connection c = Database.getInstance().getConnection();
        PreparedStatement s =
            c.prepareStatement("UPDATE prestamo SET vence=? WHERE id=? AND devolucion IS NULL")) {
      s.setDate(1, java.sql.Date.valueOf(vence));
      s.setLong(2, id);
      if (s.executeUpdate() != 1)
        throw new IllegalArgumentException("Solo se renuevan préstamos activos.");
    }
  }

  public void eliminar(long id) throws SQLException {
    try (Connection c = Database.getInstance().getConnection();
        PreparedStatement s =
            c.prepareStatement("DELETE FROM prestamo WHERE id=? AND devolucion IS NOT NULL")) {
      s.setLong(1, id);
      if (s.executeUpdate() != 1)
        throw new IllegalArgumentException("Devuelve el libro antes de eliminar el préstamo.");
    }
  }
}
