<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="sesionBean" class="co.edu.grado.control.SesionBean" scope="page" />
<c:set target="${sesionBean}" property="sesion" value="${pageContext.session}" />
<c:set var="cerrado" value="${sesionBean.cerrar()}" />
<c:redirect url="/login.jsp" />
