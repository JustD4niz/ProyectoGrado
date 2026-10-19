<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="ideas" class="co.edu.grado.control.IdeaBean" scope="request" />
<jsp:setProperty name="ideas" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${ideas}" property="filtroEstado" value="${param.estado}" />
<c:set target="${ideas}" property="filtroNivel" value="${param.nivel}" />
<c:set target="${ideas}" property="filtroOrigen" value="${param.origen}" />
<c:set target="${ideas}" property="pagina" value="${empty param.pagina ? 1 : param.pagina}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Trazabilidad de ideas" roles="COMITE ADMIN" activo="vistas/ideas/trazabilidad.jsp">
    <div class="card">
        <form class="filter-bar" method="get" action="${ctx}/vistas/ideas/trazabilidad.jsp">
            <div class="field">
                <label for="estado">Estado</label>
                <select id="estado" name="estado" data-auto>
                    <option value="">Todos</option>
                    <option value="PENDIENTE" ${param.estado eq 'PENDIENTE' ? 'selected' : ''}>Pendiente</option>
                    <option value="APROBADA" ${param.estado eq 'APROBADA' ? 'selected' : ''}>Aprobada</option>
                    <option value="RECHAZADA" ${param.estado eq 'RECHAZADA' ? 'selected' : ''}>Rechazada</option>
                </select>
            </div>
            <div class="field">
                <label for="nivel">Nivel</label>
                <select id="nivel" name="nivel" data-auto>
                    <option value="">Todos</option>
                    <option value="Tecnología" ${param.nivel eq 'Tecnología' ? 'selected' : ''}>Tecnolog&iacute;a</option>
                    <option value="INGENIERIA" ${param.nivel eq 'INGENIERIA' ? 'selected' : ''}>Ingenier&iacute;a</option>
                </select>
            </div>
            <div class="field">
                <label for="origen">Origen</label>
                <select id="origen" name="origen" data-auto>
                    <option value="">Todos</option>
                    <option value="BANCO" ${param.origen eq 'BANCO' ? 'selected' : ''}>Banco</option>
                    <option value="PROPUESTA_ESTUDIANTE" ${param.origen eq 'PROPUESTA_ESTUDIANTE' ? 'selected' : ''}>Propuesta de estudiante</option>
                </select>
            </div>
        </form>

        <c:choose>
            <c:when test="${empty ideas.trazabilidad}">
                <div class="empty">No hay ideas con los filtros seleccionados.</div>
            </c:when>
            <c:otherwise>
                <table class="tabla">
                    <thead>
                        <tr><th>Código</th><th>Fecha</th><th>T&iacute;tulo</th><th>Origen</th><th>Proponente</th>
                            <th>Nivel</th><th>Estado</th><th>Revis&oacute; por</th><th>Observaciones</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="idea" items="${ideas.trazabilidad}">
                            <tr>
                                <td>${idea.codigo}</td>
                                <td>${idea.fechaCreacion}</td>
                                <td>${idea.titulo}</td>
                                <td>${idea.deEstudiante ? 'Propuesta' : 'Banco'}</td>
                                <td>${idea.deEstudiante ? idea.nombreEstudiante : idea.nombreProponente}</td>
                                <td>${etiquetas.nivel(idea.nivel)}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${idea.estado eq 'APROBADA'}"><span class="tag tag-ok">Aprobada</span></c:when>
                                        <c:when test="${idea.estado eq 'RECHAZADA'}"><span class="tag tag-error">Rechazada</span></c:when>
                                        <c:otherwise><span class="tag tag-pendiente">Pendiente</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${empty idea.nombreRevisor ? '-' : idea.nombreRevisor}</td>
                                <td>${empty idea.observaciones ? '-' : idea.observaciones}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <c:if test="${ideas.totalPaginas > 1}">
            <div class="pagination">
                <c:forEach var="i" begin="1" end="${ideas.totalPaginas}">
                    <a class="btn btn-sm ${i eq ideas.pagina ? 'active' : 'btn-outline'}"
                       href="${ctx}/vistas/ideas/trazabilidad.jsp?pagina=${i}&estado=${param.estado}&nivel=${param.nivel}&origen=${param.origen}">${i}</a>
                </c:forEach>
            </div>
        </c:if>
    </div>
</t:pagina>
