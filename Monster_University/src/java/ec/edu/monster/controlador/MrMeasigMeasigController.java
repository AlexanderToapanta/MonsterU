package ec.edu.monster.controlador;

import ec.edu.monster.modelo.MrMeasigMeasig;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.MrMeasigMeasigFacade;

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

@Named("mrMeasigMeasigController")
@SessionScoped
public class MrMeasigMeasigController implements Serializable {

    @EJB
    private ec.edu.monster.facades.MrMeasigMeasigFacade ejbFacade;
    private List<MrMeasigMeasig> items = null;
    private MrMeasigMeasig selected;

    public MrMeasigMeasigController() {
    }

    public MrMeasigMeasig getSelected() {
        return selected;
    }

    public void setSelected(MrMeasigMeasig selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getMrMeasigMeasigPK().setMeasigId(selected.getMeasigAsigna().getMeasigId());
    }

    protected void initializeEmbeddableKey() {
        selected.setMrMeasigMeasigPK(new ec.edu.monster.modelo.MrMeasigMeasigPK());
    }

    private MrMeasigMeasigFacade getFacade() {
        return ejbFacade;
    }

    public MrMeasigMeasig prepareCreate() {
        selected = new MrMeasigMeasig();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MrMeasigMeasigCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MrMeasigMeasigUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MrMeasigMeasigDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MrMeasigMeasig> getItems() {
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

    public MrMeasigMeasig getMrMeasigMeasig(ec.edu.monster.modelo.MrMeasigMeasigPK id) {
        return getFacade().find(id);
    }

    public List<MrMeasigMeasig> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MrMeasigMeasig> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MrMeasigMeasig.class)
    public static class MrMeasigMeasigControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MrMeasigMeasigController controller = (MrMeasigMeasigController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mrMeasigMeasigController");
            return controller.getMrMeasigMeasig(getKey(value));
        }

        ec.edu.monster.modelo.MrMeasigMeasigPK getKey(String value) {
            ec.edu.monster.modelo.MrMeasigMeasigPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new ec.edu.monster.modelo.MrMeasigMeasigPK();
            key.setMeasigId(values[0]);
            key.setMeaMeasigId(values[1]);
            return key;
        }

        String getStringKey(ec.edu.monster.modelo.MrMeasigMeasigPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getMeasigId());
            sb.append(SEPARATOR);
            sb.append(value.getMeaMeasigId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MrMeasigMeasig) {
                MrMeasigMeasig o = (MrMeasigMeasig) object;
                return getStringKey(o.getMrMeasigMeasigPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MrMeasigMeasig.class.getName()});
                return null;
            }
        }

    }

}
