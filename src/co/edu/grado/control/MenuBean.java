package co.edu.grado.control;

import java.util.ArrayList;
import java.util.List;

import co.edu.grado.dao.IdeaDAO;
import co.edu.grado.dao.ProyectoDAO;
import co.edu.grado.dao.SolicitudDAO;

/**
 * Construye el menu lateral segun el rol del usuario. (RF08)
 * Las rutas son relativas al contexto y el layout agrega el prefijo.
 */
public class MenuBean {

    // Datos de los iconos (SVG tipo Feather)
    private static final String I_HOME =
            "M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10";
    private static final String I_USERS =
            "M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z "
            + "M23 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75";
    private static final String I_BULB =
            "M9 18h6 M10 22h4 M12 2a7 7 0 0 1 4 12.7V17H8v-2.3A7 7 0 0 1 12 2z";
    private static final String I_FOLDER =
            "M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z";
    private static final String I_FILE =
            "M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6";
    private static final String I_CLIP =
            "M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2 "
            + "M9 2h6a1 1 0 0 1 1 1v2H8V3a1 1 0 0 1 1-1z";
    private static final String I_CHECK =
            "M9 11l3 3L22 4 M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11";
    private static final String I_CHART = "M12 20V10 M18 20V4 M6 20v-4";
    private static final String I_CAL =
            "M8 2v4 M16 2v4 M3 10h18 M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z";
    private static final String I_BOOK =
            "M4 19.5A2.5 2.5 0 0 1 6.5 17H20 M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z";
    private static final String I_USER =
            "M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z";
    private static final String I_BELL =
            "M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9 M13.73 21a2 2 0 0 1-3.46 0";
    private static final String I_SEND = "M22 2L11 13 M22 2l-7 20-4-9-9-4z";
    private static final String I_SHIELD =
            "M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z";

    public List<Opcion> getMenus(String rol) {
        List<Opcion> m = new ArrayList<>();
        if (rol == null) {
            return m;
        }
        m.add(new Opcion("Inicio", "vistas/inicio.jsp", I_HOME));
        m.add(new Opcion("Mi perfil", "vistas/perfil.jsp", I_USER));
        switch (rol) {
            case "ADMIN":
                m.add(new Opcion("Usuarios", "vistas/usuarios/listar.jsp", I_USERS));
                m.add(new Opcion("Proyectos", "vistas/proyectos/listar.jsp", I_FOLDER));
                m.add(new Opcion("Periodos academicos", "vistas/config/periodo.jsp", I_CAL));
                m.add(new Opcion("Calendario", "vistas/config/calendario.jsp", I_CLIP));
                m.add(new Opcion("Reportes", "vistas/reportes/index.jsp", I_CHART));
                break;
            case "COMITE":
                m.add(new Opcion("Bandeja de ideas", "vistas/ideas/bandeja.jsp", I_BULB));
                m.add(new Opcion("Trazabilidad de ideas", "vistas/ideas/trazabilidad.jsp", I_CHECK));
                m.add(new Opcion("Proyectos", "vistas/proyectos/listar.jsp", I_FOLDER));
                m.add(new Opcion("Reportes", "vistas/reportes/index.jsp", I_CHART));
                break;
            case "DIRECTOR":
                m.add(new Opcion("Banco de ideas", "vistas/ideas/banco-ideas.jsp", I_BULB));
                m.add(new Opcion("Banco de propuestas", "vistas/ideas/mis-ideas.jsp", I_FILE));
                m.add(new Opcion("Proyectos director", "vistas/proyectos/mis-proyectos.jsp", I_FOLDER));
                m.add(new Opcion("Evaluador", "vistas/proyectos/proyectos-evaluador.jsp", I_CHECK));
                break;
            case "EVALUADOR":
                m.add(new Opcion("Banco de ideas", "vistas/ideas/banco-ideas.jsp", I_BULB));
                m.add(new Opcion("Banco de propuestas", "vistas/ideas/mis-ideas.jsp", I_FILE));
                m.add(new Opcion("Evaluador", "vistas/proyectos/proyectos-evaluador.jsp", I_FOLDER));
                break;
            case "DOCENTE":
                m.add(new Opcion("Banco de ideas", "vistas/ideas/banco-ideas.jsp", I_BULB));
                m.add(new Opcion("Banco de propuestas", "vistas/ideas/mis-ideas.jsp", I_FILE));
                m.add(new Opcion("Proyectos director", "vistas/proyectos/mis-proyectos.jsp", I_FOLDER));
                m.add(new Opcion("Evaluador", "vistas/proyectos/proyectos-evaluador.jsp", I_CHECK));
                break;
            case "ESTUDIANTE":
                m.add(new Opcion("Banco de ideas", "vistas/ideas/banco.jsp", I_BULB));
                m.add(new Opcion("Mi proyecto", "vistas/proyectos/mi-proyecto.jsp", I_FOLDER));
                break;
            default:
                break;
        }
        m.add(new Opcion("Base documental", "vistas/base-documental.jsp", I_BOOK));
        return m;
    }

    /** Contador de elementos pendientes para el rol, mostrado como insignia. */
    public int getPendientes(String rol) {
        int total = 0;
        if ("COMITE".equals(rol)) {
            total += new IdeaDAO().contarPendientes();
        }
        return total;
    }

    public int getProyectosAbiertos() {
        return 0;
    }
}
