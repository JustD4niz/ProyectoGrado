package co.edu.grado.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Metodos de formato para usar desde las vistas JSP mediante jsp:useBean.
 * Ejemplo: ${F.fecha(p.fechaVencimiento)}
 */
public class Formato {

    public String fecha(LocalDate fecha) {
        return FechaUtil.formato(fecha);
    }

    public String fechaHora(LocalDateTime fecha) {
        return FechaUtil.formato(fecha);
    }

    public String numero(Double valor) {
        if (valor == null) {
            return "N/A";
        }
        if (valor == Math.floor(valor)) {
            return String.valueOf(valor.intValue());
        }
        return String.format("%.2f", valor);
    }

    public String siNo(boolean valor) {
        return valor ? "Si" : "No";
    }

    public String corto(String texto, int max) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= max) {
            return texto;
        }
        return texto.substring(0, max) + "...";
    }

    public String url(String valor) {
        return valor == null ? "#" : valor;
    }
}
