package ec.edu.monster.controlador;

import ec.edu.monster.modelo.XeopcOpcion;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.XeopcOpcionFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("xeopcOpcionController")
@SessionScoped
public class XeopcOpcionController implements Serializable {

    @EJB
    private XeopcOpcionFacade ejbFacade;
    private List<XeopcOpcion> items = null;
    private XeopcOpcion selected;

    public XeopcOpcionController() {
    }

    public XeopcOpcion getSelected() {
        return selected;
    }

    public void setSelected(XeopcOpcion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private XeopcOpcionFacade getFacade() {
        return ejbFacade;
    }

    public XeopcOpcion prepareCreate() {
        selected = new XeopcOpcion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("XeopcOpcionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("XeopcOpcionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("XeopcOpcionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<XeopcOpcion> getItems() {
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

    // Método para encontrar una opción por ID (necesario para la interfaz)
    public XeopcOpcion findXeopcOpcion(String id) {
        return getFacade().find(id);
    }

    // Método para obtener una opción por ID (manteniendo compatibilidad)
    public XeopcOpcion getXeopcOpcion(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<XeopcOpcion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<XeopcOpcion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    /**
     * Método adicional para obtener opciones ordenadas por nombre
     */
    public List<XeopcOpcion> getOpcionesOrdenadas() {
        return getFacade().findAllOrdenadas();
    }

    /**
     * Método para verificar si una opción existe
     */
    public boolean existeOpcion(String id) {
        return getFacade().find(id) != null;
    }

    /**
     * Reinicia la lista de items (útil después de operaciones CRUD)
     */
    public void reiniciarLista() {
        items = null;
        selected = null;
    }

    @FacesConverter(forClass = XeopcOpcion.class)
    public static class XeopcOpcionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            XeopcOpcionController controller = (XeopcOpcionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "xeopcOpcionController");
            
            if (controller == null) {
                return null;
            }
            
            return controller.getXeopcOpcion(getKey(value));
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
            if (object instanceof XeopcOpcion) {
                XeopcOpcion o = (XeopcOpcion) object;
                return getStringKey(o.getXeopcId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, 
                    "object {0} is of type {1}; expected type: {2}", 
                    new Object[]{object, object.getClass().getName(), XeopcOpcion.class.getName()});
                return null;
            }
        }
    }
}