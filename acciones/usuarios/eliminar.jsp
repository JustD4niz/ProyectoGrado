<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioSesion or sessionScope.usuarioSesion.rol ne 'ADMIN'}">
    <c:redirect url="/vistas/inicio.jsp" />
</c:if>
<jsp:useBean id="bean" class="co.edu.grado.control.UsuarioBean" scope="page" />
<c:set target="${bean}" property="id" value="${param.id}" />
<c:choose>
    <c:when test="${bean.eliminar()}">
        <c:set var="flashExito" value="${bean.mensaje}" scope="session" />
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="${bean.error}" scope="session" />
    </c:otherwise>
</c:choose>
<c:redirect url="/vistas/usuarios/listar.jsp" />
