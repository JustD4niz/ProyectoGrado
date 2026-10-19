<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="rep" class="co.edu.grado.control.ReporteBean" scope="request" />
<jsp:setProperty name="rep" property="periodoId" value="${param.periodoId}" />
<jsp:setProperty name="rep" property="tipo" value="${empty param.tipo ? 'estudiante' : param.tipo}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Reportes" roles="COMITE ADMIN" activo="vistas/reportes/index.jsp">
    <div class="card">
        <form class="filter-bar" method="get" action="${ctx}/vistas/reportes/index.jsp">
            <div class="field">
                <label for="tipo">Tipo de reporte</label>
                <select id="tipo" name="tipo" data-auto>
                    <option value="estudiante" ${rep.tipo eq 'estudiante' ? 'selected' : ''}>Proyectos por estudiante</option>
                    <option value="director" ${rep.tipo eq 'director' ? 'selected' : ''}>Carga por director</option>
                    <option value="evaluador" ${rep.tipo eq 'evaluador' ? 'selected' : ''}>Carga por evaluador</option>
                    <option value="modalidad" ${rep.tipo eq 'modalidad' ? 'selected' : ''}>Por modalidad</option>
                    <option value="nivel" ${rep.tipo eq 'nivel' ? 'selected' : ''}>Por nivel</option>
                    <option value="fase" ${rep.tipo eq 'fase' ? 'selected' : ''}>Por fase</option>
                    <option value="estudiantes_proyecto" ${rep.tipo eq 'estudiantes_proyecto' ? 'selected' : ''}>Estudiantes por proyecto</option>
                    <option value="aprueban" ${rep.tipo eq 'aprueban' ? 'selected' : ''}>Aprueban informe final</option>
                    <option value="no_aprueban" ${rep.tipo eq 'no_aprueban' ? 'selected' : ''}>No aprueban informe final</option>
                </select>
            </div>
            <div class="field">
                <label for="periodoId">Periodo</label>
                <select id="periodoId" name="periodoId" data-auto>
                    <option value="">Todos</option>
                    <c:forEach var="p" items="${rep.periodos}">
                        <option value="${p.id}" ${rep.periodoId eq p.id ? 'selected' : ''}>${p.nombre}</option>
                    </c:forEach>
                </select>
            </div>
        </form>

        <div class="page-head">
            <h2 style="margin:0;">${rep.titulo}</h2>
            <div class="form-actions">
                <a class="btn btn-outline btn-sm" href="${ctx}/acciones/exportar-excel.jsp?tipo=${rep.tipo}&periodoId=${rep.periodoId}">Exportar Excel</a>
                <a class="btn btn-outline btn-sm" href="${ctx}/acciones/exportar-pdf.jsp?tipo=${rep.tipo}&periodoId=${rep.periodoId}">Exportar PDF</a>
            </div>
        </div>

        <table class="tabla">
            <thead>
                <tr>
                    <c:forEach var="c" items="${rep.columnas}"><th>${c}</th></c:forEach>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="fila" items="${rep.filas}">
                    <tr>
                        <c:forEach var="entry" items="${fila}">
                            <td>${entry.value eq null ? '-' : entry.value}</td>
                        </c:forEach>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty rep.filas}"><div class="empty">Sin datos para los filtros seleccionados.</div></c:if>
    </div>
</t:pagina>
