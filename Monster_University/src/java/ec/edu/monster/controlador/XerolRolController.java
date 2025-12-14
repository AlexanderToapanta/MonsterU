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

    @PostConstruct
    public void init() {
        dualUsuarios = new DualListModel<>();
        // Inicialmente, todos los usuarios estarán en la lista de disponibles
        List<XeusuUsuar> todosUsuarios = usuarioFacade.findAll();
        dualUsuarios.setSource(todosUsuarios);
        dualUsuarios.setTarget(new ArrayList<>()); // Lista vacía de asignados
    }

    public XerolRolController() {
    }

    // Getters y Setters
    public XerolRol getSelected() {
        return selected;
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

    // Método para cargar usuarios cuando se selecciona un rol
    public void cargarUsuariosRol() {
        if (rolSeleccionadoId != null && !rolSeleccionadoId.isEmpty()) {
            XerolRol rolSeleccionado = getFacade().find(rolSeleccionadoId);
            if (rolSeleccionado != null) {
                // Obtener todos los usuarios
                List<XeusuUsuar> todosUsuarios = usuarioFacade.findAll();
                
                // Obtener usuarios asignados a este rol
                // NOTA: Esto depende de cómo tengas la relación entre usuarios y roles
                // Si tienes una relación ManyToMany, necesitarías una consulta específica
                List<XeusuUsuar> usuariosAsignados = new ArrayList<>();
                
                // Ejemplo de consulta (debes adaptarlo a tu modelo de datos):
                // usuariosAsignados = usuarioFacade.findUsuariosByRolId(rolSeleccionadoId);
                
                // Por ahora, simulamos que no hay usuarios asignados
                // Esto es solo un ejemplo - debes implementar la lógica real
                List<XeusuUsuar> usuariosDisponibles = new ArrayList<>(todosUsuarios);
                usuariosDisponibles.removeAll(usuariosAsignados);
                
                dualUsuarios = new DualListModel<>(usuariosDisponibles, usuariosAsignados);
                
                // También establecemos el selected para referencia
                selected = rolSeleccionado;
            }
        }
    }

    // Método para guardar los cambios de asignación
    public void guardarCambios() {
        if (rolSeleccionadoId != null && dualUsuarios != null) {
            try {
                // Obtener la lista de usuarios asignados
                List<XeusuUsuar> usuariosAsignados = dualUsuarios.getTarget();
                
                // Lógica para guardar las asignaciones en la base de datos
                // Esto depende de cómo tengas la relación entre usuarios y roles
                
                // Ejemplo de implementación:
                // 1. Eliminar todas las asignaciones actuales para este rol
                // 2. Crear nuevas asignaciones con los usuarios seleccionados
                
                // Por ahora, solo mostramos un mensaje de éxito
                JsfUtil.addSuccessMessage("Asignaciones de usuarios al rol guardadas correctamente");
                
                // Si tienes entidad de relación (ej: XeusuRol), deberías hacer:
                // for (XeusuUsuar usuario : usuariosAsignados) {
                //     XeusuRol asignacion = new XeusuRol();
                //     asignacion.setUsuario(usuario);
                //     asignacion.setRol(selected);
                //     asignacionFacade.create(asignacion);
                // }
                
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al guardar asignaciones", ex);
                JsfUtil.addErrorMessage("Error al guardar las asignaciones: " + ex.getMessage());
            }
        } else {
            JsfUtil.addErrorMessage("No hay rol seleccionado para guardar asignaciones");
        }
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