<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${empty sessionScope.usuarioSesion or not sessionScope.usuarioSesion.esDocente()}">
        <c:redirect url="/vistas/inicio.jsp" />
    </c:when>
    <c:otherwise>
        <jsp:useBean id="fase" class="co.edu.grado.control.FaseBean" scope="page" />
        <jsp:setProperty name="fase" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
        <c:set target="${fase}" property="proyectoId" value="${param.proyectoId}" />
        <c:set target="${fase}" property="observaciones" value="${param.observaciones}" />
        <c:set var="ok" value="${fase.aprobarPropuestaDirector(param.accion eq 'aprobar')}" />
        <c:choose>
            <c:when test="${ok}">
                <c:set var="flashExito" value="${fase.mensaje}" scope="session" />
            </c:when>
            <c:otherwise>
                <c:set var="flashError" value="${fase.error}" scope="session" />
            </c:otherwise>
        </c:choose>
        <c:redirect url="/vistas/proyectos/fase.jsp?p=${param.proyectoId}" />
    </c:otherwise>
</c:choose>
