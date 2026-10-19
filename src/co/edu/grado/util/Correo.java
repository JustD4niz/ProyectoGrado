package co.edu.grado.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Envio de correos con reintentos y registro de cada envio.
 * RNF20: reintentar ante fallos y registrar los envios.
 * El envio real solo se intenta si mail.enabled = true (parametrizable).
 */
public final class Correo {

    private static final int MAX_INTENTOS = 3;

    private Correo() {
    }

    public static void enviar(String destinatario, String asunto, String cuerpo) {
        if (destinatario == null || destinatario.trim().isEmpty()) {
            return;
        }
        boolean habilitado = Configuracion.booleano("mail.enabled", false);
        boolean exito = false;
        String error = null;
        int intentos = 0;

        if (habilitado) {
            for (int i = 1; i <= MAX_INTENTOS && !exito; i++) {
                intentos = i;
                try {
                    enviarSmtp(destinatario, asunto, cuerpo);
                    exito = true;
                } catch (Exception e) {
                    error = e.getMessage();
                }
            }
        } else {
            // Modo demostracion: el correo queda registrado pero no se envia.
            exito = false;
            error = "Envio deshabilitado (mail.enabled=false)";
        }
        registrar(destinatario, asunto, cuerpo, exito, intentos, error);
    }

    private static void enviarSmtp(String destinatario, String asunto, String cuerpo) throws Exception {
        String host = Configuracion.get("mail.smtp.host", "");
        String puerto = Configuracion.get("mail.smtp.port", "587");
        String remitente = Configuracion.get("mail.remitente", "no-responder@uts.edu.co");
        if (host == null || host.isEmpty()) {
            throw new IllegalStateException("Sin servidor SMTP configurado");
        }
        Properties p = new Properties();
        p.put("mail.smtp.host", host);
        p.put("mail.smtp.port", puerto);
        p.put("mail.smtp.auth", "false");
        p.put("mail.smtp.starttls.enable", "true");
        Session sesion = Session.getInstance(p);
        MimeMessage msg = new MimeMessage(sesion);
        msg.setFrom(new InternetAddress(remitente));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        msg.setSubject(asunto, "UTF-8");
        msg.setText(cuerpo, "UTF-8");
        Transport.send(msg);
    }

    private static void registrar(String destinatario, String asunto, String cuerpo,
                                  boolean exito, int intentos, String error) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(
                    "INSERT INTO correos_enviados (destinatario, asunto, cuerpo, exitoso, intentos, error) "
                            + "VALUES (?,?,?,?,?,?)");
            ps.setString(1, destinatario);
            ps.setString(2, asunto);
            ps.setString(3, cuerpo);
            ps.setBoolean(4, exito);
            ps.setInt(5, intentos);
            ps.setString(6, error);
            ps.executeUpdate();
        } catch (Exception ignored) {
            // el registro del correo no debe interrumpir el flujo
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }
}
