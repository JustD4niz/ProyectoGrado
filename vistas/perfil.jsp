<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="perfil" class="co.edu.grado.control.PerfilBean" scope="request" />
<jsp:setProperty name="perfil" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set var="u" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="editando" value="${param.editar eq '1'}" />

<t:pagina titulo="Mi perfil" activo="vistas/perfil.jsp">
    <div class="perfil-head">
        <div class="perfil-photo">
            <svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4.4 3.6-8 8-8s8 3.6 8 8"/></svg>
        </div>
        <div class="perfil-head-info">
            <h2>${u.nombre}</h2>
            <p class="muted">${u.email}</p>
            <span class="tag tag-ok">${etiquetas.rol(u.rol)}</span>
        </div>
    </div>

    <div class="grid grid-2">
        <div class="card">
            <h2>Datos personales</h2>
            <div class="dl">
                <dt>Nombre</dt><dd>${u.nombre}</dd>
                <dt>C&eacute;dula</dt><dd>${u.cedula}</dd>
                <dt>Correo</dt><dd>${u.email}</dd>
            </div>
        </div>
        <div class="card">
            <h2>Informaci&oacute;n acad&eacute;mica</h2>
            <div class="dl">
                <dt>Rol</dt><dd>${etiquetas.rol(u.rol)}</dd>
                <dt>Nivel</dt><dd>${empty u.nivel ? '-' : etiquetas.nivel(u.nivel)}</dd>
                <dt>Estado</dt><dd><span class="tag tag-ok">Activo</span></dd>
            </div>
        </div>
    </div>

    <div class="card">
        <c:choose>
            <c:when test="${editando}">
                <div class="perfil-edit-head">
                    <h2>Actualizar datos</h2>
                    <a class="btn btn-ghost btn-sm" href="${ctx}/vistas/perfil.jsp">Cerrar</a>
                </div>

                <h3>Datos de la cuenta</h3>
                <form method="post" action="${ctx}/acciones/perfil.jsp">
                    <input type="hidden" name="accion" value="datos" />
                    <div class="grid grid-2">
                        <div class="field"><label for="nombre">Nombre completo</label><input type="text" name="nombre" id="nombre" value="${u.nombre}" required /></div>
                        <div class="field"><label for="email">Correo electr&oacute;nico</label><input type="email" name="email" id="email" value="${u.email}" required /></div>
                    </div>
                    <button class="btn btn-primary" type="submit">Guardar datos</button>
                </form>

                <hr />

                <h3>Cambiar contrase&ntilde;a</h3>
                <form method="post" action="${ctx}/acciones/perfil.jsp">
                    <input type="hidden" name="accion" value="password" />
                    <div class="grid grid-3">
                        <div class="field"><label for="actual">Contrase&ntilde;a actual</label><input type="password" name="actual" id="actual" required /></div>
                        <div class="field"><label for="nueva">Nueva contrase&ntilde;a</label><input type="password" name="nueva" id="nueva" minlength="6" required /></div>
                        <div class="field"><label for="confirma">Confirmar nueva</label><input type="password" name="confirma" id="confirma" minlength="6" required /></div>
                    </div>
                    <button class="btn btn-primary" type="submit">Actualizar contrase&ntilde;a</button>
                </form>
            </c:when>
            <c:otherwise>
                <a class="btn btn-primary" href="${ctx}/vistas/perfil.jsp?editar=1">
                    <svg viewBox="0 0 24 24"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4z"/></svg>
                    Actualizar datos
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</t:pagina>
