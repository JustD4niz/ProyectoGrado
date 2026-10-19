<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="ideaDAO" class="co.edu.grado.dao.IdeaDAO" scope="request" />
<c:set var="edit" value="${empty param.id ? null : ideaDAO.porId(param.id)}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="u" value="${sessionScope.usuarioSesion}" />

<t:pagina titulo="${empty edit ? 'Nueva propuesta' : 'Editar propuesta'}" roles="ESTUDIANTE DIRECTOR EVALUADOR DOCENTE" activo="${u.rol eq 'ESTUDIANTE' ? 'vistas/ideas/banco.jsp' : 'vistas/ideas/mis-ideas.jsp'}">
    <div class="card">
        <form method="post" action="${ctx}/acciones/ideas/guardar.jsp">
            <input type="hidden" name="id" value="${edit.id}" />
            <div class="field">
                <label for="titulo">T&iacute;tulo *</label>
                <input type="text" id="titulo" name="titulo" value="${edit.titulo}" required />
            </div>
            <div class="field">
                <label for="descripcion">Descripci&oacute;n *</label>
                <textarea id="descripcion" name="descripcion" rows="5" required>${edit.descripcion}</textarea>
            </div>
            <div class="grid grid-2">
                <div class="field">
                    <label for="nivel">Nivel *</label>
                    <select id="nivel" name="nivel" required>
                        <option value="">Seleccione...</option>
                        <option value="TECNOLOGIA" ${(empty edit ? u.nivel : edit.nivel) eq 'TECNOLOGIA' ? 'selected' : ''}>Tecnolog&iacute;a</option>
                        <option value="INGENIERIA" ${(empty edit ? u.nivel : edit.nivel) eq 'INGENIERIA' ? 'selected' : ''}>Ingenier&iacute;a</option>
                    </select>
                    <small class="hint">Para estudiantes se toma su propio nivel.</small>
                </div>
                <div class="field">
                    <label for="modalidad">Modalidad *</label>
                    <select id="modalidad" name="modalidad" required>
                        <option value="">Seleccione...</option>
                        <option value="INVESTIGACION" ${edit.modalidad eq 'INVESTIGACION' ? 'selected' : ''}>Proyecto de Investigaci&oacute;n</option>
                        <option value="DESARROLLO" ${edit.modalidad eq 'DESARROLLO' ? 'selected' : ''}>Desarrollo Tecnol&oacute;gico</option>
                        <option value="MONOGRAFIA" ${edit.modalidad eq 'MONOGRAFIA' ? 'selected' : ''}>Monograf&iacute;a</option>
                    </select>
                </div>
            </div>
            <div class="field">
                <label for="lineaInvestigacion">L&iacute;nea de investigaci&oacute;n *</label>
                <input type="text" id="lineaInvestigacion" name="lineaInvestigacion" value="${edit.lineaInvestigacion}" required placeholder="Ej. Ingenier&iacute;a de Software y Sistemas Inteligentes" />
            </div>

            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Guardar</button>
                <a class="btn btn-outline" href="${ctx}/${u.rol eq 'ESTUDIANTE' ? 'vistas/ideas/banco.jsp' : 'vistas/ideas/mis-ideas.jsp'}">Cancelar</a>
            </div>
        </form>
    </div>
</t:pagina>
