package ec.edu.monster.controlador;

import ec.edu.monster.modelo.PeperPerson;
import ec.edu.monster.modelo.PesexSexo;
import ec.edu.monster.facades.PeperPersonFacade;
import ec.edu.monster.facades.PesexSexoFacade;
import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named(value = "crearPersonaController")
@SessionScoped
public class CrearPersonaController implements Serializable {

    @EJB
    private PeperPersonFacade personaFacade;
    
    @EJB
    private PesexSexoFacade sexoFacade;  // Necesitas el facade para buscar sexo
    
    private PeperPerson nuevaPersona;

    public CrearPersonaController() {
        initNuevaPersona();
    }

    public void initNuevaPersona() {
        nuevaPersona = new PeperPerson();
        // Establecer fecha actual por defecto
        nuevaPersona.setPepeperFechIngr(new Date());
    }

    public void crearPersona() {
        try {
            // Debug en consola del servidor
            System.out.println("=== INICIANDO CREACIÓN DE PERSONA ===");
            System.out.println("Datos recibidos:");
            System.out.println("ID: " + nuevaPersona.getPeperId());
            System.out.println("Nombre: " + nuevaPersona.getPeperNombre());

            // Validaciones
            if (!validarDatos()) {
                System.out.println("❌ Validaciones fallaron");
                return;
            }
            System.out.println("✅ Validaciones pasadas");

            // Verificar si el ID ya existe
            if (personaFacade.find(nuevaPersona.getPeperId()) != null) {
                System.out.println("❌ ID de persona ya existe: " + nuevaPersona.getPeperId());
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de persona ya existe"));
                return;
            }
            System.out.println("✅ ID de persona disponible");

            // **ASIGNAR SEXO POR DEFECTO (OBLIGATORIO)**
            // Buscar un sexo por defecto en la BD, por ejemplo ID "M" para Masculino
            System.out.println("🔍 Buscando sexo por defecto...");
            PesexSexo sexoPorDefecto = sexoFacade.find("M"); // Cambia "M" por el ID que uses
            if (sexoPorDefecto == null) {
                // Si no existe, buscar el primer sexo disponible
                System.out.println("⚠️ Sexo 'M' no encontrado, buscando primer sexo disponible...");
                if (!sexoFacade.findAll().isEmpty()) {
                    sexoPorDefecto = sexoFacade.findAll().get(0);
                }
            }
            
            if (sexoPorDefecto != null) {
                nuevaPersona.setPesexId(sexoPorDefecto);
                System.out.println("✅ Sexo asignado: " + sexoPorDefecto.getPesexDescri());
            } else {
                System.out.println("❌ No se encontró ningún sexo en la BD");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "No se encontró sexo en la base de datos. Debe crear al menos un sexo primero."));
                return;
            }

            // Establecer campos FK opcionales como NULL
            nuevaPersona.setPeescId(null);      // Estado Civil - NULL (opcional)
            nuevaPersona.setXeusuId(null);      // Usuario - NULL (opcional)
            System.out.println("✅ Campos FK opcionales establecidos como NULL");

            // Guardar persona
            System.out.println("💾 Guardando en base de datos...");
            personaFacade.create(nuevaPersona);
            System.out.println("✅ personaFacade.create() ejecutado");

            // Verificar inserción
            PeperPerson personaVerificada = personaFacade.find(nuevaPersona.getPeperId());
            if (personaVerificada != null) {
                System.out.println("🎉 PERSONA CREADA EXITOSAMENTE");
                System.out.println("ID: " + personaVerificada.getPeperId());
                System.out.println("Nombre: " + personaVerificada.getPeperNombre());
                System.out.println("Sexo: " + personaVerificada.getPesexId().getPesexDescri());
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    "Persona creada correctamente con sexo: " + personaVerificada.getPesexId().getPesexDescri()));
            } else {
                System.out.println("❌ PERSONA NO SE GUARDÓ EN BD");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar persona"));
            }

            // Limpiar formulario para nueva entrada
            initNuevaPersona();

        } catch (Exception e) {
            System.out.println("💥 ERROR GENERAL: " + e.getMessage());
            System.out.println("Tipo de error: " + e.getClass().getName());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear persona: " + e.getMessage()));
        }
    }

    private boolean validarDatos() {
        // (Mantén las mismas validaciones del código anterior)
        // Validación de ID
        if (nuevaPersona.getPeperId() == null || nuevaPersona.getPeperId().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de persona es requerido"));
            return false;
        }
        if (nuevaPersona.getPeperId().length() > 5) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de persona no puede tener más de 5 caracteres"));
            return false;
        }

        // Validación de Nombre
        if (nuevaPersona.getPeperNombre() == null || nuevaPersona.getPeperNombre().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre es requerido"));
            return false;
        }
        if (nuevaPersona.getPeperNombre().length() > 25) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede tener más de 25 caracteres"));
            return false;
        }

        // Validación de Apellido
        if (nuevaPersona.getPeperApellido() == null || nuevaPersona.getPeperApellido().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El apellido es requerido"));
            return false;
        }
        if (nuevaPersona.getPeperApellido().length() > 25) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El apellido no puede tener más de 25 caracteres"));
            return false;
        }

        // Validación de Email
        if (nuevaPersona.getPeperEmail() == null || nuevaPersona.getPeperEmail().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El email es requerido"));
            return false;
        }
        if (nuevaPersona.getPeperEmail().length() > 30) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El email no puede tener más de 30 caracteres"));
            return false;
        }

        // Validación de Cédula
        if (nuevaPersona.getPeperCedula() == null || nuevaPersona.getPeperCedula().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cédula es requerida"));
            return false;
        }
        if (nuevaPersona.getPeperCedula().length() > 15) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cédula no puede tener más de 15 caracteres"));
            return false;
        }

        // Validación de Tipo
        if (nuevaPersona.getPeperTipo() == null || nuevaPersona.getPeperTipo().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El tipo de persona es requerido"));
            return false;
        }

        // Validación de Fecha
        if (nuevaPersona.getPepeperFechIngr() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha de ingreso es requerida"));
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
}