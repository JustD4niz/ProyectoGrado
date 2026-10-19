<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cfg" class="co.edu.grado.control.ConfigBean" scope="request" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Calendario por fase" roles="ADMIN" activo="vistas/config/calendario.jsp">
    <div class="card">
        <h2>Definir fechas m&iacute;nimas/m&aacute;ximas de cada fase</h2>
        <form method="post" action="${ctx}/acciones/config/calendario.jsp">
            <div class="grid grid-3">
                <div class="field">
                    <label for="faseSeleccionada">Fase</label>
                    <select id="faseSeleccionada" name="faseSeleccionada" required>
                        <option value="">Seleccione...</option>
                        <option value="PAGO">Fase 1 - Pago</option>
                        <option value="PROPUESTA_I">Fase 2 - Propuesta I (F-DC 124)</option>
                        <option value="PROPUESTA_II">Fase 3 - Propuesta II</option>
                        <option value="INFORME_I">Fase 4 - Informe I</option>
                        <option value="INFORME_II">Fase 5 - Informe II (F-DC 125)</option>
                        <option value="SUSTENTACION">Fase 6 - Sustentaci&oacute;n</option>
                        <option value="CERRADO">Cierre</option>
                    </select>
                </div>
                <div class="field"><label for="faseMin">Fecha m&iacute;nima</label><input type="date" id="faseMin" name="faseMin" /></div>
                <div class="field"><label for="faseMax">Fecha m&aacute;xima</label><input type="date" id="faseMax" name="faseMax" /></div>
            </div>
            <div class="field">
                <label for="faseResponsable">Responsable</label>
                <select id="faseResponsable" name="faseResponsable">
                    <option value="COMITE">Comit&eacute;</option>
                    <option value="DIRECTOR">Director</option>
                    <option value="EVALUADOR">Evaluador</option>
                    <option value="ESTUDIANTE">Estudiante</option>
                </select>
            </div>
            <button class="btn btn-primary" type="submit">Guardar</button>
        </form>
    </div>

    <div class="card">
        <h2>Calendario actual</h2>
        <table class="tabla">
            <thead><tr><th>Fase</th><th>Responsable</th><th>Fecha m&iacute;nima</th><th>Fecha m&aacute;xima</th></tr></thead>
            <tbody>
                <c:forEach var="f" items="${cfg.fases}">
                    <tr>
                        <td>${f.fase}</td>
                        <td>${f.responsable}</td>
                        <td>${empty f.fechaMinima ? '-' : f.fechaMinima}</td>
                        <td>${empty f.fechaMaxima ? '-' : f.fechaMaxima}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty cfg.fases}"><div class="empty">No hay calendario definido.</div></c:if>
    </div>
</t:pagina>
