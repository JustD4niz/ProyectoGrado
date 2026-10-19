package co.edu.grado.control;

import java.util.ArrayList;
import java.util.List;

/** Opcion del menu lateral. (RF08) **/
public class Opcion {

    private String etiqueta;
    private String url;
    private String icono;
    private int contador;

    public Opcion() {
    }

    public Opcion(String etiqueta, String url, String icono) {
        this.etiqueta = etiqueta;
        this.url = url;
        this.icono = icono;
    }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public int getContador() { return contador; }
    public void setContador(int contador) { this.contador = contador; }
}
