package ec.edu.monster.controlador;

import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.modelo.XerolRol;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.XeusuUsuarFacade;
import ec.edu.monster.facades.XerolRolFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import java.security.NoSuchAlgorithmException;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@Named("xeusuUsuarController")
@SessionScoped
public class XeusuUsuarController implements Serializable {

    @EJB
    private XeusuUsuarFacade ejbFacade;
    
    @EJB
    private XerolRolFacade rolFacade;

    private List<XeusuUsuar> items = null;
    private XeusuUsuar selected;
    private String usuarioSeleccionadoId;
    private XerolRol rolSeleccionado;

    public XeusuUsuarController() {
    }
    
    // -----------------------
    // CRUD original
    // -----------------------
    public XeusuUsuar getSelected() {
        return selected;
    }

    public void setSelected(XeusuUsuar selected) {
        this.selected = selected;
        // Cuando se selecciona un usuario, cargamos su rol actual
        if (selected != null && selected.getXerolId() != null) {
            this.rolSeleccionado = selected.getXerolId();
        } else {
            this.rolSeleccionado = null;
        }
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private XeusuUsuarFacade getFacade() {
        return ejbFacade;
    }

    public XeusuUsuar prepareCreate() {
        selected = new XeusuUsuar();
        rolSeleccionado = null;
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        // Asignar el rol seleccionado al usuario antes de crear
        if (rolSeleccionado != null) {
            selected.setXerolId(rolSeleccionado);
        }
        hashPasswordIfNeeded();
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("XeusuUsuarCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        // Asignar el rol seleccionado al usuario antes de actualizar
        if (rolSeleccionado != null) {
            selected.setXerolId(rolSeleccionado);
        }
        hashPasswordIfNeeded();
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("XeusuUsuarUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("XeusuUsuarDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<XeusuUsuar> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
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

    private void hashPasswordIfNeeded() {
        if (selected == null) {
            return;
        }
        String plain = selected.getXeusuContra();
        if (plain == null || plain.trim().isEmpty()) {
            return;
        }
        // Avoid double-hashing: if it looks like a SHA-256 hex (64 hex chars), skip
        if (plain.matches("^[a-fA-F0-9]{64}$")) {
            return;
        }
        try {
            PasswordController pc = new PasswordController();
            String hashed = pc.encriptarClave(plain);
            selected.setXeusuContra(hashed);
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Password hashing failed", e);
            JsfUtil.addErrorMessage("Error al encriptar la contraseña");
        } catch (IllegalArgumentException e) {
            JsfUtil.addErrorMessage(e.getMessage());
        }
    }

    // -----------------------
    // Métodos para manejo de roles (NUEVA ESTRUCTURA)
    // -----------------------

    public XerolRol getRolSeleccionado() {
        return rolSeleccionado;
    }

    public void setRolSeleccionado(XerolRol rolSeleccionado) {
        this.rolSeleccionado = rolSeleccionado;
    }

    public List<XerolRol> getListaRoles() {
        return rolFacade.findAll();
    }

    /**
     * Método para asignar/actualizar el rol de un usuario
     */
    public void asignarRolAUsuario() {
        try {
            if (selected == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un usuario", ""));
                return;
            }

            if (rolSeleccionado == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un rol", ""));
                return;
            }

            // Verificar si el rol ya está asignado
            XerolRol rolActual = selected.getXerolId();
            if (rolActual != null && rolActual.getXerolId().equals(rolSeleccionado.getXerolId())) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "El usuario ya tiene este rol asignado", ""));
                return;
            }

            // Asignar el nuevo rol
            selected.setXerolId(rolSeleccionado);
            ejbFacade.edit(selected);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Rol asignado correctamente", ""));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al asignar rol", e.getMessage()));
        }
    }

    /**
     * Método para quitar el rol de un usuario
     */
    public void quitarRolDeUsuario() {
        try {
            if (selected == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un usuario", ""));
                return;
            }

            if (selected.getXerolId() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "El usuario no tiene rol asignado", ""));
                return;
            }

            // Quitar el rol
            selected.setXerolId(null);
            ejbFacade.edit(selected);
            rolSeleccionado = null;

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Rol eliminado correctamente", ""));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al quitar rol", e.getMessage()));
        }
    }

    /**
     * Cargar información del usuario seleccionado
     */
    public void cargarUsuario() {
        if (usuarioSeleccionadoId == null || usuarioSeleccionadoId.trim().isEmpty()) {
            selected = null;
            rolSeleccionado = null;
            return;
        }

        XeusuUsuar usuario = ejbFacade.find(usuarioSeleccionadoId);
        if (usuario == null) {
            JsfUtil.addErrorMessage("Usuario no encontrado.");
            selected = null;
            rolSeleccionado = null;
            return;
        }

        selected = usuario;
        rolSeleccionado = usuario.getXerolId();
    }

    // -----------------------
    // Getters & Setters
    // -----------------------

    public String getUsuarioSeleccionadoId() {
        return usuarioSeleccionadoId;
    }

    public void setUsuarioSeleccionadoId(String usuarioSeleccionadoId) {
        this.usuarioSeleccionadoId = usuarioSeleccionadoId;
    }

    // -----------------------
    // Métodos de utilidad para la vista
    // -----------------------

    public XeusuUsuar getXeusuUsuar(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<XeusuUsuar> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<XeusuUsuar> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    // -----------------------
    // Converter
    // -----------------------

    @FacesConverter(forClass = XeusuUsuar.class)
    public static class XeusuUsuarControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            XeusuUsuarController controller = (XeusuUsuarController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "xeusuUsuarController");
            return controller.getXeusuUsuar(getKey(value));
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
            if (object instanceof XeusuUsuar) {
                XeusuUsuar o = (XeusuUsuar) object;
                return getStringKey(o.getXeusuId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", 
                    new Object[]{object, object.getClass().getName(), XeusuUsuar.class.getName()});
                return null;
            }
        }
    }
}