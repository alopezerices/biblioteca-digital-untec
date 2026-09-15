package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.model.Usuario;
import java.sql.SQLException;
import javax.servlet.http.*;

public abstract class BaseServlet extends HttpServlet {
  protected Usuario user(HttpServletRequest r) {
    return (Usuario) r.getSession().getAttribute("usuario");
  }

  protected boolean admin(HttpServletRequest r) {
    return "ADMIN".equals(user(r).getRol());
  }

  protected void requireAdmin(HttpServletRequest r) {
    if (!admin(r)) throw new SecurityException("Acceso restringido al personal de biblioteca.");
  }

  protected long id(HttpServletRequest r, String name) {
    return Long.parseLong(r.getParameter(name));
  }

  protected void flash(HttpServletRequest r, String message) {
    r.getSession().setAttribute("mensaje", message);
  }

  protected void error(HttpServletRequest r, Exception e) {
    if (e instanceof SQLException) {
      getServletContext().log("Operación de datos rechazada", e);
      flash(r, "No se pudo guardar: revisa datos duplicados o registros relacionados.");
    } else
      flash(
          r,
          e instanceof NumberFormatException
              ? "Revisa los valores numéricos del formulario."
              : e.getMessage());
  }
}
