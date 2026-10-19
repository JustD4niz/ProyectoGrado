<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioSesion or sessionScope.usuarioSesion.rol ne 'ESTUDIANTE'}">
    <c:redirect url="/vistas/inicio.jsp" />
</c:if>
<jsp:useBean id="bean" class="co.edu.grado.control.ProyectoBean" scope="page" />
<jsp:setProperty name="bean" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${bean}" property="ideaId" value="${param.ideaId}" />
<c:set target="${bean}" property="titulo" value="${param.titulo}" />
<c:set target="${bean}" property="objetivos" value="${param.objetivos}" />
<c:set target="${bean}" property="modalidad" value="${param.modalidad}" />
<c:set target="${bean}" property="cedulas" value="${param.cedulas}" />
<c:choose>
    <c:when test="${bean.crearDesdeIdea()}">
        <c:set var="flashExito" value="${bean.mensaje}" scope="session" />
        <c:redirect url="/vistas/proyectos/mi-proyecto.jsp" />
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="${bean.error}" scope="session" />
        <c:redirect url="/vistas/proyectos/inscribir.jsp?ideaId=${param.ideaId}" />
    </c:otherwise>
</c:choose>
