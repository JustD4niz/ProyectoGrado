<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="aviso" class="co.edu.grado.control.AvisoBean" scope="page" />
<jsp:setProperty name="aviso" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="ok" value="${aviso.marcarLeidas()}" />
<c:redirect url="/vistas/notificaciones.jsp" />
