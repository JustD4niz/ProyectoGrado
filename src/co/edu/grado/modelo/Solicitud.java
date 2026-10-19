package co.edu.grado.modelo;

import java.time.LocalDateTime;

/** Solicitud: prorroga, cambio de director, retiro o PQRS. (RF48 - RF54) **/
public class Solicitud {

    private int id;
    private int proyectoId;
    private String tipo;          // PRORROGA, CAMBIO_DIRECTOR, RETIRO, PQRS
    private int solicitanteId;
    private String justificacion;
    private String urlCarta;
    private String urlComprobante;
    private Integer docenteSolicitadoId;
    private String estado;        // PENDIENTE, APROBADA, RECHAZADA
    private String observacionesResolucion;
    private Integer resueltaPor;
    private LocalDateTime fecha;
    private LocalDateTime fechaResolucion;

    // Datos de apoyo
    private String nombreSolicitante;
    private String nombreProyecto;
    private String codigoProyecto;
    private String nombreDocenteSolicitado;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(int solicitanteId) { this.solicitanteId = solicitanteId; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public String getUrlCarta() { return urlCarta; }
    public void setUrlCarta(String urlCarta) { this.urlCarta = urlCarta; }

    public String getUrlComprobante() { return urlComprobante; }
    public void setUrlComprobante(String urlComprobante) { this.urlComprobante = urlComprobante; }

    public Integer getDocenteSolicitadoId() { return docenteSolicitadoId; }
    public void setDocenteSolicitadoId(Integer docenteSolicitadoId) { this.docenteSolicitadoId = docenteSolicitadoId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservacionesResolucion() { return observacionesResolucion; }
    public void setObservacionesResolucion(String observacionesResolucion) { this.observacionesResolucion = observacionesResolucion; }

    public Integer getResueltaPor() { return resueltaPor; }
    public void setResueltaPor(Integer resueltaPor) { this.resueltaPor = resueltaPor; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getNombreSolicitante() { return nombreSolicitante; }
    public void setNombreSolicitante(String nombreSolicitante) { this.nombreSolicitante = nombreSolicitante; }

    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

    public String getCodigoProyecto() { return codigoProyecto; }
    public void setCodigoProyecto(String codigoProyecto) { this.codigoProyecto = codigoProyecto; }

    public String getNombreDocenteSolicitado() { return nombreDocenteSolicitado; }
    public void setNombreDocenteSolicitado(String nombreDocenteSolicitado) { this.nombreDocenteSolicitado = nombreDocenteSolicitado; }
}
