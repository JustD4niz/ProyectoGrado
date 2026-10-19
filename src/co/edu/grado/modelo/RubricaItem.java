package co.edu.grado.modelo;

import java.time.LocalDate;

/** Fila de la rubrica institucional aplicada al informe final. (RF39) **/
public class RubricaItem {

    private int id;
    private int proyectoId;
    private int evaluadorId;
    private String criterio;
    private Double puntaje;
    private Double maximo;
    private String observaciones;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public int getEvaluadorId() { return evaluadorId; }
    public void setEvaluadorId(int evaluadorId) { this.evaluadorId = evaluadorId; }

    public String getCriterio() { return criterio; }
    public void setCriterio(String criterio) { this.criterio = criterio; }

    public Double getPuntaje() { return puntaje; }
    public void setPuntaje(Double puntaje) { this.puntaje = puntaje; }

    public Double getMaximo() { return maximo; }
    public void setMaximo(Double maximo) { this.maximo = maximo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
