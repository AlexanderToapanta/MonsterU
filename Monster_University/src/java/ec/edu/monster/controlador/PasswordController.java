/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;

@Named(value = "passwordController")
@RequestScoped
public class PasswordController {
    
    public String encriptarClave(String clave) throws NoSuchAlgorithmException {
        if (clave == null || clave.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        
        // Debug
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts()
            .add("console.log('🔐 Encriptando: ' + '" + clave.replace("'", "\\'") + "');");
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(clave.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        
        // Convertir a hexadecimal (64 caracteres máximo)
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        String resultado = hexString.toString();
        
        // Debug del resultado
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts()
            .add("console.log('✅ Resultado encriptación: ' + '" + resultado + "');");
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts()
            .add("console.log('📏 Longitud: ' + " + resultado.length() + ");");
        
        return resultado;
    }
    
    public String generarContraseñaAleatoria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        
        return sb.toString();
    }
}