package cl.untec.biblioteca.config;

import java.sql.*;
import org.h2.jdbcx.JdbcDataSource;

/** Singleton de DataSource. Cada operación obtiene y cierra su propia conexión. */
public final class Database {
  private static final Database INSTANCE = new Database();
  private final JdbcDataSource source = new JdbcDataSource();

  private Database() {
    String base = System.getProperty("catalina.base", System.getProperty("user.home"));
    source.setURL(
        System.getProperty(
            "biblioteca.db.url", "jdbc:h2:file:" + base + "/data/untec;DB_CLOSE_ON_EXIT=FALSE"));
    source.setUser("sa");
    source.setPassword(System.getProperty("biblioteca.db.password", ""));
  }

  public static Database getInstance() {
    return INSTANCE;
  }

  public Connection getConnection() throws SQLException {
    return source.getConnection();
  }
}
