<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.usuarioSesion or (sessionScope.usuarioSesion.rol ne 'COMITE' and sessionScope.usuarioSesion.rol ne 'ADMIN')}">
    <c:redirect url="/vistas/inicio.jsp" />
</c:if>
<jsp:useBean id="rep" class="co.edu.grado.control.ReporteBean" scope="page" />
<jsp:setProperty name="rep" property="tipo" value="${param.tipo}" />
<jsp:setProperty name="rep" property="periodoId" value="${empty param.periodoId ? null : param.periodoId}" />
<c:out value="${rep.exportarExcel(pageContext)}" escapeXml="false" />
