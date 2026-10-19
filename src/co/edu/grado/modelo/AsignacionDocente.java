package co.edu.grado.modelo;

import java.time.LocalDateTime;

/** Asignacion de un docente (director o evaluador) a un proyecto. (RF23, RF24) **/
public class AsignacionDocente {

    private int id;
    private int proyectoId;
    private int docenteId;
    private String rol;        // DIRECTOR, EVALUADOR
    private boolean activo;
    private LocalDateTime fechaAsignacion;

    // Datos de apoyo
    private String nombreDocente;
    private String emailDocente;
    private int carga;         // proyectos que tiene el docente (RF24)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public int getDocenteId() { return docenteId; }
    public void setDocenteId(int docenteId) { this.docenteId = docenteId; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public String getNombreDocente() { return nombreDocente; }
    public void setNombreDocente(String nombreDocente) { this.nombreDocente = nombreDocente; }

    public String getEmailDocente() { return emailDocente; }
    public void setEmailDocente(String emailDocente) { this.emailDocente = emailDocente; }

    public int getCarga() { return carga; }
    public void setCarga(int carga) { this.carga = carga; }
}
