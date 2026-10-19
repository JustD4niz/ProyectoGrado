<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioSesion or sessionScope.usuarioSesion.rol ne 'COMITE'}">
    <c:redirect url="/vistas/inicio.jsp" />
</c:if>
<jsp:useBean id="bean" class="co.edu.grado.control.IdeaBean" scope="page" />
<c:set target="${bean}" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${bean}" property="observaciones" value="${param.observaciones}" />
<c:choose>
    <c:when test="${bean.rechazar(param.id)}">
        <c:set var="flashExito" value="Idea rechazada." scope="session" />
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="${bean.error}" scope="session" />
    </c:otherwise>
</c:choose>
<c:redirect url="/vistas/ideas/bandeja.jsp" />
