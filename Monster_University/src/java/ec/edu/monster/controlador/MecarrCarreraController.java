package ec.edu.monster.controlador;

import ec.edu.monster.modelo.MecarrCarrera;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.MecarrCarreraFacade;

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

@Named("mecarrCarreraController")
@SessionScoped
public class MecarrCarreraController implements Serializable {

    @EJB
    private ec.edu.monster.facades.MecarrCarreraFacade ejbFacade;
    private List<MecarrCarrera> items = null;
    private MecarrCarrera selected;

    public MecarrCarreraController() {
    }

    public MecarrCarrera getSelected() {
        return selected;
    }

    public void setSelected(MecarrCarrera selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MecarrCarreraFacade getFacade() {
        return ejbFacade;
    }

    public MecarrCarrera prepareCreate() {
        selected = new MecarrCarrera();
        initializeEmbeddableKey();
        try {
            // Autogenerate next numeric id (max + 1) and assign to selected
            String nextId = getFacade().nextNumericId("mecarrId");
            selected.setMecarrId(nextId);
        } catch (Exception ex) {
            // ignore and leave id null — creation will fail validation if required
        }
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MecarrCarreraCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MecarrCarreraUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MecarrCarreraDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MecarrCarrera> getItems() {
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

    public MecarrCarrera getMecarrCarrera(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<MecarrCarrera> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MecarrCarrera> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MecarrCarrera.class)
    public static class MecarrCarreraControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MecarrCarreraController controller = (MecarrCarreraController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mecarrCarreraController");
            return controller.getMecarrCarrera(getKey(value));
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
            if (object instanceof MecarrCarrera) {
                MecarrCarrera o = (MecarrCarrera) object;
                return getStringKey(o.getMecarrId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MecarrCarrera.class.getName()});
                return null;
            }
        }

    }

}
