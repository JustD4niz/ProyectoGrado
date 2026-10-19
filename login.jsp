<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Iniciar sesi&oacute;n &middot; UTS</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css" />
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card">
        <span class="brand">
            <span class="brand-mark">UTS</span>
            <span class="brand-text">Universitaria Tecnológica de Santander</span>
        </span>
        <h1>Iniciar sesi&oacute;n</h1>

        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-error">${sessionScope.flashError}</div>
            <c:remove var="flashError" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.flashExito}">
            <div class="alert alert-ok">${sessionScope.flashExito}</div>
            <c:remove var="flashExito" scope="session" />
        </c:if>

        <form method="post" action="${ctx}/acciones/login.jsp" autocomplete="off">
            <div class="field">
                <label for="email">Correo institucional</label>
                <input type="email" id="email" name="email" placeholder="usuario@uts.edu.co" required />
            </div>
            <div class="field">
                <label for="password">Contrase&ntilde;a</label>
                <input type="password" id="password" name="password" placeholder="********" required />
            </div>
            <button type="submit" class="btn btn-primary btn-block">Ingresar</button>
        </form>

        <div class="demo-users">
            <strong>Usuarios de demostraci&oacute;n</strong> (clave <code>Clave123*</code>):
            <ul style="margin:8px 0 0;padding-left:18px;">
                <li>admin@uts.edu.co &mdash; Administrador</li>
                <li>comite@uts.edu.co &mdash; Comit&eacute;</li>
                <li>directora@uts.edu.co &mdash; Docente (director/evaluador)</li>
                <li>director2@uts.edu.co &mdash; Docente (director/evaluador)</li>
                <li>evaluadora@uts.edu.co &mdash; Docente (director/evaluador)</li>
                <li>juan.perez@uts.edu.co &mdash; Estudiante</li>
            </ul>
        </div>

    </div>
</div>
</body>
</html>
