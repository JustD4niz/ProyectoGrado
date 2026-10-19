<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioSesion or sessionScope.usuarioSesion.rol ne 'ADMIN'}">
    <c:redirect url="/vistas/inicio.jsp" />
</c:if>
<jsp:useBean id="bean" class="co.edu.grado.control.UsuarioBean" scope="page" />
<c:set target="${bean}" property="id" value="${empty param.id ? 0 : param.id}" />
<c:set target="${bean}" property="cedula" value="${param.cedula}" />
<c:set target="${bean}" property="nombre" value="${param.nombre}" />
<c:set target="${bean}" property="email" value="${param.email}" />
<c:set target="${bean}" property="rol" value="${param.rol}" />
<c:set target="${bean}" property="nivel" value="${param.nivel}" />
<c:set target="${bean}" property="password" value="${param.password}" />
<c:set target="${bean}" property="activo" value="${param.activo eq 'true'}" />
<c:set target="${bean}" property="consentimiento" value="${param.consentimiento eq 'true'}" />
<c:choose>
    <c:when test="${bean.guardar()}">
        <c:set var="flashExito" value="${bean.mensaje}" scope="session" />
        <c:redirect url="/vistas/usuarios/listar.jsp" />
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="${bean.error}" scope="session" />
        <c:redirect url="/vistas/usuarios/formulario.jsp?id=${param.id}" />
    </c:otherwise>
</c:choose>
