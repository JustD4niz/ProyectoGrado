<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="usuarios" class="co.edu.grado.control.UsuarioBean" scope="request" />
<c:set target="${usuarios}" property="filtroRol" value="${param.rol}" />
<c:set target="${usuarios}" property="busqueda" value="${param.q}" />
<c:set target="${usuarios}" property="pagina" value="${empty param.pagina ? 1 : param.pagina}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:pagina titulo="Usuarios" roles="ADMIN" activo="vistas/usuarios/listar.jsp">
    <div class="card">
        <form class="filter-bar" method="get" action="${ctx}/vistas/usuarios/listar.jsp">
            <div class="field">
                <label for="rol">Rol</label>
                <select id="rol" name="rol" data-auto>
                    <option value="">Todos</option>
                    <option value="ADMIN" ${param.rol eq 'ADMIN' ? 'selected' : ''}>Administrador</option>
                    <option value="COMITE" ${param.rol eq 'COMITE' ? 'selected' : ''}>Comit&eacute;</option>
                    <option value="DIRECTOR" ${param.rol eq 'DIRECTOR' ? 'selected' : ''}>Director</option>
                    <option value="EVALUADOR" ${param.rol eq 'EVALUADOR' ? 'selected' : ''}>Evaluador</option>
                    <option value="ESTUDIANTE" ${param.rol eq 'ESTUDIANTE' ? 'selected' : ''}>Estudiante</option>
                </select>
            </div>
            <div class="field" style="flex:1;min-width:220px;">
                <label for="q">Buscar por nombre, c&eacute;dula o correo</label>
                <input type="text" id="q" name="q" value="${param.q}" placeholder="Ej. Juan o 12345" />
            </div>
            <div class="field"><button class="btn btn-primary" type="submit">Buscar</button></div>
            <div class="field"><a class="btn btn-outline" href="${ctx}/vistas/usuarios/formulario.jsp">Nuevo usuario</a></div>
        </form>

        <c:choose>
            <c:when test="${empty usuarios.lista}">
                <div class="empty">No hay usuarios que coincidan con el filtro.</div>
            </c:when>
            <c:otherwise>
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>C&eacute;dula</th><th>Nombre</th><th>Correo</th>
                            <th>Rol</th><th>Nivel</th><th>Estado</th><th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="us" items="${usuarios.lista}">
                            <tr>
                                <td>${us.cedula}</td>
                                <td>${us.nombre}</td>
                                <td>${us.email}</td>
                                <td><span class="tag">${etiquetas.rol(us.rol)}</span></td>
                                <td>${empty us.nivel ? '-' : etiquetas.nivel(us.nivel)}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${us.activo}"><span class="tag tag-ok">Activo</span></c:when>
                                        <c:otherwise><span class="tag tag-error">Inactivo</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="acciones">
                                    <a class="btn btn-sm btn-outline" href="${ctx}/vistas/usuarios/formulario.jsp?id=${us.id}">Editar</a>
                                    <a class="btn btn-sm btn-danger" data-confirmar="Eliminar a ${us.nombre}? Esta acci&oacute;n no se puede deshacer."
                                       href="${ctx}/acciones/usuarios/eliminar.jsp?id=${us.id}">Eliminar</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <c:if test="${usuarios.totalPaginas > 1}">
            <div class="pagination">
                <c:forEach var="i" begin="1" end="${usuarios.totalPaginas}">
                    <a class="btn btn-sm ${i eq usuarios.pagina ? 'active' : 'btn-outline'}"
                       href="${ctx}/vistas/usuarios/listar.jsp?pagina=${i}&rol=${param.rol}&q=${param.q}">${i}</a>
                </c:forEach>
            </div>
        </c:if>
    </div>
</t:pagina>
