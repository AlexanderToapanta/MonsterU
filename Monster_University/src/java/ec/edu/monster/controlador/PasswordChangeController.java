/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelo.XeusuUsuar;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "passwordChangeController")
@ViewScoped
public class PasswordChangeController implements Serializable {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
    
    @EJB
    private ec.edu.monster.facades.XeusuUsuarFacade ejbFacade;
    
    private final PasswordController passController;

    public PasswordChangeController() {
        passController = new PasswordController();
    }

    // Getters y Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void changePassword() throws NoSuchAlgorithmException, IOException {
        // Validaciones básicas
        if (newPassword == null || confirmPassword == null || !newPassword.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden"));
            return;
        }

        if (newPassword.length() < 6) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe tener al menos 6 caracteres"));
            return;
        }

        // Obtener usuario de la sesión
        XeusuUsuar usuario = (XeusuUsuar) FacesContext.getCurrentInstance()
            .getExternalContext().getSessionMap().get("usuario");
        
        if (usuario == null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/Monster_University/faces/login.xhtml");
            return;
        }

        // Verificar contraseña actual (si se está cambiando, no en primer acceso)
        if (currentPassword != null && !currentPassword.isEmpty()) {
            String currentPasswordEncrypted = passController.encriptarClave(currentPassword);
            if (!usuario.getXeusuContra().equals(currentPasswordEncrypted)) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Contraseña actual incorrecta"));
                return;
            }
        }

        // Actualizar contraseña
        String newPasswordEncrypted = passController.encriptarClave(newPassword);
        usuario.setXeusuContra(newPasswordEncrypted);
        
        ejbFacade.edit(usuario);

        // Mensaje de éxito
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Contraseña cambiada correctamente"));
        
        // Redirigir al inicio
        FacesContext.getCurrentInstance().getExternalContext().redirect("/Monster_University/faces/index1.xhtml");
    }
    
    public void cancel() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("/Monster_University/faces/index1.xhtml");
    }
}
