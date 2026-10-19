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
        <c:set target="${fase}" property="c1" value="${empty param.c1 ? 0 : param.c1}" />
        <c:set target="${fase}" property="c2" value="${empty param.c2 ? 0 : param.c2}" />
        <c:set target="${fase}" property="c3" value="${empty param.c3 ? 0 : param.c3}" />
        <c:set target="${fase}" property="c4" value="${empty param.c4 ? 0 : param.c4}" />
        <c:set target="${fase}" property="c5" value="${empty param.c5 ? 0 : param.c5}" />
        <c:set target="${fase}" property="cumpleObjetivoGeneral" value="${param.cumpleObjetivoGeneral eq 'true'}" />
        <c:set target="${fase}" property="cumpleObjetivosEspecificos" value="${param.cumpleObjetivosEspecificos eq 'true'}" />
        <c:set target="${fase}" property="observaciones" value="${param.observaciones}" />
        <c:choose>
            <c:when test="${fase.evaluarInforme()}">
                <c:set var="flashExito" value="${fase.mensaje}" scope="session" />
            </c:when>
            <c:otherwise>
                <c:set var="flashError" value="${fase.error}" scope="session" />
            </c:otherwise>
        </c:choose>
        <c:redirect url="/vistas/proyectos/fase.jsp?p=${param.proyectoId}" />
    </c:otherwise>
</c:choose>
