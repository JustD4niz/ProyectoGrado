package co.edu.grado.modelo;

import java.time.LocalDateTime;

/** Documento registrado como URL, con historial de versiones. (RF45, RF47, RNF06) **/
public class Documento {

    private int id;
    private int proyectoId;
    private String fase;
    private String tipo;
    private String url;
    private int version;
    private int autorId;
    private String estado;      // PENDIENTE, APROBADO, RECHAZADO
    private String observaciones;
    private boolean vistoBuenoDirector;
    private LocalDateTime fecha;

    // Datos de apoyo
    private String nombreAutor;
    private String codigoProyecto;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public int getAutorId() { return autorId; }
    public void setAutorId(int autorId) { this.autorId = autorId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public boolean isVistoBuenoDirector() { return vistoBuenoDirector; }
    public void setVistoBuenoDirector(boolean vistoBuenoDirector) { this.vistoBuenoDirector = vistoBuenoDirector; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }

    public String getCodigoProyecto() { return codigoProyecto; }
    public void setCodigoProyecto(String codigoProyecto) { this.codigoProyecto = codigoProyecto; }
}
