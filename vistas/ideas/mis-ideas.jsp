<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="ideas" class="co.edu.grado.control.IdeaBean" scope="request" />
<jsp:setProperty name="ideas" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Banco de propuestas" roles="DIRECTOR EVALUADOR DOCENTE" activo="vistas/ideas/mis-ideas.jsp">
    <div class="card">
        <div class="page-head">
            <h2 style="margin:0;">Banco de propuestas</h2>
            <a class="btn btn-primary btn-sm" href="${ctx}/vistas/ideas/formulario.jsp">Nueva propuesta</a>
        </div>
        <p class="muted">Propuestas que has registrado en el banco. Solo se pueden editar o eliminar mientras est&eacute;n pendientes de revisi&oacute;n del Comitéeacute;.</p>
        <c:choose>
            <c:when test="${empty ideas.misIdeas}">
                <div class="empty">A&uacute;n no has registrado ideas en el banco.</div>
            </c:when>
            <c:otherwise>
                <table class="tabla">
                    <thead><tr><th>T&iacute;tulo</th><th>Nivel</th><th>Modalidad</th><th>Estado</th><th>Acciones</th></tr></thead>
                    <tbody>
                        <c:forEach var="idea" items="${ideas.misIdeas}">
                            <tr>
                                <td>${idea.titulo}</td>
                                <td>${etiquetas.nivel(idea.nivel)}</td>
                                <td>${etiquetas.modalidad(idea.modalidad)}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${idea.estado eq 'APROBADA'}"><span class="tag tag-ok">Aprobada</span></c:when>
                                        <c:when test="${idea.estado eq 'RECHAZADA'}"><span class="tag tag-error">Rechazada</span></c:when>
                                        <c:otherwise><span class="tag tag-pendiente">Pendiente</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="acciones">
                                    <a class="btn btn-sm btn-outline" href="${ctx}/vistas/ideas/detalle.jsp?id=${idea.id}">Ver detalles</a>
                                    <c:if test="${idea.estado eq 'PENDIENTE'}">
                                        <a class="btn btn-sm btn-outline" href="${ctx}/vistas/ideas/formulario.jsp?id=${idea.id}">Editar</a>
                                        <a class="btn btn-sm btn-danger" data-confirmar="Eliminar la propuesta '${idea.titulo}'?"
                                           href="${ctx}/acciones/ideas/eliminar.jsp?id=${idea.id}">Eliminar</a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</t:pagina>
