<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="perfil" class="co.edu.grado.control.PerfilBean" scope="page" />
<jsp:setProperty name="perfil" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:choose>
    <c:when test="${param.accion eq 'password'}">
        <c:set target="${perfil}" property="passwordActual" value="${param.actual}" />
        <c:set target="${perfil}" property="passwordNueva" value="${param.nueva}" />
        <c:set target="${perfil}" property="passwordConfirmar" value="${param.confirma}" />
        <c:choose>
            <c:when test="${perfil.cambiarPassword()}">
                <c:set var="flashExito" value="${perfil.mensaje}" scope="session" />
            </c:when>
            <c:otherwise>
                <c:set var="flashError" value="${perfil.error}" scope="session" />
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${param.accion eq 'datos'}">
        <c:set target="${perfil}" property="nombre" value="${param.nombre}" />
        <c:set target="${perfil}" property="email" value="${param.email}" />
        <c:choose>
            <c:when test="${perfil.actualizarDatos()}">
                <c:set var="flashExito" value="${perfil.mensaje}" scope="session" />
                <c:set var="sessionScope.usuarioSesion" value="${perfil.usuarioSesion}" />
            </c:when>
            <c:otherwise>
                <c:set var="flashError" value="${perfil.error}" scope="session" />
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="Accion no reconocida" scope="session" />
    </c:otherwise>
</c:choose>
<c:redirect url="/vistas/perfil.jsp" />
