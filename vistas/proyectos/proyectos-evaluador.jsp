<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="proy" class="co.edu.grado.control.ProyectoBean" scope="request" />
<jsp:setProperty name="proy" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Proyectos como evaluador" roles="DIRECTOR EVALUADOR DOCENTE" activo="vistas/proyectos/proyectos-evaluador.jsp">
    <div class="card">
        <h2>Proyectos como evaluador</h2>
        <p class="muted">Proyectos en los que participas como evaluador.</p>
        <c:set var="lista" value="${proy.getMisProyectos('EVALUADOR')}" />
        <c:choose>
            <c:when test="${empty lista}">
                <div class="empty">No tienes proyectos asignados como evaluador.</div>
            </c:when>
            <c:otherwise>
                <table class="tabla">
                    <thead><tr><th>C&oacute;digo</th><th>T&iacute;tulo</th><th>Fase</th><th>Estado</th><th>Aviso</th><th>Acciones</th></tr></thead>
                    <tbody>
                        <c:forEach var="p" items="${lista}">
                            <tr>
                                <td>${p.codigo}</td>
                                <td>${p.titulo}</td>
                                <td>${etiquetas.fase(p.faseActual)}</td>
                                <td>${etiquetas.estadoProyecto(p.estado)}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.estado eq 'EN_FLUJO' and p.responsableActual eq 'EVALUADOR'}">
                                            <span class="tag tag-pendiente">Requiere tu revisi&oacute;n</span>
                                        </c:when>
                                        <c:otherwise><span class="muted">&mdash;</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="acciones">
                                    <a class="btn btn-sm btn-outline" href="${ctx}/vistas/proyectos/gestion.jsp?p=${p.id}">Detalles</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</t:pagina>
