<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="aviso" class="co.edu.grado.control.AvisoBean" scope="request" />
<jsp:setProperty name="aviso" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<jsp:useBean id="proyectoIni" class="co.edu.grado.control.ProyectoBean" scope="request" />
<jsp:setProperty name="proyectoIni" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<jsp:useBean id="usuarioIni" class="co.edu.grado.control.UsuarioBean" scope="request" />
<jsp:useBean id="ideaIni" class="co.edu.grado.control.IdeaBean" scope="request" />
<jsp:setProperty name="ideaIni" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="u" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Inicio" activo="vistas/inicio.jsp">
    <div class="card">
        <h2>Bienvenido(a), ${u.nombre}</h2>
        <p class="muted">
            Ha ingresado como <strong>${etiquetas.rol(u.rol)}</strong>.
            <c:if test="${u.rol eq 'ESTUDIANTE'}"> Nivel: <strong>${etiquetas.nivel(u.nivel)}</strong>.</c:if>
            Este es su panel de trabajo de grado; desde aqu&iacute; puede gestionar sus ideas, proyectos y documentos.
        </p>
    </div>

    <c:if test="${not empty aviso.recordatorios}">
        <c:forEach var="r" items="${aviso.recordatorios}">
            <div class="alert alert-warn">${r}</div>
        </c:forEach>
    </c:if>

    <c:choose>
        <c:when test="${u.rol eq 'ESTUDIANTE'}">
            <c:choose>
                <c:when test="${empty proyectoIni.miProyecto}">
                    <div class="card">
                        <h2>A&uacute;n no tienes un proyecto inscrito</h2>
                        <p class="muted">Explora el banco de ideas disponibles para tu nivel, registra tu propia propuesta y realiza tu inscripci&oacute;n.</p>
                        <a class="btn btn-primary" href="${ctx}/vistas/ideas/banco.jsp">Ver banco de ideas</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:set var="p" value="${proyectoIni.miProyecto}" />
                    <div class="grid grid-3">
                        <div class="stat"><div class="n">${p.codigo}</div><div class="l">C&oacute;digo</div></div>
                        <div class="stat"><div class="n">${etiquetas.fase(p.faseActual)}</div><div class="l">Fase actual</div></div>
                        <div class="stat"><div class="n">${proyectoIni.getAvance(p.id)}%</div><div class="l">Avance</div></div>
                    </div>
                    <div class="card">
                        <h2>${p.titulo}</h2>
                        <p class="muted">Modalidad: ${etiquetas.modalidad(p.modalidad)} &middot; Estado: ${etiquetas.estadoProyecto(p.estado)}
                            &middot; Responsable: ${etiquetas.responsable(p.responsableActual)}</p>
                        <div class="progress" style="margin:14px 0;"><span style="width:${proyectoIni.getAvance(p.id)}%"></span></div>
                        <div class="form-actions">
                            <a class="btn btn-primary" href="${ctx}/vistas/proyectos/mi-proyecto.jsp">Ver mi proyecto</a>
                            <a class="btn btn-outline" href="${ctx}/vistas/base-documental.jsp">Base documental</a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>

            <div class="card">
                <h2>Accesos r&aacute;pidos</h2>
                <div class="form-actions">
                    <a class="btn btn-outline" href="${ctx}/vistas/ideas/banco.jsp">Banco de ideas</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/proyectos/mi-proyecto.jsp">Mi proyecto</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/base-documental.jsp">Base documental</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/notificaciones.jsp">Notificaciones</a>
                </div>
            </div>
        </c:when>

        <c:when test="${u.rol eq 'COMITE'}">
            <div class="grid grid-3">
                <div class="stat"><div class="n">${fn:length(ideaIni.bandeja)}</div><div class="l">Ideas por revisar</div></div>
                <div class="stat"><div class="n">${fn:length(proyectoIni.pendientesComite)}</div><div class="l">Proyectos pendientes</div></div>
                <div class="stat"><div class="n">${aviso.noLeidas}</div><div class="l">Notificaciones</div></div>
            </div>

            <c:if test="${not empty proyectoIni.pendientesComite}">
                <div class="card">
                    <h2>Pendientes de tu revisi&oacute;n</h2>
                    <ul style="list-style:none;margin:0;padding:0;">
                        <c:forEach var="p" items="${proyectoIni.pendientesComite}">
                            <li style="padding:10px 0;border-bottom:1px solid var(--gris-borde);">
                                <span class="tag tag-pendiente">${etiquetas.fase(p.faseActual)}</span>
                                <strong>${p.codigo}</strong> &mdash; ${p.titulo}
                                <a class="btn btn-sm btn-primary" href="${ctx}/vistas/proyectos/fase.jsp?p=${p.id}">Revisar</a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <div class="card">
                <h2>Acciones r&aacute;pidas</h2>
                <p class="muted">Revise las propuestas pendientes y haga seguimiento a los proyectos inscritos.</p>
                <div class="form-actions">
                    <a class="btn btn-primary" href="${ctx}/vistas/ideas/bandeja.jsp">Bandeja de ideas</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/ideas/trazabilidad.jsp">Trazabilidad de ideas</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/proyectos/listar.jsp">Proyectos</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/base-documental.jsp">Base documental</a>
                </div>
            </div>
        </c:when>

        <c:when test="${u.rol eq 'ADMIN'}">
            <div class="grid grid-3">
                <div class="stat"><div class="n">${usuarioIni.total}</div><div class="l">Usuarios</div></div>
                <div class="stat"><div class="n">${proyectoIni.total}</div><div class="l">Proyectos</div></div>
                <div class="stat"><div class="n">${aviso.noLeidas}</div><div class="l">Notificaciones</div></div>
            </div>
            <div class="card">
                <h2>Administraci&oacute;n</h2>
                <p class="muted">Gestione usuarios, periodos acad&eacute;micos, calendario y genere reportes.</p>
                <div class="form-actions">
                    <a class="btn btn-primary" href="${ctx}/vistas/usuarios/listar.jsp">Gestionar usuarios</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/config/periodo.jsp">Periodo y plazos</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/config/calendario.jsp">Calendario</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/reportes/index.jsp">Reportes</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/base-documental.jsp">Base documental</a>
                </div>
            </div>
        </c:when>

        <c:when test="${u.rol eq 'DIRECTOR' or u.rol eq 'EVALUADOR' or u.rol eq 'DOCENTE'}">
            <c:set var="listaDir" value="${proyectoIni.getMisProyectos('DIRECTOR')}" />
            <c:set var="listaEva" value="${proyectoIni.getMisProyectos('EVALUADOR')}" />

            <div class="grid grid-3">
                <div class="stat"><div class="n">${fn:length(listaDir)}</div><div class="l">Proyectos director</div></div>
                <div class="stat"><div class="n">${fn:length(listaEva)}</div><div class="l">Proyectos evaluador</div></div>
                <div class="stat"><div class="n">${aviso.noLeidas}</div><div class="l">Notificaciones</div></div>
            </div>

            <div class="card">
                <h2>Pendientes de tu revisi&oacute;n</h2>
                <c:set var="hayPend" value="false" />
                <ul style="list-style:none;margin:0;padding:0;">
                    <c:forEach var="p" items="${listaDir}">
                        <c:if test="${p.estado eq 'EN_FLUJO' and p.responsableActual eq 'DIRECTOR'}">
                            <c:set var="hayPend" value="true" />
                            <li style="padding:10px 0;border-bottom:1px solid var(--gris-borde);">
                                <span class="tag tag-pendiente">Director</span>
                                <strong>${p.codigo}</strong> &mdash; ${p.titulo}
                                <a class="btn btn-sm btn-primary" href="${ctx}/vistas/proyectos/gestion.jsp?p=${p.id}">Revisar</a>
                            </li>
                        </c:if>
                    </c:forEach>
                    <c:forEach var="p" items="${listaEva}">
                        <c:if test="${p.estado eq 'EN_FLUJO' and p.responsableActual eq 'EVALUADOR'}">
                            <c:set var="hayPend" value="true" />
                            <li style="padding:10px 0;border-bottom:1px solid var(--gris-borde);">
                                <span class="tag tag-pendiente">Evaluador</span>
                                <strong>${p.codigo}</strong> &mdash; ${p.titulo}
                                <a class="btn btn-sm btn-primary" href="${ctx}/vistas/proyectos/gestion.jsp?p=${p.id}">Revisar</a>
                            </li>
                        </c:if>
                    </c:forEach>
                </ul>
                <c:if test="${not hayPend}">
                    <div class="empty">No tienes revisiones pendientes. Todo al d&iacute;a.</div>
                </c:if>
            </div>

            <div class="card">
                <h2>Accesos r&aacute;pidos</h2>
                <div class="form-actions">
                    <a class="btn btn-primary" href="${ctx}/vistas/proyectos/mis-proyectos.jsp">Proyectos director</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/proyectos/proyectos-evaluador.jsp">Evaluador</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/ideas/banco-ideas.jsp">Banco de ideas</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/ideas/mis-ideas.jsp">Banco de propuestas</a>
                    <a class="btn btn-outline" href="${ctx}/vistas/base-documental.jsp">Base documental</a>
                </div>
            </div>
        </c:when>
    </c:choose>

    <c:if test="${not empty aviso.notificaciones}">
        <div class="card">
            <div class="page-head">
                <h2 style="margin:0;">Notificaciones recientes</h2>
                <a class="btn btn-outline btn-sm" href="${ctx}/vistas/notificaciones.jsp">Ver todas</a>
            </div>
            <ul style="list-style:none;margin:0;padding:0;">
                <c:forEach var="n" items="${aviso.notificaciones}" varStatus="st">
                    <c:if test="${st.index < 4}">
                        <li style="padding:10px 0;border-bottom:1px solid var(--gris-borde);">
                            ${n.mensaje}
                            <c:if test="${not empty n.enlace}"> <a href="${ctx}/${n.enlace}">Ir a detalle</a></c:if>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
    </c:if>
</t:pagina>
