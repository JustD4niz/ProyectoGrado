<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Error · UTS</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css" />
</head>
<body>
<div class="auth-wrap">
    <div class="auth-card" style="text-align:center;">
        <span class="brand brand-mark" style="display:grid;margin:0 auto;">TG</span>
        <h1>Algo no sali&oacute; bien</h1>
        <p class="sub">
            No fue posible procesar su solicitud.
            <c:if test="${not empty pageContext.exception}">
                <br /><span class="muted" style="font-size:.82rem;">${pageContext.exception.message}</span>
            </c:if>
        </p>
        <c:choose>
            <c:when test="${not empty sessionScope.usuarioSesion}">
                <a class="btn btn-primary btn-block" href="${ctx}/vistas/inicio.jsp">Volver al panel</a>
            </c:when>
            <c:otherwise>
                <a class="btn btn-primary btn-block" href="${ctx}/login.jsp">Ir a iniciar sesi&oacute;n</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
