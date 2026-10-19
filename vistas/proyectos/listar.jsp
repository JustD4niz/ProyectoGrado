<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="proy" class="co.edu.grado.control.ProyectoBean" scope="request" />
<jsp:setProperty name="proy" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${proy}" property="pagina" value="${empty param.pagina ? 1 : param.pagina}" />
<c:set target="${proy}" property="busqueda" value="${param.q}" />
<c:set target="${proy}" property="fase" value="${param.fase}" />
<c:set target="${proy}" property="estado" value="${param.estado}" />
<c:set target="${proy}" property="nivel" value="${param.nivel}" />
<c:set target="${proy}" property="periodoId" value="${empty param.periodoId ? null : param.periodoId}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Proyectos" roles="COMITE ADMIN" activo="vistas/proyectos/listar.jsp">
    <div class="card">
        <form class="filter-bar" method="get" action="${ctx}/vistas/proyectos/listar.jsp">
            <div class="field"><label for="q">Buscar</label><input type="text" id="q" name="q" value="${param.q}" placeholder="C&oacute;digo, t&iacute;tulo" /></div>
            <div class="field"><label for="fase">Fase</label>
                <select id="fase" name="fase" data-auto>
                    <option value="">Todas</option>
                    <option value="PAGO" ${param.fase eq 'PAGO' ? 'selected' : ''}>1. Pago</option>
                    <option value="PROPUESTA_I" ${param.fase eq 'PROPUESTA_I' ? 'selected' : ''}>2. Propuesta I</option>
                    <option value="PROPUESTA_II" ${param.fase eq 'PROPUESTA_II' ? 'selected' : ''}>3. Propuesta II</option>
                    <option value="INFORME_I" ${param.fase eq 'INFORME_I' ? 'selected' : ''}>4. Informe I</option>
                    <option value="INFORME_II" ${param.fase eq 'INFORME_II' ? 'selected' : ''}>5. Informe II</option>
                    <option value="SUSTENTACION" ${param.fase eq 'SUSTENTACION' ? 'selected' : ''}>6. Sustentaci&oacute;n</option>
                    <option value="CERRADO" ${param.fase eq 'CERRADO' ? 'selected' : ''}>Cerrado</option>
                </select>
            </div>
            <div class="field"><label for="estado">Estado</label>
                <select id="estado" name="estado" data-auto>
                    <option value="">Todos</option>
                    <option value="EN_FLUJO" ${param.estado eq 'EN_FLUJO' ? 'selected' : ''}>En flujo</option>
                    <option value="FINALIZADO" ${param.estado eq 'FINALIZADO' ? 'selected' : ''}>Finalizado</option>
                    <option value="RECHAZADO" ${param.estado eq 'RECHAZADO' ? 'selected' : ''}>Rechazado</option>
                    <option value="VENCIDO" ${param.estado eq 'VENCIDO' ? 'selected' : ''}>Vencido</option>
                    <option value="CANCELADO" ${param.estado eq 'CANCELADO' ? 'selected' : ''}>Cancelado</option>
                </select>
            </div>
        </form>
        <table class="tabla">
            <thead><tr><th>C&oacute;digo</th><th>T&iacute;tulo</th><th>Nivel</th><th>Fase</th><th>Estado</th><th>Aviso</th><th>Acciones</th></tr></thead>
            <tbody>
                <c:forEach var="p" items="${proy.lista}">
                    <tr>
                        <td>${p.codigo}</td>
                        <td>${p.titulo}</td>
                        <td>${etiquetas.nivel(p.nivel)}</td>
                        <td>${etiquetas.fase(p.faseActual)}</td>
                        <td>${etiquetas.estadoProyecto(p.estado)}</td>
                        <td>
                            <c:choose>
                                <c:when test="${p.estado eq 'EN_FLUJO' and p.responsableActual eq 'COMITE'}">
                                    <span class="tag tag-pendiente">Requiere tu acci&oacute;n</span>
                                </c:when>
                                <c:otherwise><span class="muted">&mdash;</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="acciones"><a class="btn btn-sm btn-outline" href="${ctx}/vistas/proyectos/gestion.jsp?p=${p.id}">Detalles</a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty proy.lista}"><div class="empty">Sin proyectos.</div></c:if>
    </div>
</t:pagina>
