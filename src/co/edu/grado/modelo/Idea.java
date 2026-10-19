package co.edu.grado.modelo;

import java.time.LocalDateTime;

/** Idea del banco o propuesta por un estudiante. (RF10 - RF19) **/
public class Idea {

    private int id;
    private String codigo;
    private String titulo;
    private String descripcion;
    private String nivel;
    private String lineaInvestigacion;
    private String modalidad;
    private Integer proponenteId;
    private String origen;         // BANCO, PROPUESTA_ESTUDIANTE
    private Integer estudianteId;
    private String estado;         // PENDIENTE, APROBADA, RECHAZADA
    private String observaciones;
    private boolean disponible;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRevision;
    private Integer revisadaPor;

    // Campos de apoyo
    private String nombreProponente;
    private String nombreEstudiante;
    private String nombreRevisor;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getLineaInvestigacion() { return lineaInvestigacion; }
    public void setLineaInvestigacion(String lineaInvestigacion) { this.lineaInvestigacion = lineaInvestigacion; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public Integer getProponenteId() { return proponenteId; }
    public void setProponenteId(Integer proponenteId) { this.proponenteId = proponenteId; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public Integer getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Integer estudianteId) { this.estudianteId = estudianteId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }

    public Integer getRevisadaPor() { return revisadaPor; }
    public void setRevisadaPor(Integer revisadaPor) { this.revisadaPor = revisadaPor; }

    public String getNombreProponente() { return nombreProponente; }
    public void setNombreProponente(String nombreProponente) { this.nombreProponente = nombreProponente; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getNombreRevisor() { return nombreRevisor; }
    public void setNombreRevisor(String nombreRevisor) { this.nombreRevisor = nombreRevisor; }

    public boolean isDeEstudiante() {
        return "PROPUESTA_ESTUDIANTE".equals(origen);
    }
}
