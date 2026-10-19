<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cfg" class="co.edu.grado.control.ConfigBean" scope="request" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Periodos acad&eacute;micos" roles="ADMIN" activo="vistas/config/periodo.jsp">
    <div class="card">
        <h2>Periodo acad&eacute;mico activo</h2>
        <c:set var="p" value="${cfg.periodoActivo}" />
        <c:choose>
            <c:when test="${empty p}">
                <p class="muted">No hay periodo activo. Registre un periodo con el formulario de abajo.</p>
            </c:when>
            <c:otherwise>
                <div class="dl">
                    <dt>Nombre</dt><dd>${p.nombre}</dd>
                    <dt>Inicio</dt><dd>${p.fechaInicio}</dd>
                    <dt>Fin</dt><dd>${p.fechaFin}</dd>
                    <dt>D&iacute;as duraci&oacute;n</dt><dd>${p.diasDuracion}</dd>
                    <dt>M&iacute;n d&iacute;as informe</dt><dd>${p.diasMinInforme}</dd>
                    <dt>M&aacute;x pr&oacute;rrogas</dt><dd>${p.maxProrrogas}</dd>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="card">
        <h2>Agregar nuevo periodo</h2>
        <p class="muted">El nuevo periodo quedar&aacute; activo y el anterior pasar&aacute; al hist&oacute;rico. Se generar&aacute;n fechas por defecto para las fases 1 y 2.</p>
        <form method="post" action="${ctx}/acciones/config/periodo.jsp">
            <div class="grid grid-3">
                <div class="field"><label for="nombre">Nombre</label><input type="text" id="nombre" name="nombre" placeholder="Ej. 2026-2" required /></div>
                <div class="field"><label for="fechaInicio">Fecha inicio</label><input type="date" id="fechaInicio" name="fechaInicio" required /></div>
                <div class="field"><label for="fechaFin">Fecha fin</label><input type="date" id="fechaFin" name="fechaFin" required /></div>
                <div class="field"><label for="semanaPagoMax">Semanas m&aacute;x pago</label><input type="number" id="semanaPagoMax" name="semanaPagoMax" value="8" /></div>
                <div class="field"><label for="semanaPropuestaMax">Semanas m&aacute;x propuesta</label><input type="number" id="semanaPropuestaMax" name="semanaPropuestaMax" value="12" /></div>
                <div class="field"><label for="diasDuracion">D&iacute;as duraci&oacute;n</label><input type="number" id="diasDuracion" name="diasDuracion" value="180" /></div>
                <div class="field"><label for="diasMinInforme">D&iacute;as m&iacute;n informe</label><input type="number" id="diasMinInforme" name="diasMinInforme" value="90" /></div>
                <div class="field"><label for="maxProrrogas">M&aacute;x pr&oacute;rrogas</label><input type="number" id="maxProrrogas" name="maxProrrogas" value="2" /></div>
            </div>
            <button class="btn btn-primary" type="submit">Crear periodo</button>
        </form>
    </div>

    <div class="card">
        <h2>Hist&oacute;rico de periodos</h2>
        <p class="muted">Consulte las fechas del calendario en la opci&oacute;n <strong>Calendario</strong> del men&uacute;.</p>
        <table class="tabla">
            <thead><tr><th>Nombre</th><th>Inicio</th><th>Fin</th><th>Estado</th></tr></thead>
            <tbody>
                <c:forEach var="per" items="${cfg.periodos}">
                    <tr>
                        <td>${per.nombre}</td>
                        <td>${per.fechaInicio}</td>
                        <td>${per.fechaFin}</td>
                        <td>
                            <c:choose>
                                <c:when test="${per.activo}"><span class="tag tag-aprobada">Activo</span></c:when>
                                <c:otherwise><span class="muted">Inactivo</span></c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty cfg.periodos}"><div class="empty">Sin periodos registrados.</div></c:if>
    </div>
</t:pagina>
