package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.dao.LibroDAO;
import cl.untec.biblioteca.view.Vistas;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/** Controlador del catálogo: consulta GET y modificaciones administrativas POST. */
public class LibroServlet extends BaseServlet {
  protected void doGet(HttpServletRequest q, HttpServletResponse r)
      throws IOException, ServletException {
    try {
      q.setAttribute("libros", new LibroDAO().listar(q.getParameter("q")));
      q.getRequestDispatcher(Vistas.LIBROS).forward(q, r);
    } catch (java.sql.SQLException e) {
      throw new ServletException(e);
    }
  }

  protected void doPost(HttpServletRequest q, HttpServletResponse r) throws IOException {
    try {
      requireAdmin(q);
      LibroDAO dao = new LibroDAO();
      if ("eliminar".equals(q.getParameter("accion"))) dao.eliminar(id(q, "id"));
      else if ("guardar".equals(q.getParameter("accion")))
        dao.guardar(
            id(q, "id"),
            q.getParameter("titulo"),
            q.getParameter("autor"),
            q.getParameter("isbn"),
            Integer.parseInt(q.getParameter("ejemplares")));
      else throw new IllegalArgumentException("Acción desconocida.");
      flash(q, "Catálogo actualizado correctamente.");
    } catch (SecurityException e) {
      r.sendError(403);
      return;
    } catch (Exception e) {
      error(q, e);
    }
    r.sendRedirect(q.getContextPath() + "/libros");
  }
}
