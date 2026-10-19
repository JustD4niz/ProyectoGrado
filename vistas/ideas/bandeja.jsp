<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="ideas" class="co.edu.grado.control.IdeaBean" scope="request" />
<jsp:setProperty name="ideas" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Bandeja de ideas" roles="COMITE" activo="vistas/ideas/bandeja.jsp">
    <p class="muted">Ideas y propuestas pendientes de revisión por el Comité.</p>
    <c:choose>
        <c:when test="${empty ideas.bandeja}">
            <div class="card"><div class="empty">No hay ideas pendientes. ✓</div></div>
        </c:when>
        <c:otherwise>
            <c:forEach var="idea" items="${ideas.bandeja}">
                <div class="card">
                    <div class="page-head">
                        <div>
                            <span class="tag ${idea.deEstudiante ? 'tag-pendiente' : 'tag-gris'}">
                                ${idea.deEstudiante ? 'Propuesta de estudiante' : 'Idea de banco'}
                            </span>
                            <span class="tag">${etiquetas.modalidad(idea.modalidad)}</span>
                            <span class="tag">${etiquetas.nivel(idea.nivel)}</span>
                        </div>
                    </div>
                    <h2>${idea.titulo}</h2>
                    <p class="muted">${idea.descripcion}</p>
                    <p class="muted" style="font-size:.85rem;">
                        Línea: ${idea.lineaInvestigacion} ·
                        <c:choose>
                            <c:when test="${idea.deEstudiante}">Estudiante: ${idea.nombreEstudiante}</c:when>
                            <c:otherwise>Proponente: ${idea.nombreProponente}</c:otherwise>
                        </c:choose>
                    </p>
                    <div class="acciones-idea" style="display:flex;flex-direction:column;gap:12px">
                        <form method="post" action="${ctx}/acciones/ideas/aprobar.jsp" class="form-accion">
                            <input type="hidden" name="id" value="${idea.id}" />
                            <button class="btn btn-primary" type="submit">Aprobar</button>
                        </form>
                        <form method="post" action="${ctx}/acciones/ideas/rechazar.jsp" class="form-accion">
                            <input type="hidden" name="id" value="${idea.id}" />
                            <div class="field">
                                <label for="obs${idea.id}">Observaciones del rechazo</label>
                                <textarea id="obs${idea.id}" name="observaciones" rows="2" required></textarea>
                            </div>
                            <button class="btn btn-danger" type="submit">Rechazar</button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</t:pagina>