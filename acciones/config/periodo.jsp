<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioSesion or sessionScope.usuarioSesion.rol ne 'ADMIN'}">
    <c:redirect url="/vistas/inicio.jsp" />
</c:if>
<jsp:useBean id="bean" class="co.edu.grado.control.ConfigBean" scope="page" />
<jsp:setProperty name="bean" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${bean}" property="nombre" value="${param.nombre}" />
<c:set target="${bean}" property="fechaInicio" value="${param.fechaInicio}" />
<c:set target="${bean}" property="fechaFin" value="${param.fechaFin}" />
<c:set target="${bean}" property="semanaPagoMax" value="${param.semanaPagoMax}" />
<c:set target="${bean}" property="semanaPropuestaMax" value="${param.semanaPropuestaMax}" />
<c:set target="${bean}" property="diasDuracion" value="${param.diasDuracion}" />
<c:set target="${bean}" property="diasMinInforme" value="${param.diasMinInforme}" />
<c:set target="${bean}" property="maxProrrogas" value="${param.maxProrrogas}" />
<c:choose>
    <c:when test="${bean.guardarPeriodo()}">
        <c:set var="flashExito" value="${bean.mensaje}" scope="session" />
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="${bean.error}" scope="session" />
    </c:otherwise>
</c:choose>
<c:redirect url="/vistas/config/periodo.jsp" />
