<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="bean" class="co.edu.grado.control.IdeaBean" scope="page" />
<c:set target="${bean}" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${bean}" property="id" value="${empty param.id ? 0 : param.id}" />
<c:set target="${bean}" property="titulo" value="${param.titulo}" />
<c:set target="${bean}" property="descripcion" value="${param.descripcion}" />
<c:set target="${bean}" property="nivel" value="${param.nivel}" />
<c:set target="${bean}" property="modalidad" value="${param.modalidad}" />
<c:set target="${bean}" property="lineaInvestigacion" value="${param.lineaInvestigacion}" />
<c:choose>
    <c:when test="${bean.guardar()}">
        <c:set var="flashExito" value="${bean.mensaje}" scope="session" />
        <c:choose>
            <c:when test="${sessionScope.usuarioSesion.esDocente()}">
                <c:redirect url="/vistas/ideas/mis-ideas.jsp" />
            </c:when>
            <c:otherwise>
                <c:redirect url="/vistas/ideas/banco.jsp" />
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="${bean.error}" scope="session" />
        <c:redirect url="/vistas/ideas/formulario.jsp?id=${param.id}" />
    </c:otherwise>
</c:choose>
