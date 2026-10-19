package co.edu.grado.modelo;

import java.time.LocalDateTime;

/** Resultado global de la rubrica del informe final. (RF39) **/
public class RubricaResultado {

    private int id;
    private int proyectoId;
    private int evaluadorId;
    private boolean cumpleObjetivoGeneral;
    private boolean cumpleObjetivosEspecificos;
    private Double notaFinal;
    private String observaciones;
    private LocalDateTime fecha;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public int getEvaluadorId() { return evaluadorId; }
    public void setEvaluadorId(int evaluadorId) { this.evaluadorId = evaluadorId; }

    public boolean isCumpleObjetivoGeneral() { return cumpleObjetivoGeneral; }
    public void setCumpleObjetivoGeneral(boolean cumpleObjetivoGeneral) { this.cumpleObjetivoGeneral = cumpleObjetivoGeneral; }

    public boolean isCumpleObjetivosEspecificos() { return cumpleObjetivosEspecificos; }
    public void setCumpleObjetivosEspecificos(boolean cumpleObjetivosEspecificos) { this.cumpleObjetivosEspecificos = cumpleObjetivosEspecificos; }

    public Double getNotaFinal() { return notaFinal; }
    public void setNotaFinal(Double notaFinal) { this.notaFinal = notaFinal; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
