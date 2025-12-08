package ec.edu.monster.controlador;

import ec.edu.monster.modelo.MecarrCarrera;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.MecarrCarreraFacade;

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

@Named("mecarrCarreraController")
@SessionScoped
public class MecarrCarreraController implements Serializable {

    @EJB
    private MecarrCarreraFacade ejbFacade;

    private List<MecarrCarrera> items = null;
    private List<MecarrCarrera> selectedItems;

    private MecarrCarrera selected;

    // FILTROS
    private String filtroNombre;
    private Integer filtroMin;
    private Integer filtroMax;

    // LISTA PRINCIPAL DEL REPORTE
    private List<MecarrCarrera> reportes;

    @PostConstruct
    public void init() {
        // 🔥 CARGA AUTOMÁTICA DE DATOS AL ENTRAR A LA PÁGINA
        reportes = ejbFacade.findAll();
        items = ejbFacade.findAll();
    }

    public MecarrCarreraController() {
    }

    // GETTERS Y SETTERS
    public List<MecarrCarrera> getReportes() {
        return reportes;
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public Integer getFiltroMin() {
        return filtroMin;
    }

    public Integer getFiltroMax() {
        return filtroMax;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }

    public void setFiltroMin(Integer filtroMin) {
        this.filtroMin = filtroMin;
    }

    public void setFiltroMax(Integer filtroMax) {
        this.filtroMax = filtroMax;
    }

    public MecarrCarrera getSelected() {
        return selected;
    }

    public List<MecarrCarrera> getSelectedItems() {
        return selectedItems;
    }

    public void setSelected(MecarrCarrera selected) {
        this.selected = selected;
    }

    public void setSelectedItems(List<MecarrCarrera> selectedItems) {
        this.selectedItems = selectedItems;
    }

    private MecarrCarreraFacade getFacade() {
        return ejbFacade;
    }

    public List<MecarrCarrera> getItems() {
        if (items == null) {
            items = ejbFacade.findAll();
        }
        return items;
    }

    // ------------------------------
    //          BUSCAR
    // ------------------------------
    public void buscar() {
        reportes = ejbFacade.findAll(); // Reiniciar lista

        if (filtroNombre != null && !filtroNombre.isEmpty()) {
            reportes.removeIf(c -> !c.getMecarrNombre().toLowerCase().contains(filtroNombre.toLowerCase()));
        }

        if (filtroMin != null) {
            reportes.removeIf(c -> c.getMecarrMinCred() < filtroMin);
        }

        if (filtroMax != null) {
            reportes.removeIf(c -> c.getMecarrMaxCred() > filtroMax);
        }
    }

    // CRUD BASICO
    public MecarrCarrera prepareCreate() {
        selected = new MecarrCarrera();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, "Carrera creada correctamente");
        if (!JsfUtil.isValidationFailed()) {
            items = null;
            reportes = ejbFacade.findAll();   // 🔥 Actualiza tabla tras crear
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, "Carrera actualizada");
        reportes = ejbFacade.findAll();
    }

    public void destroy() {
        if (selectedItems != null) {
            for (MecarrCarrera c : selectedItems) {
                selected = c;
                persist(PersistAction.DELETE, "Carrera eliminada");
            }
            items = null;
            reportes = ejbFacade.findAll();
        }
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    ejbFacade.edit(selected);
                } else {
                    ejbFacade.remove(selected);
                }

                JsfUtil.addSuccessMessage(successMessage);

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("Error en persistencia");
            }
        }
    }

    public MecarrCarrera getMecarrCarrera(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return ejbFacade.find(id); // aquí sí funciona porque tu PK es String
    }

    @FacesConverter(forClass = MecarrCarrera.class)
    public static class MecarrCarreraControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            MecarrCarreraController controller = (MecarrCarreraController) context.getApplication().getELResolver()
                    .getValue(context.getELContext(), null, "mecarrCarreraController");

            return controller.getMecarrCarrera(value);
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object object) {
            if (object == null) {
                return "";
            }
            return ((MecarrCarrera) object).getMecarrId();
        }
    }

}
