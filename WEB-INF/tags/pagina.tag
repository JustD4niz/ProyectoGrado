<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="titulo" required="true" %>
<%@ attribute name="roles" required="false" %>
<%@ attribute name="activo" required="false" %>

<c:set var="u" value="${sessionScope.usuarioSesion}" />
<c:set var="autorizado" value="${not empty u}" />
<c:if test="${autorizado and not empty roles}">
    <c:set var="autorizado" value="${fn:contains(' '.concat(roles).concat(' '), ' '.concat(u.rol).concat(' '))}" />
</c:if>

<c:choose>
    <c:when test="${empty u}">
        <jsp:forward page="/login.jsp" />
    </c:when>
    <c:when test="${not autorizado}">
        <jsp:forward page="/vistas/inicio.jsp" />
    </c:when>
    <c:otherwise>
        <jsp:useBean id="etiquetasTag" class="co.edu.grado.util.Etiquetas" scope="request" />
        <jsp:useBean id="menuTag" class="co.edu.grado.control.MenuBean" scope="request" />
        <jsp:useBean id="avisoTag" class="co.edu.grado.control.AvisoBean" scope="request" />
        <jsp:setProperty name="avisoTag" property="usuarioSesion" value="${u}" />
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>${titulo} · UTS</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css" />
</head>
<body>
    <header class="topbar">
        <a class="brand" href="${ctx}/vistas/inicio.jsp">
            <span class="brand-mark">UTS</span>
            <span class="brand-text">Universitaria Tecnológica de Santander</span>
        </a>
        <div class="topbar-user">
            <a class="notif-link" href="${ctx}/vistas/notificaciones.jsp" title="Notificaciones">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                    <path d="M13.73 21a2 2 0 0 1-3.46 0" />
                </svg>
                <c:if test="${avisoTag.noLeidas > 0}">
                    <span class="badge">${avisoTag.noLeidas}</span>
                </c:if>
            </a>
            <div class="user-chip">
                <span class="avatar">${u.iniciales}</span>
                <span class="user-meta">
                    <strong>${u.nombre}</strong>
                    <small>${etiquetasTag.rol(u.rol)}</small>
                </span>
            </div>
            <a class="btn btn-ghost btn-sm" href="${ctx}/acciones/logout.jsp">Salir</a>
        </div>
    </header>

    <div class="layout">
        <aside class="sidebar">
            <nav>
                <c:forEach var="op" items="${menuTag.getMenus(u.rol)}">
                    <a class="nav-item ${op.url eq activo ? 'active' : ''}" href="${ctx}/${op.url}">
                        <svg viewBox="0 0 24 24"><path d="${op.icono}" /></svg>
                        <span>${op.etiqueta}</span>
                    </a>
                </c:forEach>
            </nav>
        </aside>

        <main class="content">
            <c:if test="${not empty sessionScope.flashExito}">
                <div class="alert alert-ok">${sessionScope.flashExito}</div>
                <c:remove var="flashExito" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.flashError}">
                <div class="alert alert-error">${sessionScope.flashError}</div>
                <c:remove var="flashError" scope="session" />
            </c:if>

            <div class="page-head">
                <h1 class="page-title">${titulo}</h1>
            </div>

            <jsp:doBody />
        </main>
    </div>

    <script src="${ctx}/js/app.js"></script>
</body>
</html>
    </c:otherwise>
</c:choose>
