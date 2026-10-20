<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="proy" class="co.edu.grado.control.ProyectoBean" scope="request" />
<jsp:setProperty name="proy" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<jsp:setProperty name="proy" property="proyectoId" value="${param.p}" />
<c:set var="p" value="${proy.detalle}" />
<c:set var="u" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="dir" value="${proy.getAsignacion('DIRECTOR')}" />
<c:set var="eva" value="${proy.getAsignacion('EVALUADOR')}" />
<c:set var="autorizado" value="${u.rol eq 'COMITE' or u.rol eq 'ADMIN' or (not empty dir and dir.docenteId eq u.id) or (not empty eva and eva.docenteId eq u.id) or (u.rol eq 'ESTUDIANTE' and proy.esIntegranteDelProyecto)}" />

<t:pagina titulo="Detalle del proyecto" activo="vistas/proyectos/listar.jsp">
    <c:if test="${empty p}">
        <div class="card"><div class="empty">Proyecto no encontrado.</div></div>
    </c:if>
    <c:if test="${not empty p and not autorizado}">
        <div class="card"><div class="empty">No tienes permisos sobre este proyecto.</div></div>
    </c:if>
    <c:if test="${not empty p}">
        <div class="grid grid-3">
            <div class="stat"><div class="n">${p.codigo}</div><div class="l">C&oacute;digo</div></div>
            <div class="stat"><div class="n">${etiquetas.fase(p.faseActual)}</div><div class="l">Fase</div></div>
            <div class="stat"><div class="n">${proy.getAvance(p.id)}%</div><div class="l">Avance</div></div>
        </div>

        <div class="card">
            <h2>${p.titulo}</h2>
            <div class="dl">
                <dt>Modalidad</dt><dd>${etiquetas.modalidad(p.modalidad)}</dd>
                <dt>Nivel</dt><dd>${etiquetas.nivel(p.nivel)}</dd>
                <dt>L&iacute;nea</dt><dd>${p.lineaInvestigacion}</dd>
                <dt>Estado</dt><dd>${etiquetas.estadoProyecto(p.estado)}</dd>
                <dt>Director</dt><dd>${empty p.nombreDirector ? 'Sin asignar' : p.nombreDirector}</dd>
                <dt>Evaluador</dt><dd>${empty p.nombreEvaluador ? 'Sin asignar' : p.nombreEvaluador}</dd>
                <dt>Integrantes</dt><dd>${p.nombresIntegrantes}</dd>
                <dt>Pr&oacute;rrogas</dt><dd>${p.prorrogasUtilizadas}</dd>
            </div>
        </div>

        <div class="card">
            <h2>Integrantes</h2>
            <table class="tabla">
                <thead><tr><th>Nombre</th><th>C&eacute;dula</th><th>Correo</th><th>Estado</th></tr></thead>
                <tbody>
                    <c:forEach var="i" items="${proy.integrantes(p.id)}">
                        <tr><td>${i.nombre}</td><td>${i.cedula}</td><td>${i.email}</td><td>${i.activo ? 'Activo' : 'Retirado'}</td></tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="card">
            <h2>Fases</h2>
            <table class="tabla">
                <thead><tr><th>Fase</th><th>Estado</th><th>Responsable</th><th>Aprobaci&oacute;n</th></tr></thead>
                <tbody>
                    <c:forEach var="f" items="${proy.fases}">
                        <tr>
                            <td>${etiquetas.fase(f.fase)}</td>
                            <td>${etiquetas.estadoFase(f.estado)}</td>
                            <td>${etiquetas.responsable(f.responsable)}</td>
                            <td>${empty f.fechaAprobacion ? '-' : f.fechaAprobacion}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div class="form-actions" style="margin-top:14px;">
                <a class="btn btn-primary" href="${ctx}/vistas/proyectos/fase.jsp?p=${p.id}&f=${p.faseActual}">Gestionar fases y documentos</a>
            </div>
        </div>
    </c:if>
</t:pagina>
