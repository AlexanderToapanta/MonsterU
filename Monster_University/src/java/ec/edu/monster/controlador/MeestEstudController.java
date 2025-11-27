package ec.edu.monster.controlador;

import ec.edu.monster.modelo.MeestEstud;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.MeestEstudFacade;

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

@Named("meestEstudController")
@SessionScoped
public class MeestEstudController implements Serializable {

    @EJB
    private ec.edu.monster.facades.MeestEstudFacade ejbFacade;
    private List<MeestEstud> items = null;
    private MeestEstud selected;

    public MeestEstudController() {
    }

    public MeestEstud getSelected() {
        return selected;
    }

    public void setSelected(MeestEstud selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getMeestEstudPK().setMecarrId(selected.getMecarrCarrera().getMecarrId());
    }

    protected void initializeEmbeddableKey() {
        selected.setMeestEstudPK(new ec.edu.monster.modelo.MeestEstudPK());
    }

    private MeestEstudFacade getFacade() {
        return ejbFacade;
    }

    public MeestEstud prepareCreate() {
        selected = new MeestEstud();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MeestEstudCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MeestEstudUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MeestEstudDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MeestEstud> getItems() {
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

    public MeestEstud getMeestEstud(ec.edu.monster.modelo.MeestEstudPK id) {
        return getFacade().find(id);
    }

    public List<MeestEstud> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MeestEstud> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MeestEstud.class)
    public static class MeestEstudControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MeestEstudController controller = (MeestEstudController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "meestEstudController");
            return controller.getMeestEstud(getKey(value));
        }

        ec.edu.monster.modelo.MeestEstudPK getKey(String value) {
            ec.edu.monster.modelo.MeestEstudPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new ec.edu.monster.modelo.MeestEstudPK();
            key.setMecarrId(values[0]);
            key.setMeestId(values[1]);
            return key;
        }

        String getStringKey(ec.edu.monster.modelo.MeestEstudPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getMecarrId());
            sb.append(SEPARATOR);
            sb.append(value.getMeestId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MeestEstud) {
                MeestEstud o = (MeestEstud) object;
                return getStringKey(o.getMeestEstudPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MeestEstud.class.getName()});
                return null;
            }
        }

    }

}
