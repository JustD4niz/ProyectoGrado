<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="usuarioDAO" class="co.edu.grado.dao.UsuarioDAO" scope="request" />
<c:set var="edit" value="${empty param.id ? null : usuarioDAO.porId(param.id)}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="${empty edit ? 'Nuevo usuario' : 'Editar usuario'}" roles="ADMIN" activo="vistas/usuarios/listar.jsp">
    <div class="card">
        <form method="post" action="${ctx}/acciones/usuarios/guardar.jsp">
            <input type="hidden" name="id" value="${edit.id}" />
            <div class="grid grid-2">
                <div class="field">
                    <label for="cedula">C&eacute;dula *</label>
                    <input type="text" id="cedula" name="cedula" value="${edit.cedula}" maxlength="10" required />
                    <small class="hint">Entre 6 y 10 d&iacute;gitos.</small>
                </div>
                <div class="field">
                    <label for="nombre">Nombre completo *</label>
                    <input type="text" id="nombre" name="nombre" value="${edit.nombre}" required />
                </div>
                <div class="field">
                    <label for="email">Correo institucional *</label>
                    <input type="email" id="email" name="email" value="${edit.email}" required />
                </div>
                <div class="field">
                    <label for="rol">Rol *</label>
                    <select id="rol" name="rol" required>
                        <option value="">Seleccione...</option>
                        <option value="ADMIN" ${edit.rol eq 'ADMIN' ? 'selected' : ''}>Administrador</option>
                        <option value="COMITE" ${edit.rol eq 'COMITE' ? 'selected' : ''}>Comit&eacute;</option>
                        <option value="DOCENTE" ${edit.rol eq 'DOCENTE' ? 'selected' : ''}>Docente (director y evaluador)</option>
                        <option value="DIRECTOR" ${edit.rol eq 'DIRECTOR' ? 'selected' : ''}>Director</option>
                        <option value="EVALUADOR" ${edit.rol eq 'EVALUADOR' ? 'selected' : ''}>Evaluador</option>
                        <option value="ESTUDIANTE" ${edit.rol eq 'ESTUDIANTE' ? 'selected' : ''}>Estudiante</option>
                    </select>
                </div>
                <div class="field">
                    <label for="nivel">Nivel (solo estudiantes)</label>
                    <select id="nivel" name="nivel">
                        <option value="">No aplica</option>
                        <option value="TECNOLOGIA" ${edit.nivel eq 'TECNOLOGIA' ? 'selected' : ''}>Tecnolog&iacute;a</option>
                        <option value="INGENIERIA" ${edit.nivel eq 'INGENIERIA' ? 'selected' : ''}>Ingenier&iacute;a</option>
                    </select>
                </div>
                <c:if test="${empty edit}">
                    <div class="field">
                        <label for="password">Contrase&ntilde;a inicial *</label>
                        <input type="password" id="password" name="password" minlength="6" required />
                        <small class="hint">M&iacute;nimo 6 caracteres.</small>
                    </div>
                </c:if>
            </div>

            <div class="field checkbox">
                <input type="checkbox" id="activo" name="activo" value="true" ${empty edit or edit.activo ? 'checked' : ''} />
                <label for="activo" style="margin:0;">Usuario activo</label>
            </div>
            <div class="field checkbox">
                <input type="checkbox" id="consentimiento" name="consentimiento" value="true" ${not empty edit and edit.consentimientoDatos ? 'checked' : ''} />
                <label for="consentimiento" style="margin:0;">Autorizaci&oacute;n de tratamiento de datos personales</label>
            </div>

            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Guardar</button>
                <a class="btn btn-outline" href="${ctx}/vistas/usuarios/listar.jsp">Cancelar</a>
            </div>
        </form>
    </div>
</t:pagina>
