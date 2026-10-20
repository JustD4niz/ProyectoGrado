<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="etiquetas" class="co.edu.grado.util.Etiquetas" scope="request" />
<jsp:useBean id="faseB" class="co.edu.grado.control.FaseBean" scope="request" />
<jsp:setProperty name="faseB" property="usuarioSesion" value="${sessionScope.usuarioSesion}" />
<c:set target="${faseB}" property="proyectoId" value="${empty param.p ? 0 : param.p}" />
<c:set var="p" value="${faseB.proyecto}" />
<c:set var="u" value="${sessionScope.usuarioSesion}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="faseAct" value="${p.faseActual}" />
<c:set var="expandida" value="${empty param.f ? faseAct : param.f}" />
<c:set var="enFlujo" value="${p.estado eq 'EN_FLUJO'}" />
<c:set var="esRevisor" value="${u.rol eq 'COMITE' or u.esDocente()}" />
<c:set var="dirF" value="${faseB.getDirector()}" />
<c:set var="evaF" value="${faseB.getEvaluador()}" />
<c:set var="autorizado" value="${u.rol eq 'COMITE' or u.rol eq 'ADMIN' or (not empty dirF and dirF.docenteId eq u.id) or (not empty evaF and evaF.docenteId eq u.id) or (u.rol eq 'ESTUDIANTE' and faseB.esIntegranteDelProyecto)}" />

<t:pagina titulo="Fases y documentos" activo="vistas/proyectos/mis-proyectos.jsp">
    <c:if test="${not empty flashExito}"><div class="alert alert-ok">${flashExito}</div><c:remove var="flashExito" scope="session" /></c:if>
    <c:if test="${not empty flashError}"><div class="alert alert-error">${flashError}</div><c:remove var="flashError" scope="session" /></c:if>

    <c:if test="${empty p}"><div class="card"><div class="empty">Proyecto no encontrado.</div></div></c:if>

    <c:if test="${not empty p and not autorizado}"><div class="card"><div class="empty">No tienes permisos sobre este proyecto.</div></div></c:if>

    <c:if test="${not empty p and autorizado}">
        <div class="barra-fase">
            <c:choose>
                <c:when test="${u.rol eq 'ESTUDIANTE'}">
                    <a class="btn btn-outline btn-sm" href="${ctx}/vistas/proyectos/mi-proyecto.jsp">&larr; Volver a Mi proyecto</a>
                </c:when>
                <c:when test="${u.rol eq 'COMITE' or u.rol eq 'ADMIN'}">
                    <a class="btn btn-outline btn-sm" href="${ctx}/vistas/proyectos/detalle.jsp?p=${p.id}">&larr; Volver al proyecto</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline btn-sm" href="${ctx}/vistas/proyectos/gestion.jsp?p=${p.id}">&larr; Volver al proyecto</a>
                </c:otherwise>
            </c:choose>
            <span class="muted">${p.codigo} &middot; ${p.titulo}</span>
        </div>

        <c:if test="${not enFlujo}">
            <div class="alert alert-ok">Proyecto ${etiquetas.estadoProyecto(p.estado)} &mdash; solo lectura.</div>
        </c:if>

        <!-- ============ DETALLE DE FASE ============ -->
        <c:forEach var="f" items="${faseB.fases}">
            <c:if test="${f.fase eq expandida}">
                <c:set var="esActual" value="${f.fase eq faseAct and enFlujo and faseB.puedeEditar(f.fase)}" />
                <div class="phase-detail">
                    <header>
                        <h3>${etiquetas.fase(f.fase)}</h3>
                        <span>
                            <span class="tag ${f.estado eq 'APROBADA' ? 'tag-ok' : (f.estado eq 'RECHAZADA' ? 'tag-error' : 'tag-pendiente')}">${etiquetas.estadoFase(f.estado)}</span>
                            <span class="tag">${etiquetas.responsable(f.responsable)}</span>
                        </span>
                    </header>
                    <div class="body">
                        <table class="tabla">
                            <thead>
                                <tr><th>Tipo</th><th>Versi&oacute;n</th><th>Autor</th><th>Estado</th>
                                    <th>V.&nbsp;bueno</th><th>Fecha</th><th>Enlace</th>
                                    <c:if test="${enFlujo}"><th>Revisi&oacute;n</th></c:if></tr>
                            </thead>
                            <tbody>
                                <c:set var="hayDocs" value="false" />
                                <c:forEach var="d" items="${faseB.documentos}">
                                    <c:if test="${d.fase eq f.fase
                                        or (f.fase eq 'PROPUESTA_II' and d.fase eq 'PROPUESTA_I')
                                        or (f.fase eq 'INFORME_II' and d.fase eq 'INFORME_I')
                                        or (f.fase eq 'SUSTENTACION' and (d.fase eq 'INFORME_I' or d.fase eq 'INFORME_II'))}">
                                        <c:set var="hayDocs" value="true" />
                                        <tr>
                                            <td>${etiquetas.tipoDocumento(d.tipo)}</td>
                                            <td>${d.version}</td>
                                            <td>${d.nombreAutor}</td>
                                            <td>${etiquetas.estadoDocumento(d.estado)}</td>
                                            <td>${d.vistoBuenoDirector ? 'S&iacute;' : 'No'}</td>
                                            <td>${empty d.fecha ? '-' : fn:substring(d.fecha, 0, 16)}</td>
                                            <td><a href="${d.url}" target="_blank" rel="noopener">Abrir</a></td>
                                            <c:if test="${enFlujo}">
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${d.estado eq 'PENDIENTE' and u.esDocente() and not d.vistoBuenoDirector and d.tipo eq 'INFORME_FDC125'}">
                                                            <form method="post" action="${ctx}/acciones/fases/visto-bueno.jsp" class="acciones-inline">
                                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                                <input type="hidden" name="documentoId" value="${d.id}" />
                                                                <button class="btn btn-sm btn-primary" type="submit">Dar visto bueno</button>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>-</c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </c:if>
                                        </tr>
                                        <c:if test="${not empty d.observaciones}">
                                            <tr class="fila-obs"><td colspan="${enFlujo ? 8 : 7}"><small class="muted">Observaciones: ${d.observaciones}</small></td></tr>
                                        </c:if>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${not hayDocs}">
                                    <tr><td colspan="${enFlujo ? 8 : 7}" class="muted">A&uacute;n no hay documentos en esta fase.</td></tr>
                                </c:if>
                            </tbody>
                        </table>

                        <!-- ============ ACCIONES DE LA FASE ============ -->
                        <c:if test="${esActual}">
                            <div class="phase-acciones">
                                <!-- PAGO -->
                                <c:if test="${f.fase eq 'PAGO'}">
                                    <c:choose>
                                        <c:when test="${u.rol eq 'ESTUDIANTE'}">
                                            <form method="post" action="${ctx}/acciones/fases/registrar-documento.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <input type="hidden" name="fase" value="PAGO" />
                                                <input type="hidden" name="tipoDocumento" value="DESPRENDIBLE_PAGO" />
                                                <h4>Cargar desprendible de pago</h4>
                                                <div class="field"><label>URL del desprendible</label><input type="text" name="url" placeholder="https://..." required /></div>
                                                <button class="btn btn-primary" type="submit">Registrar</button>
                                            </form>
                                        </c:when>
                                        <c:when test="${u.rol eq 'COMITE'}">
                                            <form method="post" action="${ctx}/acciones/fases/pago.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <h4>Revisi&oacute;n del pago</h4>
                                                <div class="field"><label>Observaciones</label><input type="text" name="observaciones" /></div>
                                                <div class="form-actions">
                                                    <button class="btn btn-primary" type="submit" name="accion" value="aprobar">Aprobar pago</button>
                                                    <button class="btn btn-outline" type="submit" name="accion" value="rechazar">Rechazar</button>
                                                </div>
                                            </form>
                                        </c:when>
                                    </c:choose>
                                </c:if>

                                <!-- PROPUESTA_I -->
                                <c:if test="${f.fase eq 'PROPUESTA_I'}">
                                    <c:choose>
                                        <c:when test="${u.rol eq 'ESTUDIANTE'}">
                                            <form method="post" action="${ctx}/acciones/fases/registrar-documento.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <input type="hidden" name="fase" value="PROPUESTA_I" />
                                                <input type="hidden" name="tipoDocumento" value="PROPUESTA_FDC124" />
                                                <h4>Cargar propuesta F-DC 124</h4>
                                                <div class="field"><label>URL de la propuesta</label><input type="text" name="url" placeholder="https://..." required /></div>
                                                <button class="btn btn-primary" type="submit">Registrar</button>
                                            </form>
                                        </c:when>
                                        <c:when test="${u.esDocente()}">
                                            <form method="post" action="${ctx}/acciones/fases/propuesta-director.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <h4>Concepto del director</h4>
                                                <div class="field"><label>Observaciones</label><input type="text" name="observaciones" /></div>
                                                <div class="form-actions">
                                                    <button class="btn btn-primary" type="submit" name="accion" value="aprobar">Aprobar</button>
                                                    <button class="btn btn-outline" type="submit" name="accion" value="rechazar">Rechazar</button>
                                                </div>
                                            </form>
                                        </c:when>
                                    </c:choose>
                                </c:if>

                                <!-- PROPUESTA_II -->
                                <c:if test="${f.fase eq 'PROPUESTA_II'}">
                                    <c:choose>
                                        <c:when test="${u.esDocente()}">
                                            <form method="post" action="${ctx}/acciones/fases/propuesta-evaluador.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <h4>Concepto del evaluador</h4>
                                                <div class="field"><label>Concepto</label>
                                                    <select name="concepto" required>
                                                        <option value="">Seleccione...</option>
                                                        <option value="APROBADO">Aprobado</option>
                                                        <option value="APLAZADO">Aplazado</option>
                                                        <option value="RECHAZADO">Rechazado</option>
                                                    </select>
                                                </div>
                                                <button class="btn btn-primary" type="submit">Registrar concepto</button>
                                            </form>
                                        </c:when>
                                        <c:when test="${u.rol eq 'COMITE'}">
                                            <form method="post" action="${ctx}/acciones/fases/propuesta-comite.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <h4>Concepto final del Comit&eacute;</h4>
                                                <div class="field"><label>Concepto</label>
                                                    <select name="concepto" required>
                                                        <option value="">Seleccione...</option>
                                                        <option value="APROBADO">Aprobado</option>
                                                        <option value="RECHAZADO">Rechazado</option>
                                                    </select>
                                                </div>
                                                <div class="field"><label>Observaciones</label><input type="text" name="observaciones" /></div>
                                                <button class="btn btn-primary" type="submit">Emitir concepto</button>
                                            </form>
                                        </c:when>
                                    </c:choose>
                                </c:if>

                                <!-- INFORME_I -->
                                <c:if test="${f.fase eq 'INFORME_I'}">
                                    <c:choose>
                                        <c:when test="${u.rol eq 'ESTUDIANTE'}">
                                            <form method="post" action="${ctx}/acciones/fases/registrar-documento.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <input type="hidden" name="fase" value="INFORME_I" />
                                                <input type="hidden" name="tipoDocumento" value="INFORME_FDC125" />
                                                <h4>Cargar informe F-DC 125</h4>
                                                <div class="field"><label>URL del informe</label><input type="text" name="url" placeholder="https://..." required /></div>
                                                <button class="btn btn-primary" type="submit">Registrar</button>
                                            </form>
                                            <c:if test="${not faseB.informeHabilitado(p)}">
                                                <p class="hint">A&uacute;n no habilitado: ${faseB.motivoBloqueoInforme(p)}
                                                    &middot; <a href="${ctx}/acciones/fases/saltar-espera.jsp?proyectoId=${p.id}" style="font-size:.8rem;">saltar espera (demo)</a>
                                                </p>
                                            </c:if>
                                        </c:when>
                                        <c:when test="${u.esDocente()}">
                                            <form method="post" action="${ctx}/acciones/fases/avanzar-informe.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <h4>Enviar a revisi&oacute;n del evaluador</h4>
                                                <p class="hint">Requiere el visto bueno del informe y al menos 2 seguimientos.</p>
                                                <button class="btn btn-primary" type="submit">Avanzar a Informe II</button>
                                            </form>
                                            <form method="post" action="${ctx}/acciones/fases/seguimiento.jsp" class="form-accion">
                                                <input type="hidden" name="proyectoId" value="${p.id}" />
                                                <h4>Registrar seguimiento</h4>
                                                <div class="grid grid-2">
                                                    <div class="field"><label>Fecha</label><input type="date" name="seguimientoFecha" /></div>
                                                    <div class="field"><label>URL de evidencia</label><input type="text" name="seguimientoUrl" placeholder="https://..." /></div>
                                                </div>
                                                <div class="field"><label>Descripci&oacute;n</label><input type="text" name="seguimientoDescripcion" required /></div>
                                                <button class="btn btn-primary" type="submit">Registrar seguimiento</button>
                                            </form>
                                        </c:when>
                                    </c:choose>
                                </c:if>

                                <!-- INFORME_II -->
                                <c:if test="${f.fase eq 'INFORME_II'}">
                                    <c:if test="${not empty faseB.evaluacionRubrica}">
                                        <div class="card" style="margin-bottom:12px;background:var(--gris-suave);">
                                            <h4>Evaluaci&oacute;n del evaluador</h4>
                                            <p><strong>Nota final:</strong> ${faseB.evaluacionRubrica.notaFinal} / 5.0
                                            &middot; <strong>Obj. general:</strong> ${faseB.evaluacionRubrica.cumpleObjetivoGeneral ? 'Sí' : 'No'}
                                            &middot; <strong>Obj. espec&iacute;ficos:</strong> ${faseB.evaluacionRubrica.cumpleObjetivosEspecificos ? 'Sí' : 'No'}</p>
                                            <c:if test="${not empty faseB.evaluacionRubrica.observaciones}">
                                                <p class="muted">Observaciones: ${faseB.evaluacionRubrica.observaciones}</p>
                                            </c:if>
                                        </div>
                                    </c:if>
                                    <c:if test="${u.esDocente()}">
                                        <form method="post" action="${ctx}/acciones/fases/evaluar-informe.jsp" class="form-accion">
                                            <input type="hidden" name="proyectoId" value="${p.id}" />
                                            <h4>Evaluaci&oacute;n del informe (r&uacute;brica)</h4>
                                            <div class="grid grid-3">
                                                <div class="field"><label>Objetivo general (0-5)</label><input type="number" name="c1" min="0" max="5" step="0.1" value="0" required /></div>
                                                <div class="field"><label>Objetivos espec&iacute;ficos (0-5)</label><input type="number" name="c2" min="0" max="5" step="0.1" value="0" required /></div>
                                                <div class="field"><label>Estructura (0-5)</label><input type="number" name="c3" min="0" max="5" step="0.1" value="0" required /></div>
                                                <div class="field"><label>Resultados (0-5)</label><input type="number" name="c4" min="0" max="5" step="0.1" value="0" required /></div>
                                                <div class="field"><label>Referencias (0-5)</label><input type="number" name="c5" min="0" max="5" step="0.1" value="0" required /></div>
                                            </div>
                                            <div class="grid grid-2">
                                                <label class="checkbox"><input type="checkbox" name="cumpleObjetivoGeneral" value="true" /> Cumple objetivo general</label>
                                                <label class="checkbox"><input type="checkbox" name="cumpleObjetivosEspecificos" value="true" /> Cumple objetivos espec&iacute;ficos</label>
                                            </div>
                                            <div class="field" style="margin-top:10px;"><label>Observaciones</label><input type="text" name="observaciones" /></div>
                                            <button class="btn btn-primary" type="submit">Guardar evaluaci&oacute;n</button>
                                        </form>
                                    </c:if>
                                </c:if>

                                <!-- SUSTENTACION -->
                                <c:if test="${f.fase eq 'SUSTENTACION'}">
                                    <c:if test="${not empty faseB.evaluacionRubrica}">
                                        <div class="card" style="margin-bottom:12px;background:var(--gris-suave);">
                                            <h4>Evaluaci&oacute;n del informe (fase anterior)</h4>
                                            <p><strong>Nota:</strong> ${faseB.evaluacionRubrica.notaFinal} / 5.0
                                            &middot; <strong>Obj. general:</strong> ${faseB.evaluacionRubrica.cumpleObjetivoGeneral ? 'Sí' : 'No'}
                                            &middot; <strong>Obj. espec&iacute;ficos:</strong> ${faseB.evaluacionRubrica.cumpleObjetivosEspecificos ? 'Sí' : 'No'}</p>
                                            <c:if test="${not empty faseB.evaluacionRubrica.observaciones}">
                                                <p class="muted">Observaciones: ${faseB.evaluacionRubrica.observaciones}</p>
                                            </c:if>
                                        </div>
                                    </c:if>
                                    <c:if test="${u.rol eq 'COMITE'}">
                                        <form method="post" action="${ctx}/acciones/fases/sustentacion.jsp" class="form-accion">
                                            <input type="hidden" name="proyectoId" value="${p.id}" />
                                            <h4>Registrar sustentaci&oacute;n</h4>
                                            <div class="grid grid-3">
                                                <div class="field"><label>Fecha</label><input type="date" name="sustentacionFecha" required /></div>
                                                <div class="field"><label>Calificaci&oacute;n (0-5)</label><input type="number" name="sustentacionCalificacion" min="0" max="5" step="0.1" value="0" required /></div>
                                                <div class="field"><label>URL del acta</label><input type="text" name="sustentacionActa" placeholder="https://..." /></div>
                                            </div>
                                            <button class="btn btn-primary" type="submit">Registrar sustentaci&oacute;n</button>
                                        </form>
                                        <form method="post" action="${ctx}/acciones/fases/cerrar.jsp" class="form-accion">
                                            <input type="hidden" name="proyectoId" value="${p.id}" />
                                            <h4>Cerrar proyecto</h4>
                                            <p class="hint">Solo si todas las fases est&aacute;n aprobadas.</p>
                                            <button class="btn btn-primary" type="submit">Finalizar proyecto</button>
                                        </form>
                                    </c:if>
                                </c:if>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </c:if>
</t:pagina>
