package co.edu.grado.control;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.dao.DocumentoDAO;
import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.dao.PeriodoDAO;
import co.edu.grado.dao.ProyectoDAO;
import co.edu.grado.dao.RubricaDAO;
import co.edu.grado.dao.UsuarioDAO;
import co.edu.grado.modelo.AsignacionDocente;
import co.edu.grado.modelo.Documento;
import co.edu.grado.modelo.FaseProyecto;
import co.edu.grado.modelo.Integrante;
import co.edu.grado.modelo.Periodo;
import co.edu.grado.modelo.Proyecto;
import co.edu.grado.modelo.RubricaItem;
import co.edu.grado.modelo.RubricaResultado;
import co.edu.grado.modelo.Seguimiento;
import co.edu.grado.modelo.Sustentacion;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.Correo;
import co.edu.grado.util.FechaUtil;
import co.edu.grado.util.Notificador;
import co.edu.grado.util.ValidacionUtil;

/**
 * Flujo del proyecto por fases: pago, propuesta I, propuesta II, informe I,
 * informe II, sustentacion y cierre. Solo el responsable de la fase puede editarla. (RF27 - RF47)
 */
public class FaseBean {

    private Usuario usuarioSesion;
    private int proyectoId;

    private String fase;
    private String tipoDocumento;
    private String url;
    private String observaciones;
    private int documentoId;
    private String concepto;

    private double c1;
    private double c2;
    private double c3;
    private double c4;
    private double c5;
    private boolean cumpleObjetivoGeneral;
    private boolean cumpleObjetivosEspecificos;

    private String seguimientoDescripcion;
    private String seguimientoFecha;
    private String seguimientoUrl;

    private String sustentacionFecha;
    private double sustentacionCalificacion;
    private String sustentacionActa;

    private String mensaje;
    private String error;

    private final ProyectoDAO proyectoDAO = new ProyectoDAO();
    private final DocumentoDAO documentoDAO = new DocumentoDAO();
    private final RubricaDAO rubricaDAO = new RubricaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PeriodoDAO periodoDAO = new PeriodoDAO();

    public void setUsuarioSesion(Usuario u) { this.usuarioSesion = u; }
    public Usuario getUsuarioSesion() { return usuarioSesion; }
    public void setProyectoId(int v) { this.proyectoId = v; }
    public int getProyectoId() { return proyectoId; }
    public void setFase(String v) { this.fase = v; }
    public String getFase() { return fase; }
    public void setTipoDocumento(String v) { this.tipoDocumento = v; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setUrl(String v) { this.url = v; }
    public String getUrl() { return url; }
    public void setObservaciones(String v) { this.observaciones = v; }
    public String getObservaciones() { return observaciones; }
    public void setDocumentoId(int v) { this.documentoId = v; }
    public int getDocumentoId() { return documentoId; }
    public void setConcepto(String v) { this.concepto = v; }
    public String getConcepto() { return concepto; }
    public void setC1(double v) { this.c1 = v; }
    public void setC2(double v) { this.c2 = v; }
    public void setC3(double v) { this.c3 = v; }
    public void setC4(double v) { this.c4 = v; }
    public void setC5(double v) { this.c5 = v; }
    public void setCumpleObjetivoGeneral(boolean v) { this.cumpleObjetivoGeneral = v; }
    public void setCumpleObjetivosEspecificos(boolean v) { this.cumpleObjetivosEspecificos = v; }
    public void setSeguimientoDescripcion(String v) { this.seguimientoDescripcion = v; }
    public void setSeguimientoFecha(String v) { this.seguimientoFecha = v; }
    public void setSeguimientoUrl(String v) { this.seguimientoUrl = v; }
    public void setSustentacionFecha(String v) { this.sustentacionFecha = v; }
    public void setSustentacionCalificacion(double v) { this.sustentacionCalificacion = v; }
    public void setSustentacionActa(String v) { this.sustentacionActa = v; }
    public String getMensaje() { return mensaje; }
    public String getError() { return error; }

    // ------------------------------------------------------------------
    // REGISTRO DE DOCUMENTOS (URL)
    // ------------------------------------------------------------------

    /** RF28, RF30, RF38: el estudiante registra la URL de un documento de la fase. */
    public boolean registrarDocumentoEstudiante() {
        error = null;
        mensaje = null;
        if (!ValidacionUtil.esUrl(url)) {
            error = "Debe ingresar una URL valida (http:// o https://).";
            return false;
        }
        Proyecto p = proyectoDAO.porId(proyectoId);
        if (p == null) {
            error = "Proyecto no encontrado.";
            return false;
        }
        if (!"EN_FLUJO".equals(p.getEstado())) {
            error = "El proyecto no esta en flujo.";
            return false;
        }
        // RF37: el informe final solo se habilita tras los 3 meses
        if ("INFORME_FDC125".equals(tipoDocumento) && !informeHabilitado(p)) {
            error = "Aun no puede entregar el informe final: " + motivoBloqueoInforme(p);
            return false;
        }
        Documento d = new Documento();
        d.setProyectoId(proyectoId);
        d.setFase(fase);
        d.setTipo(tipoDocumento);
        d.setUrl(url.trim());
        d.setAutorId(usuarioSesion.getId());
        d.setVistoBuenoDirector(false);
        documentoDAO.registrar(d);
        proyectoDAO.actualizarEstadoFase(proyectoId, fase, "EN_REVISION");
        if ("PAGO".equals(fase) && faltanDesprendibles(p)) {
            // aun faltan integrantes por subir su desprendible: el responsable
            // sigue siendo ESTUDIANTE para que los demas puedan cargar el suyo.
            proyectoDAO.actualizarFase(proyectoId, "PAGO", "ESTUDIANTE");
        } else {
            avanzarResponsable(fase, p);
        }
        mensaje = "Documento registrado correctamente.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "DOCUMENTO", proyectoId, "REGISTRAR",
                "Registro " + tipoDocumento + " en fase " + fase + ": " + url);
        notificarRevision(p, fase, tipoDocumento);
        return true;
    }

    /** RF29, RF31, RF38, RF39, RF40: decision (aprobar/rechazar) con observaciones. */
    public boolean decidirDocumento(boolean aprobar) {
        error = null;
        mensaje = null;
        Documento d = documentoDAO.porId(documentoId);
        if (d == null) {
            error = "Documento no encontrado.";
            return false;
        }
        Proyecto p = proyectoDAO.porId(proyectoId);
        if (aprobar) {
            documentoDAO.actualizarEstado(documentoId, "APROBADO", observaciones);
            mensaje = "Documento aprobado.";
        } else {
            if (ValidacionUtil.esVacio(observaciones)) {
                error = "Debe registrar las observaciones del rechazo.";
                return false;
            }
            documentoDAO.actualizarEstado(documentoId, "RECHAZADO", observaciones);
            proyectoDAO.actualizarEstadoFase(proyectoId, fase, "RECHAZADA");
            mensaje = "Documento rechazado. El proyecto vuelve al estudiante para corregir.";
        }
        new HistorialDAO().registrar(usuarioSesion.getId(), "DOCUMENTO", documentoId,
                aprobar ? "APROBAR" : "RECHAZAR", d.getTipo() + " - " + (observaciones == null ? "" : observaciones));
        notificarEstudiantes(p, mensaje + " (" + d.getTipo() + ")");
        return true;
    }

    /** RF38: visto bueno del director al informe final. */
    public boolean darVistoBueno() {
        error = null;
        mensaje = null;
        Documento d = documentoDAO.porId(documentoId);
        if (d == null) {
            error = "Documento no encontrado.";
            return false;
        }
        documentoDAO.darVistoBueno(documentoId);
        mensaje = "Visto bueno registrado. Puede enviar el informe al evaluador.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "DOCUMENTO", documentoId, "VISTO_BUENO",
                "Visto bueno del director al informe final");
        return true;
    }

    // ------------------------------------------------------------------
    // FASE PAGO (RF28, RF29)
    // ------------------------------------------------------------------

    /** RF28: valida que cada integrante tenga su desprendible y aprueba el pago. */
    public boolean aprobarPago() {
        error = null;
        mensaje = null;
        List<Integrante> integrantes = proyectoDAO.integrantes(proyectoId);
        List<Documento> docs = documentoDAO.listarPorProyectoFase(proyectoId, "PAGO");
        List<String> faltantes = new ArrayList<>();
        for (Integrante i : integrantes) {
            if (!i.isActivo()) {
                continue;
            }
            boolean tiene = false;
            for (Documento d : docs) {
                if ("DESPRENDIBLE_PAGO".equals(d.getTipo())
                        && d.getAutorId() == i.getEstudianteId()
                        && !"RECHAZADO".equals(d.getEstado())) {
                    tiene = true;
                    break;
                }
            }
            if (!tiene) {
                faltantes.add(i.getNombre());
            }
        }
        if (!faltantes.isEmpty()) {
            error = "No se puede aprobar: faltan desprendibles de pago de " + String.join(", ", faltantes) + ".";
            return false;
        }
        for (Documento d : docs) {
            if ("DESPRENDIBLE_PAGO".equals(d.getTipo()) && !"RECHAZADO".equals(d.getEstado())) {
                documentoDAO.actualizarEstado(d.getId(), "APROBADO", d.getObservaciones());
            }
        }
        proyectoDAO.actualizarEstadoFase(proyectoId, "PAGO", "APROBADA");
        proyectoDAO.actualizarFase(proyectoId, "PROPUESTA_I", "ESTUDIANTE");
        mensaje = "Pago aprobado. El proyecto avanza a Fase 2 - Propuesta I (F-DC 124).";
        new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "APROBAR_PAGO",
                "Fase de pago aprobada");
        Proyecto p = proyectoDAO.porId(proyectoId);
        notificarEstudiantes(p, "Su pago fue aprobado. Puede cargar la propuesta F-DC 124.");
        return true;
    }

    /** Indica si algun integrante activo aun no tiene desprendible de pago valido. */
    private boolean faltanDesprendibles(Proyecto p) {
        List<Integrante> integrantes = proyectoDAO.integrantes(p.getId());
        List<Documento> docs = documentoDAO.listarPorProyectoFase(p.getId(), "PAGO");
        for (Integrante i : integrantes) {
            if (!i.isActivo()) {
                continue;
            }
            boolean tiene = false;
            for (Documento d : docs) {
                if ("DESPRENDIBLE_PAGO".equals(d.getTipo())
                        && d.getAutorId() == i.getEstudianteId()
                        && !"RECHAZADO".equals(d.getEstado())) {
                    tiene = true;
                    break;
                }
            }
            if (!tiene) {
                return true;
            }
        }
        return false;
    }

    /** RF29: rechaza la documentacion de pago con observaciones. */
    public boolean rechazarPago() {
        error = null;
        mensaje = null;
        if (ValidacionUtil.esVacio(observaciones)) {
            error = "Debe registrar las observaciones del rechazo.";
            return false;
        }
        List<Documento> docs = documentoDAO.listarPorProyectoFase(proyectoId, "PAGO");
        for (Documento d : docs) {
            if ("DESPRENDIBLE_PAGO".equals(d.getTipo()) && !"APROBADO".equals(d.getEstado())) {
                documentoDAO.actualizarEstado(d.getId(), "RECHAZADO", observaciones);
            }
        }
        proyectoDAO.actualizarEstadoFase(proyectoId, "PAGO", "RECHAZADA");
        proyectoDAO.actualizarFase(proyectoId, "PAGO", "ESTUDIANTE");
        mensaje = "Documentación de pago rechazada.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "RECHAZAR_PAGO",
                observaciones);
        notificarEstudiantes(proyectoDAO.porId(proyectoId), "Su pago fue rechazado: " + observaciones);
        return true;
    }

    // ------------------------------------------------------------------
    // FASE PROPUESTA (RF30 - RF34)
    // ------------------------------------------------------------------

    /** RF31: el director aprueba la propuesta y habilita al evaluador. */
    public boolean aprobarPropuestaDirector(boolean aprobar) {
        error = null;
        mensaje = null;
        Proyecto p = proyectoDAO.porId(proyectoId);
        Documento doc = documentoDAO.ultimo(proyectoId, "PROPUESTA_FDC124");
        if (aprobar) {
            if (doc != null) {
                documentoDAO.actualizarEstado(doc.getId(), "APROBADO", doc.getObservaciones());
            }
            proyectoDAO.actualizarEstadoFase(proyectoId, "PROPUESTA_I", "APROBADA");
            proyectoDAO.actualizarFase(proyectoId, "PROPUESTA_II", "EVALUADOR");
            mensaje = "Propuesta I aprobada por el director. Pasa a Propuesta II (Evaluador/Comité).";
            notificarEvaluador(p, "La propuesta del proyecto " + p.getCodigo() + " requiere su concepto.");
        } else {
            if (ValidacionUtil.esVacio(observaciones)) {
                error = "Debe registrar las observaciones del rechazo.";
                return false;
            }
            if (doc != null) {
                documentoDAO.actualizarEstado(doc.getId(), "RECHAZADO", observaciones);
            }
            proyectoDAO.actualizarEstadoFase(proyectoId, "PROPUESTA_I", "RECHAZADA");
            proyectoDAO.actualizarFase(proyectoId, "PROPUESTA_I", "ESTUDIANTE");
            mensaje = "Propuesta rechazada. El estudiante debe corregir y reenviar.";
            notificarEstudiantes(p, "Su propuesta fue rechazada por el director: " + observaciones);
        }
        new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId,
                aprobar ? "APROBAR_PROPUESTA_DIRECTOR" : "RECHAZAR_PROPUESTA_DIRECTOR", observaciones);
        return true;
    }

    /** RF32: el evaluador emite su concepto de la propuesta. */
    public boolean conceptoEvaluadorPropuesta() {
        error = null;
        mensaje = null;
        if (ValidacionUtil.esVacio(concepto)) {
            error = "Debe seleccionar un concepto.";
            return false;
        }
        Proyecto p = proyectoDAO.porId(proyectoId);
        proyectoDAO.registrarConceptoEvaluador(proyectoId, concepto);
        proyectoDAO.actualizarFase(proyectoId, "PROPUESTA_II", "COMITE");
        mensaje = "Concepto del evaluador registrado. El Comité emite el concepto final.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "CONCEPTO_EVALUADOR",
                "Concepto propuesta: " + concepto);
        notificarComite("El evaluador emitió su concepto (" + concepto + ") de la propuesta "
                + p.getCodigo() + ". Se requiere el concepto final del Comité.");
        return true;
    }

    /** RF33: concepto final del Comite; prevalece sobre el evaluador. */
    public boolean conceptoFinalComite() {
        error = null;
        mensaje = null;
        Proyecto p = proyectoDAO.porId(proyectoId);
        if ("APROBADO".equals(concepto)) {
            Periodo per = periodoDAO.activo();
            int dias = per != null ? per.getDiasDuracion() : 180;
            Documento doc = documentoDAO.ultimo(proyectoId, "PROPUESTA_FDC124");
            if (doc != null) {
                documentoDAO.actualizarEstado(doc.getId(), "APROBADO", doc.getObservaciones());
            }
            proyectoDAO.aprobarPropuesta(proyectoId, dias); // RF36
            proyectoDAO.actualizarEstadoFase(proyectoId, "PROPUESTA_II", "APROBADA");
            proyectoDAO.actualizarFase(proyectoId, "INFORME_I", "ESTUDIANTE");
            mensaje = "Propuesta aprobada definitivamente. Inicia el conteo de " + dias + " días. Avanza a Informe I.";
            new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "APROBAR_PROPUESTA",
                    "Concepto final del Comité: APROBADO");
            notificarInvolucrados(p, "La propuesta del proyecto " + p.getCodigo()
                    + " fue aprobada. Puede cargar F-DC 125 en Informe I.");
        } else {
            if (ValidacionUtil.esVacio(observaciones)) {
                error = "El rechazo requiere observaciones (RF40).";
                return false;
            }
            Documento doc = documentoDAO.ultimo(proyectoId, "PROPUESTA_FDC124");
            if (doc != null) {
                documentoDAO.actualizarEstado(doc.getId(), "RECHAZADO", observaciones);
            }
            proyectoDAO.rechazarPropuesta(proyectoId, "RECHAZADO");
            proyectoDAO.actualizarEstadoFase(proyectoId, "PROPUESTA_II", "RECHAZADA");
            proyectoDAO.actualizarFase(proyectoId, "PROPUESTA_I", "ESTUDIANTE");
            mensaje = "Propuesta rechazada. El estudiante debe corregir y reenviar (vuelve a Fase 2 - Propuesta I).";
            new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "RECHAZAR_PROPUESTA",
                    observaciones);
            notificarEstudiantes(p, "La propuesta fue rechazada por el Comité: " + observaciones);
        }
        return true;
    }

    // ------------------------------------------------------------------
    // INFORME I/II
    // ------------------------------------------------------------------

    /** RF37, RF42: habilita la entrega del informe final. */
    public boolean avanzarInformeFinal() {
        error = null;
        mensaje = null;
        Proyecto p = proyectoDAO.porId(proyectoId);
        if (p.getFechaAprobacionPropuesta() == null) {
            error = "La propuesta aún no ha sido aprobada.";
            return false;
        }
        if (!informeHabilitado(p)) {
            error = "Aun no puede entregar el informe final: " + motivoBloqueoInforme(p);
            return false;
        }
        if (proyectoDAO.contarSeguimientos(proyectoId) < 2) {
            error = "El director debe registrar al menos dos seguimientos academicos antes de la entrega.";
            return false;
        }
        Documento informe = documentoDAO.ultimo(proyectoId, "INFORME_FDC125");
        if (informe == null || !informe.isVistoBuenoDirector()) {
            error = "El director debe dar el visto bueno al informe final antes de enviarlo al evaluador.";
            return false;
        }
            proyectoDAO.actualizarEstadoFase(proyectoId, "INFORME_I", "APROBADA");
            proyectoDAO.actualizarFase(proyectoId, "INFORME_II", "EVALUADOR");
            mensaje = "Avanzado a Informe II para revisión del F-DC 125.";
            new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "AVANZAR_INFORME",
                    "Avanzado a Informe II");
            notificarEvaluador(p, "El informe final del proyecto " + p.getCodigo()
                    + " está listo para su evaluación (plazo: 5 días hábiles).");
        return true;
    }

    /** RF42: registra un seguimiento del director. */
    public boolean registrarSeguimiento() {
        error = null;
        mensaje = null;
        if (ValidacionUtil.esVacio(seguimientoDescripcion)) {
            error = "Debe describir el seguimiento.";
            return false;
        }
        if (!ValidacionUtil.esVacio(seguimientoUrl) && !ValidacionUtil.esUrl(seguimientoUrl)) {
            error = "La URL de evidencia no es valida.";
            return false;
        }
        Seguimiento s = new Seguimiento();
        s.setProyectoId(proyectoId);
        s.setDirectorId(usuarioSesion.getId());
        s.setFecha(parseFecha(seguimientoFecha, FechaUtil.hoy()));
        s.setDescripcion(seguimientoDescripcion);
        s.setUrlEvidencia(ValidacionUtil.esVacio(seguimientoUrl) ? null : seguimientoUrl.trim());
        proyectoDAO.crearSeguimiento(s);
        mensaje = "Seguimiento registrado.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "SEGUIMIENTO",
                seguimientoDescripcion);
        return true;
    }

    /** RF39: el evaluador evalua el informe final aplicando la rejilla. */
    public boolean evaluarInforme() {
        error = null;
        mensaje = null;
        if (!cumpleObjetivoGeneral || !cumpleObjetivosEspecificos) {
            error = "Debe verificar el cumplimiento del objetivo general y de los especificos.";
            return false;
        }
        double total = c1 + c2 + c3 + c4 + c5;
        double nota = total / 5.0; // promedio de 5 criterios (cada uno 0-5) => 0-5
        String[] criterios = {
            "Cumplimiento del objetivo general",
            "Cumplimiento de los objetivos especificos",
            "Estructura y metodologia",
            "Resultados y conclusiones",
            "Referencias y ortografia"
        };
        double[] puntajes = { c1, c2, c3, c4, c5 };
        for (int i = 0; i < criterios.length; i++) {
            RubricaItem item = new RubricaItem();
            item.setProyectoId(proyectoId);
            item.setEvaluadorId(usuarioSesion.getId());
            item.setCriterio(criterios[i]);
            item.setPuntaje(puntajes[i]);
            item.setMaximo(5.0);
            item.setObservaciones(observaciones);
            rubricaDAO.guardarItem(item);
        }
        RubricaResultado r = new RubricaResultado();
        r.setProyectoId(proyectoId);
        r.setEvaluadorId(usuarioSesion.getId());
        r.setCumpleObjetivoGeneral(cumpleObjetivoGeneral);
        r.setCumpleObjetivosEspecificos(cumpleObjetivosEspecificos);
        r.setNotaFinal(nota);
        r.setObservaciones(observaciones);
        rubricaDAO.guardarResultado(r);

        Proyecto p = proyectoDAO.porId(proyectoId);
        boolean aprueba = nota >= 3.0 && cumpleObjetivoGeneral && cumpleObjetivosEspecificos;
        Documento informe = documentoDAO.ultimo(proyectoId, "INFORME_FDC125");
        if (aprueba) {
            if (informe != null) {
                documentoDAO.actualizarEstado(informe.getId(), "APROBADO", observaciones);
            }
            proyectoDAO.actualizarEstadoFase(proyectoId, "INFORME_II", "APROBADA");
            proyectoDAO.actualizarFase(proyectoId, "SUSTENTACION", "COMITE");
            mensaje = "Informe final aprobado con nota " + String.format("%.2f", nota)
                    + ". Pasa a sustentación.";
            new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "EVALUAR_INFORME",
                    "Informe aprobado, nota " + nota);
            notificarComite("El informe final del proyecto " + p.getCodigo()
                    + " fue aprobado. Programe la sustentación.");
            notificarEstudiantes(p, "Su informe final fue aprobado con nota " + String.format("%.2f", nota) + ".");
        } else {
            if (ValidacionUtil.esVacio(observaciones)) {
                error = "Al no aprobar debe registrar las observaciones (RF40).";
                return false;
            }
            if (informe != null) {
                documentoDAO.actualizarEstado(informe.getId(), "RECHAZADO", observaciones);
            }
            proyectoDAO.actualizarEstadoFase(proyectoId, "INFORME_II", "RECHAZADA");
            proyectoDAO.actualizarFase(proyectoId, "INFORME_I", "ESTUDIANTE");
            mensaje = "Informe final no aprobado. El estudiante debe corregir y reenviar (vuelve a Informe I).";
            new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "RECHAZAR_INFORME",
                    observaciones);
            notificarEstudiantes(p, "Su informe final no fue aprobado: " + observaciones);
        }
        return true;
    }

    // ------------------------------------------------------------------
    // SUSTENTACION Y CIERRE (RF43, RF44)
    // ------------------------------------------------------------------

    /** RF43: registra fecha y calificacion de la sustentacion. */
    public boolean registrarSustentacion() {
        error = null;
        mensaje = null;
        if (ValidacionUtil.esVacio(sustentacionFecha)) {
            error = "Debe indicar la fecha de la sustentación.";
            return false;
        }
        if (sustentacionCalificacion < 0 || sustentacionCalificacion > 5) {
            error = "La calificación debe estar entre 0.0 y 5.0.";
            return false;
        }
        if (!ValidacionUtil.esVacio(sustentacionActa) && !ValidacionUtil.esUrl(sustentacionActa)) {
            error = "La URL del acta no es valida.";
            return false;
        }
        Sustentacion s = new Sustentacion();
        s.setProyectoId(proyectoId);
        s.setFecha(parseFecha(sustentacionFecha, FechaUtil.hoy()));
        s.setCalificacion(sustentacionCalificacion);
        s.setUrlActa(ValidacionUtil.esVacio(sustentacionActa) ? null : sustentacionActa.trim());
        s.setRegistradoPor(usuarioSesion.getId());
        proyectoDAO.guardarSustentacion(s);
        proyectoDAO.actualizarEstadoFase(proyectoId, "SUSTENTACION", "APROBADA");
        mensaje = "Sustentacion registrada con calificación " + sustentacionCalificacion + ".";
        new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "SUSTENTACION",
                "Fecha " + sustentacionFecha + ", nota " + sustentacionCalificacion);
        notificarEstudiantes(proyectoDAO.porId(proyectoId),
                "Su sustentación fue registrada con calificación " + sustentacionCalificacion + ".");
        return true;
    }

    /** RF44: cierra el proyecto solo si todas las fases estan completas. */
    public boolean cerrarProyecto() {
        error = null;
        mensaje = null;
        Proyecto p = proyectoDAO.porId(proyectoId);
        for (FaseProyecto f : proyectoDAO.fases(proyectoId)) {
            if (!"CERRADO".equals(f.getFase()) && !"APROBADA".equals(f.getEstado())) {
                error = "No se puede cerrar: la fase " + f.getFase() + " no esta aprobada.";
                return false;
            }
        }
        Double nota = null;
        RubricaResultado r = rubricaDAO.resultado(proyectoId);
        Sustentacion s = proyectoDAO.sustentacion(proyectoId);
        if (s != null && s.getCalificacion() != null) {
            nota = s.getCalificacion();
        } else if (r != null && r.getNotaFinal() != null) {
            nota = r.getNotaFinal();
        }
        proyectoDAO.actualizarEstadoFase(proyectoId, "CERRADO", "APROBADA");
        proyectoDAO.finalizar(proyectoId, nota);
        mensaje = "Proyecto cerrado como FINALIZADO.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", proyectoId, "CERRAR",
                "Proyecto finalizado, nota " + nota);
        notificarInvolucrados(p, "El proyecto " + p.getCodigo() + " fue finalizado.");
        return true;
    }

    // ------------------------------------------------------------------
    // AYUDAS
    // ------------------------------------------------------------------

    /** RF27: indica si el usuario actual es el responsable de editar la fase. */
    public boolean puedeEditar(String faseEvaluada) {
        if (usuarioSesion == null) {
            return false;
        }
        Proyecto p = proyectoDAO.porId(proyectoId);
        if (p == null || !"EN_FLUJO".equals(p.getEstado()) || !faseEvaluada.equals(p.getFaseActual())) {
            return false;
        }
        if ("ADMIN".equals(usuarioSesion.getRol())) {
            return false;
        }
        String responsable = p.getResponsableActual();
        if ("DIRECTOR".equals(responsable) || "EVALUADOR".equals(responsable)) {
            // Un docente (rol DOCENTE) puede ser director o evaluador; se valida
            // que sea el asignado en el rol que corresponde a la fase.
            if (!usuarioSesion.esDocente()) {
                return false;
            }
            AsignacionDocente a = proyectoDAO.asignacionActiva(proyectoId, responsable);
            return a != null && a.getDocenteId() == usuarioSesion.getId();
        }
        return responsable != null && responsable.equals(usuarioSesion.getRol());
    }

    private void avanzarResponsable(String faseDoc, Proyecto p) {
        if ("PAGO".equals(faseDoc)) {
            proyectoDAO.actualizarFase(proyectoId, "PAGO", "COMITE");
        } else if ("PROPUESTA_I".equals(faseDoc)) {
            proyectoDAO.actualizarFase(proyectoId, "PROPUESTA_I", "DIRECTOR");
        } else if ("PROPUESTA_II".equals(faseDoc)) {
            proyectoDAO.actualizarFase(proyectoId, "PROPUESTA_II", "COMITE");
        } else if ("INFORME_I".equals(faseDoc)) {
            proyectoDAO.actualizarFase(proyectoId, "INFORME_I", "DIRECTOR");
        } else if ("INFORME_II".equals(faseDoc)) {
            proyectoDAO.actualizarFase(proyectoId, "INFORME_II", "EVALUADOR");
        }
    }

    /** Solo demostracion: adelanta la fecha de aprobacion para saltar la espera. */
    public boolean saltarEsperaInforme() {
        error = null;
        mensaje = null;
        Proyecto p = proyectoDAO.porId(proyectoId);
        if (p == null) {
            error = "Proyecto no encontrado.";
            return false;
        }
        if (p.getFechaAprobacionPropuesta() == null) {
            error = "La propuesta aún no ha sido aprobada.";
            return false;
        }
        proyectoDAO.actualizarFechaAprobacionPropuesta(proyectoId,
                java.sql.Date.valueOf(java.time.LocalDate.now().minusDays(120)));
        mensaje = "Espera de 3 meses saltada (solo demostración). Ya puede cargar el informe.";
        return true;
    }

    /** RF37: el informe se habilita tras los 3 meses de aprobada la propuesta. */
    public boolean informeHabilitado(Proyecto p) {
        if (p == null || p.getFechaAprobacionPropuesta() == null) {
            return false;
        }
        Periodo per = periodoDAO.porId(p.getPeriodoId());
        int dias = per != null ? per.getDiasMinInforme() : 90;
        return FechaUtil.diasTranscurridos(p.getFechaAprobacionPropuesta()) >= dias;
    }

    public String motivoBloqueoInforme(Proyecto p) {
        if (p == null || p.getFechaAprobacionPropuesta() == null) {
            return "la propuesta aún no ha sido aprobada.";
        }
        Periodo per = periodoDAO.porId(p.getPeriodoId());
        int dias = per != null ? per.getDiasMinInforme() : 90;
        long faltan = dias - FechaUtil.diasTranscurridos(p.getFechaAprobacionPropuesta());
        return "faltan " + faltan + " día(s) (mínimo " + dias + " días desde la aprobación de la propuesta).";
    }

    private LocalDate parseFecha(String valor, LocalDate defecto) {
        try {
            return LocalDate.parse(valor);
        } catch (Exception e) {
            return defecto;
        }
    }

    // ------------------------------------------------------------------
    // NOTIFICACIONES (RF55 - RF60)
    // ------------------------------------------------------------------

    private void notificarEstudiantes(Proyecto p, String msg) {
        if (p == null) {
            return;
        }
        for (Integrante i : proyectoDAO.integrantes(p.getId())) {
            if (i.isActivo()) {
                new Notificador().avisar(i.getEstudianteId(), msg, "vistas/proyectos/mi-proyecto.jsp");
                Correo.enviar(i.getEmail(), "Trabajo de grado " + p.getCodigo(), msg);
            }
        }
    }

    private void notificarDirector(Proyecto p, String msg) {
        AsignacionDocente a = proyectoDAO.asignacionActiva(p.getId(), "DIRECTOR");
        if (a != null) {
            new Notificador().avisar(a.getDocenteId(), msg, "vistas/proyectos/gestion.jsp?p=" + p.getId());
            Correo.enviar(a.getEmailDocente(), "Trabajo de grado " + p.getCodigo(), msg);
        }
    }

    private void notificarEvaluador(Proyecto p, String msg) {
        AsignacionDocente a = proyectoDAO.asignacionActiva(p.getId(), "EVALUADOR");
        if (a != null) {
            new Notificador().avisar(a.getDocenteId(), msg, "vistas/proyectos/gestion.jsp?p=" + p.getId());
            Correo.enviar(a.getEmailDocente(), "Trabajo de grado " + p.getCodigo(), msg);
        }
    }

    private void notificarComite(String msg) {
        for (Usuario c : usuarioDAO.listarPorRol("COMITE")) {
            new Notificador().avisar(c.getId(), msg, "vistas/proyectos/listar.jsp");
            Correo.enviar(c.getEmail(), "Trabajo de grado", msg);
        }
    }

    private void notificarInvolucrados(Proyecto p, String msg) {
        notificarEstudiantes(p, msg);
        notificarDirector(p, msg);
        notificarEvaluador(p, msg);
    }

    private void notificarRevision(Proyecto p, String faseDoc, String tipo) {
        if ("PROPUESTA_I".equals(faseDoc)) {
            notificarDirector(p, "El estudiante cargó la propuesta F-DC 124 del proyecto "
                    + p.getCodigo() + ". Requiere su aprobación.");
        } else if ("INFORME_I".equals(faseDoc)) {
            notificarDirector(p, "El estudiante cargó el informe F-DC 125 del proyecto "
                    + p.getCodigo() + ". Requiere su visto bueno.");
        } else if ("PAGO".equals(faseDoc)) {
            notificarComite("Se registró un desprendible de pago del proyecto " + p.getCodigo() + ".");
        }
    }

    // ------------------------------------------------------------------
    // GETTERS PARA LAS VISTAS
    // ------------------------------------------------------------------

    public Proyecto getProyecto() {
        return proyectoId > 0 ? proyectoDAO.porId(proyectoId) : null;
    }

    public List<FaseProyecto> getFases() {
        return proyectoDAO.fases(proyectoId);
    }

    public List<Documento> getDocumentos() {
        return documentoDAO.listarPorProyecto(proyectoId);
    }

    public List<Documento> getDocumentosFase(String f) {
        return documentoDAO.listarPorProyectoFase(proyectoId, f);
    }

    public Documento getUltimoAprobado(String tipo) {
        return documentoDAO.ultimoAprobado(proyectoId, tipo);
    }

    public Documento getUltimo(String tipo) {
        return documentoDAO.ultimo(proyectoId, tipo);
    }

    public RubricaResultado getEvaluacionRubrica() {
        return rubricaDAO.resultado(proyectoId);
    }

    public List<Seguimiento> getSeguimientos() {
        return proyectoDAO.seguimientos(proyectoId);
    }

    public int getTotalSeguimientos() {
        return proyectoDAO.contarSeguimientos(proyectoId);
    }

    public Sustentacion getSustentacion() {
        return proyectoDAO.sustentacion(proyectoId);
    }

    public List<RubricaItem> getRubricaItems() {
        return rubricaDAO.items(proyectoId);
    }

    public RubricaResultado getRubricaResultado() {
        return rubricaDAO.resultado(proyectoId);
    }

    public AsignacionDocente getDirector() {
        return proyectoDAO.asignacionActiva(proyectoId, "DIRECTOR");
    }

    public AsignacionDocente getEvaluador() {
        return proyectoDAO.asignacionActiva(proyectoId, "EVALUADOR");
    }
}
