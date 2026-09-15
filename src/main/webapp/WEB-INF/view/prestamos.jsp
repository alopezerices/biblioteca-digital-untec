<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Biblioteca Digital UNTEC</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<header>
<a class="brand" href="${pageContext.request.contextPath}/libros">
<span class="mark">U</span>
<span>UNTEC<small>Biblioteca digital</small>
</span>
</a>
<nav>
<a href="${pageContext.request.contextPath}/libros">Catálogo</a>
<a href="${pageContext.request.contextPath}/prestamos">Préstamos</a>
<c:if test="${sessionScope.usuario.rol eq 'ADMIN'}">
<a href="${pageContext.request.contextPath}/usuarios">Usuarios</a>
</c:if>
</nav>
<div class="identity">
<span>
<c:out value="${sessionScope.usuario.nombre}"/>
</span>
<form action="${pageContext.request.contextPath}/login" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="salir">
<button class="link">Cerrar sesión</button>
</form>
</div>
</header>
<main>
<c:if test="${not empty sessionScope.mensaje}">
<div class="notice" role="status">
<c:out value="${sessionScope.mensaje}"/>
</div>
<c:remove var="mensaje" scope="session"/>
</c:if>
<section class="intro">
<p class="eyebrow">TUS LECTURAS, ORGANIZADAS</p>
<h1>Préstamos e historial</h1>
<p>Revisa las fechas de entrega y registra las devoluciones de tus libros.</p>
</section>
<c:if test="${sessionScope.usuario.rol eq 'ADMIN'}">
<section class="panel">
<h2>Registrar un préstamo</h2>
<form class="formgrid" action="${pageContext.request.contextPath}/prestamos" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="crear">
<label>Usuario<select name="usuarioId" required>
<c:forEach items="${usuarios}" var="u">
<option value="${u.id}">
<c:out value="${u.nombre}"/>
</option>
</c:forEach>
</select>
</label>
<label>Libro<select name="libroId" required>
<c:forEach items="${libros}" var="l">
<c:if test="${l.disponibles gt 0}">
<option value="${l.id}">
<c:out value="${l.titulo}"/> (${l.disponibles})</option>
</c:if>
</c:forEach>
</select>
</label>
<button class="primary">Registrar préstamo</button>
</form>
</section>
</c:if>
<c:if test="${empty prestamos}">
<div class="empty">
<h2>Aún no hay préstamos</h2>
<p>Tu próxima lectura te espera en el catálogo.</p>
<a href="${pageContext.request.contextPath}/libros">Explorar libros →</a>
</div>
</c:if>
<div class="loans">
<c:forEach items="${prestamos}" var="p">
<article class="loan">
<div>
<span class="eyebrow">PRÉSTAMO #${p.id}</span>
<h2>
<c:out value="${p.libro}"/>
</h2>
<p>
<c:out value="${p.usuario}"/>
</p>
<div class="dates">
<span>Solicitud <b>${p.fecha}</b>
</span>
<span>Vencimiento <b>${p.vence}</b>
</span>
</div>
</div>
<div class="loanactions">
<c:choose>
<c:when test="${empty p.devolucion}">
<span class="badge">Préstamo activo</span>
<form action="${pageContext.request.contextPath}/prestamos" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="devolver">
<input type="hidden" name="id" value="${p.id}">
<button class="primary">Registrar devolución</button>
</form>
<c:if test="${sessionScope.usuario.rol eq 'ADMIN'}">
<details>
<summary>Cambiar vencimiento</summary>
<form action="${pageContext.request.contextPath}/prestamos" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="renovar">
<input type="hidden" name="id" value="${p.id}">
<label>Nueva fecha<input type="date" name="vence" value="${p.vence}" required>
</label>
<button>Guardar fecha</button>
</form>
</details>
</c:if>
</c:when>
<c:otherwise>
<span class="badge returned">Devuelto el ${p.devolucion}</span>
<c:if test="${sessionScope.usuario.rol eq 'ADMIN'}">
<details>
<summary>Eliminar del historial</summary>
<form class="danger" action="${pageContext.request.contextPath}/prestamos" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="eliminar">
<input type="hidden" name="id" value="${p.id}">
<label>
<input type="checkbox" required> Confirmo eliminar este registro devuelto</label>
<button>Eliminar registro</button>
</form>
</details>
</c:if>
</c:otherwise>
</c:choose>
</div>
</article>
</c:forEach>
</div>
</main>
<footer>Universidad UNTEC · Un espacio para descubrir y aprender.</footer>
</body>
</html>
