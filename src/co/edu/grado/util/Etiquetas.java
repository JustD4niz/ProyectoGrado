package co.edu.grado.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduce los codigos de las enumeraciones a etiquetas legibles en espanol.
 * Se expone en las vistas JSP mediante jsp:useBean. (RNF24)
 */
public class Etiquetas {

    private static final Map<String, String> ROL = new LinkedHashMap<>();
    private static final Map<String, String> NIVEL = new LinkedHashMap<>();
    private static final Map<String, String> MODALIDAD = new LinkedHashMap<>();
    private static final Map<String, String> FASE = new LinkedHashMap<>();
    private static final Map<String, String> ESTADO_PROYECTO = new LinkedHashMap<>();
    private static final Map<String, String> ESTADO_IDEA = new LinkedHashMap<>();
    private static final Map<String, String> ESTADO_SOLICITUD = new LinkedHashMap<>();
    private static final Map<String, String> TIPO_SOLICITUD = new LinkedHashMap<>();
    private static final Map<String, String> TIPO_DOCUMENTO = new LinkedHashMap<>();
    private static final Map<String, String> RESPONSABLE = new LinkedHashMap<>();
    private static final Map<String, String> CONCEPTO = new LinkedHashMap<>();
    private static final Map<String, String> ESTADO_DOCUMENTO = new LinkedHashMap<>();
    private static final Map<String, String> ESTADO_FASE = new LinkedHashMap<>();

    static {
        ROL.put("ADMIN", "Administrador");
        ROL.put("COMITE", "Comité de Trabajo de Grado");
        ROL.put("DIRECTOR", "Director");
        ROL.put("EVALUADOR", "Evaluador");
        ROL.put("DOCENTE", "Docente");
        ROL.put("ESTUDIANTE", "Estudiante");

        NIVEL.put("TECNOLOGIA", "Tecnología");
        NIVEL.put("INGENIERIA", "Ingeniería");

        MODALIDAD.put("INVESTIGACION", "Proyecto de Investigación");
        MODALIDAD.put("DESARROLLO", "Desarrollo Tecnológico");
        MODALIDAD.put("MONOGRAFIA", "Monografía");

        FASE.put("PAGO", "Fase 1 - Pago");
        FASE.put("PROPUESTA_I", "Fase 2 - Propuesta I");
        FASE.put("PROPUESTA_II", "Fase 3 - Propuesta II");
        FASE.put("INFORME_I", "Fase 4 - Informe I");
        FASE.put("INFORME_II", "Fase 5 - Informe II");
        FASE.put("SUSTENTACION", "Fase 6 - Sustentación");
        FASE.put("CERRADO", "Cierre");

        ESTADO_PROYECTO.put("EN_FLUJO", "En flujo");
        ESTADO_PROYECTO.put("FINALIZADO", "Finalizado");
        ESTADO_PROYECTO.put("RECHAZADO", "Rechazado");
        ESTADO_PROYECTO.put("VENCIDO", "Vencido");
        ESTADO_PROYECTO.put("CANCELADO", "Cancelado");

        ESTADO_IDEA.put("PENDIENTE", "Pendiente");
        ESTADO_IDEA.put("APROBADA", "Aprobada");
        ESTADO_IDEA.put("RECHAZADA", "Rechazada");

        ESTADO_SOLICITUD.put("PENDIENTE", "Pendiente");
        ESTADO_SOLICITUD.put("APROBADA", "Aprobada");
        ESTADO_SOLICITUD.put("RECHAZADA", "Rechazada");

        TIPO_SOLICITUD.put("PRORROGA", "Prórroga");
        TIPO_SOLICITUD.put("CAMBIO_DIRECTOR", "Cambio de director");
        TIPO_SOLICITUD.put("RETIRO", "Retiro de integrante");
        TIPO_SOLICITUD.put("PQRS", "PQRS");

        TIPO_DOCUMENTO.put("DESPRENDIBLE_PAGO", "Desprendible de pago");
        TIPO_DOCUMENTO.put("PROPUESTA_FDC124", "Propuesta F-DC 124");
        TIPO_DOCUMENTO.put("INFORME_FDC125", "Informe final F-DC 125");
        TIPO_DOCUMENTO.put("CARTA_PRORROGA", "Carta de prórroga");
        TIPO_DOCUMENTO.put("COMPROBANTE_PRORROGA", "Comprobante de pago");
        TIPO_DOCUMENTO.put("CARTA_RETIRO", "Carta de retiro");
        TIPO_DOCUMENTO.put("CARTA_CAMBIO_DIRECTOR", "Carta de cambio de director");
        TIPO_DOCUMENTO.put("ACTA_SUSTENTACION", "Acta de sustentación");
        TIPO_DOCUMENTO.put("OTRO", "Otro");

        RESPONSABLE.put("ESTUDIANTE", "Estudiante");
        RESPONSABLE.put("DIRECTOR", "Director");
        RESPONSABLE.put("EVALUADOR", "Evaluador");
        RESPONSABLE.put("COMITE", "Comité");

        CONCEPTO.put("APROBADO", "Aprobado");
        CONCEPTO.put("APROBADO_CON_CORRECCIONES", "Aprobado con correcciones");
        CONCEPTO.put("NO_APROBADO", "No aprobado");
        CONCEPTO.put("RECHAZADO", "Rechazado");

        ESTADO_DOCUMENTO.put("PENDIENTE", "Pendiente de revisión");
        ESTADO_DOCUMENTO.put("APROBADO", "Aprobado");
        ESTADO_DOCUMENTO.put("RECHAZADO", "Rechazado");

        ESTADO_FASE.put("PENDIENTE", "Pendiente");
        ESTADO_FASE.put("EN_REVISION", "Pendiente de revisión");
        ESTADO_FASE.put("APROBADA", "Aprobada");
        ESTADO_FASE.put("RECHAZADA", "Rechazada");
    }

    private String buscar(Map<String, String> mapa, String clave) {
        if (clave == null) {
            return "";
        }
        String v = mapa.get(clave);
        return v == null ? clave : v;
    }

    public String rol(String v) { return buscar(ROL, v); }
    public String nivel(String v) { return buscar(NIVEL, v); }
    public String modalidad(String v) { return buscar(MODALIDAD, v); }
    public String fase(String v) { return buscar(FASE, v); }
    public String estadoProyecto(String v) { return buscar(ESTADO_PROYECTO, v); }
    public String estadoIdea(String v) { return buscar(ESTADO_IDEA, v); }
    public String estadoSolicitud(String v) { return buscar(ESTADO_SOLICITUD, v); }
    public String tipoSolicitud(String v) { return buscar(TIPO_SOLICITUD, v); }
    public String tipoDocumento(String v) { return buscar(TIPO_DOCUMENTO, v); }
    public String responsable(String v) { return buscar(RESPONSABLE, v); }
    public String concepto(String v) { return buscar(CONCEPTO, v); }
    public String estadoDocumento(String v) { return buscar(ESTADO_DOCUMENTO, v); }
    public String estadoFase(String v) { return buscar(ESTADO_FASE, v); }
}
