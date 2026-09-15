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
<p class="eyebrow">EXPLORA Y DESCUBRE</p>
<h1>Catálogo de libros</h1>
<p>Encuentra tu siguiente lectura. Consulta la disponibilidad y solicita un ejemplar.</p>
</section>
<form class="search" action="${pageContext.request.contextPath}/libros" method="get">
<input aria-label="Buscar por título o autor" name="q" placeholder="Busca por título o autor" value="<c:out value='${param.q}'/>">
<button class="primary">Buscar</button>
<a href="${pageContext.request.contextPath}/libros">Ver todos</a>
</form>
<div class="sectionline">
<h2>Lecturas disponibles</h2>
<span>${libros.size()} títulos en esta selección</span>
</div>
<c:if test="${empty libros}">
<div class="empty">No encontramos libros con ese criterio. Prueba otra búsqueda.</div>
</c:if>
<div class="grid">
<c:forEach items="${libros}" var="l" varStatus="i">
<article class="book">
<div class="cover tone${i.index % 4}">
<span>UNTEC / COLECCIÓN</span>
<strong>
<c:out value="${l.titulo}"/>
</strong>
<em>
<c:out value="${l.autor}"/>
</em>
</div>
<div class="bookbody">
<span class="badge">${l.disponibles} de ${l.ejemplares} disponibles</span>
<h3>
<c:out value="${l.titulo}"/>
</h3>
<p>
<c:out value="${l.autor}"/>
</p>
<small>ISBN <c:out value="${l.isbn}"/>
</small>
<c:if test="${sessionScope.usuario.rol ne 'ADMIN'}">
<form action="${pageContext.request.contextPath}/prestamos" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="crear">
<input type="hidden" name="libroId" value="${l.id}">
<c:choose>
<c:when test="${l.disponibles gt 0}">
<button class="primary full">Solicitar préstamo</button>
</c:when>
<c:otherwise>
<button disabled class="full">Sin ejemplares</button>
</c:otherwise>
</c:choose>
</form>
</c:if>
<c:if test="${sessionScope.usuario.rol eq 'ADMIN'}">
<details>
<summary>Editar libro</summary>
<form action="${pageContext.request.contextPath}/libros" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="guardar">
<input type="hidden" name="id" value="${l.id}">
<label>Título<input name="titulo" value="<c:out value='${l.titulo}'/>" required maxlength="160">
</label>
<label>Autor<input name="autor" value="<c:out value='${l.autor}'/>" required maxlength="120">
</label>
<label>ISBN<input name="isbn" value="<c:out value='${l.isbn}'/>" required maxlength="30">
</label>
<label>Ejemplares<input name="ejemplares" type="number" min="1" max="1000" value="${l.ejemplares}" required>
</label>
<button class="primary">Guardar cambios</button>
</form>
<form class="danger" action="${pageContext.request.contextPath}/libros" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="eliminar">
<input type="hidden" name="id" value="${l.id}">
<label>
<input type="checkbox" required> Confirmo eliminar este libro sin historial asociado</label>
<button>Eliminar libro</button>
</form>
</details>
</c:if>
</div>
</article>
</c:forEach>
</div>
<c:if test="${sessionScope.usuario.rol eq 'ADMIN'}">
<section class="panel">
<h2>Agregar un libro</h2>
<form class="formgrid" action="${pageContext.request.contextPath}/libros" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<input type="hidden" name="accion" value="guardar">
<input type="hidden" name="id" value="0">
<label>Título<input name="titulo" required maxlength="160">
</label>
<label>Autor<input name="autor" required maxlength="120">
</label>
<label>ISBN<input name="isbn" required maxlength="30">
</label>
<label>Ejemplares<input type="number" name="ejemplares" value="1" min="1" max="1000" required>
</label>
<button class="primary">Agregar al catálogo</button>
</form>
</section>
</c:if>
</main>
<footer>Universidad UNTEC · Un espacio para descubrir y aprender.</footer>
</body>
</html>
