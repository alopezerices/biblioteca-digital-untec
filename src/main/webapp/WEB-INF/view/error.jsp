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
<main>
<section class="empty">
<h1>No se pudo completar la solicitud</h1>
<p>Tu sesión puede haber caducado o no tienes permiso para esta operación. Vuelve a iniciar sesión y prueba nuevamente.</p>
<a href="${pageContext.request.contextPath}/index.jsp">Volver al inicio</a>
</section>
</main>
</body>
</html>
