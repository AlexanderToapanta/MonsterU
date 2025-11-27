/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.facades.XeusuUsuarFacade;
import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.modelo.UserCache;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {
    
    private final PasswordController passController;
    private XeusuUsuar usuario;
    private ExternalContext context = FacesContext.getCurrentInstance().getExternalContext(); 
    private UserCache usu = new UserCache();
    
    @EJB
    private XeusuUsuarFacade usuarioFacade;

    public LoginController() {
        usuario = new XeusuUsuar();
        passController = new PasswordController();
    }

    // Getters y Setters
    public XeusuUsuar getUsuario() {
        return usuario;
    }

    public void setUsuario(XeusuUsuar usuario) {
        this.usuario = usuario;
    }

    public ExternalContext getContext() {
        return context;
    }

    public void setContext(ExternalContext context) {
        this.context = context;
    }

    public UserCache getUsu() {
        return usu;
    }

    public void setUsu(UserCache usu) {
        this.usu = usu;
    }
    
    @PostConstruct
    public void init() {
        XeusuUsuar x = (XeusuUsuar) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuario");
        if (x != null) {
            // Si ya hay sesión, cargar datos en cache
            cargarDatosUsuarioCache(x);
        }
    }

    public void doLogin() throws NoSuchAlgorithmException, IOException {
        String clave = usuario.getXeusuContra();
        String claveCifrada = passController.encriptarClave(clave);
        
        XeusuUsuar usuarioLogueado = usuarioFacade.doLogin(usuario.getXeusuNombre(), claveCifrada);
        
        if (usuarioLogueado != null) {
            // Verificar estado del usuario
            if (!"ACTIVO".equals(usuarioLogueado.getXeusuEstado())) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario inactivo"));
                return;
            }
            
            // Guardar usuario en sesión
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuarioLogueado);
            
            // Cargar datos en cache
            cargarDatosUsuarioCache(usuarioLogueado);
            
            // Redirección según necesidad
            if (necesitaCambiarContrasena(usuarioLogueado)) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("/Monster_University/faces/cambioContrasena.xhtml");
            } else {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("/Monster_University/faces/index1.xhtml");
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Credenciales incorrectas"));
        }
    }
    
    private void cargarDatosUsuarioCache(XeusuUsuar usuario) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("BienesMonster_G08PU");
            EntityManager em = emf.createEntityManager();
            
            // Cargar información básica del usuario
            usu.setUsuario(usuario.getXeusuNombre());
            usu.setNombre(usuario.getXeusuNombre()); 
            
            em.close();
            emf.close();
            
        } catch (Exception e) {
            System.out.println("Error cargando datos de usuario en cache: " + e.getMessage());
        }
    }
    
    private boolean necesitaCambiarContrasena(XeusuUsuar usuario) {
        // Lógica para determinar si necesita cambiar contraseña
        // Por ejemplo, si es primer acceso o contraseña expirada
        // return usuario.getXeusuUltpass() == null;
        return false; // Ajusta según tu lógica
    }

    public String doLogout() throws IOException {
    // Limpiar sesión
    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("usuario");
    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("usu");
    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    
    // Redirigir al login
    FacesContext.getCurrentInstance().getExternalContext()
            .redirect("/Monster_University/faces/login.xhtml");
    return null;
}
    
    // Método para verificar si hay sesión activa
    public boolean isLoggedIn() {
    return getUsuarioLogueado() != null;
}

// Y este getter si no lo tienes
public XeusuUsuar getUsuarioLogueado() {
    return (XeusuUsuar) FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().get("usuario");
}
}