package ec.edu.monster.controlador;

import java.io.Serializable;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Named("correoController")
@SessionScoped
public class CorreoController implements Serializable {
    
    private String correoDestino;
    private String mensaje;
    private String asunto;
    
    // ========== ¡¡¡IMPORTANTE!!! ==========
    // Para Gmail desde casa necesitas:
    // 1. Una CONTRASEÑA DE APLICACIÓN (no tu contraseña normal)
    // 2. Verificación en 2 pasos ACTIVADA
    
    // CÓMO OBTENER CONTRASEÑA DE APLICACIÓN:
    // 1. Ve a: https://myaccount.google.com/
    // 2. Activa "Verificación en dos pasos"
    // 3. Ve a "Contraseñas de aplicaciones"
    // 4. Genera una nueva para "Otra aplicación"
    // 5. Usa ESA contraseña aquí ↓↓↓
    
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_USER = "TUEMAIL@gmail.com"; // ← TU EMAIL
    private static final String SMTP_PASSWORD = "abcdefghijklmnop"; // ← CONTRASEÑA DE APLÍ
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public CorreoController() {
        System.out.println("Controlador inicializado para uso DOMÉSTICO");
    }
    
    public void enviarCorreo() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        if (!validarCampos()) {
            return;
        }
        
        // PROBAR 3 CONFIGURACIONES DIFERENTES
        boolean enviado = false;
        String ultimoError = "";
        
        // Intento 1: Puerto 465 (SSL) - EL MÁS PROBABLE
        if (!enviado) {
            enviado = enviarConConfiguracion("SSL", 465, true);
            if (!enviado) ultimoError = "Puerto 465 falló";
        }
        
        // Intento 2: Puerto 587 (TLS)
        if (!enviado) {
            enviado = enviarConConfiguracion("TLS", 587, false);
            if (!enviado) ultimoError = "Puerto 587 falló";
        }
        
        // Intento 3: Puerto 25 (SMTP estándar)
        if (!enviado) {
            enviado = enviarConConfiguracion("SMTP", 25, false);
            if (!enviado) ultimoError = "Todos los puertos fallaron";
        }
        
        if (enviado) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "✅ Éxito", "Correo enviado correctamente"));
            limpiarCampos();
        } else {
            String mensajeError = 
                "<b>No se pudo enviar el correo</b><br/><br/>" +
                "<b>Causas comunes desde casa:</b><br/>" +
                "1. <b>Contraseña incorrecta:</b> Necesitas CONTRASEÑA DE APLICACIÓN<br/>" +
                "2. <b>Verificación 2 pasos:</b> Debe estar ACTIVADA<br/>" +
                "3. <b>ISP bloquea puertos:</b> Tu proveedor de internet bloquea SMTP<br/><br/>" +
                "<b>Pasos para solucionar:</b><br/>" +
                "1. Ve a: <a href='https://myaccount.google.com/' target='_blank'>myaccount.google.com</a><br/>" +
                "2. Activa 'Verificación en dos pasos'<br/>" +
                "3. Genera una 'Contraseña de aplicación'<br/>" +
                "4. Usa ESA contraseña en el código<br/><br/>" +
                "<b>Error técnico:</b> " + ultimoError;
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "❌ Error", mensajeError));
        }
    }
    
    private boolean enviarConConfiguracion(String tipo, int puerto, boolean ssl) {
        System.out.println("=== Probando " + tipo + " puerto " + puerto + " ===");
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", String.valueOf(puerto));
            props.put("mail.debug", "true");
            
            if (ssl) {
                // Configuración SSL (puerto 465)
                props.put("mail.smtp.socketFactory.port", String.valueOf(puerto));
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
            } else if (puerto == 587) {
                // Configuración TLS (puerto 587)
                props.put("mail.smtp.starttls.enable", "true");
            }
            
            // Timeout más largo para pruebas
            props.put("mail.smtp.connectiontimeout", "15000");
            props.put("mail.smtp.timeout", "15000");
            
            Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        System.out.println("Autenticando como: " + SMTP_USER);
                        return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
                    }
                });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER));
            message.setRecipients(Message.RecipientType.TO, 
                InternetAddress.parse(correoDestino));
            message.setSubject(asunto != null && !asunto.trim().isEmpty() ? 
                asunto : "Mensaje desde aplicación Monster");
            message.setText(mensaje);
            
            Transport.send(message);
            System.out.println("✅ ¡ÉXITO con " + tipo + " puerto " + puerto + "!");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ FALLO con " + tipo + " puerto " + puerto);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Método ALTERNATIVO: Usar Yahoo u Outlook (a veces funcionan mejor)
    public void enviarConYahoo() {
        String host = "smtp.mail.yahoo.com";
        int port = 587;
        String user = "tuemail@yahoo.com";
        String pass = "tucontraseñaYahoo";
        
        // Similar configuración...
    }
    
    private boolean validarCampos() {
        FacesContext context = FacesContext.getCurrentInstance();
        boolean valido = true;
        
        if (correoDestino == null || correoDestino.trim().isEmpty()) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "El correo destino es obligatorio"));
            valido = false;
        } else if (!EMAIL_PATTERN.matcher(correoDestino.trim()).matches()) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Formato de correo inválido"));
            valido = false;
        }
        
        if (mensaje == null || mensaje.trim().isEmpty()) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "El mensaje es obligatorio"));
            valido = false;
        }
        
        return valido;
    }
    
    private void limpiarCampos() {
        correoDestino = "";
        mensaje = "";
        asunto = "";
    }
    
    // ========== ¡¡¡PRUEBA ESTO PRIMERO!!! ==========
    public void probarConexionSimple() {
        System.out.println("=== PRUEBA DE CONEXIÓN SIMPLE ===");
        
        // Prueba 1: ¿Puede tu aplicación salir a internet?
        probarInternet();
        
        // Prueba 2: ¿Puede llegar a Gmail?
        probarGmail();
        
        // Prueba 3: ¿Tienes las librerías correctas?
        verificarLibrerias();
    }
    
    private void probarInternet() {
        try {
            java.net.InetAddress address = java.net.InetAddress.getByName("google.com");
            boolean reachable = address.isReachable(5000);
            System.out.println("✅ Internet: " + (reachable ? "CONECTADO" : "SIN CONEXIÓN"));
        } catch (Exception e) {
            System.err.println("❌ No hay conexión a internet");
        }
    }
    
    private void probarGmail() {
        System.out.println("Probando puertos de Gmail...");
        
        int[] puertos = {465, 587, 25};
        for (int puerto : puertos) {
            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress("smtp.gmail.com", puerto), 5000);
                System.out.println("✅ Puerto " + puerto + ": ABIERTO");
                socket.close();
            } catch (Exception e) {
                System.out.println("❌ Puerto " + puerto + ": BLOQUEADO o timeout");
            }
        }
    }
    
    private void verificarLibrerias() {
        System.out.println("=== VERIFICANDO LIBRERÍAS ===");
        try {
            Class.forName("javax.mail.Session");
            System.out.println("✅ mail.jar: PRESENTE");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ mail.jar: NO ENCONTRADA");
        }
        
        try {
            Class.forName("javax.activation.DataHandler");
            System.out.println("✅ activation.jar: PRESENTE");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ activation.jar: NO ENCONTRADA");
        }
    }
    
    // Getters y Setters
    public String getCorreoDestino() { return correoDestino; }
    public void setCorreoDestino(String correoDestino) { this.correoDestino = correoDestino; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
}