<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="aviso" class="co.edu.grado.control.AvisoBean" scope="request" />
<jsp:setProperty name="aviso" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Notificaciones" activo="vistas/notificaciones.jsp">
    <div class="card">
        <div class="page-head">
            <h2 style="margin:0;">Bandeja</h2>
            <a class="btn btn-outline btn-sm" href="${ctx}/acciones/notificaciones.jsp">Marcar todas como le&iacute;das</a>
        </div>
        <c:choose>
            <c:when test="${empty aviso.notificaciones}">
                <div class="empty">No tienes notificaciones.</div>
            </c:when>
            <c:otherwise>
                <ul style="list-style:none;margin:0;padding:0;">
                    <c:forEach var="n" items="${aviso.notificaciones}">
                        <li style="padding:12px 0;border-bottom:1px solid var(--gris-borde);">
                            <strong>${n.fecha}</strong> &mdash; ${n.mensaje}
                            <c:if test="${not empty n.enlace}"> <a href="${ctx}/${n.enlace}">Ir a detalle</a></c:if>
                        </li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </div>
</t:pagina>
