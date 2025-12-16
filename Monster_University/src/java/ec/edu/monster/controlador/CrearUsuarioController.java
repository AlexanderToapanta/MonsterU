package ec.edu.monster.controlador;

import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.modelo.PeperPerson;
import ec.edu.monster.facades.XeusuUsuarFacade;
import ec.edu.monster.facades.PeperPersonFacade;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named(value = "crearUsuarioController")
@SessionScoped
public class CrearUsuarioController implements Serializable {

    @EJB
    private XeusuUsuarFacade usuarioFacade;
    
    @EJB
    private PeperPersonFacade personaFacade;
    
    private XeusuUsuar nuevoUsuario;
    private String confirmarContrasena;
    private PasswordController passwordController;
    private String personaId; // Para buscar la persona
    private PeperPerson personaSeleccionada; // Persona seleccionada

    public CrearUsuarioController() {
        nuevoUsuario = new XeusuUsuar();
        passwordController = new PasswordController();
    }

    public void initNuevoUsuario() {
        nuevoUsuario = new XeusuUsuar();
        confirmarContrasena = "";
        personaId = "";
        personaSeleccionada = null;
        nuevoUsuario.setXeusuEstado("ACTIVO");
        // Generar automáticamente el ID al inicializar
        generarIdUsuarioAutomatico();
    }
    
    /**
     * Genera un ID único basado en el máximo entre personas y usuarios
     * Esto asegura que no haya duplicados entre ambas entidades
     */
    public List<XeusuUsuar> getTodosUsuarios() {
    try {
        // Si tu AbstractFacade tiene el método findAll()
        return usuarioFacade.findAll();
    } catch (Exception e) {
        System.out.println("❌ Error obteniendo usuarios: " + e.getMessage());
        return new ArrayList<>();
    }
    
}
    public void generarIdUsuarioAutomatico() {
        try {
            // Obtener todos los IDs de personas
            List<PeperPerson> todasPersonas = personaFacade.findAll();
            Set<String> idsExistentes = new HashSet<>();
            
            // Agregar IDs de personas al conjunto
            for (PeperPerson persona : todasPersonas) {
                if (persona.getPeperId() != null) {
                    idsExistentes.add(persona.getPeperId());
                }
            }
            
            // Obtener todos los IDs de usuarios
            List<XeusuUsuar> todosUsuarios = usuarioFacade.findAll();
            
            // Agregar IDs de usuarios al conjunto
            for (XeusuUsuar usuario : todosUsuarios) {
                if (usuario.getXeusuId() != null) {
                    idsExistentes.add(usuario.getXeusuId());
                }
            }
            
            System.out.println("Total de IDs únicos en el sistema: " + idsExistentes.size());
            
            // Buscar el próximo ID disponible
            String nuevoId = null;
            
            // Buscar desde PE001/US001 hasta PE999/US999
            for (int i = 1; i <= 999; i++) {
                String idPersona = String.format("PE%03d", i);
                String idUsuario = String.format("US%03d", i);
                
                // Verificar si alguno de los dos formatos está disponible
                if (!idsExistentes.contains(idPersona) && !idsExistentes.contains(idUsuario)) {
                    // Para usuarios, usamos formato USXXX
                    nuevoId = idUsuario;
                    break;
                }
            }
            
            if (nuevoId == null) {
                // Si no encontramos huecos, buscar el máximo número
                int maxNumero = 0;
                for (String id : idsExistentes) {
                    if (id != null && (id.startsWith("PE") || id.startsWith("US")) && id.length() == 5) {
                        try {
                            String numeroStr = id.substring(2);
                            int numero = Integer.parseInt(numeroStr);
                            if (numero > maxNumero) {
                                maxNumero = numero;
                            }
                        } catch (NumberFormatException e) {
                            // Ignorar IDs con formato incorrecto
                        }
                    }
                }
                nuevoId = String.format("US%03d", maxNumero + 1);
            }
            
            nuevoUsuario.setXeusuId(nuevoId);
            System.out.println("✅ ID global generado: " + nuevoId);
            
        } catch (Exception e) {
            System.out.println("💥 ERROR generando ID global: " + e.getMessage());
            nuevoUsuario.setXeusuId("US001");
        }
    }
    
    /**
     * Método para generar ID de persona (si necesitas consistencia en ambos controladores)
     */
    public String generarIdPersonaAutomatico() {
        try {
            // Obtener todos los IDs de personas
            List<PeperPerson> todasPersonas = personaFacade.findAll();
            Set<String> idsExistentes = new HashSet<>();
            
            // Agregar IDs de personas al conjunto
            for (PeperPerson persona : todasPersonas) {
                if (persona.getPeperId() != null) {
                    idsExistentes.add(persona.getPeperId());
                }
            }
            
            // Obtener todos los IDs de usuarios
            List<XeusuUsuar> todosUsuarios = usuarioFacade.findAll();
            
            // Agregar IDs de usuarios al conjunto
            for (XeusuUsuar usuario : todosUsuarios) {
                if (usuario.getXeusuId() != null) {
                    idsExistentes.add(usuario.getXeusuId());
                }
            }
            
            // Buscar el próximo ID disponible para persona
            String nuevoId = null;
            
            // Buscar desde PE001/US001 hasta PE999/US999
            for (int i = 1; i <= 999; i++) {
                String idPersona = String.format("PE%03d", i);
                String idUsuario = String.format("US%03d", i);
                
                // Verificar si alguno de los dos formatos está disponible
                if (!idsExistentes.contains(idPersona) && !idsExistentes.contains(idUsuario)) {
                    // Para personas, usamos formato PEXXX
                    nuevoId = idPersona;
                    break;
                }
            }
            
            if (nuevoId == null) {
                // Si no encontramos huecos, buscar el máximo número
                int maxNumero = 0;
                for (String id : idsExistentes) {
                    if (id != null && (id.startsWith("PE") || id.startsWith("US")) && id.length() == 5) {
                        try {
                            String numeroStr = id.substring(2);
                            int numero = Integer.parseInt(numeroStr);
                            if (numero > maxNumero) {
                                maxNumero = numero;
                            }
                        } catch (NumberFormatException e) {
                            // Ignorar IDs con formato incorrecto
                        }
                    }
                }
                nuevoId = String.format("PE%03d", maxNumero + 1);
            }
            
            System.out.println("✅ ID de persona generado: " + nuevoId);
            return nuevoId;
            
        } catch (Exception e) {
            System.out.println("💥 ERROR generando ID de persona: " + e.getMessage());
            return "PE001";
        }
    }
    
    public void buscarPersona() {
        try {
            System.out.println("🔍 Buscando persona con ID: " + personaId);
            if (personaId != null && !personaId.trim().isEmpty()) {
                personaSeleccionada = personaFacade.find(personaId.trim());
                if (personaSeleccionada != null) {
                    System.out.println("✅ Persona encontrada: " + 
                        personaSeleccionada.getPeperNombre() + " " + 
                        personaSeleccionada.getPeperApellido());
                    
                    // Verificar si esta persona ya tiene un usuario asignado
                    if (personaSeleccionada.getXeusuId() != null) {
                        System.out.println("⚠️ Persona ya tiene usuario asignado: " + 
                            personaSeleccionada.getXeusuId().getXeusuId());
                        FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", 
                            "Esta persona ya tiene un usuario asignado: " + 
                            personaSeleccionada.getXeusuId().getXeusuId()));
                        personaSeleccionada = null;
                        return;
                    }
                    
                    // Generar automáticamente datos del usuario basados en la persona
                    generarDatosUsuarioDesdePersona();
                } else {
                    System.out.println("❌ Persona no encontrada");
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", 
                        "No se encontró una persona con el ID: " + personaId));
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error buscando persona: " + e.getMessage());
        }
    }
    
    private void generarDatosUsuarioDesdePersona() {
        if (personaSeleccionada == null) return;
        
        try {
            // Generar nombre de usuario: primera letra del nombre + apellido completo
            String nombreUsuario = generarNombreUsuario(personaSeleccionada);
            nuevoUsuario.setXeusuNombre(nombreUsuario);
            
            // La contraseña será la cédula
            String contrasenia = personaSeleccionada.getPeperCedula();
            nuevoUsuario.setXeusuContra(contrasenia);
            confirmarContrasena = contrasenia;
            
            // Asignar la persona al usuario
            nuevoUsuario.setPeperId(personaSeleccionada);
            
            System.out.println("✅ Datos de usuario generados automáticamente");
            System.out.println("   ID: " + nuevoUsuario.getXeusuId());
            System.out.println("   Nombre: " + nombreUsuario);
            System.out.println("   Persona asociada: " + personaSeleccionada.getPeperId());
            
        } catch (Exception e) {
            System.out.println("❌ Error generando datos de usuario: " + e.getMessage());
        }
    }
    
    private String generarNombreUsuario(PeperPerson persona) {
        String nombre = persona.getPeperNombre().trim();
        String apellido = persona.getPeperApellido().trim();
        
        if (nombre.isEmpty() || apellido.isEmpty()) {
            return "usuario";
        }
        
        // Primera letra del nombre en mayúscula + apellido completo
        String primeraLetra = nombre.substring(0, 1).toUpperCase();
        return primeraLetra + apellido;
    }

    public void crearUsuario() {
        try {
            System.out.println("=== INICIANDO CREACIÓN DE USUARIO ===");
            System.out.println("Datos recibidos:");
            System.out.println("ID: " + nuevoUsuario.getXeusuId());
            System.out.println("Nombre: " + nuevoUsuario.getXeusuNombre());
            System.out.println("Estado: " + nuevoUsuario.getXeusuEstado());
            System.out.println("Persona asociada: " + 
                (nuevoUsuario.getPeperId() != null ? nuevoUsuario.getPeperId().getPeperId() : "Ninguna"));

            // Validaciones
            if (!validarDatos()) {
                System.out.println("❌ Validaciones fallaron");
                return;
            }
            System.out.println("✅ Validaciones pasadas");

            // Verificar si el ID ya existe en usuarios O en personas
            boolean idExiste = false;
            
            // Verificar en usuarios
            if (usuarioFacade.find(nuevoUsuario.getXeusuId()) != null) {
                idExiste = true;
                System.out.println("❌ ID ya existe en tabla de usuarios");
            }
            
            // Verificar en personas (por si alguien usó formato USXXX como ID de persona)
            if (personaFacade.find(nuevoUsuario.getXeusuId()) != null) {
                idExiste = true;
                System.out.println("❌ ID ya existe en tabla de personas");
            }
            
            if (idExiste) {
                System.out.println("❌ ID de usuario ya existe: " + nuevoUsuario.getXeusuId());
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "El ID ya existe en el sistema. Se generará uno nuevo automáticamente"));
                
                // Generar un nuevo ID automáticamente
                generarIdUsuarioAutomatico();
                return;
            }
            System.out.println("✅ ID de usuario disponible globalmente");

            // Encriptar contraseña
            System.out.println("🔐 Encriptando contraseña...");
            String contrasenaEncriptada = passwordController.encriptarClave(nuevoUsuario.getXeusuContra());
            nuevoUsuario.setXeusuContra(contrasenaEncriptada);
            System.out.println("✅ Contraseña encriptada. Longitud: " + contrasenaEncriptada.length());

            // Si hay una persona asociada, actualizar su campo XEUSU_ID
            if (nuevoUsuario.getPeperId() != null) {
                PeperPerson persona = nuevoUsuario.getPeperId();
                persona.setXeusuId(nuevoUsuario); // Establecer la referencia bidireccional
                personaFacade.edit(persona);
                System.out.println("✅ Persona actualizada con XEUSU_ID: " + nuevoUsuario.getXeusuId());
            }

            // Establecer campos NULL explícitamente si no hay persona
            if (nuevoUsuario.getPeperId() == null) {
                nuevoUsuario.setPeperId(null);
            }
            nuevoUsuario.setMeestEstud(null);

            // Guardar usuario
            System.out.println("💾 Guardando en base de datos...");
            usuarioFacade.create(nuevoUsuario);
            System.out.println("✅ usuarioFacade.create() ejecutado");

            // Verificar inserción
            XeusuUsuar usuarioVerificado = usuarioFacade.find(nuevoUsuario.getXeusuId());
            if (usuarioVerificado != null) {
                System.out.println("🎉 USUARIO CREADO EXITOSAMENTE");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    "Usuario " + nuevoUsuario.getXeusuId() + " creado correctamente"));
            } else {
                System.out.println("❌ USUARIO NO SE GUARDÓ EN BD");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar usuario"));
            }

            // Limpiar formulario y preparar para nuevo registro
            initNuevoUsuario();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("❌ Error de encriptación: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al encriptar contraseña: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println("💥 ERROR GENERAL: " + e.getMessage());
            System.out.println("Tipo de error: " + e.getClass().getName());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear usuario: " + e.getMessage()));
        }
    }

    private boolean validarDatos() {
        if (nuevoUsuario.getXeusuId() == null || nuevoUsuario.getXeusuId().trim().isEmpty()) {
            // Si no hay ID, generarlo automáticamente
            generarIdUsuarioAutomatico();
        }

        if (nuevoUsuario.getXeusuId().length() > 5) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "El ID de usuario no puede tener más de 5 caracteres. Formato: USXXX"));
            return false;
        }

        if (nuevoUsuario.getXeusuNombre() == null || nuevoUsuario.getXeusuNombre().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre es requerido"));
            return false;
        }

        if (nuevoUsuario.getXeusuNombre().length() > 100) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede tener más de 100 caracteres"));
            return false;
        }

        if (nuevoUsuario.getXeusuContra() == null || nuevoUsuario.getXeusuContra().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña es requerida"));
            return false;
        }

        if (nuevoUsuario.getXeusuContra().length() < 6) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe tener al menos 6 caracteres"));
            return false;
        }

        if (confirmarContrasena == null || !confirmarContrasena.equals(nuevoUsuario.getXeusuContra())) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden"));
            return false;
        }

        if (nuevoUsuario.getXeusuEstado() == null || nuevoUsuario.getXeusuEstado().trim().isEmpty()) {
            nuevoUsuario.setXeusuEstado("ACTIVO");
        }

        return true;
    }

    public void generarContrasenaAleatoria() {
        try {
            String contrasenaAleatoria = passwordController.generarContraseñaAleatoria();
            nuevoUsuario.setXeusuContra(contrasenaAleatoria);
            confirmarContrasena = contrasenaAleatoria;
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Contraseña generada", 
                "Se ha generado una contraseña aleatoria"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "Error al generar contraseña: " + e.getMessage()));
        }
    }
    public List<XeusuUsuar> getUsuariosSinRol() {
    try {
        return usuarioFacade.findByCriteria("x.xerolId IS NULL");
    } catch (Exception e) {
        System.out.println("Error obteniendo usuarios sin rol: " + e.getMessage());
        return new ArrayList<>();
    }
}
    
    /**
     * Método para regenerar el ID si el usuario lo necesita
     */
    public void regenerarIdUsuario() {
        generarIdUsuarioAutomatico();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "ID regenerado", 
            "Se ha generado un nuevo ID automáticamente"));
    }

    // Getters y Setters
    public XeusuUsuar getNuevoUsuario() {
        return nuevoUsuario;
    }

    public void setNuevoUsuario(XeusuUsuar nuevoUsuario) {
        this.nuevoUsuario = nuevoUsuario;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }
    
    public String getPersonaId() {
        return personaId;
    }

    public void setPersonaId(String personaId) {
        this.personaId = personaId;
    }

    public PeperPerson getPersonaSeleccionada() {
        return personaSeleccionada;
    }

    public void setPersonaSeleccionada(PeperPerson personaSeleccionada) {
        this.personaSeleccionada = personaSeleccionada;
    }
}