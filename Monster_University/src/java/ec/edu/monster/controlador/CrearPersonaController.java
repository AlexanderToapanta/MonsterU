package ec.edu.monster.controlador;

import ec.edu.monster.modelo.PeperPerson;
import ec.edu.monster.modelo.PesexSexo;
import ec.edu.monster.facades.PeperPersonFacade;
import ec.edu.monster.facades.PesexSexoFacade;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

@Named(value = "crearPersonaController")
@SessionScoped
public class CrearPersonaController implements Serializable {

    @EJB
    private PeperPersonFacade personaFacade;
    
    @EJB
    private PesexSexoFacade sexoFacade;

    private PeperPerson nuevaPersona;
    private String idGenerado;
    private String codigoSexoSeleccionado; // Cambiar de String a String simple

    public CrearPersonaController() {
        nuevaPersona = new PeperPerson();
    }

    // Este getter es lo que usa el formulario
    public String getCodigoSexoSeleccionado() {
        return codigoSexoSeleccionado;
    }

    public void setCodigoSexoSeleccionado(String codigoSexoSeleccionado) {
        this.codigoSexoSeleccionado = codigoSexoSeleccionado;
    }

    public String getIdGenerado() {
        if (idGenerado == null) {
            generarNuevoId();
        }
        return idGenerado;
    }

    public void initNuevaPersona() {
        nuevaPersona = new PeperPerson();
        codigoSexoSeleccionado = null; // Limpiar selección
        generarNuevoId();
        nuevaPersona.setPepeperFechIngr(new Date());
        
        // Opcional: establecer sexo por defecto
        // codigoSexoSeleccionado = "M";
    }
    
    private void generarNuevoId() {
        try {
            System.out.println("=== GENERANDO NUEVO ID ===");
            
            List<PeperPerson> todasPersonas = personaFacade.findAll();
            System.out.println("Total personas en BD: " + todasPersonas.size());
            
            if (todasPersonas.isEmpty()) {
                idGenerado = "PE001";
                nuevaPersona.setPeperId(idGenerado);
                System.out.println("✅ No hay personas, ID inicial: " + idGenerado);
                return;
            }
            
            int maxNumero = 0;
            for (PeperPerson persona : todasPersonas) {
                String id = persona.getPeperId();
                System.out.println("ID encontrado: " + id);
                
                if (id != null && id.startsWith("PE") && id.length() == 5) {
                    try {
                        String numeroStr = id.substring(2);
                        int numero = Integer.parseInt(numeroStr);
                        if (numero > maxNumero) {
                            maxNumero = numero;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ ID con formato incorrecto: " + id);
                    }
                }
            }
            
            System.out.println("Máximo número encontrado: " + maxNumero);
            
            // Buscar siguiente disponible
            for (int i = 1; i <= 999; i++) {
                String idCandidato = String.format("PE%03d", i);
                
                boolean existe = false;
                for (PeperPerson persona : todasPersonas) {
                    if (idCandidato.equals(persona.getPeperId())) {
                        existe = true;
                        break;
                    }
                }
                
                if (!existe) {
                    idGenerado = idCandidato;
                    nuevaPersona.setPeperId(idGenerado);
                    System.out.println("✅ ID asignado: " + idGenerado);
                    return;
                }
            }
            
            idGenerado = String.format("PE%03d", maxNumero + 1);
            nuevaPersona.setPeperId(idGenerado);
            System.out.println("✅ ID asignado (del máximo): " + idGenerado);
            
        } catch (Exception e) {
            System.out.println("💥 ERROR: " + e.getMessage());
            e.printStackTrace();
            idGenerado = "PE001";
            nuevaPersona.setPeperId(idGenerado);
        }
    }

    public void crearPersona() {
        try {
            System.out.println("=== INICIANDO CREACIÓN DE PERSONA ===");
            System.out.println("ID actual: " + idGenerado);
            System.out.println("Código sexo seleccionado: " + codigoSexoSeleccionado);
            
            // Validar que se seleccionó un sexo
            if (codigoSexoSeleccionado == null || codigoSexoSeleccionado.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "Debe seleccionar un sexo"));
                return;
            }
            
            // Buscar el objeto PesexSexo correspondiente
            System.out.println("🔍 Buscando sexo con código: " + codigoSexoSeleccionado);
            PesexSexo sexo = buscarSexoPorCodigo(codigoSexoSeleccionado);
            
            if (sexo == null) {
                System.out.println("❌ No se encontró el sexo con código: " + codigoSexoSeleccionado);
                
                // Mostrar sexos disponibles para debug
                List<PesexSexo> todosSexos = sexoFacade.findAll();
                System.out.println("Sexos disponibles en BD:");
                for (PesexSexo s : todosSexos) {
                    System.out.println("  - Código: '" + s.getPesexId() + 
                                     "', Descripción: '" + s.getPesexDescri() + "'");
                }
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "El sexo seleccionado no existe en la base de datos. Contacte al administrador."));
                return;
            }
            
            // Asignar el sexo a la persona
            nuevaPersona.setPesexId(sexo);
            System.out.println("✅ Sexo asignado: " + sexo.getPesexDescri() + 
                             " (Código: " + sexo.getPesexId() + ")");

            // Verificar ID
            if (personaFacade.existeId(idGenerado)) {
                System.out.println("⚠️ El ID ya existe, generando uno nuevo...");
                generarNuevoId();
                System.out.println("Nuevo ID generado: " + idGenerado);
            }

            // Validaciones
            if (!validarDatos()) {
                System.out.println("❌ Validaciones fallaron");
                return;
            }
            System.out.println("✅ Validaciones pasadas");

            // Establecer campos FK opcionales como NULL
            nuevaPersona.setPeescId(null);
            nuevaPersona.setXeusuId(null);
            System.out.println("✅ Campos FK opcionales establecidos como NULL");

            // Asegurar que el ID esté asignado
            nuevaPersona.setPeperId(idGenerado);
            System.out.println("✅ ID asignado a la entidad: " + nuevaPersona.getPeperId());

            // Guardar persona
            System.out.println("💾 Guardando en base de datos...");
            personaFacade.create(nuevaPersona);
            System.out.println("✅ personaFacade.create() ejecutado");

            // Verificar inserción
            PeperPerson personaVerificada = personaFacade.find(idGenerado);
            if (personaVerificada != null) {
                System.out.println("🎉 PERSONA CREADA EXITOSAMENTE");
                System.out.println("ID: " + personaVerificada.getPeperId());
                System.out.println("Nombre: " + personaVerificada.getPeperNombre());
                System.out.println("Sexo: " + (personaVerificada.getPesexId() != null ? 
                    personaVerificada.getPesexId().getPesexDescri() : "null"));
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    "Persona creada correctamente con ID: " + personaVerificada.getPeperId()));
            } else {
                System.out.println("❌ PERSONA NO SE GUARDÓ EN BD");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "Error al guardar persona"));
                return;
            }

            // Limpiar formulario para nueva entrada
            initNuevaPersona();
            System.out.println("🔄 Formulario reiniciado");

        } catch (Exception e) {
            System.out.println("💥 ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "Error al crear persona: " + e.getMessage()));
        }
    }
    
    private PesexSexo buscarSexoPorCodigo(String codigo) {
        try {
            // Intentar buscar directamente
            PesexSexo sexo = sexoFacade.find(codigo);
            if (sexo != null) {
                return sexo;
            }
            
            // Si no se encuentra, buscar en todos los sexos
            List<PesexSexo> todosSexos = sexoFacade.findAll();
            for (PesexSexo s : todosSexos) {
                // Intentar diferentes formas de comparar
                if (codigo.equals(s.getPesexId())) {
                    return s;
                }
                // También comparar con la primera letra de la descripción
                if (s.getPesexDescri() != null && !s.getPesexDescri().isEmpty()) {
                    String primeraLetra = s.getPesexDescri().substring(0, 1).toUpperCase();
                    if (codigo.equals(primeraLetra)) {
                        return s;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error al buscar sexo: " + e.getMessage());
        }
        return null;
    }

    private boolean validarDatos() {
        if (nuevaPersona.getPeperNombre() == null || 
            nuevaPersona.getPeperNombre().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "El nombre es requerido"));
            return false;
        }
        
        if (nuevaPersona.getPeperApellido() == null || 
            nuevaPersona.getPeperApellido().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "El apellido es requerido"));
            return false;
        }
        
        if (nuevaPersona.getPeperCedula() == null || 
            nuevaPersona.getPeperCedula().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "La cédula es requerida"));
            return false;
        }
        
        if (nuevaPersona.getPeperEmail() == null || 
            nuevaPersona.getPeperEmail().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "El email es requerido"));
            return false;
        }
        
        if (nuevaPersona.getPepeperFechIngr() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "La fecha de ingreso es requerida"));
            return false;
        }
        
        return true;
    }

    // Getters y Setters
    public PeperPerson getNuevaPersona() {
        return nuevaPersona;
    }

    public void setNuevaPersona(PeperPerson nuevaPersona) {
        this.nuevaPersona = nuevaPersona;
    }
    
    public void setIdGenerado(String idGenerado) {
        this.idGenerado = idGenerado;
    }
}

// Opcional: Si necesitas un conversor para mostrar el sexo en otros lugares
@FacesConverter(forClass = PesexSexo.class)
class SexoConverter implements Converter {
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        PesexSexoFacade facade = context.getApplication()
            .evaluateExpressionGet(context, "#{sexoFacade}", PesexSexoFacade.class);
        return facade.find(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof PesexSexo) {
            return ((PesexSexo) value).getPesexId();
        }
        return "";
    }
}