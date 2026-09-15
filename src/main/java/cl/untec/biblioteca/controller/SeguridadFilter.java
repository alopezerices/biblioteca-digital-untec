package cl.untec.biblioteca.controller;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.*;
import javax.servlet.http.*;

/** Protege las rutas privadas y valida el token de los formularios POST. */
public class SeguridadFilter implements Filter {
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest q = (HttpServletRequest) request;
    HttpServletResponse r = (HttpServletResponse) response;
    q.setCharacterEncoding("UTF-8");
    r.setCharacterEncoding("UTF-8");
    r.setHeader("X-Content-Type-Options", "nosniff");
    r.setHeader("X-Frame-Options", "DENY");
    r.setHeader("Cache-Control", "no-store");
    r.setHeader(
        "Content-Security-Policy",
        "default-src 'self'; style-src 'self'; img-src 'self'; form-action 'self'; frame-ancestors"
            + " 'none'");
    String path = q.getServletPath();
    if (path.startsWith("/assets/")) {
      chain.doFilter(q, r);
      return;
    }
    HttpSession session = q.getSession();
    if (session.getAttribute("csrf") == null)
      session.setAttribute("csrf", UUID.randomUUID().toString());
    if ("POST".equals(q.getMethod())
        && !session.getAttribute("csrf").equals(q.getParameter("csrf"))) {
      r.sendError(403, "Formulario expirado. Recarga la página.");
      return;
    }
    boolean publico = path.equals("/index.jsp") || path.equals("/login") || path.equals("/");
    if (!publico && session.getAttribute("usuario") == null) {
      r.sendRedirect(q.getContextPath() + "/index.jsp");
      return;
    }
    chain.doFilter(q, r);
  }
}
