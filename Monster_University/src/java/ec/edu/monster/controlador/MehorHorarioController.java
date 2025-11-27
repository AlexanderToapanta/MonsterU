package ec.edu.monster.controlador;

import ec.edu.monster.modelo.MehorHorario;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.MehorHorarioFacade;

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

@Named("mehorHorarioController")
@SessionScoped
public class MehorHorarioController implements Serializable {

    @EJB
    private ec.edu.monster.facades.MehorHorarioFacade ejbFacade;
    private List<MehorHorario> items = null;
    private MehorHorario selected;

    public MehorHorarioController() {
    }

    public MehorHorario getSelected() {
        return selected;
    }

    public void setSelected(MehorHorario selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getMehorHorarioPK().setMegrpId(selected.getMegrpGrupo().getMegrpId());
    }

    protected void initializeEmbeddableKey() {
        selected.setMehorHorarioPK(new ec.edu.monster.modelo.MehorHorarioPK());
    }

    private MehorHorarioFacade getFacade() {
        return ejbFacade;
    }

    public MehorHorario prepareCreate() {
        selected = new MehorHorario();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MehorHorarioCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MehorHorarioUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MehorHorarioDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MehorHorario> getItems() {
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

    public MehorHorario getMehorHorario(ec.edu.monster.modelo.MehorHorarioPK id) {
        return getFacade().find(id);
    }

    public List<MehorHorario> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MehorHorario> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MehorHorario.class)
    public static class MehorHorarioControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MehorHorarioController controller = (MehorHorarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mehorHorarioController");
            return controller.getMehorHorario(getKey(value));
        }

        ec.edu.monster.modelo.MehorHorarioPK getKey(String value) {
            ec.edu.monster.modelo.MehorHorarioPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new ec.edu.monster.modelo.MehorHorarioPK();
            key.setMegrpId(values[0]);
            key.setMehorId(values[1]);
            return key;
        }

        String getStringKey(ec.edu.monster.modelo.MehorHorarioPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getMegrpId());
            sb.append(SEPARATOR);
            sb.append(value.getMehorId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MehorHorario) {
                MehorHorario o = (MehorHorario) object;
                return getStringKey(o.getMehorHorarioPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MehorHorario.class.getName()});
                return null;
            }
        }

    }

}
