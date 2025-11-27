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
            // Validaciones
            if (!validarDatos()) {
                return;
            }

            // Verificar si el ID ya existe
            if (usuarioFacade.find(nuevoUsuario.getXeusuId()) != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de usuario ya existe"));
                return;
            }

            // Encriptar contraseña
            String contrasenaEncriptada = passwordController.encriptarClave(nuevoUsuario.getXeusuContra());
            nuevoUsuario.setXeusuContra(contrasenaEncriptada);

            // Establecer campos NULL explícitamente
            nuevoUsuario.setPeperId(null);
            nuevoUsuario.setMeestEstud(null);

            // Guardar usuario
            usuarioFacade.create(nuevoUsuario);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario creado correctamente"));

            // Limpiar formulario
            initNuevoUsuario();

        } catch (NoSuchAlgorithmException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al encriptar contraseña: " + e.getMessage()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear usuario: " + e.getMessage()));
        }
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