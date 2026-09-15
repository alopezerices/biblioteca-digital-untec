package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.dao.UsuarioDAO;
import cl.untec.biblioteca.model.Usuario;
import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;

/** Procesa credenciales y administra el ciclo de vida de la sesión. */
public class LoginServlet extends HttpServlet {
  protected void doGet(HttpServletRequest q, HttpServletResponse r) throws IOException {
    r.sendRedirect(q.getContextPath() + "/index.jsp");
  }

  protected void doPost(HttpServletRequest q, HttpServletResponse r)
      throws IOException, ServletException {
    if ("salir".equals(q.getParameter("accion"))) {
      q.getSession().invalidate();
      r.sendRedirect(q.getContextPath() + "/index.jsp");
      return;
    }
    String email = q.getParameter("email"), password = q.getParameter("password");
    try {
      Usuario u =
          email == null || password == null || email.length() > 150 || password.length() > 100
              ? null
              : new UsuarioDAO().autenticar(email, password);
      if (u == null) {
        q.setAttribute("error", "Correo o contraseña incorrectos.");
        q.getRequestDispatcher("/index.jsp").forward(q, r);
        return;
      }
      q.changeSessionId();
      q.getSession().setAttribute("usuario", u);
      r.sendRedirect(q.getContextPath() + "/libros");
    } catch (SQLException e) {
      throw new ServletException("No fue posible iniciar sesión", e);
    }
  }
}
