<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="ideaDAO" class="co.edu.grado.dao.IdeaDAO" scope="request" />
<c:set var="idea" value="${empty param.ideaId ? null : ideaDAO.porId(param.ideaId)}" />
<c:set var="u" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Inscribir proyecto de grado" roles="ESTUDIANTE" activo="vistas/ideas/banco.jsp">
    <c:if test="${empty idea}">
        <div class="card"><div class="empty">Debe seleccionar una idea aprobada.</div></div>
    </c:if>
    <c:if test="${not empty idea}">
        <div class="card">
            <h2>Idea seleccionada</h2>
            <div class="dl">
                <dt>T&iacute;tulo</dt><dd>${idea.titulo}</dd>
                <dt>Modalidad</dt><dd>${idea.modalidad}</dd>
                <dt>Nivel</dt><dd>${idea.nivel}</dd>
                <dt>L&iacute;nea</dt><dd>${idea.lineaInvestigacion}</dd>
                <dt>Descripci&oacute;n</dt><dd>${idea.descripcion}</dd>
            </div>
        </div>

        <div class="card">
            <h2>Datos del proyecto</h2>
            <form method="post" action="${ctx}/acciones/proyectos/inscribir.jsp">
                <input type="hidden" name="ideaId" value="${idea.id}" />
                <div class="field">
                    <label for="titulo">T&iacute;tulo definitivo (opcional)</label>
                    <input type="text" id="titulo" name="titulo" placeholder="Dejar vac&iacute;o para usar el t&iacute;tulo de la idea" />
                </div>
                <div class="field">
                    <label for="objetivos">Objetivos generales y espec&iacute;ficos (RF20)</label>
                    <textarea id="objetivos" name="objetivos" rows="5" required></textarea>
                </div>
                <div class="field">
                    <label for="modalidad">Modalidad (opcional, se tomar&aacute; la de la idea)</label>
                    <select id="modalidad" name="modalidad">
                        <option value="">Mantener: ${idea.modalidad}</option>
                        <option value="INVESTIGACION">Proyecto de Investigaci&oacute;n</option>
                        <option value="DESARROLLO">Desarrollo Tecnol&oacute;gico</option>
                        <option value="MONOGRAFIA">Monograf&iacute;a</option>
                    </select>
                </div>
                <div class="field">
                    <label for="cedulas">C&eacute;dulas de los integrantes</label>
                    <input type="text" id="cedulas" name="cedulas" placeholder="Ej. 12345678, 87654321" />
                    <small class="hint">Para <strong>Monograf&iacute;a</strong> m&aacute;ximo 2 integrantes; para <strong>Investigaci&oacute;n o Desarrollo</strong> m&aacute;ximo 3. Solo estudiantes activos del mismo nivel.</small>
                </div>
                <div class="form-actions">
                    <button class="btn btn-primary" type="submit"
                            data-confirmar="Al inscribir el proyecto, se tomar&aacute; la idea seleccionada. &iquest;Deseas continuar?">Inscribir proyecto</button>
                    <a class="btn btn-outline" href="${ctx}/vistas/ideas/banco.jsp">Cancelar</a>
                </div>
            </form>
        </div>
    </c:if>
</t:pagina>
