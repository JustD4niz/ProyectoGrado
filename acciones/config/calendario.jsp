<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioSesion or sessionScope.usuarioSesion.rol ne 'ADMIN'}">
    <c:redirect url="/vistas/inicio.jsp" />
</c:if>
<jsp:useBean id="bean" class="co.edu.grado.control.ConfigBean" scope="page" />
<jsp:setProperty name="bean" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${bean}" property="faseSeleccionada" value="${param.faseSeleccionada}" />
<c:set target="${bean}" property="faseMin" value="${param.faseMin}" />
<c:set target="${bean}" property="faseMax" value="${param.faseMax}" />
<c:set target="${bean}" property="faseResponsable" value="${param.faseResponsable}" />
<c:choose>
    <c:when test="${bean.guardarFase()}">
        <c:set var="flashExito" value="${bean.mensaje}" scope="session" />
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="${bean.error}" scope="session" />
    </c:otherwise>
</c:choose>
<c:redirect url="/vistas/config/calendario.jsp" />
