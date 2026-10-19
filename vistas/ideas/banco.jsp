<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="ideas" class="co.edu.grado.control.IdeaBean" scope="request" />
<jsp:setProperty name="ideas" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${ideas}" property="pagina" value="${empty param.pagina ? 1 : param.pagina}" />
<c:set target="${ideas}" property="busqueda" value="${param.q}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Banco de ideas" roles="ESTUDIANTE" activo="vistas/ideas/banco.jsp">
    <div class="card">
        <h2>&iquest;Tienes tu propia propuesta?</h2>
        <p class="muted">Puedes registrar una propuesta propia y enviarla al Comit&eacute; para su evaluaci&oacute;n.</p>
        <a class="btn btn-primary" href="${ctx}/vistas/ideas/formulario.jsp">Registrar propuesta</a>
    </div>

    <div class="card">
        <form class="filter-bar" method="get" action="${ctx}/vistas/ideas/banco.jsp">
            <div class="field" style="flex:2;">
                <label for="q">Buscar</label>
                <input id="q" name="q" type="text" placeholder="T&iacute;tulo, l&iacute;nea o descripci&oacute;n"
                       value="${param.q}" />
            </div>
        </form>

        <c:choose>
            <c:when test="${empty ideas.banco}">
                <div class="empty">No hay ideas disponibles para tu nivel en este momento.</div>
            </c:when>
            <c:otherwise>
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>T&iacute;tulo</th>
                            <th>Nivel</th>
                            <th>Modalidad</th>
                            <th>Tomada</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="idea" items="${ideas.banco}">
                            <tr>
                                <td>${idea.codigo}</td>
                                <td>${idea.titulo}</td>
                                <td>${etiquetas.nivel(idea.nivel)}</td>
                                <td>${etiquetas.modalidad(idea.modalidad)}</td>
                                <td>${idea.disponible ? 'No' : 'S&iacute;'}</td>
                                <td>
                                    <a class="btn btn-sm btn-outline" href="${ctx}/vistas/ideas/detalle.jsp?id=${idea.id}">Ver completa</a>
                                    <c:if test="${idea.disponible}">
                                        <a class="btn btn-sm btn-primary" href="${ctx}/vistas/proyectos/inscribir.jsp?ideaId=${idea.id}">Inscribir proyecto</a>
                                    </c:if>
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
                       href="${ctx}/vistas/ideas/banco.jsp?pagina=${i}&q=${param.q}">${i}</a>
                </c:forEach>
            </div>
        </c:if>
    </div>
</t:pagina>
