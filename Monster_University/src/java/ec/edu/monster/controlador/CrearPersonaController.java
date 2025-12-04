package ec.edu.monster.controlador;

import ec.edu.monster.modelo.PeperPerson;
import ec.edu.monster.modelo.PesexSexo;
import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.facades.PeperPersonFacade;
import ec.edu.monster.facades.PesexSexoFacade;
import ec.edu.monster.facades.XeusuUsuarFacade;
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
    
    @EJB
    private XeusuUsuarFacade usuarioFacade;

    private PeperPerson nuevaPersona;
    private String idGenerado;
    private String codigoSexoSeleccionado;
    private boolean crearUsuarioAutomatico = true; // Siempre true según tu requerimiento

    public CrearPersonaController() {
        nuevaPersona = new PeperPerson();
    }

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
        codigoSexoSeleccionado = null;
        generarNuevoId();
        nuevaPersona.setPepeperFechIngr(new Date());
    }
    
    private void generarNuevoId() {
        try {
            System.out.println("=== GENERANDO NUEVO ID PERSONA ===");
            
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
            System.out.println("=== INICIANDO PROCESO COMPLETO: PERSONA + USUARIO ===");
            
            // Validaciones iniciales
            if (!validarDatosPersona()) {
                System.out.println("❌ Validaciones de persona fallaron");
                return;
            }
            
            // Validar que se seleccionó un sexo
            if (codigoSexoSeleccionado == null || codigoSexoSeleccionado.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "Debe seleccionar un sexo"));
                return;
            }
            
            // Buscar el objeto PesexSexo
            System.out.println("🔍 Buscando sexo con código: " + codigoSexoSeleccionado);
            PesexSexo sexo = buscarSexoPorCodigo(codigoSexoSeleccionado);
            
            if (sexo == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "El sexo seleccionado no existe en la base de datos"));
                return;
            }
            
            // Asignar el sexo a la persona
            nuevaPersona.setPesexId(sexo);
            System.out.println("✅ Sexo asignado: " + sexo.getPesexDescri());

            // Verificar ID
            if (personaFacade.existeId(idGenerado)) {
                System.out.println("⚠️ El ID ya existe, generando uno nuevo...");
                generarNuevoId();
                System.out.println("Nuevo ID generado: " + idGenerado);
            }

            // Establecer campos FK opcionales como NULL temporalmente
            nuevaPersona.setPeescId(null);
            nuevaPersona.setXeusuId(null);
            
            // Asegurar que el ID esté asignado
            nuevaPersona.setPeperId(idGenerado);
            System.out.println("✅ ID de persona asignado: " + nuevaPersona.getPeperId());

            // PASO 1: Guardar persona
            System.out.println("💾 Guardando persona en base de datos...");
            personaFacade.create(nuevaPersona);
            
            // Verificar inserción de persona
            PeperPerson personaVerificada = personaFacade.find(idGenerado);
            if (personaVerificada == null) {
                System.out.println("❌ PERSONA NO SE GUARDÓ EN BD");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "Error al guardar persona"));
                return;
            }
            
            System.out.println("🎉 PERSONA CREADA EXITOSAMENTE");
            System.out.println("ID: " + personaVerificada.getPeperId());
            System.out.println("Nombre: " + personaVerificada.getPeperNombre());

            // PASO 2: Crear usuario automáticamente
            System.out.println("🔄 Creando usuario automático...");
            XeusuUsuar usuarioCreado = crearUsuarioParaPersona(personaVerificada);
            
            if (usuarioCreado != null) {
                // PASO 3: Actualizar la persona con el ID del usuario
                personaVerificada.setXeusuId(usuarioCreado);
                personaFacade.edit(personaVerificada);
                System.out.println("✅ Persona actualizada con XEUSU_ID: " + usuarioCreado.getXeusuId());
                
                // Mensaje de éxito completo
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    "Persona creada con ID: " + personaVerificada.getPeperId() + 
                    " y Usuario creado con ID: " + usuarioCreado.getXeusuId()));
            } else {
                // Mensaje de advertencia (persona creada pero usuario no)
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", 
                    "Persona creada pero no se pudo crear el usuario automático. ID: " + personaVerificada.getPeperId()));
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
    
    private XeusuUsuar crearUsuarioParaPersona(PeperPerson persona) {
        try {
            System.out.println("=== CREANDO USUARIO PARA PERSONA ===");
            System.out.println("Persona ID: " + persona.getPeperId());
            
            // Generar ID de usuario (US001, US002, etc.)
            String usuarioId = generarIdUsuario();
            System.out.println("ID de usuario generado: " + usuarioId);
            
            // Generar nombre de usuario: primera letra del nombre + apellido completo
            String nombreUsuario = generarNombreUsuario(persona);
            System.out.println("Nombre de usuario generado: " + nombreUsuario);
            
            // La contraseña será la cédula
            String contrasenia = persona.getPeperCedula();
            System.out.println("Contraseña (cédula): " + contrasenia);
            
            // Crear el objeto usuario
            XeusuUsuar nuevoUsuario = new XeusuUsuar();
            nuevoUsuario.setXeusuId(usuarioId);
            nuevoUsuario.setXeusuNombre(nombreUsuario);
            nuevoUsuario.setXeusuContra(contrasenia);
            nuevoUsuario.setXeusuEstado("ACTIVO");
            nuevoUsuario.setPeperId(persona); // Establecer la referencia a la persona
            
            // Encriptar contraseña
            PasswordController passwordController = new PasswordController();
            String contrasenaEncriptada = passwordController.encriptarClave(contrasenia);
            nuevoUsuario.setXeusuContra(contrasenaEncriptada);
            
            // Verificar si el ID de usuario ya existe
            if (usuarioFacade.find(usuarioId) != null) {
                System.out.println("⚠️ ID de usuario ya existe, generando nuevo...");
                usuarioId = generarIdUsuarioDisponible(usuarioId);
                nuevoUsuario.setXeusuId(usuarioId);
            }
            
            // Guardar usuario
            usuarioFacade.create(nuevoUsuario);
            System.out.println("✅ Usuario creado: " + usuarioId);
            
            return nuevoUsuario;
                
        } catch (Exception e) {
            System.out.println("❌ ERROR al crear usuario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private String generarIdUsuario() {
        try {
            List<XeusuUsuar> todosUsuarios = usuarioFacade.findAll();
            System.out.println("Total usuarios en BD: " + todosUsuarios.size());
            
            if (todosUsuarios.isEmpty()) {
                return "US001";
            }
            
            int maxNumero = 0;
            for (XeusuUsuar usuario : todosUsuarios) {
                String id = usuario.getXeusuId();
                if (id != null && id.startsWith("US") && id.length() == 5) {
                    try {
                        String numeroStr = id.substring(2);
                        int numero = Integer.parseInt(numeroStr);
                        if (numero > maxNumero) {
                            maxNumero = numero;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ ID de usuario con formato incorrecto: " + id);
                    }
                }
            }
            
            // Buscar siguiente disponible
            for (int i = 1; i <= 999; i++) {
                String idCandidato = String.format("US%03d", i);
                
                boolean existe = false;
                for (XeusuUsuar usuario : todosUsuarios) {
                    if (idCandidato.equals(usuario.getXeusuId())) {
                        existe = true;
                        break;
                    }
                }
                
                if (!existe) {
                    return idCandidato;
                }
            }
            
            return String.format("US%03d", maxNumero + 1);
            
        } catch (Exception e) {
            System.out.println("💥 ERROR generando ID de usuario: " + e.getMessage());
            return "US001";
        }
    }
    
    private String generarIdUsuarioDisponible(String idBase) {
        try {
            for (int i = 1; i <= 999; i++) {
                String idCandidato = String.format("US%03d", i);
                if (usuarioFacade.find(idCandidato) == null) {
                    return idCandidato;
                }
            }
            return idBase;
        } catch (Exception e) {
            return idBase;
        }
    }
    
    private String generarNombreUsuario(PeperPerson persona) {
        String nombre = persona.getPeperNombre().trim();
        String apellido = persona.getPeperApellido().trim();
        
        if (nombre.isEmpty() || apellido.isEmpty()) {
            return "usuario_" + persona.getPeperCedula();
        }
        
        // Primera letra del nombre en mayúscula + apellido completo
        String primeraLetra = nombre.substring(0, 1).toUpperCase();
        String nombreUsuario = primeraLetra + apellido;
        
        // Limitar a 100 caracteres si es necesario
        if (nombreUsuario.length() > 100) {
            nombreUsuario = nombreUsuario.substring(0, 100);
        }
        
        return nombreUsuario;
    }
    
    private boolean validarDatosPersona() {
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
        
        // Validar que la cédula tenga al menos 6 caracteres para la contraseña
        if (nuevaPersona.getPeperCedula().trim().length() < 6) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "La cédula debe tener al menos 6 caracteres para generar la contraseña del usuario"));
            return false;
        }
        
        return true;
    }
    
    private PesexSexo buscarSexoPorCodigo(String codigo) {
        try {
            // Buscar directamente por ID
            PesexSexo sexo = sexoFacade.find(codigo);
            if (sexo != null) {
                return sexo;
            }
            
            // Si no encuentra, buscar en todos
            List<PesexSexo> todosSexos = sexoFacade.findAll();
            for (PesexSexo s : todosSexos) {
                if (codigo.equals(s.getPesexId())) {
                    return s;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error al buscar sexo: " + e.getMessage());
        }
        return null;
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