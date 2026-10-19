<%@ page contentType="application/pdf" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="rep" class="co.edu.grado.control.ReporteBean" scope="page" />
<jsp:setProperty name="rep" property="tipo" value="${param.tipo}" />
<jsp:setProperty name="rep" property="periodoId" value="${empty param.periodoId ? null : param.periodoId}" />
<c:out value="${rep.exportarPdf(pageContext)}" escapeXml="false" />
