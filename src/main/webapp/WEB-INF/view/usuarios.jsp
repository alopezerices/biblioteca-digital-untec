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
<p class="eyebrow">COMUNIDAD UNTEC</p>
<h1>Usuarios de la biblioteca</h1>
<p>Registra estudiantes y consulta las cuentas habilitadas.</p>
</section>
<section class="panel">
<h2>Registrar estudiante</h2>
<form class="formgrid" action="${pageContext.request.contextPath}/usuarios" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<label>Nombre completo<input name="nombre" required maxlength="100">
</label>
<label>Correo electrónico<input type="email" name="email" required maxlength="150">
</label>
<label>Contraseña inicial<input name="password" type="password" required minlength="8" maxlength="100" autocomplete="new-password">
</label>
<button class="primary">Crear cuenta</button>
</form>
</section>
<div class="tablewrap">
<table>
<thead>
<tr>
<th>Nombre</th>
<th>Correo</th>
<th>Perfil</th>
</tr>
</thead>
<tbody>
<c:forEach items="${usuarios}" var="u">
<tr>
<td>
<c:out value="${u.nombre}"/>
</td>
<td>
<c:out value="${u.email}"/>
</td>
<td>
<c:out value="${u.rol}"/>
</td>
</tr>
</c:forEach>
</tbody>
</table>
</div>
</main>
<footer>Universidad UNTEC · Un espacio para descubrir y aprender.</footer>
</body>
</html>
