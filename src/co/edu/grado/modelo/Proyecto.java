package co.edu.grado.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.util.FechaUtil;

/** Proyecto de grado. (RF20 - RF54) **/
public class Proyecto {

    private int id;
    private String codigo;
    private String titulo;
    private String objetivos;
    private String modalidad;
    private String nivel;
    private String lineaInvestigacion;
    private int periodoId;
    private Integer ideaId;
    private String estado;             // EN_FLUJO, FINALIZADO, RECHAZADO, VENCIDO, CANCELADO
    private String faseActual;
    private String responsableActual;
    private LocalDate fechaAprobacionPropuesta;
    private LocalDate fechaInicio180;
    private LocalDate fechaVencimiento;
    private int prorrogasUtilizadas;
    private Double calificacionFinal;
    private String conceptoEvaluador;
    private String conceptoComite;
    private LocalDateTime fechaCreacion;

    // Campos de apoyo para vistas
    private String nombrePeriodo;
    private int integrantes;
    private String nombresIntegrantes;
    private String nombreDirector;
    private String nombreEvaluador;
    private long diasRestantes;
    private boolean vencido;
    private boolean informeHabilitado;
    private String motivoBloqueoInforme;
    private boolean vencimientoProximo;
    private long diasParaVencimiento;
    private String propuestaUrl;
    private String informeUrl;

    private final List<FaseProyecto> fases = new ArrayList<>();
    private final List<Integrante> listaIntegrantes = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getObjetivos() { return objetivos; }
    public void setObjetivos(String objetivos) { this.objetivos = objetivos; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getLineaInvestigacion() { return lineaInvestigacion; }
    public void setLineaInvestigacion(String lineaInvestigacion) { this.lineaInvestigacion = lineaInvestigacion; }

    public int getPeriodoId() { return periodoId; }
    public void setPeriodoId(int periodoId) { this.periodoId = periodoId; }

    public Integer getIdeaId() { return ideaId; }
    public void setIdeaId(Integer ideaId) { this.ideaId = ideaId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFaseActual() { return faseActual; }
    public void setFaseActual(String faseActual) { this.faseActual = faseActual; }

    public String getResponsableActual() { return responsableActual; }
    public void setResponsableActual(String responsableActual) { this.responsableActual = responsableActual; }

    public LocalDate getFechaAprobacionPropuesta() { return fechaAprobacionPropuesta; }
    public void setFechaAprobacionPropuesta(LocalDate v) { this.fechaAprobacionPropuesta = v; }

    public LocalDate getFechaInicio180() { return fechaInicio180; }
    public void setFechaInicio180(LocalDate v) { this.fechaInicio180 = v; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate v) { this.fechaVencimiento = v; }

    public int getProrrogasUtilizadas() { return prorrogasUtilizadas; }
    public void setProrrogasUtilizadas(int prorrogasUtilizadas) { this.prorrogasUtilizadas = prorrogasUtilizadas; }

    public Double getCalificacionFinal() { return calificacionFinal; }
    public void setCalificacionFinal(Double calificacionFinal) { this.calificacionFinal = calificacionFinal; }

    public String getConceptoEvaluador() { return conceptoEvaluador; }
    public void setConceptoEvaluador(String conceptoEvaluador) { this.conceptoEvaluador = conceptoEvaluador; }

    public String getConceptoComite() { return conceptoComite; }
    public void setConceptoComite(String conceptoComite) { this.conceptoComite = conceptoComite; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getNombrePeriodo() { return nombrePeriodo; }
    public void setNombrePeriodo(String nombrePeriodo) { this.nombrePeriodo = nombrePeriodo; }

    public int getIntegrantes() { return integrantes; }
    public void setIntegrantes(int integrantes) { this.integrantes = integrantes; }

    public String getNombresIntegrantes() { return nombresIntegrantes; }
    public void setNombresIntegrantes(String nombresIntegrantes) { this.nombresIntegrantes = nombresIntegrantes; }

    public String getNombreDirector() { return nombreDirector; }
    public void setNombreDirector(String nombreDirector) { this.nombreDirector = nombreDirector; }

    public String getNombreEvaluador() { return nombreEvaluador; }
    public void setNombreEvaluador(String nombreEvaluador) { this.nombreEvaluador = nombreEvaluador; }

    public long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(long diasRestantes) { this.diasRestantes = diasRestantes; }

    public boolean isVencido() { return vencido; }
    public void setVencido(boolean vencido) { this.vencido = vencido; }

    public boolean isInformeHabilitado() { return informeHabilitado; }
    public void setInformeHabilitado(boolean informeHabilitado) { this.informeHabilitado = informeHabilitado; }

    public String getMotivoBloqueoInforme() { return motivoBloqueoInforme; }
    public void setMotivoBloqueoInforme(String motivoBloqueoInforme) { this.motivoBloqueoInforme = motivoBloqueoInforme; }

    public boolean isVencimientoProximo() { return vencimientoProximo; }
    public void setVencimientoProximo(boolean vencimientoProximo) { this.vencimientoProximo = vencimientoProximo; }

    public long getDiasParaVencimiento() { return diasParaVencimiento; }
    public void setDiasParaVencimiento(long diasParaVencimiento) { this.diasParaVencimiento = diasParaVencimiento; }

    public String getPropuestaUrl() { return propuestaUrl; }
    public void setPropuestaUrl(String propuestaUrl) { this.propuestaUrl = propuestaUrl; }

    public String getInformeUrl() { return informeUrl; }
    public void setInformeUrl(String informeUrl) { this.informeUrl = informeUrl; }

    public List<FaseProyecto> getFases() { return fases; }
    public List<Integrante> getListaIntegrantes() { return listaIntegrantes; }

    /** Calcula el estado efectivo mostrado en la interfaz (RF46). **/
    public String getEstadoVisible() {
        if (vencido && "EN_FLUJO".equals(estado)) {
            return "VENCIDO";
        }
        return estado;
    }

    public boolean isFinalizado() {
        return "FINALIZADO".equals(estado);
    }

    /** Un proyecto en flujo con fecha de vencimiento pasada. */
    public boolean calcularVencido() {
        if (fechaVencimiento == null || !"EN_FLUJO".equals(estado)) {
            return false;
        }
        return FechaUtil.diasRestantes(fechaVencimiento) < 0;
    }
}
