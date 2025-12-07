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
import org.primefaces.model.DualListModel;

@Named("xeusuUsuarController")
@SessionScoped
public class XeusuUsuarController implements Serializable {

    @EJB
    private XeusuUsuarFacade ejbFacade;


    private List<XeusuUsuar> items = null;
    private XeusuUsuar selected;
    private DualListModel<XerolRol> dualRoles;

    // -----------------------
    // Campos para asignación
    // -----------------------
    private String usuarioSeleccionadoId; // id del usuario seleccionado en la UI
    private List<XerolRol> rolesAsignados = new ArrayList<>();
    private List<XerolRol> rolesNoAsignados = new ArrayList<>();

    // selecciones en los listboxes
    private List<XerolRol> rolesSeleccionadosIzq = new ArrayList<>(); // disponibles -> asignar
    private List<XerolRol> rolesSeleccionadosDer = new ArrayList<>(); // asignados -> quitar

    public XeusuUsuarController() {
    }
    
    @Inject
private XerolRolFacade rolFacade;   // Inyectar facade de roles

private XerolRol rolSeleccionado;   // Rol elegido del combo

public XerolRol getRolSeleccionado() {
    return rolSeleccionado;
}

public void setRolSeleccionado(XerolRol rolSeleccionado) {
    this.rolSeleccionado = rolSeleccionado;
}

public List<XerolRol> getListaRoles() {
    return rolFacade.findAll(); // Cargar roles desde BD
}
public DualListModel<XerolRol> getDualRoles() {
    if (dualRoles == null) {
        dualRoles = new DualListModel<>(rolesNoAsignados, rolesAsignados);
    }
    return dualRoles;
}

public void setDualRoles(DualListModel<XerolRol> dualRoles) {
    this.dualRoles = dualRoles;
}


    // -----------------------
    // CRUD original
    // -----------------------
    public XeusuUsuar getSelected() {
        return selected;
    }

    public void setSelected(XeusuUsuar selected) {
        this.selected = selected;
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
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        hashPasswordIfNeeded();
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("XeusuUsuarCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
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
    // Métodos para asignación de roles (Option B)
    // -----------------------

    /**
     * Cargar listas de roles para el usuario seleccionado:
     * - rolesAsignados  = roles que ya tiene el usuario
     * - rolesNoAsignados = todos los roles menos los asignados
     */
    public void asignarRol() {
    try {
        if (selected == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un usuario", "")
            );
            return;
        }

        if (rolSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un rol", "")
            );
            return;
        }

        if (!selected.getRoles().contains(rolSeleccionado)) {
            selected.getRoles().add(rolSeleccionado);
            getFacade().edit(selected);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Rol asignado correctamente"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "El usuario ya tiene este rol", ""));
        }

    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al asignar rol", e.getMessage()));
    }
}

    public void quitarRol(XerolRol rol) {
    try {
        selected.getRoles().remove(rol);
        getFacade().edit(selected);

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Rol eliminado correctamente"));

    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al quitar rol", e.getMessage()));
    }
}

    public void cargarRolesUsuario() {
    if (usuarioSeleccionadoId == null || usuarioSeleccionadoId.trim().isEmpty()) {
        rolesAsignados = new ArrayList<>();
        rolesNoAsignados = rolFacade.findAll();
        rolesSeleccionadosIzq.clear();
        rolesSeleccionadosDer.clear();
        dualRoles = new DualListModel<>(rolesNoAsignados, rolesAsignados);
        return;
    }

    XeusuUsuar user = ejbFacade.find(usuarioSeleccionadoId);
    if (user == null) {
        JsfUtil.addErrorMessage("Usuario no encontrado.");
        rolesAsignados = new ArrayList<>();
        rolesNoAsignados = rolFacade.findAll();
        dualRoles = new DualListModel<>(rolesNoAsignados, rolesAsignados);
        return;
    }

    // roles asignados (copiar para no modificar la colección original)
    if (user.getXerolRolCollection() != null) {
        rolesAsignados = new ArrayList<>(user.getXerolRolCollection());
    } else {
        rolesAsignados = new ArrayList<>();
    }

    // roles no asignados = todos - asignados
    List<XerolRol> todos = rolFacade.findAll();
    rolesNoAsignados = new ArrayList<>();
    for (XerolRol r : todos) {
        boolean esta = false;
        for (XerolRol ra : rolesAsignados) {
            if (ra.getXerolId().equals(r.getXerolId())) {
                esta = true;
                break;
            }
        }
        if (!esta) {
            rolesNoAsignados.add(r);
        }
    }

    // actualizar DualListModel para PickList
    dualRoles = new DualListModel<>(rolesNoAsignados, rolesAsignados);

    // limpiar selecciones
    rolesSeleccionadosIzq.clear();
    rolesSeleccionadosDer.clear();
}

    public void moverDerecha() {
        if (rolesSeleccionadosIzq == null || rolesSeleccionadosIzq.isEmpty()) {
            JsfUtil.addErrorMessage("Seleccione al menos un rol para asignar.");
            return;
        }
        rolesAsignados.addAll(rolesSeleccionadosIzq);
        rolesNoAsignados.removeAll(rolesSeleccionadosIzq);
        rolesSeleccionadosIzq.clear();
    }

    public void moverTodoDerecha() {
        rolesAsignados.addAll(rolesNoAsignados);
        rolesNoAsignados.clear();
        rolesSeleccionadosIzq.clear();
    }

    public void moverIzquierda() {
        if (rolesSeleccionadosDer == null || rolesSeleccionadosDer.isEmpty()) {
            JsfUtil.addErrorMessage("Seleccione al menos un rol para quitar.");
            return;
        }
        rolesNoAsignados.addAll(rolesSeleccionadosDer);
        rolesAsignados.removeAll(rolesSeleccionadosDer);
        rolesSeleccionadosDer.clear();
    }

    public void moverTodoIzquierda() {
        rolesNoAsignados.addAll(rolesAsignados);
        rolesAsignados.clear();
        rolesSeleccionadosDer.clear();
    }

    /**
     * Guardar cambios: persiste la colección de roles del usuario seleccionado.
     * Actualiza la entidad usuario (merge/edit). Se mantiene la integridad ManyToMany.
     */
    public void guardarCambios() {
    if (usuarioSeleccionadoId == null || usuarioSeleccionadoId.trim().isEmpty()) {
        JsfUtil.addErrorMessage("Seleccione un usuario primero.");
        return;
    }

    try {
        XeusuUsuar user = ejbFacade.find(usuarioSeleccionadoId);
        if (user == null) {
            JsfUtil.addErrorMessage("Usuario no encontrado.");
            return;
        }

        // Actualizar roles con los seleccionados en el PickList
        rolesAsignados = dualRoles.getTarget();
        user.setXerolRolCollection(new java.util.HashSet<>(rolesAsignados));

        // persistir cambios
        ejbFacade.edit(user);

        JsfUtil.addSuccessMessage("Asignaciones actualizadas correctamente.");
        // recargar listas
        cargarRolesUsuario();

    } catch (Exception e) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        JsfUtil.addErrorMessage("Error al guardar las asignaciones: " + e.getMessage());
    }
}

    // -----------------------
    // Getters & Setters para asignación
    // -----------------------

    public String getUsuarioSeleccionadoId() {
        return usuarioSeleccionadoId;
    }

    public void setUsuarioSeleccionadoId(String usuarioSeleccionadoId) {
        this.usuarioSeleccionadoId = usuarioSeleccionadoId;
    }

    public List<XerolRol> getRolesAsignados() {
        return rolesAsignados;
    }

    public List<XerolRol> getRolesNoAsignados() {
        return rolesNoAsignados;
    }

    public List<XerolRol> getRolesSeleccionadosIzq() {
        return rolesSeleccionadosIzq;
    }

    public void setRolesSeleccionadosIzq(List<XerolRol> rolesSeleccionadosIzq) {
        this.rolesSeleccionadosIzq = rolesSeleccionadosIzq;
    }

    public List<XerolRol> getRolesSeleccionadosDer() {
        return rolesSeleccionadosDer;
    }

    public void setRolesSeleccionadosDer(List<XerolRol> rolesSeleccionadosDer) {
        this.rolesSeleccionadosDer = rolesSeleccionadosDer;
    }

    // -----------------------
    // Resto del controller: utilitarios y converters
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
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), XeusuUsuar.class.getName()});
                return null;
            }
        }

    }

}
