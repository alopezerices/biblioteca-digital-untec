package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.dao.UsuarioDAO;
import cl.untec.biblioteca.view.Vistas;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/** Controlador administrativo para registrar y consultar estudiantes. */
public class UsuarioServlet extends BaseServlet {
  protected void doGet(HttpServletRequest q, HttpServletResponse r)
      throws IOException, ServletException {
    if (!admin(q)) {
      r.sendError(403);
      return;
    }
    try {
      q.setAttribute("usuarios", new UsuarioDAO().listar());
      q.getRequestDispatcher(Vistas.USUARIOS).forward(q, r);
    } catch (java.sql.SQLException e) {
      throw new ServletException(e);
    }
  }

  protected void doPost(HttpServletRequest q, HttpServletResponse r) throws IOException {
    if (!admin(q)) {
      r.sendError(403);
      return;
    }
    try {
      new UsuarioDAO()
          .crear(q.getParameter("nombre"), q.getParameter("email"), q.getParameter("password"));
      flash(q, "Estudiante registrado correctamente.");
    } catch (Exception e) {
      error(q, e);
    }
    r.sendRedirect(q.getContextPath() + "/usuarios");
  }
}
