<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cal" class="co.edu.grado.control.CalendarioBean" scope="request" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Calendario acad&eacute;mico" activo="vistas/base-documental.jsp">
    <c:set var="p" value="${cal.periodoActivo}" />
    <c:choose>
        <c:when test="${empty p}">
            <div class="card">
                <div class="empty">A&uacute;n no hay un periodo acad&eacute;mico configurado.</div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="cal-hero">
                <div class="cal-hero-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M8 2v4M16 2v4M3 10h18"/></svg>
                </div>
                <div class="cal-hero-info">
                    <span class="cal-hero-eyebrow">Periodo acad&eacute;mico</span>
                    <h2>${p.nombre}</h2>
                    <p class="muted">Consulta las fechas importantes y los eventos del proceso de trabajo de grado.</p>
                </div>
                <div class="cal-hero-fechas">
                    <div class="cal-chip"><span class="cal-chip-l">Inicio</span><span class="cal-chip-v">${cal.inicioTexto}</span></div>
                    <div class="cal-chip"><span class="cal-chip-l">Fin</span><span class="cal-chip-v">${cal.finTexto}</span></div>
                </div>
            </div>

            <div class="card">
                <h2>Fechas clave</h2>
                <p class="muted">Hitos del proceso ordenados cronol&oacute;gicamente.</p>
                <div class="clave-grid">
                    <c:forEach var="f" items="${cal.fechasClave}">
                        <div class="clave-item ${f.tipo}">
                            <div class="clave-fecha">
                                <span class="clave-dd">${f.dia}</span>
                                <span class="clave-anio">${f.anio}</span>
                            </div>
                            <span class="clave-txt">${f.etiqueta}</span>
                        </div>
                    </c:forEach>
                </div>
                <c:if test="${empty cal.fechasClave}">
                    <div class="empty">A&uacute;n no hay fechas clave registradas.</div>
                </c:if>
            </div>

            <div class="card">
                <h2>Calendario</h2>
                <div class="cal-leyenda">
                    <span><i class="cal-punto inicio"></i> Inicio</span>
                    <span><i class="cal-punto fase"></i> Fases</span>
                    <span><i class="cal-punto fin"></i> Fin</span>
                    <span><i class="cal-punto hoy"></i> Hoy</span>
                </div>
                <div class="cal-grid">
                    <c:forEach var="mes" items="${cal.meses}">
                        <div class="cal-mes">
                            <h3>${mes.titulo}</h3>
                            <div class="cal-sem cal-cab">
                                <c:forEach var="dh" items="${cal.dias}"><span>${dh}</span></c:forEach>
                            </div>
                            <c:forEach var="semana" items="${mes.semanas}">
                                <div class="cal-sem">
                                    <c:forEach var="d" items="${semana}">
                                        <c:choose>
                                            <c:when test="${not d.enMes}">
                                                <div class="cal-dia vacio"></div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="cal-dia ${d.hoy ? 'hoy' : ''} ${d.tieneEvento ? 'evento' : ''}" title="${d.titulo}">
                                                    <span class="cal-num">${d.dia}</span>
                                                    <c:if test="${d.tieneEvento}">
                                                        <span class="cal-puntos">
                                                            <c:forEach var="ev" items="${d.eventos}"><i class="cal-punto ${ev.tipo}"></i></c:forEach>
                                                        </span>
                                                    </c:if>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="barra-fase">
                <a class="btn btn-outline btn-sm" href="${ctx}/vistas/base-documental.jsp">&larr; Volver a Base documental</a>
            </div>
        </c:otherwise>
    </c:choose>
</t:pagina>
