package cl.untec.biblioteca.dao;

import cl.untec.biblioteca.config.Database;
import cl.untec.biblioteca.model.Usuario;
import cl.untec.biblioteca.service.Passwords;
import java.sql.*;
import java.util.*;

/** Persistencia y autenticación de usuarios con contraseñas protegidas. */
public class UsuarioDAO {
  private Usuario map(ResultSet r) throws SQLException {
    Usuario u = new Usuario();
    u.setId(r.getLong("id"));
    u.setNombre(r.getString("nombre"));
    u.setEmail(r.getString("email"));
    u.setRol(r.getString("rol"));
    return u;
  }

  public Usuario autenticar(String email, String password) throws SQLException {
    try (Connection c = Database.getInstance().getConnection();
        PreparedStatement s = c.prepareStatement("SELECT * FROM usuario WHERE email=?")) {
      s.setString(1, email.toLowerCase(Locale.ROOT).trim());
      try (ResultSet r = s.executeQuery()) {
        if (r.next() && Passwords.verify(password, r.getString("password_hash"))) return map(r);
        return null;
      }
    }
  }

  public List<Usuario> listar() throws SQLException {
    List<Usuario> list = new ArrayList<>();
    try (Connection c = Database.getInstance().getConnection();
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("SELECT * FROM usuario ORDER BY nombre")) {
      while (r.next()) list.add(map(r));
    }
    return list;
  }

  public void crear(String nombre, String email, String password) throws SQLException {
    if (nombre == null
        || nombre.isBlank()
        || nombre.length() > 100
        || email == null
        || !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")
        || email.length() > 150
        || password == null
        || password.length() < 8
        || password.length() > 100)
      throw new IllegalArgumentException(
          "Revisa nombre, correo y contraseña (8 a 100 caracteres).");
    try (Connection c = Database.getInstance().getConnection();
        PreparedStatement s =
            c.prepareStatement(
                "INSERT INTO usuario(nombre,email,password_hash,rol) VALUES(?,?,?,'ESTUDIANTE')")) {
      s.setString(1, nombre.trim());
      s.setString(2, email.toLowerCase(Locale.ROOT).trim());
      s.setString(3, Passwords.hash(password));
      s.executeUpdate();
    }
  }
}
