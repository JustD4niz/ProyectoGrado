package co.edu.grado.modelo;

import java.time.LocalDateTime;

/** Estado de una fase dentro de un proyecto. (RF27, RF44) **/
public class FaseProyecto {

    private int id;
    private int proyectoId;
    private String fase;
    private String estado;        // PENDIENTE, EN_REVISION, APROBADA, RECHAZADA
    private String responsable;   // ESTUDIANTE, DIRECTOR, EVALUADOR, COMITE
    private LocalDateTime fechaAprobacion;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDateTime fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }
}
