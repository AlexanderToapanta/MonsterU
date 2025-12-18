package ec.edu.monster.controlador;

import ec.edu.monster.modelo.XerolRol;
import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.XerolRolFacade;
import ec.edu.monster.facades.XeusuUsuarFacade;
import org.primefaces.model.DualListModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("xerolRolController")
@SessionScoped
public class XerolRolController implements Serializable {

    @EJB
    private XerolRolFacade ejbFacade;
    
    @EJB
    private XeusuUsuarFacade usuarioFacade;
    
    private List<XerolRol> items = null;
    private XerolRol selected;
    private String rolSeleccionadoId;
    private DualListModel<XeusuUsuar> dualUsuarios;
    
    private List<XeusuUsuar> usuariosSinRol; // Usuarios sin rol (lista izquierda)
    private List<XeusuUsuar> usuariosConRol; // Usuarios con este rol específico (lista derecha)
    private List<XeusuUsuar> usuariosSeleccionadosSinRol = new ArrayList<>();
private List<XeusuUsuar> usuariosSeleccionadosConRol = new ArrayList<>();

    @PostConstruct
    public void init() {
        dualUsuarios = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
    }

    public XerolRolController() {
    }

    // Getters y Setters
    public XerolRol getSelected() {
        return selected;
    }
// En XerolRolController.java
public XerolRol findXerolRol(String id) {
    return getFacade().find(id);
}
    public void setSelected(XerolRol selected) {
        this.selected = selected;
    }

    public String getRolSeleccionadoId() {
        return rolSeleccionadoId;
    }

    public void setRolSeleccionadoId(String rolSeleccionadoId) {
        this.rolSeleccionadoId = rolSeleccionadoId;
    }

    public DualListModel<XeusuUsuar> getDualUsuarios() {
        return dualUsuarios;
    }

    public void setDualUsuarios(DualListModel<XeusuUsuar> dualUsuarios) {
        this.dualUsuarios = dualUsuarios;
    }
    
    public List<XeusuUsuar> getUsuariosSinRol() {
        return usuariosSinRol;
    }
    
    public List<XeusuUsuar> getUsuariosConRol() {
        return usuariosConRol;
    }
public List<XeusuUsuar> getUsuariosSeleccionadosSinRol() {
    return usuariosSeleccionadosSinRol;
}

public void setUsuariosSeleccionadosSinRol(List<XeusuUsuar> usuariosSeleccionadosSinRol) {
    this.usuariosSeleccionadosSinRol = usuariosSeleccionadosSinRol;
}

public List<XeusuUsuar> getUsuariosSeleccionadosConRol() {
    return usuariosSeleccionadosConRol;
}

public void setUsuariosSeleccionadosConRol(List<XeusuUsuar> usuariosSeleccionadosConRol) {
    this.usuariosSeleccionadosConRol = usuariosSeleccionadosConRol;
}
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private XerolRolFacade getFacade() {
        return ejbFacade;
    }

    public XerolRol prepareCreate() {
        selected = new XerolRol();
        initializeEmbeddableKey();
        try {
            String nextId = getFacade().nextNumericId("xerolId");
            selected.setXerolId(nextId);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error generando ID automático para rol", ex);
        }
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("XerolRolCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("XerolRolUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("XerolRolDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null;
            items = null;
        }
    }

    public List<XerolRol> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    
    public void asignarUsuariosSeleccionados() {
    if (selected == null || usuariosSeleccionadosSinRol.isEmpty()) {
        JsfUtil.addErrorMessage("Seleccione usuarios para asignar");
        return;
    }
    
    // Mover de sin rol a con rol
    for (XeusuUsuar usuario : usuariosSeleccionadosSinRol) {
        if (usuariosSinRol.contains(usuario)) {
            usuariosSinRol.remove(usuario);
            usuariosConRol.add(usuario);
        }
    }
    
    // Actualizar las tablas
    usuariosSeleccionadosSinRol.clear();
    JsfUtil.addSuccessMessage("Usuarios movidos a la lista de asignados");
}

public void quitarUsuariosSeleccionados() {
    if (selected == null || usuariosSeleccionadosConRol.isEmpty()) {
        JsfUtil.addErrorMessage("Seleccione usuarios para quitar");
        return;
    }
    
    // Mover de con rol a sin rol
    for (XeusuUsuar usuario : usuariosSeleccionadosConRol) {
        if (usuariosConRol.contains(usuario)) {
            usuariosConRol.remove(usuario);
            usuariosSinRol.add(usuario);
        }
    }
    
    // Actualizar las tablas
    usuariosSeleccionadosConRol.clear();
    JsfUtil.addSuccessMessage("Usuarios movidos a la lista de disponibles");
}

// Modificar el método guardarCambios
public void guardarCambios() {
    if (selected == null) {
        JsfUtil.addErrorMessage("No hay rol seleccionado para guardar asignaciones");
        return;
    }
    
    try {
        // 1. Quitar rol a todos los usuarios que estaban en usuariosConRol originalmente
        List<XeusuUsuar> usuariosConRolOriginal = usuarioFacade.findUsuariosByRolId(selected.getXerolId());
        
        for (XeusuUsuar usuarioOriginal : usuariosConRolOriginal) {
            boolean sigueEnLista = false;
            for (XeusuUsuar usuarioActual : usuariosConRol) {
                if (usuarioActual.getXeusuId().equals(usuarioOriginal.getXeusuId())) {
                    sigueEnLista = true;
                    break;
                }
            }
            
            if (!sigueEnLista) {
                usuarioOriginal.setXerolId(null);
                usuarioFacade.edit(usuarioOriginal);
                System.out.println("Rol quitado de: " + usuarioOriginal.getXeusuId());
            }
        }
        
        // 2. Asignar rol a los usuarios que están en usuariosConRol ahora
        for (XeusuUsuar usuarioActual : usuariosConRol) {
            boolean yaTieneRol = false;
            for (XeusuUsuar usuarioOriginal : usuariosConRolOriginal) {
                if (usuarioOriginal.getXeusuId().equals(usuarioActual.getXeusuId())) {
                    yaTieneRol = true;
                    break;
                }
            }
            
            if (!yaTieneRol) {
                usuarioActual.setXerolId(selected);
                usuarioFacade.edit(usuarioActual);
                System.out.println("Rol asignado a: " + usuarioActual.getXeusuId());
            }
        }
        
        JsfUtil.addSuccessMessage("Asignaciones guardadas correctamente en la base de datos");
        
    } catch (Exception ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al guardar asignaciones", ex);
        JsfUtil.addErrorMessage("Error al guardar las asignaciones: " + ex.getMessage());
    }
}
    
    // Método para cargar usuarios cuando se selecciona un rol
    public void cargarUsuariosRol() {
        if (rolSeleccionadoId != null && !rolSeleccionadoId.isEmpty()) {
            selected = getFacade().find(rolSeleccionadoId);
            if (selected != null) {
                // Obtener TODOS los usuarios SIN rol
                usuariosSinRol = usuarioFacade.findUsuariosSinRol();
                
                // Obtener usuarios CON este rol específico
                usuariosConRol = usuarioFacade.findUsuariosByRolId(rolSeleccionadoId);
                
                // Configurar el DualListModel
                dualUsuarios = new DualListModel<>(usuariosSinRol, usuariosConRol);
                
                // Log para depuración
                System.out.println("=== Cargando usuarios para rol: " + selected.getXerolNombre() + " ===");
                System.out.println("Usuarios sin rol (izquierda): " + (usuariosSinRol != null ? usuariosSinRol.size() : 0));
                System.out.println("Usuarios con este rol (derecha): " + (usuariosConRol != null ? usuariosConRol.size() : 0));
                
                if (usuariosSinRol != null && !usuariosSinRol.isEmpty()) {
                    System.out.println("Ejemplo de usuario sin rol: " + usuariosSinRol.get(0).getXeusuNombre());
                }
                if (usuariosConRol != null && !usuariosConRol.isEmpty()) {
                    System.out.println("Ejemplo de usuario con rol: " + usuariosConRol.get(0).getXeusuNombre());
                }
                
            } else {
                // Rol no encontrado
                JsfUtil.addErrorMessage("El rol seleccionado no existe");
                limpiarSeleccion();
            }
        } else {
            // Si no hay rol seleccionado, limpiar las listas
            limpiarSeleccion();
        }
    }

    // Método para guardar los cambios de asignación
    

    // Método para limpiar selección
    public void limpiarSeleccion() {
        rolSeleccionadoId = null;
        selected = null;
        usuariosSinRol = new ArrayList<>();
        usuariosConRol = new ArrayList<>();
        dualUsuarios = new DualListModel<>(usuariosSinRol, usuariosConRol);
        System.out.println("Selección limpiada");
    }

    // Método para obtener estadísticas
    public String getEstadisticas() {
        if (selected == null) {
            return "Seleccione un rol para ver estadísticas";
        }
        int totalSinRol = usuariosSinRol != null ? usuariosSinRol.size() : 0;
        int totalConRol = usuariosConRol != null ? usuariosConRol.size() : 0;
        return String.format("Usuarios sin rol: %d | Usuarios con este rol: %d", totalSinRol, totalConRol);
    }
    
    // Método para verificar si hay un rol seleccionado
    public boolean isRolSeleccionado() {
        return selected != null;
    }
    
    // Método para obtener total de usuarios sin rol (para mostrar en UI)
    public int getTotalUsuariosSinRol() {
        return usuariosSinRol != null ? usuariosSinRol.size() : 0;
    }
    
    // Método para obtener total de usuarios con rol seleccionado
    public int getTotalUsuariosConRol() {
        return usuariosConRol != null ? usuariosConRol.size() : 0;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public XerolRol getXerolRol(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<XerolRol> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<XerolRol> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = XerolRol.class)
    public static class XerolRolControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            XerolRolController controller = (XerolRolController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "xerolRolController");
            return controller.getXerolRol(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof XerolRol) {
                XerolRol o = (XerolRol) object;
                return getStringKey(o.getXerolId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", 
                    new Object[]{object, object.getClass().getName(), XerolRol.class.getName()});
                return null;
            }
        }
    }
}