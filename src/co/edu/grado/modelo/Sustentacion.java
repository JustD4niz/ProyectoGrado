package co.edu.grado.modelo;

import java.time.LocalDate;

/** Registro de sustentacion. (RF43) **/
public class Sustentacion {

    private int id;
    private int proyectoId;
    private LocalDate fecha;
    private Double calificacion;
    private String urlActa;
    private int registradoPor;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }

    public String getUrlActa() { return urlActa; }
    public void setUrlActa(String urlActa) { this.urlActa = urlActa; }

    public int getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(int registradoPor) { this.registradoPor = registradoPor; }
}
