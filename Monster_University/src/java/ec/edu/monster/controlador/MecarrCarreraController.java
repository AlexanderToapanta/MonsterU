package ec.edu.monster.controlador;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import ec.edu.monster.modelo.MecarrCarrera;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.controlador.util.JsfUtil.PersistAction;
import ec.edu.monster.facades.MecarrCarreraFacade;

import java.io.Serializable;
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
import javax.faces.context.ExternalContext;
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

    // Campos para filtros y reportes
    private String filtroNombre;
    private Integer filtroMin;
    private Integer filtroMax;
    private List<MecarrCarrera> reportes;

    @PostConstruct
    public void init() {
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

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }

    public Integer getFiltroMin() {
        return filtroMin;
    }

    public void setFiltroMin(Integer filtroMin) {
        this.filtroMin = filtroMin;
    }

    public Integer getFiltroMax() {
        return filtroMax;
    }

    public void setFiltroMax(Integer filtroMax) {
        this.filtroMax = filtroMax;
    }

    public MecarrCarrera getSelected() {
        return selected;
    }

    public void setSelected(MecarrCarrera selected) {
        this.selected = selected;
    }

    public List<MecarrCarrera> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<MecarrCarrera> selectedItems) {
        this.selectedItems = selectedItems;
    }

    private MecarrCarreraFacade getFacade() {
        return ejbFacade;
    }

    public List<MecarrCarrera> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    // MÉTODOS DE BÚSQUEDA Y FILTRADO
    public void buscar() {
        reportes = getFacade().findAll();

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

    // MÉTODOS CRUD
    public MecarrCarrera prepareCreate() {
        selected = new MecarrCarrera();
        initializeEmbeddableKey();
        try {
            // Autogenerate next numeric id (max + 1) - FUNCIONALIDAD DEL CONTROLADOR VIEJO
            String nextId = getFacade().nextNumericId("mecarrId");
            selected.setMecarrId(nextId);
        } catch (Exception ex) {
            // ignore and leave id null — creation will fail validation if required
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error generando ID automático", ex);
        }
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MecarrCarreraCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            reportes = getFacade().findAll();   // Actualiza tabla de reportes tras crear
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MecarrCarreraUpdated"));
        reportes = getFacade().findAll(); // Actualiza reportes
    }

    public void prepareView() {
        if (selectedItems != null && !selectedItems.isEmpty()) {
            selected = selectedItems.get(0);
        }
    }

    public void prepareEdit() {
        if (selectedItems != null && !selectedItems.isEmpty()) {
            selected = selectedItems.get(0);
        }
    }

    public void destroy() {
        if (selectedItems != null && !selectedItems.isEmpty()) {
            for (MecarrCarrera item : selectedItems) {
                selected = item;
                persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MecarrCarreraDeleted"));
            }
            if (!JsfUtil.isValidationFailed()) {
                selected = null;
                selectedItems = null;
                items = null;
                reportes = getFacade().findAll(); // Actualiza reportes
            }
        }
    }

    // MÉTODO PARA GENERAR PDF
    public void generarPdf() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"reporte_carreras.pdf\"");
            externalContext.addResponseCookie("fileDownload", "true", new java.util.HashMap<>());

            Document document = new Document();
            PdfWriter.getInstance(document, externalContext.getResponseOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Reporte de Carreras\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 4f, 3f, 3f});

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            table.addCell(new PdfPCell(new Phrase("ID", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Nombre", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Créditos Mínimos", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Créditos Máximos", headerFont)));

            for (MecarrCarrera c : reportes) {
                table.addCell(c.getMecarrId());
                table.addCell(c.getMecarrNombre());
                table.addCell(String.valueOf(c.getMecarrMinCred()));
                table.addCell(String.valueOf(c.getMecarrMaxCred()));
            }

            document.add(table);
            document.close();

            facesContext.responseComplete();

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error generando PDF", e);
            JsfUtil.addErrorMessage("Error al generar el reporte PDF");
        }
    }

    // MÉTODOS DE PERSISTENCIA (DEL CONTROLADOR VIEJO)
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
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
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", 
                    new Object[]{object, object.getClass().getName(), MecarrCarrera.class.getName()});
                return null;
            }
        }
    }
}