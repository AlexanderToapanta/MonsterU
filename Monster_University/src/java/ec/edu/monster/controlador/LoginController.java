/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.facades.XeusuUsuarFacade;
import ec.edu.monster.facades.XrXerolXeopcFacade;
import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.modelo.XeopcOpcion;
import ec.edu.monster.modelo.XrXerolXeopc;
import ec.edu.monster.modelo.UserCache;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {
    
    private final PasswordController passController;
    private XeusuUsuar usuario;
    private ExternalContext context = FacesContext.getCurrentInstance().getExternalContext(); 
    private UserCache usu = new UserCache();
    
    @EJB
    private XeusuUsuarFacade usuarioFacade;
    
    @EJB
    private XrXerolXeopcFacade xrXerolXeopcFacade;
    
    // Listas para almacenar opciones del usuario
    private List<XeopcOpcion> opcionesUsuario;
    private List<String> idsOpcionesUsuario;

    public LoginController() {
        usuario = new XeusuUsuar();
        passController = new PasswordController();
        opcionesUsuario = new ArrayList<>();
        idsOpcionesUsuario = new ArrayList<>();
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
    
    public List<XeopcOpcion> getOpcionesUsuario() {
        return opcionesUsuario;
    }
    
    public List<String> getIdsOpcionesUsuario() {
        return idsOpcionesUsuario;
    }
    
    @PostConstruct
    public void init() {
        XeusuUsuar x = getUsuarioLogueado();
        if (x != null) {
            // Si ya hay sesión, cargar datos en cache
            cargarDatosUsuarioCache(x);
            cargarOpcionesUsuario();
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
            
            // Cargar opciones del usuario según su rol
            cargarOpcionesUsuario();
            
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
    
    /**
     * Carga las opciones del usuario según su rol
     */
    public void cargarOpcionesUsuario() {
        opcionesUsuario.clear();
        idsOpcionesUsuario.clear();
        
        XeusuUsuar usuarioLogueado = getUsuarioLogueado();
        
        if (usuarioLogueado != null && usuarioLogueado.getXerolId() != null) {
            try {
                // Obtener opciones del rol del usuario
                List<XrXerolXeopc> asignaciones = xrXerolXeopcFacade.findOpcionesPorRol(usuarioLogueado.getXerolId().getXerolId());
                
                // Extraer las opciones de las asignaciones
                for (XrXerolXeopc asignacion : asignaciones) {
                    if (asignacion.getXropFechaRetiro() == null) { // Solo opciones activas
                        XeopcOpcion opcion = asignacion.getXeopcOpcion();
                        if (opcion != null) {
                            opcionesUsuario.add(opcion);
                            idsOpcionesUsuario.add(opcion.getXeopcId());
                        }
                    }
                }
                
                // Log para depuración
                System.out.println("Usuario " + usuarioLogueado.getXeusuNombre() + 
                                 " tiene " + opcionesUsuario.size() + " opciones asignadas");
                idsOpcionesUsuario.forEach(id -> System.out.println(" - Opción: " + id));
                
            } catch (Exception e) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, "Error al cargar opciones", e);
            }
        }
    }
    
    /**
     * Verifica si el usuario tiene una opción específica
     */
    public boolean tieneOpcion(String opcionId) {
        if (opcionId == null || opcionId.trim().isEmpty()) {
            return false;
        }
        
        // Si la lista está vacía, intentar cargarla
        if (idsOpcionesUsuario.isEmpty() && isLoggedIn()) {
            cargarOpcionesUsuario();
        }
        
        return idsOpcionesUsuario.contains(opcionId);
    }
    
    /**
     * Verifica si el usuario tiene al menos una opción de un grupo
     */
    public boolean tieneOpcionEnGrupo(String... opcionesIds) {
        // Si la lista está vacía, intentar cargarla
        if (idsOpcionesUsuario.isEmpty() && isLoggedIn()) {
            cargarOpcionesUsuario();
        }
        
        for (String opcionId : opcionesIds) {
            if (idsOpcionesUsuario.contains(opcionId)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifica si el usuario tiene acceso a una opción principal (PER, FIN, ACA, SEG)
     */
    public boolean tieneAccesoMenu(String menuId) {
        // Opciones principales que representan menús
        switch (menuId) {
            case "PER":
                return tieneOpcionEnGrupo("PER", "CRE");
            case "FIN":
                return tieneOpcion("FIN");
            case "ACA":
                return tieneOpcionEnGrupo("ACA", "AC1", "AC2");
            case "SEG":
                return tieneOpcionEnGrupo("SEG", "SE1", "SE2");
            default:
                return false;
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
        
        // Limpiar opciones del usuario
        opcionesUsuario.clear();
        idsOpcionesUsuario.clear();
        
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        // Redirigir al login
        FacesContext.getCurrentInstance().getExternalContext()
                .redirect("/Monster_University/faces/login.xhtml");
        return null;
    }
    
    /**
     * Método para verificar si hay sesión activa
     */
    public boolean isLoggedIn() {
        return getUsuarioLogueado() != null;
    }

    /**
     * Obtiene el usuario logueado de la sesión
     */
    public XeusuUsuar getUsuarioLogueado() {
        return (XeusuUsuar) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("usuario");
    }
    
    /**
     * Forzar recarga de opciones (útil después de cambios de rol)
     */
    public void recargarOpciones() {
        cargarOpcionesUsuario();
    }
    
    /**
     * Obtiene el nombre del rol del usuario logueado
     */
    public String getNombreRolUsuario() {
        XeusuUsuar usuario = getUsuarioLogueado();
        if (usuario != null && usuario.getXerolId() != null) {
            return usuario.getXerolId().getXerolNombre();
        }
        return "Sin rol asignado";
    }
}