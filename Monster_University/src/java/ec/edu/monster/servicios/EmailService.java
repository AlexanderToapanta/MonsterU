package ec.edu.monster.servicios;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.ejb.Stateless;
import javax.ejb.Asynchronous;
import java.util.logging.Logger;

@Stateless
public class EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    
    // Configuración del servidor SMTP (ajusta estos valores según tu proveedor)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SMTP_USERNAME = "tucorreo@gmail.com"; // Tu correo Gmail
    private static final String SMTP_PASSWORD = "tucontraseña"; // Tu contraseña o App Password
    
    // Para Gmail, necesitarás generar una "Contraseña de aplicación" si usas 2FA
    // Ve a: Google Account > Seguridad > Contraseñas de aplicación
    
    /**
     * Envía un correo de forma asíncrona
     */
    @Asynchronous
    public void enviarCorreoAsync(String destinatario, String asunto, String mensaje) {
        try {
            enviarCorreo(destinatario, asunto, mensaje);
            LOGGER.info("✅ Correo enviado exitosamente a: " + destinatario);
        } catch (Exception e) {
            LOGGER.severe("❌ Error al enviar correo: " + e.getMessage());
        }
    }
    
    /**
     * Envía un correo de forma síncrona
     */
    public void enviarCorreo(String destinatario, String asunto, String mensaje) throws MessagingException {
        // Validaciones básicas
        if (destinatario == null || destinatario.trim().isEmpty()) {
            throw new IllegalArgumentException("El destinatario no puede estar vacío");
        }
        
        if (asunto == null || asunto.trim().isEmpty()) {
            throw new IllegalArgumentException("El asunto no puede estar vacío");
        }
        
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }
        
        // Configurar propiedades del servidor SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        // Crear sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
        
        try {
            // Crear mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            
            // Configurar contenido del mensaje (HTML)
            String contenidoHTML = crearContenidoHTML(asunto, mensaje);
            message.setContent(contenidoHTML, "text/html; charset=utf-8");
            
            // Enviar mensaje
            Transport.send(message);
            
            LOGGER.info("✉️ Correo enviado a: " + destinatario);
            
        } catch (MessagingException e) {
            LOGGER.severe("❌ Error de mensajería: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Método específico para enviar credenciales de usuario
     */
    public void enviarCredencialesUsuario(String emailDestinatario, String nombrePersona, 
                                          String usuarioId, String nombreUsuario, 
                                          String contrasenaPlana) {
        
        String asunto = "🎓 Monster University - Tus Credenciales de Acceso";
        
        String mensaje = "<h2>¡Bienvenido/a a Monster University!</h2>" +
                        "<p>Estimado/a <strong>" + nombrePersona + "</strong>,</p>" +
                        "<p>Se ha creado tu cuenta de acceso al sistema con los siguientes datos:</p>" +
                        "<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                        "<p><strong>ID de Usuario:</strong> " + usuarioId + "</p>" +
                        "<p><strong>Nombre de Usuario:</strong> " + nombreUsuario + "</p>" +
                        "<p><strong>Contraseña Temporal:</strong> " + contrasenaPlana + "</p>" +
                        "</div>" +
                        "<p><strong>📋 Instrucciones importantes:</strong></p>" +
                        "<ul>" +
                        "<li>Guarda esta información en un lugar seguro</li>" +
                        "<li>Cambia tu contraseña en tu primer inicio de sesión</li>" +
                        "<li>Tu contraseña temporal es tu número de cédula</li>" +
                        "<li>Para mayor seguridad, se recomienda cambiar la contraseña periódicamente</li>" +
                        "</ul>" +
                        "<p>Para acceder al sistema, visita nuestro portal web.</p>" +
                        "<p>Si tienes algún problema con tu acceso, por favor contacta al administrador del sistema.</p>" +
                        "<br>" +
                        "<p>Atentamente,</p>" +
                        "<p><strong>Equipo de Monster University</strong></p>" +
                        "<p><em>Este es un mensaje automático, por favor no responder.</em></p>";
        
        try {
            enviarCorreoAsync(emailDestinatario, asunto, mensaje);
        } catch (Exception e) {
            LOGGER.severe("❌ Error al enviar credenciales: " + e.getMessage());
        }
    }
    
    /**
     * Crea el contenido HTML del correo
     */
    private String crearContenidoHTML(String asunto, String mensaje) {
        return "<!DOCTYPE html>" +
               "<html lang='es'>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<title>" + asunto + "</title>" +
               "<style>" +
               "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
               ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
               ".header { background-color: #4a6fa5; color: white; padding: 20px; text-align: center; }" +
               ".content { background-color: #f9f9f9; padding: 20px; }" +
               ".footer { background-color: #333; color: white; padding: 10px; text-align: center; font-size: 12px; }" +
               ".credential-box { background-color: #e8f4fd; border-left: 4px solid #4a6fa5; padding: 15px; margin: 15px 0; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1>Monster University</h1>" +
               "</div>" +
               "<div class='content'>" +
               mensaje +
               "</div>" +
               "<div class='footer'>" +
               "<p>© 2024 Monster University. Todos los derechos reservados.</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
}