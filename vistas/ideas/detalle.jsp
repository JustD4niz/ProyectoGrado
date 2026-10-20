<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="ideas" class="co.edu.grado.control.IdeaBean" scope="request" />
<jsp:setProperty name="ideas" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="id" value="${empty param.id ? 0 : param.id}" />
<c:set var="idea" value="${ideas.porId(id)}" />
<c:set var="u" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="visibleEst" value="${idea.estado eq 'APROBADA' and idea.disponible and idea.nivel eq u.nivel}" />
<c:set var="autorizadoIdea" value="${u.rol ne 'ESTUDIANTE' or visibleEst or (not empty idea.estudianteId and idea.estudianteId eq u.id)}" />

<t:pagina titulo="Detalle de idea" activo="vistas/ideas/banco.jsp">
    <c:if test="${empty idea}">
        <div class="card"><div class="empty">Idea no encontrada.</div></div>
    </c:if>
    <c:if test="${not empty idea and not autorizadoIdea}">
        <div class="card"><div class="empty">No tienes permisos para ver esta idea.</div></div>
    </c:if>
    <c:if test="${not empty idea and autorizadoIdea}">
        <div class="card">
            <h2>Detalle de la idea</h2>
            <dl class="dl">
                <dt>Código</dt><dd>${idea.codigo}</dd>
                <dt>T&iacute;tulo</dt><dd>${idea.titulo}</dd>
                <dt>Descripci&oacute;n</dt><dd style="white-space:normal;">${idea.descripcion}</dd>
                <dt>Nivel</dt><dd>${etiquetas.nivel(idea.nivel)}</dd>
                <dt>L&iacute;nea de investigaci&oacute;n</dt><dd>${idea.lineaInvestigacion}</dd>
                <dt>Modalidad</dt><dd>${etiquetas.modalidad(idea.modalidad)}</dd>
                <dt>Origen</dt><dd>${idea.deEstudiante ? 'Propuesta de estudiante' : 'Banco'}</dd>
                <dt>Estado</dt><dd>${etiquetas.estadoIdea(idea.estado)}</dd>
                <dt>Disponible</dt><dd>${idea.disponible ? 'S&iacute;' : 'No'}</dd>
                <c:if test="${not empty idea.nombreProponente}"><dt>Proponente (docente)</dt><dd>${idea.nombreProponente}</dd></c:if>
                <c:if test="${not empty idea.nombreEstudiante}"><dt>Estudiante proponente</dt><dd>${idea.nombreEstudiante}</dd></c:if>
                <c:if test="${not empty idea.fechaCreacion}"><dt>Fecha de propuesta</dt><dd>${fn:substring(idea.fechaCreacion, 0, 16)}</dd></c:if>
                <c:choose>
                    <c:when test="${idea.estado eq 'APROBADA'}"><dt>Fecha de aprobaci&oacute;n</dt><dd>${empty idea.fechaRevision ? '-' : fn:substring(idea.fechaRevision, 0, 16)}</dd></c:when>
                    <c:when test="${idea.estado eq 'RECHAZADA'}"><dt>Fecha de rechazo</dt><dd>${empty idea.fechaRevision ? '-' : fn:substring(idea.fechaRevision, 0, 16)}</dd></c:when>
                </c:choose>
                <c:if test="${not empty idea.nombreRevisor}"><dt>Revisada por</dt><dd>${idea.nombreRevisor}</dd></c:if>
                <c:if test="${not empty idea.observaciones}"><dt>Observaciones</dt><dd style="white-space:normal;">${idea.observaciones}</dd></c:if>
            </dl>
        </div>
        <div class="form-actions">
            <c:choose>
                <c:when test="${sessionScope.usuarioSesion.rol eq 'ESTUDIANTE'}">
                    <a class="btn btn-outline" href="${ctx}/vistas/ideas/banco.jsp">Volver al banco de ideas</a>
                </c:when>
                <c:when test="${sessionScope.usuarioSesion.rol eq 'COMITE' or sessionScope.usuarioSesion.rol eq 'ADMIN'}">
                    <a class="btn btn-outline" href="${ctx}/vistas/ideas/trazabilidad.jsp">Volver</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline" href="${ctx}/vistas/ideas/banco-ideas.jsp">Volver al banco de ideas</a>
                </c:otherwise>
            </c:choose>
            <c:if test="${idea.disponible and sessionScope.usuarioSesion.rol eq 'ESTUDIANTE' and idea.estado eq 'APROBADA'}">
                <a class="btn btn-primary" href="${ctx}/vistas/proyectos/inscribir.jsp?ideaId=${idea.id}">Inscribir proyecto</a>
            </c:if>
        </div>
    </c:if>
</t:pagina>
