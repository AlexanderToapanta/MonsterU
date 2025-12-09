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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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

        // ============================
        //   TÍTULO DEL REPORTE
        // ============================
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("Reporte de Carreras\n\n", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // ============================
        //       TABLA PDF
        // ============================
        PdfPTable table = new PdfPTable(4); 
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 4f, 3f, 3f});

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        table.addCell(new PdfPCell(new Phrase("ID", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Nombre", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Créditos Mínimos", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Créditos Máximos", headerFont)));

        // ============================
        //   AGREGAR FILAS DEL REPORTE
        // ============================
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
        e.printStackTrace();
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
