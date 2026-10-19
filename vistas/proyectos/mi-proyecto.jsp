<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="proy" class="co.edu.grado.control.ProyectoBean" scope="request" />
<jsp:setProperty name="proy" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="p" value="${proy.miProyecto}" />
<c:if test="${not empty p}"><c:set target="${proy}" property="proyectoId" value="${p.id}" /></c:if>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Mi proyecto" roles="ESTUDIANTE" activo="vistas/proyectos/mi-proyecto.jsp">
    <c:choose>
        <c:when test="${empty p}">
            <div class="card">
                <div class="empty">No tienes un proyecto activo inscrito.</div>
                <div style="text-align:center;margin-top:18px;">
                    <a class="btn btn-primary" href="${ctx}/vistas/ideas/banco.jsp">Ir al banco de ideas</a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="grid grid-3">
                <div class="stat"><div class="n">${p.codigo}</div><div class="l">C&oacute;digo</div></div>
                <div class="stat"><div class="n">${etiquetas.fase(p.faseActual)}</div><div class="l">Fase actual</div></div>
                <div class="stat"><div class="n">${p.diasRestantes >= 0 ? p.diasRestantes : 0} d&iacute;as</div><div class="l">D&iacute;as restantes</div></div>
            </div>

            <div class="card">
                <h2>${p.titulo}</h2>
                <p class="muted">Modalidad: ${etiquetas.modalidad(p.modalidad)} &middot; Nivel: ${etiquetas.nivel(p.nivel)}
                    &middot; Estado: ${etiquetas.estadoProyecto(p.estado)} &middot; Responsable actual: ${etiquetas.responsable(p.responsableActual)}</p>
                <div class="dl" style="margin-top:12px;">
                    <dt>Director</dt><dd>${empty p.nombreDirector ? 'Sin asignar' : p.nombreDirector}</dd>
                    <dt>Evaluador</dt><dd>${empty p.nombreEvaluador ? 'Sin asignar' : p.nombreEvaluador}</dd>
                    <dt>Integrantes</dt><dd>${p.nombresIntegrantes}</dd>
                    <dt>Pr&oacute;rrogas</dt><dd>${p.prorrogasUtilizadas}</dd>
                </div>
            </div>

            <div class="card">
                <h2>Flujo del proyecto</h2>
                <div class="fase-grid">
                    <c:forEach var="f" items="${proy.fases}">
                        <c:if test="${f.fase ne 'CERRADO'}">
                            <div class="fase-card ${f.estado eq 'APROBADA' ? 'completed' : ''} ${f.fase eq p.faseActual ? 'current' : ''}">
                                <div class="fase-icon">
                                    <c:choose>
                                        <c:when test="${f.fase eq 'PAGO'}"><svg viewBox="0 0 24 24"><rect x="2" y="5" width="20" height="14" rx="2"/><circle cx="12" cy="12" r="2.5"/><path d="M6 12h.01M18 12h.01"/></svg></c:when>
                                        <c:when test="${f.fase eq 'PROPUESTA_I'}"><svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/><path d="M9 13h6M9 17h6"/></svg></c:when>
                                        <c:when test="${f.fase eq 'PROPUESTA_II'}"><svg viewBox="0 0 24 24"><path d="M9 2h6a1 1 0 0 1 1 1v1h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2V3a1 1 0 0 1 1-1z"/><path d="M9 14l2 2 4-4"/></svg></c:when>
                                        <c:when test="${f.fase eq 'INFORME_I'}"><svg viewBox="0 0 24 24"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg></c:when>
                                        <c:when test="${f.fase eq 'INFORME_II'}"><svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="6"/><path d="M8.21 13.89L7 23l5-3 5 3-1.21-9.12"/></svg></c:when>
                                        <c:when test="${f.fase eq 'SUSTENTACION'}"><svg viewBox="0 0 24 24"><path d="M22 10L12 5 2 10l10 5 10-5z"/><path d="M6 12v5c0 1.66 2.69 3 6 3s6-1.34 6-3v-5"/></svg></c:when>
                                    </c:choose>
                                </div>
                                <h3>${etiquetas.fase(f.fase)}</h3>
                                <c:if test="${f.fase eq p.faseActual}"><span class="fase-now">Fase actual</span></c:if>
                                <p class="fase-desc">
                                    <c:choose>
                                        <c:when test="${f.fase eq 'PAGO'}">Registro y revisi&oacute;n del desprendible de pago de cada integrante.</c:when>
                                        <c:when test="${f.fase eq 'PROPUESTA_I'}">El estudiante carga la propuesta F-DC 124 y el director la revisa.</c:when>
                                        <c:when test="${f.fase eq 'PROPUESTA_II'}">El evaluador emite su concepto y el Comit&eacute; toma la decisi&oacute;n final.</c:when>
                                        <c:when test="${f.fase eq 'INFORME_I'}">El estudiante carga el informe F-DC 125 con el aval del director.</c:when>
                                        <c:when test="${f.fase eq 'INFORME_II'}">El evaluador aplica la r&uacute;brica y aprueba el informe final.</c:when>
                                        <c:when test="${f.fase eq 'SUSTENTACION'}">Se registra la sustentaci&oacute;n y se cierra el proyecto.</c:when>
                                    </c:choose>
                                </p>
                                <a class="btn btn-outline btn-sm fase-btn" href="${ctx}/vistas/proyectos/fase.jsp?p=${p.id}&f=${f.fase}">Ver m&aacute;s</a>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</t:pagina>
