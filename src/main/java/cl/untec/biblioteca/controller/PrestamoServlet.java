package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.dao.*;
import cl.untec.biblioteca.view.Vistas;
import java.io.*;
import java.time.LocalDate;
import javax.servlet.*;
import javax.servlet.http.*;

/** Controlador de solicitudes, renovaciones, devoluciones e historial. */
public class PrestamoServlet extends BaseServlet {
  protected void doGet(HttpServletRequest q, HttpServletResponse r)
      throws IOException, ServletException {
    try {
      q.setAttribute("prestamos", new PrestamoDAO().listar(admin(q) ? null : user(q).getId()));
      q.setAttribute("usuarios", new UsuarioDAO().listar());
      q.setAttribute("libros", new LibroDAO().listar(null));
      q.getRequestDispatcher(Vistas.PRESTAMOS).forward(q, r);
    } catch (java.sql.SQLException e) {
      throw new ServletException(e);
    }
  }

  protected void doPost(HttpServletRequest q, HttpServletResponse r) throws IOException {
    try {
      PrestamoDAO dao = new PrestamoDAO();
      String a = q.getParameter("accion");
      if ("crear".equals(a)) {
        long uid = admin(q) ? id(q, "usuarioId") : user(q).getId();
        dao.crear(uid, id(q, "libroId"));
      } else if ("devolver".equals(a)) dao.devolver(id(q, "id"), user(q).getId(), admin(q));
      else if ("renovar".equals(a)) {
        requireAdmin(q);
        dao.renovar(id(q, "id"), LocalDate.parse(q.getParameter("vence")));
      } else if ("eliminar".equals(a)) {
        requireAdmin(q);
        dao.eliminar(id(q, "id"));
      } else throw new IllegalArgumentException("Acción desconocida.");
      flash(q, "Operación de préstamo realizada correctamente.");
    } catch (SecurityException e) {
      r.sendError(403);
      return;
    } catch (java.time.DateTimeException e) {
      flash(q, "Introduce una fecha válida.");
    } catch (Exception e) {
      error(q, e);
    }
    r.sendRedirect(q.getContextPath() + "/prestamos");
  }
}
