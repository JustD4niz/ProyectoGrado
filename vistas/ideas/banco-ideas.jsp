<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="ideas" class="co.edu.grado.control.IdeaBean" scope="request" />
<jsp:setProperty name="ideas" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${ideas}" property="pagina" value="${empty param.pagina ? 1 : param.pagina}" />
<c:set target="${ideas}" property="busqueda" value="${param.q}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Banco de ideas" roles="DIRECTOR EVALUADOR DOCENTE" activo="vistas/ideas/banco-ideas.jsp">
    <div class="card">
        <h2>Banco de ideas</h2>
        <p class="muted">Todas las ideas y propuestas registradas (del banco y de estudiantes), con su estado de revisi&oacute;n.</p>
        <form class="filter-bar" method="get" action="${ctx}/vistas/ideas/banco-ideas.jsp">
            <div class="field" style="flex:2;">
                <label for="q">Buscar</label>
                <input id="q" name="q" type="text" placeholder="ID, t&iacute;tulo, l&iacute;nea o descripci&oacute;n"
                       value="${param.q}" />
            </div>
        </form>

        <c:choose>
            <c:when test="${empty ideas.lista}">
                <div class="empty">No hay ideas registradas.</div>
            </c:when>
            <c:otherwise>
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>Código</th><th>T&iacute;tulo</th><th>Origen</th><th>Nivel</th>
                            <th>Modalidad</th><th>Estado</th><th>Tomada</th><th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="idea" items="${ideas.lista}">
                            <tr>
                                <td>${idea.codigo}</td>
                                <td>${idea.titulo}</td>
                                <td>${idea.deEstudiante ? 'Propuesta de estudiante' : 'Banco'}</td>
                                <td>${etiquetas.nivel(idea.nivel)}</td>
                                <td>${etiquetas.modalidad(idea.modalidad)}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${idea.estado eq 'APROBADA'}"><span class="tag tag-ok">Aprobada</span></c:when>
                                        <c:when test="${idea.estado eq 'RECHAZADA'}"><span class="tag tag-error">Rechazada</span></c:when>
                                        <c:otherwise><span class="tag tag-pendiente">Pendiente</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${idea.disponible ? 'No' : 'S&iacute;'}</td>
                                <td>
                                    <a class="btn btn-sm btn-outline" href="${ctx}/vistas/ideas/detalle.jsp?id=${idea.id}">Ver completa</a>
                                </td>
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
                       href="${ctx}/vistas/ideas/banco-ideas.jsp?pagina=${i}&q=${param.q}">${i}</a>
                </c:forEach>
            </div>
        </c:if>
    </div>
</t:pagina>
