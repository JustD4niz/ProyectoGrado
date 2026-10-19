package co.edu.grado.modelo;

import java.time.LocalDate;

/** Seguimiento academico registrado por el director. (RF42) **/
public class Seguimiento {

    private int id;
    private int proyectoId;
    private int directorId;
    private LocalDate fecha;
    private String descripcion;
    private String urlEvidencia;

    private String nombreDirector;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public int getDirectorId() { return directorId; }
    public void setDirectorId(int directorId) { this.directorId = directorId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUrlEvidencia() { return urlEvidencia; }
    public void setUrlEvidencia(String urlEvidencia) { this.urlEvidencia = urlEvidencia; }

    public String getNombreDirector() { return nombreDirector; }
    public void setNombreDirector(String nombreDirector) { this.nombreDirector = nombreDirector; }
}
