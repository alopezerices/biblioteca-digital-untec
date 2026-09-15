package cl.untec.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import cl.untec.biblioteca.config.*;
import cl.untec.biblioteca.dao.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;

class BibliotecaTest {
  final LibroDAO libros = new LibroDAO();
  final PrestamoDAO prestamos = new PrestamoDAO();
  final UsuarioDAO usuarios = new UsuarioDAO();

  @BeforeAll
  static void init() throws Exception {
    System.setProperty("biblioteca.db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    Bootstrap.initialize();
  }

  @BeforeEach
  void reset() throws Exception {
    try (Connection c = Database.getInstance().getConnection();
        Statement s = c.createStatement()) {
      s.executeUpdate("DELETE FROM prestamo");
      s.executeUpdate("DELETE FROM libro WHERE isbn LIKE 'TEST%'");
    }
  }

  long libro(int stock) throws Exception {
    libros.guardar(0, "Prueba", "Autor", "TEST-1", stock);
    return libros.listar("Prueba").get(0).getId();
  }

  @Test
  void autenticacion() throws Exception {
    assertEquals("ADMIN", usuarios.autenticar("admin@untec.test", "Biblioteca2026!").getRol());
    assertNull(usuarios.autenticar("admin@untec.test", "incorrecta"));
    assertNull(usuarios.autenticar("' OR 1=1 --", "incorrecta"));
  }

  @Test
  void registro() throws Exception {
    usuarios.crear("Nuevo", "nuevo@untec.test", "Clave2026!");
    assertEquals("ESTUDIANTE", usuarios.autenticar("nuevo@untec.test", "Clave2026!").getRol());
    assertThrows(
        IllegalArgumentException.class, () -> usuarios.crear("N", "correo invalido", "Clave2026!"));
    assertThrows(SQLException.class, () -> usuarios.crear("N", "nuevo@untec.test", "Clave2026!"));
  }

  @Test
  void crudLibros() throws Exception {
    long id = libro(2);
    libros.guardar(id, "Prueba editada", "Autor", "TEST-1", 4);
    assertEquals(4, libros.listar("editada").get(0).getDisponibles());
    libros.eliminar(id);
    assertTrue(libros.listar("editada").isEmpty());
  }

  @Test
  void crudPrestamos() throws Exception {
    long id = libro(1);
    prestamos.crear(2, id);
    long loan = prestamos.listar(2L).get(0).getId();
    assertEquals(0, libros.listar("Prueba").get(0).getDisponibles());
    prestamos.renovar(loan, LocalDate.now().plusDays(20));
    assertEquals(LocalDate.now().plusDays(20).toString(), prestamos.listar(2L).get(0).getVence());
    prestamos.devolver(loan, 2, false);
    assertEquals(1, libros.listar("Prueba").get(0).getDisponibles());
    prestamos.eliminar(loan);
    assertTrue(prestamos.listar(null).isEmpty());
  }

  @Test
  void integridadYPermisos() throws Exception {
    long id = libro(2);
    prestamos.crear(2, id);
    long loan = prestamos.listar(2L).get(0).getId();
    assertThrows(IllegalArgumentException.class, () -> prestamos.crear(2, id));
    assertThrows(IllegalArgumentException.class, () -> prestamos.devolver(loan, 3, false));
    assertThrows(IllegalArgumentException.class, () -> prestamos.eliminar(loan));
    assertThrows(SQLException.class, () -> libros.eliminar(id));
    prestamos.crear(3, id);
    assertThrows(
        IllegalArgumentException.class, () -> libros.guardar(id, "Prueba", "Autor", "TEST-1", 1));
  }

  @Test
  void rollback() throws Exception {
    long id = libro(1);
    assertThrows(SQLException.class, () -> prestamos.crear(99999, id));
    assertEquals(1, libros.listar("Prueba").get(0).getDisponibles());
  }

  @Test
  void concurrenciaUltimoEjemplar() throws Exception {
    long id = libro(1);
    ExecutorService pool = Executors.newFixedThreadPool(2);
    CountDownLatch start = new CountDownLatch(1);
    try {
      Future<Boolean> a =
          pool.submit(
              () -> {
                start.await();
                try {
                  prestamos.crear(2, id);
                  return true;
                } catch (IllegalArgumentException e) {
                  return false;
                }
              });
      Future<Boolean> b =
          pool.submit(
              () -> {
                start.await();
                try {
                  prestamos.crear(3, id);
                  return true;
                } catch (IllegalArgumentException e) {
                  return false;
                }
              });
      start.countDown();
      assertNotEquals(a.get(10, TimeUnit.SECONDS), b.get(10, TimeUnit.SECONDS));
      assertEquals(1, prestamos.listar(null).size());
    } finally {
      pool.shutdownNow();
    }
  }
}
