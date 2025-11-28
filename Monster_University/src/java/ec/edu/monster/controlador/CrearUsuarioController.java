/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.facades.XeusuUsuarFacade;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named(value = "crearUsuarioController")
@SessionScoped
public class CrearUsuarioController implements Serializable {

    @EJB
    private XeusuUsuarFacade usuarioFacade;
    
    private XeusuUsuar nuevoUsuario;
    private String confirmarContrasena;
    private PasswordController passwordController;

    public CrearUsuarioController() {
        nuevoUsuario = new XeusuUsuar();
        passwordController = new PasswordController();
    }

    public void initNuevoUsuario() {
        nuevoUsuario = new XeusuUsuar();
        confirmarContrasena = "";
        // Establecer estado por defecto
        nuevoUsuario.setXeusuEstado("ACTIVO");
    }

   public void crearUsuario() {
    try {
        // Mensaje inicial en consola del navegador
        ejecutarJavaScript("console.log('=== INICIANDO CREACIÓN DE USUARIO ===');");
        
        // 1. Mostrar datos recibidos
        ejecutarJavaScript("console.log('Datos recibidos:');");
        ejecutarJavaScript("console.log('ID: " + escapeJavaScript(nuevoUsuario.getXeusuId()) + "');");
        ejecutarJavaScript("console.log('Nombre: " + escapeJavaScript(nuevoUsuario.getXeusuNombre()) + "');");
        ejecutarJavaScript("console.log('Contraseña (plana): " + escapeJavaScript(nuevoUsuario.getXeusuContra()) + "');");
        ejecutarJavaScript("console.log('Estado: " + escapeJavaScript(nuevoUsuario.getXeusuEstado()) + "');");

        // Validaciones
        if (!validarDatos()) {
            ejecutarJavaScript("console.error('❌ Validaciones fallaron');");
            return;
        }
        ejecutarJavaScript("console.log('✅ Validaciones pasadas');");

        // Verificar si el ID ya existe
        if (usuarioFacade.find(nuevoUsuario.getXeusuId()) != null) {
            ejecutarJavaScript("console.error('❌ ID de usuario ya existe: " + escapeJavaScript(nuevoUsuario.getXeusuId()) + "');");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de usuario ya existe"));
            return;
        }
        ejecutarJavaScript("console.log('✅ ID de usuario disponible');");

        // Encriptar contraseña
        ejecutarJavaScript("console.log('🔐 Encriptando contraseña...');");
        String contrasenaEncriptada = passwordController.encriptarClave(nuevoUsuario.getXeusuContra());
        nuevoUsuario.setXeusuContra(contrasenaEncriptada);
        ejecutarJavaScript("console.log('✅ Contraseña encriptada. Longitud: " + contrasenaEncriptada.length() + "');");

        // Establecer campos NULL explícitamente
        nuevoUsuario.setPeperId(null);
        nuevoUsuario.setMeestEstud(null);

        // Guardar usuario
        ejecutarJavaScript("console.log('💾 Guardando en base de datos...');");
        usuarioFacade.create(nuevoUsuario);
        ejecutarJavaScript("console.log('✅ usuarioFacade.create() ejecutado');");

        // Verificar inserción
        XeusuUsuar usuarioVerificado = usuarioFacade.find(nuevoUsuario.getXeusuId());
        if (usuarioVerificado != null) {
            ejecutarJavaScript("console.log('🎉 USUARIO CREADO EXITOSAMENTE');");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario creado correctamente"));
        } else {
            ejecutarJavaScript("console.error('❌ USUARIO NO SE GUARDÓ EN BD');");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar usuario"));
        }

        // Limpiar formulario
        initNuevoUsuario();

    } catch (NoSuchAlgorithmException e) {
        ejecutarJavaScript("console.error('❌ Error de encriptación: " + escapeJavaScript(e.getMessage()) + "');");
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al encriptar contraseña: " + e.getMessage()));
    } catch (Exception e) {
        ejecutarJavaScript("console.error('💥 ERROR GENERAL: " + escapeJavaScript(e.getMessage()) + "');");
        ejecutarJavaScript("console.error('Tipo de error: " + escapeJavaScript(e.getClass().getName()) + "');");
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear usuario: " + e.getMessage()));
    }
}

// Método para ejecutar JavaScript desde Java
private void ejecutarJavaScript(String script) {
    FacesContext context = FacesContext.getCurrentInstance();
    if (context != null) {
        context.getPartialViewContext().getEvalScripts().add(script);
    }
    // También imprimir en consola del servidor por si acaso
    System.out.println("[JS] " + script.replace("console.log('", "").replace("');", ""));
}

// Método para escapar caracteres especiales en JavaScript
private String escapeJavaScript(String text) {
    if (text == null) return "null";
    return text.replace("'", "\\'")
               .replace("\"", "\\\"")
               .replace("\n", "\\n")
               .replace("\r", "\\r")
               .replace("\t", "\\t");
}

    private boolean validarDatos() {
        if (nuevoUsuario.getXeusuId() == null || nuevoUsuario.getXeusuId().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de usuario es requerido"));
            return false;
        }

        if (nuevoUsuario.getXeusuId().length() > 5) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de usuario no puede tener más de 5 caracteres"));
            return false;
        }

        if (nuevoUsuario.getXeusuNombre() == null || nuevoUsuario.getXeusuNombre().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre es requerido"));
            return false;
        }

        if (nuevoUsuario.getXeusuNombre().length() > 100) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede tener más de 100 caracteres"));
            return false;
        }

        if (nuevoUsuario.getXeusuContra() == null || nuevoUsuario.getXeusuContra().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña es requerida"));
            return false;
        }

        if (nuevoUsuario.getXeusuContra().length() < 6) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe tener al menos 6 caracteres"));
            return false;
        }

        if (confirmarContrasena == null || !confirmarContrasena.equals(nuevoUsuario.getXeusuContra())) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden"));
            return false;
        }

        if (nuevoUsuario.getXeusuEstado() == null || nuevoUsuario.getXeusuEstado().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El estado es requerido"));
            return false;
        }

        return true;
    }

    public void generarContrasenaAleatoria() {
        try {
            String contrasenaAleatoria = passwordController.generarContraseñaAleatoria();
            nuevoUsuario.setXeusuContra(contrasenaAleatoria);
            confirmarContrasena = contrasenaAleatoria;
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Contraseña generada", "Se ha generado una contraseña aleatoria"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al generar contraseña: " + e.getMessage()));
        }
    }

    // Getters y Setters
    public XeusuUsuar getNuevoUsuario() {
        return nuevoUsuario;
    }

    public void setNuevoUsuario(XeusuUsuar nuevoUsuario) {
        this.nuevoUsuario = nuevoUsuario;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }
}