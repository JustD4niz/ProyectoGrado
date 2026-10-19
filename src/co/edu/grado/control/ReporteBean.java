package co.edu.grado.control;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import co.edu.grado.dao.PeriodoDAO;
import co.edu.grado.dao.ReporteDAO;
import co.edu.grado.modelo.Periodo;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.Etiquetas;

/**
 * Generacion de informes y reportes con exportacion a Excel y PDF. (RF66 - RF71)
 */
public class ReporteBean {

    private Usuario usuarioSesion;
    private Integer periodoId;
    private String tipo = "estudiante";

    private final ReporteDAO dao = new ReporteDAO();
    private final PeriodoDAO periodoDAO = new PeriodoDAO();
    private final Etiquetas etiquetas = new Etiquetas();

    public void setUsuarioSesion(Usuario u) { this.usuarioSesion = u; }
    public void setPeriodoId(Integer periodoId) { this.periodoId = periodoId; }
    public Integer getPeriodoId() { return periodoId; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTipo() { return tipo; }

    public List<Periodo> getPeriodos() {
        return periodoDAO.listar();
    }

    public String getTitulo() {
        switch (tipo) {
            case "director": return "Carga de proyectos por director";
            case "evaluador": return "Carga de proyectos por evaluador";
            case "modalidad": return "Proyectos por modalidad";
            case "nivel": return "Proyectos por nivel";
            case "fase": return "Proyectos por fase";
            case "estudiantes_proyecto": return "Estudiantes por proyecto";
            case "aprueban": return "Estudiantes que aprueban el informe final";
            case "no_aprueban": return "Estudiantes que no aprueban el informe final";
            default: return "Proyectos por estudiante";
        }
    }

    public List<Map<String, Object>> getFilas() {
        switch (tipo) {
            case "director": return dao.cargaDocentes("DIRECTOR");
            case "evaluador": return dao.cargaDocentes("EVALUADOR");
            case "modalidad": return dao.proyectosPorModalidad(periodoId);
            case "nivel": return dao.proyectosPorNivel(periodoId);
            case "fase": return dao.proyectosPorFase(periodoId);
            case "estudiantes_proyecto": return dao.estudiantesPorProyecto(periodoId);
            case "aprueban": return dao.informeFinal(true, periodoId);
            case "no_aprueban": return dao.informeFinal(false, periodoId);
            default: return dao.proyectosPorEstudiante(periodoId);
        }
    }

    /** Encabezados legibles derivados de las columnas del resultado. */
    public List<String> getColumnas() {
        List<String> cols = new ArrayList<>();
        List<Map<String, Object>> filas = getFilas();
        if (filas.isEmpty()) {
            return cols;
        }
        for (String clave : filas.get(0).keySet()) {
            cols.add(etiquetaColumna(clave));
        }
        return cols;
    }

    private String etiquetaColumna(String clave) {
        Map<String, String> mapa = new LinkedHashMap<>();
        mapa.put("estudiante", "Estudiante");
        mapa.put("cedula", "Cedula");
        mapa.put("nivel", "Nivel");
        mapa.put("codigo", "Código");
        mapa.put("titulo", "Titulo");
        mapa.put("modalidad", "Modalidad");
        mapa.put("fase_actual", "Fase");
        mapa.put("estado", "Estado");
        mapa.put("docente", "Docente");
        mapa.put("email", "Correo");
        mapa.put("en_flujo", "En flujo");
        mapa.put("finalizados", "Finalizados");
        mapa.put("total", "Total");
        mapa.put("estudiantes", "Estudiantes");
        mapa.put("nota_final", "Nota final");
        mapa.put("cumple_objetivo_general", "Obj. general");
        mapa.put("cumple_objetivos_especificos", "Obj. especificos");
        String v = mapa.get(clave);
        return v == null ? clave : v;
    }

    private String valor(Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof Boolean) {
            return ((Boolean) o) ? "Si" : "No";
        }
        return String.valueOf(o);
    }

    /** Traduce valores de enumeracion al texto mostrado. */
    private String valorLegible(String clave, Object o) {
        String v = valor(o);
        if ("modalidad".equals(clave)) {
            return etiquetas.modalidad(v);
        }
        if ("nivel".equals(clave)) {
            return etiquetas.nivel(v);
        }
        if ("fase_actual".equals(clave)) {
            return etiquetas.fase(v);
        }
        if ("estado".equals(clave)) {
            return etiquetas.estadoProyecto(v);
        }
        return v;
    }

    // ------------------------------------------------------------------
    // EXPORTACION (RF71)
    // ------------------------------------------------------------------

    /** Genera un archivo CSV/Excel descargable y devuelve el contenido. */
    public String exportarExcel(PageContext pc) {
        List<Map<String, Object>> filas = getFilas();
        StringBuilder sb = new StringBuilder();
        sb.append('\uFEFF'); // BOM para Excel
        List<String> cols = getColumnas();
        sb.append(String.join(";", cols)).append("\r\n");
        for (Map<String, Object> fila : filas) {
            List<String> vals = new ArrayList<>();
            for (String clave : fila.keySet()) {
                String v = valorLegible(clave, fila.get(clave));
                vals.add(v.replace(";", ",").replace("\n", " ").replace("\r", " "));
            }
            sb.append(String.join(";", vals)).append("\r\n");
        }
        try {
            javax.servlet.http.HttpServletResponse resp =
                    (javax.servlet.http.HttpServletResponse) pc.getResponse();
            resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"reporte_" + tipo + ".xls\"");
        } catch (Exception ignored) {
            // la cabecera es opcional
        }
        return sb.toString();
    }

    /** Genera el PDF descargable y lo escribe directamente en la respuesta. */
    public String exportarPdf(PageContext pc) {
        try {
            javax.servlet.http.HttpServletResponse resp =
                    (javax.servlet.http.HttpServletResponse) pc.getResponse();
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"reporte_" + tipo + ".pdf\"");
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(doc, resp.getOutputStream());
            doc.open();
            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            doc.add(new Paragraph(getTitulo(), titulo));
            doc.add(new Paragraph(" "));
            List<Map<String, Object>> filas = getFilas();
            List<String> cols = getColumnas();
            if (!cols.isEmpty()) {
                PdfPTable tabla = new PdfPTable(cols.size());
                tabla.setWidthPercentage(100);
                for (String c : cols) {
                    PdfPCell celda = new PdfPCell(new Phrase(c, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(celda);
                }
                for (Map<String, Object> fila : filas) {
                    for (String clave : fila.keySet()) {
                        tabla.addCell(new Phrase(valorLegible(clave, fila.get(clave)),
                                FontFactory.getFont(FontFactory.HELVETICA, 9)));
                    }
                }
                doc.add(tabla);
            } else {
                doc.add(new Paragraph("Sin datos para los filtros seleccionados."));
            }
            doc.close();
        } catch (Exception e) {
            return "";
        }
        return "";
    }
}
