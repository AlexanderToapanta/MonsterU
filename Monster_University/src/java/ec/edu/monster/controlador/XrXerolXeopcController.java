package ec.edu.monster.controlador;

import ec.edu.monster.modelo.XrXerolXeopc;
import ec.edu.monster.modelo.XrXerolXeopcPK;
import ec.edu.monster.modelo.XerolRol;
import ec.edu.monster.modelo.XeopcOpcion;
import ec.edu.monster.controlador.util.JsfUtil;
import ec.edu.monster.facades.XrXerolXeopcFacade;
import ec.edu.monster.facades.XerolRolFacade;
import ec.edu.monster.facades.XeopcOpcionFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.ArrayList;

@Named("xrXerolXeopcController")
@SessionScoped
public class XrXerolXeopcController implements Serializable {

    @EJB
    private XrXerolXeopcFacade ejbFacade;
    
    @EJB
    private XerolRolFacade rolFacade;
    
    @EJB
    private XeopcOpcionFacade opcionFacade;

    private List<XrXerolXeopc> items = null;
    private XrXerolXeopc selected;
    
    // Variables para asignación
    private String rolSeleccionadoId;
    private String opcionSeleccionadoId;
    private List<XrXerolXeopc> asignacionesFiltradas = null;
    private List<XeopcOpcion> selectedOpcionesNoAsignadas;
private List<XrXerolXeopc> selectedOpcionesAsignadas;

    public XrXerolXeopcController() {
    }

    public XrXerolXeopc getSelected() {
        return selected;
    }

    public void setSelected(XrXerolXeopc selected) {
        this.selected = selected;
    }

    private XrXerolXeopcFacade getFacade() {
        return ejbFacade;
    }

    public List<XrXerolXeopc> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
public List<XeopcOpcion> getSelectedOpcionesNoAsignadas() {
    return selectedOpcionesNoAsignadas;
}

public void setSelectedOpcionesNoAsignadas(List<XeopcOpcion> selectedOpcionesNoAsignadas) {
    this.selectedOpcionesNoAsignadas = selectedOpcionesNoAsignadas;
}

public List<XrXerolXeopc> getSelectedOpcionesAsignadas() {
    return selectedOpcionesAsignadas;
}

public void setSelectedOpcionesAsignadas(List<XrXerolXeopc> selectedOpcionesAsignadas) {
    this.selectedOpcionesAsignadas = selectedOpcionesAsignadas;
    
}

public void inicializarSelecciones() {
    if (selectedOpcionesNoAsignadas == null) {
        selectedOpcionesNoAsignadas = new ArrayList<>();
    }
    if (selectedOpcionesAsignadas == null) {
        selectedOpcionesAsignadas = new ArrayList<>();
    }
}

public boolean isRolSeleccionado() {
    return rolSeleccionadoId != null && !rolSeleccionadoId.trim().isEmpty();
}

/**
 * Obtiene el total de opciones no asignadas
 */
public int getTotalOpcionesNoAsignadas() {
    List<XeopcOpcion> opciones = getOpcionesNoAsignadas();
    return opciones != null ? opciones.size() : 0;
}

/**
 * Obtiene el total de opciones asignadas
 */
public int getTotalOpcionesAsignadas() {
    return asignacionesFiltradas != null ? asignacionesFiltradas.size() : 0;
}

/**
 * Limpia todas las selecciones
 */
public void limpiarSelecciones() {
    if (selectedOpcionesNoAsignadas != null) {
        selectedOpcionesNoAsignadas.clear();
    }
    if (selectedOpcionesAsignadas != null) {
        selectedOpcionesAsignadas.clear();
    }
    selected = null;
    JsfUtil.addSuccessMessage("Selecciones limpiadas");
}

/**
 * Verifica si hay opciones seleccionadas en la tabla de no asignadas
 */
public boolean isHayOpcionesNoAsignadasSeleccionadas() {
    return selectedOpcionesNoAsignadas != null && !selectedOpcionesNoAsignadas.isEmpty();
}

/**
 * Verifica si hay opciones seleccionadas en la tabla de asignadas
 */
public boolean isHayOpcionesAsignadasSeleccionadas() {
    return selectedOpcionesAsignadas != null && !selectedOpcionesAsignadas.isEmpty();
}
    // -----------------------
    // MÉTODOS PARA LA INTERFAZ
    // -----------------------

    /**
     * Carga las asignaciones del rol seleccionado
     */
    public void cargarAsignacionesPorRol() {
        if (rolSeleccionadoId == null || rolSeleccionadoId.trim().isEmpty()) {
            asignacionesFiltradas = null;
            return;
        }
        
        try {
            asignacionesFiltradas = ejbFacade.findOpcionesPorRol(rolSeleccionadoId);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al cargar asignaciones", e);
            asignacionesFiltradas = null;
        }
    }

    /**
     * Obtiene las opciones NO asignadas al rol seleccionado
     */
    public List<XeopcOpcion> getOpcionesNoAsignadas() {
        if (rolSeleccionadoId == null || rolSeleccionadoId.trim().isEmpty()) {
            // Si no hay rol seleccionado, mostrar todas las opciones
            return getListaOpciones();
        }
        
        List<XeopcOpcion> todasOpciones = getListaOpciones();
        List<XrXerolXeopc> asignadas = ejbFacade.findOpcionesPorRol(rolSeleccionadoId);
        
        // Filtrar las opciones que ya están asignadas
        if (asignadas != null) {
            for (XrXerolXeopc asignacion : asignadas) {
                // Usar getXeopcId() del método helper
                todasOpciones.removeIf(opcion -> 
                    opcion.getXeopcId().equals(asignacion.getXeopcId()));
            }
        }
        
        return todasOpciones;
    }

    /**
     * Asigna una opción a un rol
     */
    public void asignarOpcionARol() {
        try {
            if (rolSeleccionadoId == null || rolSeleccionadoId.trim().isEmpty()) {
                JsfUtil.addErrorMessage("Seleccione un rol");
                return;
            }
            
            if (opcionSeleccionadoId == null || opcionSeleccionadoId.trim().isEmpty()) {
                JsfUtil.addErrorMessage("Seleccione una opción");
                return;
            }
            
            // Verificar si ya existe la asignación
            if (ejbFacade.existeAsignacion(rolSeleccionadoId, opcionSeleccionadoId)) {
                JsfUtil.addErrorMessage("Esta opción ya está asignada a este rol");
                return;
            }
            
            // Crear nueva asignación con EmbeddedId
            XrXerolXeopcPK pk = new XrXerolXeopcPK(rolSeleccionadoId, opcionSeleccionadoId);
            XrXerolXeopc nuevaAsignacion = new XrXerolXeopc(pk, new Date());
            
            // Obtener objetos relacionados
            XerolRol rol = rolFacade.find(rolSeleccionadoId);
            XeopcOpcion opcion = opcionFacade.find(opcionSeleccionadoId);
            
            if (rol == null || opcion == null) {
                JsfUtil.addErrorMessage("Error: Rol u Opción no encontrados");
                return;
            }
            
            nuevaAsignacion.setXerolRol(rol);
            nuevaAsignacion.setXeopcOpcion(opcion);
            
            // Guardar la asignación
            getFacade().create(nuevaAsignacion);
            
            JsfUtil.addSuccessMessage("Opción asignada correctamente al rol");
            
            // Limpiar y refrescar
            items = null;
            asignacionesFiltradas = null;
            opcionSeleccionadoId = null;
            selected = null;
            
            // Recargar asignaciones
            cargarAsignacionesPorRol();
            
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al asignar opción", e);
            JsfUtil.addErrorMessage("Error al asignar opción: " + e.getMessage());
        }
    }
public void asignarOpcionesSeleccionadas() {
    if (rolSeleccionadoId == null || rolSeleccionadoId.trim().isEmpty()) {
        JsfUtil.addErrorMessage("Seleccione un rol primero");
        return;
    }
    
    if (selectedOpcionesNoAsignadas == null || selectedOpcionesNoAsignadas.isEmpty()) {
        JsfUtil.addErrorMessage("Seleccione al menos una opción para asignar");
        return;
    }
    
    int asignadas = 0;
    for (XeopcOpcion opcion : selectedOpcionesNoAsignadas) {
        try {
            if (!ejbFacade.existeAsignacion(rolSeleccionadoId, opcion.getXeopcId())) {
                XrXerolXeopcPK pk = new XrXerolXeopcPK(rolSeleccionadoId, opcion.getXeopcId());
                XrXerolXeopc nuevaAsignacion = new XrXerolXeopc(pk, new Date());
                
                XerolRol rol = rolFacade.find(rolSeleccionadoId);
                nuevaAsignacion.setXerolRol(rol);
                nuevaAsignacion.setXeopcOpcion(opcion);
                
                ejbFacade.create(nuevaAsignacion);
                asignadas++;
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al asignar opción", e);
        }
    }
    
    if (asignadas > 0) {
        JsfUtil.addSuccessMessage(asignadas + " opción(es) asignada(s) correctamente");
        // Limpiar y refrescar
        selectedOpcionesNoAsignadas = null;
        items = null;
        asignacionesFiltradas = null;
        cargarAsignacionesPorRol();
    }
}

/**
 * Retira múltiples opciones seleccionadas
 */
public void retirarOpcionesSeleccionadas() {
    if (rolSeleccionadoId == null || rolSeleccionadoId.trim().isEmpty()) {
        JsfUtil.addErrorMessage("Seleccione un rol primero");
        return;
    }
    
    if (selectedOpcionesAsignadas == null || selectedOpcionesAsignadas.isEmpty()) {
        JsfUtil.addErrorMessage("Seleccione al menos una opción para retirar");
        return;
    }
    
    int retiradas = 0;
    for (XrXerolXeopc asignacion : selectedOpcionesAsignadas) {
        try {
            asignacion.setXropFechaRetiro(new Date());
            ejbFacade.edit(asignacion);
            retiradas++;
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al retirar opción", e);
        }
    }
    
    if (retiradas > 0) {
        JsfUtil.addSuccessMessage(retiradas + " opción(es) retirada(s) correctamente");
        // Limpiar y refrescar
        selectedOpcionesAsignadas = null;
        items = null;
        asignacionesFiltradas = null;
        cargarAsignacionesPorRol();
    }
}

/**
 * Guarda cambios (método dummy para mantener compatibilidad)
 */
public void guardarCambios() {
    JsfUtil.addSuccessMessage("Los cambios han sido guardados automáticamente");
}
    /**
     * Retira una opción de un rol (marca fecha de retiro)
     */
    public void retirarOpcionDeRol() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Seleccione una asignación para retirar");
            return;
        }
        
        try {
            // Marcar fecha de retiro
            selected.setXropFechaRetiro(new Date());
            getFacade().edit(selected);
            
            JsfUtil.addSuccessMessage("Opción retirada del rol");
            
            // Limpiar y refrescar
            selected = null;
            items = null;
            asignacionesFiltradas = null;
            
            // Recargar asignaciones
            cargarAsignacionesPorRol();
            
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al retirar opción", e);
            JsfUtil.addErrorMessage("Error al retirar opción: " + e.getMessage());
        }
    }

    /**
     * Elimina permanentemente una asignación
     */
    public void eliminarAsignacion() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Seleccione una asignación para eliminar");
            return;
        }
        
        try {
            getFacade().remove(selected);
            
            JsfUtil.addSuccessMessage("Asignación eliminada permanentemente");
            
            // Limpiar y refrescar
            selected = null;
            items = null;
            asignacionesFiltradas = null;
            
            // Recargar asignaciones
            cargarAsignacionesPorRol();
            
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al eliminar asignación", e);
            JsfUtil.addErrorMessage("Error al eliminar asignación: " + e.getMessage());
        }
    }

    /**
     * Reinicia los filtros
     */
    public void reiniciarFiltros() {
        rolSeleccionadoId = null;
        opcionSeleccionadoId = null;
        asignacionesFiltradas = null;
        selected = null;
        items = null;
        JsfUtil.addSuccessMessage("Filtros reiniciados");
    }

    /**
     * Método para asignación rápida desde la interfaz
     */
    public void asignarOpcionRapida() {
        asignarOpcionARol();
    }

    // -----------------------
    // GETTERS Y SETTERS
    // -----------------------

    public String getRolSeleccionadoId() {
        return rolSeleccionadoId;
    }

    public void setRolSeleccionadoId(String rolSeleccionadoId) {
        this.rolSeleccionadoId = rolSeleccionadoId;
        // Cuando se cambia el rol, cargar sus asignaciones
        if (rolSeleccionadoId != null && !rolSeleccionadoId.trim().isEmpty()) {
            cargarAsignacionesPorRol();
        }
    }

    public String getOpcionSeleccionadoId() {
        return opcionSeleccionadoId;
    }

    public void setOpcionSeleccionadoId(String opcionSeleccionadoId) {
        this.opcionSeleccionadoId = opcionSeleccionadoId;
    }

    public List<XrXerolXeopc> getAsignacionesFiltradas() {
        if (asignacionesFiltradas == null && rolSeleccionadoId != null) {
            cargarAsignacionesPorRol();
        }
        return asignacionesFiltradas;
    }

    public void setAsignacionesFiltradas(List<XrXerolXeopc> asignacionesFiltradas) {
        this.asignacionesFiltradas = asignacionesFiltradas;
    }

    public List<XerolRol> getListaRoles() {
        return rolFacade.findAll();
    }

    public List<XeopcOpcion> getListaOpciones() {
        return opcionFacade.findAll();
    }

    /**
     * Obtiene las asignaciones activas (sin fecha de retiro)
     */
    public List<XrXerolXeopc> getAsignacionesActivas() {
        if (asignacionesFiltradas != null) {
            // Filtrar solo las que no tienen fecha de retiro
            return asignacionesFiltradas.stream()
                .filter(a -> a.getXropFechaRetiro() == null)
                .toList();
        }
        return asignacionesFiltradas;
    }

    // -----------------------
    // MÉTODOS DE UTILIDAD
    // -----------------------

    /**
     * Verifica si una opción está asignada al rol seleccionado
     */
    /**
 * Verifica si una opción está asignada al rol seleccionado
 */
public boolean estaOpcionAsignada(String opcionId) {
    if (asignacionesFiltradas == null || opcionId == null) {
        return false;
    }
    
    return asignacionesFiltradas.stream()
        .anyMatch(a -> opcionId.equals(a.getXeopcId()) && a.getXropFechaRetiro() == null);
}

    /**
     * Prepara una nueva asignación
     */
    public XrXerolXeopc prepareCreate() {
        selected = new XrXerolXeopc();
        return selected;
    }

    /**
     * Obtiene una asignación por ID compuesto
     */
    public XrXerolXeopc getXrXerolXeopc(String rolId, String opcionId) {
        return ejbFacade.encontrarAsignacion(rolId, opcionId);
    }

    // -----------------------
    // CONVERTER (actualizado)
    // -----------------------

    @FacesConverter(forClass = XrXerolXeopc.class)
    public static class XrXerolXeopcControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            
            XrXerolXeopcController controller = (XrXerolXeopcController) facesContext.getApplication().getELResolver()
                    .getValue(facesContext.getELContext(), null, "xrXerolXeopcController");
            
            if (controller == null) {
                return null;
            }
            
            String[] parts = value.split(SEPARATOR_ESCAPED);
            if (parts.length != 2) {
                return null;
            }
            
            return controller.ejbFacade.encontrarAsignacion(parts[0], parts[1]);
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            
            if (object instanceof XrXerolXeopc) {
                XrXerolXeopc o = (XrXerolXeopc) object;
                return getStringKey(o.getXerolId(), o.getXeopcId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, 
                    "object {0} is of type {1}; expected type: {2}", 
                    new Object[]{object, object.getClass().getName(), XrXerolXeopc.class.getName()});
                return null;
            }
        }

        private String getStringKey(String rolId, String opcionId) {
            if (rolId == null || opcionId == null) {
                return null;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(rolId);
            sb.append(SEPARATOR);
            sb.append(opcionId);
            return sb.toString();
        }
    }
}