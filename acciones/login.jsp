<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="loginBean" class="co.edu.grado.control.LoginBean" scope="page" />
<c:set target="${loginBean}" property="email" value="${param.email}" />
<c:set target="${loginBean}" property="password" value="${param.password}" />
<c:set target="${loginBean}" property="sesion" value="${pageContext.session}" />
<c:choose>
    <c:when test="${loginBean.iniciarSesion()}">
        <c:set var="usuarioSesion" value="${loginBean.usuario}" scope="session" />
        <c:redirect url="/vistas/inicio.jsp" />
    </c:when>
    <c:otherwise>
        <c:set var="flashError" value="${loginBean.error}" scope="session" />
        <c:redirect url="/login.jsp" />
    </c:otherwise>
</c:choose>
