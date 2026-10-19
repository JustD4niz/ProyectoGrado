<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cfg" class="co.edu.grado.control.ConfigBean" scope="request" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Base documental" activo="vistas/base-documental.jsp">
    <div class="card">
        <h2>Documentaci&oacute;n oficial</h2>
        <p class="muted">Formatos institucionales, reglamentos y rejillas para el desarrollo del trabajo de grado.</p>
        <div class="fase-grid">
            <div class="fase-card">
                <div class="fase-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M8 2v4M16 2v4M3 10h18"/></svg>
                </div>
                <h3>Calendario Acad&eacute;mico</h3>
                <p class="fase-desc">Consulta las fechas importantes y eventos del proceso.</p>
                <a class="btn btn-outline btn-sm fase-btn" href="${ctx}/vistas/calendario-academico.jsp">Ver calendario</a>
            </div>
            <c:forEach var="d" items="${cfg.documental}">
                <div class="fase-card">
                    <div class="fase-icon">
                        <c:choose>
                            <c:when test="${d.clave eq 'documental.fdc124'}"><svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/><path d="M9 13h6M9 17h6"/></svg></c:when>
                            <c:when test="${d.clave eq 'documental.fdc125'}"><svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/><path d="M9 15l2 2 4-4"/></svg></c:when>
                            <c:when test="${d.clave eq 'documental.rejilla'}"><svg viewBox="0 0 24 24"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18M3 15h18M9 3v18M15 3v18"/></svg></c:when>
                            <c:when test="${d.clave eq 'documental.reglamento'}"><svg viewBox="0 0 24 24"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg></c:when>
                            <c:otherwise><svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="6"/><path d="M8.21 13.89L7 23l5-3 5 3-1.21-9.12"/></svg></c:otherwise>
                        </c:choose>
                    </div>
                    <h3>${d.etiqueta}</h3>
                    <p class="fase-desc">
                        <c:choose>
                            <c:when test="${d.clave eq 'documental.fdc124'}">Formato oficial para presentar la propuesta de trabajo de grado.</c:when>
                            <c:when test="${d.clave eq 'documental.fdc125'}">Formato oficial del informe final de trabajo de grado.</c:when>
                            <c:when test="${d.clave eq 'documental.rejilla'}">Criterios y puntajes para la evaluaci&oacute;n del trabajo de grado.</c:when>
                            <c:when test="${d.clave eq 'documental.reglamento'}">Normas y lineamientos del trabajo de grado.</c:when>
                            <c:otherwise>Reconocimientos y est&iacute;mulos para estudiantes y docentes.</c:otherwise>
                        </c:choose>
                    </p>
                    <a class="btn btn-outline btn-sm fase-btn" href="${d.url}" target="_blank" rel="noopener">Abrir documento</a>
                </div>
            </c:forEach>
        </div>
    </div>
</t:pagina>
