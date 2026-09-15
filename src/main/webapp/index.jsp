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
<div class="login">
<section class="welcome">
<div class="brand">
<span class="mark">U</span>
<span>UNTEC<small>Biblioteca digital</small>
</span>
</div>
<div>
<p class="eyebrow">CONOCIMIENTO A TU ALCANCE</p>
<h1>Tu próxima<br>gran idea<br>empieza aquí.</h1>
<p>Explora el catálogo, solicita tus libros y mantén tus lecturas al día.</p>
</div>
<span>Universidad UNTEC · Comunidad y aprendizaje</span>
</section>
<section class="access">
<div class="loginbox">
<p class="eyebrow">BIENVENIDO A TU BIBLIOTECA</p>
<h2>Inicia sesión</h2>
<p>Accede con tu cuenta de la biblioteca.</p>
<c:if test="${not empty error}">
<div class="notice" role="alert">
<c:out value="${error}"/>
</div>
</c:if>
<form action="${pageContext.request.contextPath}/login" method="post">
<input type="hidden" name="csrf" value="${sessionScope.csrf}">
<label>Correo electrónico<input type="email" name="email" required maxlength="150" autocomplete="username" placeholder="tu.correo@untec.test">
</label>
<label>Contraseña<input type="password" name="password" required maxlength="100" autocomplete="current-password">
</label>
<button class="primary full">Entrar a la biblioteca →</button>
</form>
<p class="muted">¿Necesitas una cuenta? Solicítala al personal de biblioteca.</p>
</div>
</section>
</div>
</body>
</html>
