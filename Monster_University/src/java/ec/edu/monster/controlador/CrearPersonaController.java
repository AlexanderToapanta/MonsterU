package ec.edu.monster.controlador;

import ec.edu.monster.modelo.PeperPerson;
import ec.edu.monster.modelo.PesexSexo;
import ec.edu.monster.modelo.PeescEstciv;
import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.facades.PeperPersonFacade;
import ec.edu.monster.facades.PesexSexoFacade;
import ec.edu.monster.facades.PeescEstcivFacade;
import ec.edu.monster.facades.XeusuUsuarFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import org.primefaces.model.file.UploadedFile;
import java.util.Base64;

@Named(value = "crearPersonaController")
@SessionScoped
public class CrearPersonaController implements Serializable {

    @EJB
    private PeperPersonFacade personaFacade;
    
    @EJB
    private PesexSexoFacade sexoFacade;
    
    @EJB
    private PeescEstcivFacade estcivFacade;
    
    @EJB
    private XeusuUsuarFacade usuarioFacade;
    
    private static final Pattern PATRON_EMAIL = 
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_CELULAR = 
        Pattern.compile("^09[0-9]{8}$");
    
    private PeperPerson nuevaPersona;
    private String idGenerado;
    private String codigoSexoSeleccionado;
    private String codigoEstcivSeleccionado;
    private boolean cedulaValida = false;
    private boolean emailValido = false;
    private boolean celularValido = false;
    private UploadedFile imagenSubida;
    private String nombreArchivoImagen; 


    public CrearPersonaController() {
        nuevaPersona = new PeperPerson();
    }

    public UploadedFile getImagenSubida() {
        return imagenSubida;
    }

    public void setImagenSubida(UploadedFile imagenSubida) {
        this.imagenSubida = imagenSubida;
    }

    public String getCodigoSexoSeleccionado() {
        return codigoSexoSeleccionado;
    }

    public void setCodigoSexoSeleccionado(String codigoSexoSeleccionado) {
        this.codigoSexoSeleccionado = codigoSexoSeleccionado;
    }

    public String getCodigoEstcivSeleccionado() {
        return codigoEstcivSeleccionado;
    }

    public void setCodigoEstcivSeleccionado(String codigoEstcivSeleccionado) {
        this.codigoEstcivSeleccionado = codigoEstcivSeleccionado;
    }

    public String getIdGenerado() {
        if (idGenerado == null) {
            generarNuevoId();
        }
        return idGenerado;
    }

    public String getNombreArchivoImagen() {
        return nombreArchivoImagen;
    }

    public void setNombreArchivoImagen(String nombreArchivoImagen) {
        this.nombreArchivoImagen = nombreArchivoImagen;
    }

    public void initNuevaPersona() {
        nuevaPersona = new PeperPerson();
        codigoSexoSeleccionado = null;
        codigoEstcivSeleccionado = null;
        cedulaValida = false;
        emailValido = false;
        celularValido = false;
        imagenSubida = null;
        nombreArchivoImagen = null; // Limpiar nombre de archivo
        generarNuevoId();
        nuevaPersona.setPepeperFechIngr(new Date());
        nuevaPersona.setPepeperFecNa(null);
        nuevaPersona.setPepeperImag(null);
    }
    
/**
 * Método para asignar la imagen (convertir a Base64 y asignar a la persona)
 */
public void subirImagen() {
    System.out.println("=== DEBUG: SUBIR IMAGEN ===");
    
    if (imagenSubida == null) {
        System.out.println("❌ ERROR: No hay imagen seleccionada");
        return;
    }
    
    System.out.println("📤 Archivo seleccionado: " + imagenSubida.getFileName());
    System.out.println("📏 Tamaño: " + imagenSubida.getSize() + " bytes");
    
    try {
        // 1. Verificar cédula
        if (nuevaPersona.getPeperCedula() == null || nuevaPersona.getPeperCedula().trim().isEmpty()) {
            System.out.println("⚠️ ADVERTENCIA: No hay cédula, usando nombre temporal");
            // Puedes continuar con nombre temporal
        }
        
        // 2. Generar nombre único
        String cedula = "temp";
        if (nuevaPersona.getPeperCedula() != null && !nuevaPersona.getPeperCedula().trim().isEmpty()) {
            cedula = nuevaPersona.getPeperCedula().replaceAll("[^0-9]", "");
        } else {
            cedula = "temp_" + System.currentTimeMillis();
        }
        
        String nombreOriginal = imagenSubida.getFileName();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        nombreArchivoImagen = cedula + "_" + System.currentTimeMillis() + extension;
        
        // 3. Definir ruta EXACTA
        String rutaBase = "C:\\Users\\Usuario\\Documents\\MonsterUniversity\\Monster_University\\web\\img\\";
        String rutaCompleta = rutaBase + nombreArchivoImagen;
        
        System.out.println("📁 RUTA COMPLETA: " + rutaCompleta);
        
        // 4. Crear directorio si no existe
        File dir = new File(rutaBase);
        if (!dir.exists()) {
            System.out.println("Creando directorio...");
            boolean creado = dir.mkdirs();
            System.out.println("✅ Directorio creado: " + creado);
        }
        
        // 5. Guardar archivo
        System.out.println("💾 Guardando archivo...");
        try (InputStream in = imagenSubida.getInputStream();
             FileOutputStream out = new FileOutputStream(rutaCompleta)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytes = 0;
            
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            
            System.out.println("✅ Bytes escritos: " + totalBytes);
        }
        
        // 6. VERIFICAR que se guardó
        File archivoGuardado = new File(rutaCompleta);
        if (archivoGuardado.exists()) {
            System.out.println("🎉 ARCHIVO GUARDADO EXITOSAMENTE");
            System.out.println("   Nombre: " + archivoGuardado.getName());
            System.out.println("   Tamaño: " + archivoGuardado.length() + " bytes");
            System.out.println("   Ruta: " + archivoGuardado.getAbsolutePath());
        } else {
            System.out.println("❌ ERROR: El archivo NO se guardó");
        }
        
        // 7. Asignar a la persona
        nuevaPersona.setPepeperImag(nombreArchivoImagen);
        System.out.println("📝 Asignado a persona: " + nuevaPersona.getPepeperImag());
        
        // 8. Mensaje de éxito
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
            "Imagen '" + nombreOriginal + "' guardada como: " + nombreArchivoImagen));
        
        // 9. Limpiar
        imagenSubida = null;
        
    } catch (Exception e) {
        System.out.println("💥 ERROR CRÍTICO: " + e.getMessage());
        e.printStackTrace();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
            "Error: " + e.getMessage()));
    }
}
    /**
     * Método para eliminar la imagen
     */
    public void eliminarImagen() {
    try {
        // 1. Eliminar archivo físico si existe
        if (nuevaPersona.getPepeperImag() != null && !nuevaPersona.getPepeperImag().isEmpty()) {
            String rutaBase = "C:\\Users\\Usuario\\Documents\\MonsterUniversity\\Monster_University\\web\\img\\";
            String rutaCompleta = rutaBase + nuevaPersona.getPepeperImag();
            
            System.out.println("🗑️ Intentando eliminar: " + rutaCompleta);
            
            if (Files.deleteIfExists(Paths.get(rutaCompleta))) {
                System.out.println("✅ Imagen eliminada del servidor: " + nuevaPersona.getPepeperImag());
            } else {
                System.out.println("⚠️ La imagen no existía en el servidor: " + nuevaPersona.getPepeperImag());
            }
        }
        
        // 2. Limpiar referencia en la persona
        nuevaPersona.setPepeperImag(null);
        nombreArchivoImagen = null;
        imagenSubida = null;
        
        System.out.println("🔄 Referencias de imagen limpiadas");
        
        FacesContext.getCurrentInstance().addMessage("formCrearPersona:imagenPersona",
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", 
            "Imagen eliminada"));
            
    } catch (Exception e) {
        System.out.println("⚠️ Error al eliminar imagen: " + e.getMessage());
        e.printStackTrace();
        FacesContext.getCurrentInstance().addMessage("formCrearPersona:imagenPersona",
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
            "Error al eliminar la imagen: " + e.getMessage()));
    }
}
    
 public String getUrlImagen() {
    if (nuevaPersona.getPepeperImag() == null || nuevaPersona.getPepeperImag().isEmpty()) {
        System.out.println("⚠️ getUrlImagen: pepeperImag es null/vacío");
        return null;
    }
    
    String url = "img/" + nuevaPersona.getPepeperImag();
 
    String rutaFisica = "C:\\Users\\Usuario\\Documents\\MonsterUniversity\\Monster_University\\web" + url;
    File archivo = new File(rutaFisica);
    System.out.println("📁 Archivo existe físicamente: " + archivo.exists());
   
    return url;
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
            System.out.println("=== INICIANDO PROCESO CREAR PERSONA ===");
            
            // Verificar si hay imagen subida que no se ha procesado
           // Verificar si hay imagen seleccionada pero no subida
if (imagenSubida != null && imagenSubida.getSize() > 0 && 
                (nuevaPersona.getPepeperImag() == null || nuevaPersona.getPepeperImag().isEmpty())) {
                FacesContext.getCurrentInstance().addMessage("formCrearPersona:imagenPersona",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", 
                    "Tiene una imagen seleccionada. Por favor haga clic en 'Subir Imagen' para procesarla"));
                return;
            }
            
            if (!validarDatosCompletos()) {
                System.out.println("❌ Validaciones completas fallaron");
                return;
            }
            
            if (codigoSexoSeleccionado == null || codigoSexoSeleccionado.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "Debe seleccionar un sexo"));
                return;
            }
            
            System.out.println("🔍 Buscando sexo con código: " + codigoSexoSeleccionado);
            PesexSexo sexo = buscarSexoPorCodigo(codigoSexoSeleccionado);
            
            if (sexo == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "El sexo seleccionado no existe en la base de datos"));
                return;
            }
            
            nuevaPersona.setPesexId(sexo);
            System.out.println("✅ Sexo asignado: " + sexo.getPesexDescri());

            if (codigoEstcivSeleccionado != null && !codigoEstcivSeleccionado.trim().isEmpty()) {
                System.out.println("🔍 Buscando estado civil con código: " + codigoEstcivSeleccionado);
                PeescEstciv estciv = buscarEstcivPorCodigo(codigoEstcivSeleccionado);
                
                if (estciv != null) {
                    nuevaPersona.setPeescId(estciv);
                    System.out.println("✅ Estado civil asignado: " + estciv.getPeescDescri());
                } else {
                    System.out.println("⚠️ Estado civil no encontrado, dejando como null");
                    nuevaPersona.setPeescId(null);
                }
            } else {
                nuevaPersona.setPeescId(null);
                System.out.println("✅ Estado civil no seleccionado, dejando como null");
            }

            if (personaFacade.existeId(idGenerado)) {
                System.out.println("⚠️ El ID ya existe, generando uno nuevo...");
                generarNuevoId();
                System.out.println("Nuevo ID generado: " + idGenerado);
            }

            nuevaPersona.setXeusuId(null);
            nuevaPersona.setPeperId(idGenerado);
            System.out.println("✅ ID de persona asignado: " + nuevaPersona.getPeperId());
            
            // Verificar imagen en Base64
              if (nuevaPersona.getPepeperImag() != null) {
                System.out.println("📷 Nombre de imagen guardado: " + nuevaPersona.getPepeperImag());
            } else {
                System.out.println("📷 Sin imagen adjunta");
            }

            System.out.println("💾 Guardando persona en base de datos...");
            personaFacade.create(nuevaPersona);
            
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

            System.out.println("🔄 Creando usuario automático...");
            XeusuUsuar usuarioCreado = crearUsuarioParaPersona(personaVerificada);
            
            if (usuarioCreado != null) {
                personaVerificada.setXeusuId(usuarioCreado);
                personaFacade.edit(personaVerificada);
                System.out.println("✅ Persona actualizada con XEUSU_ID: " + usuarioCreado.getXeusuId());
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    "Persona creada con ID: " + personaVerificada.getPeperId() + 
                    " y Usuario creado con ID: " + usuarioCreado.getXeusuId()));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", 
                    "Persona creada pero no se pudo crear el usuario automático. ID: " + personaVerificada.getPeperId()));
            }

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
            
            String usuarioId = generarIdUsuario();
            System.out.println("ID de usuario generado: " + usuarioId);
            
            String nombreUsuario = generarNombreUsuario(persona);
            System.out.println("Nombre de usuario generado: " + nombreUsuario);
            
            String contrasenia = persona.getPeperCedula();
            System.out.println("Contraseña (cédula): " + contrasenia);
            
            XeusuUsuar nuevoUsuario = new XeusuUsuar();
            nuevoUsuario.setXeusuId(usuarioId);
            nuevoUsuario.setXeusuNombre(nombreUsuario);
            nuevoUsuario.setXeusuContra(contrasenia);
            nuevoUsuario.setXeusuEstado("ACTIVO");
            nuevoUsuario.setPeperId(persona);
            
            PasswordController passwordController = new PasswordController();
            String contrasenaEncriptada = passwordController.encriptarClave(contrasenia);
            nuevoUsuario.setXeusuContra(contrasenaEncriptada);
            
            if (usuarioFacade.find(usuarioId) != null) {
                System.out.println("⚠️ ID de usuario ya existe, generando nuevo...");
                usuarioId = generarIdUsuarioDisponible(usuarioId);
                nuevoUsuario.setXeusuId(usuarioId);
            }
            
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
        
        String primeraLetra = nombre.substring(0, 1).toUpperCase();
        String nombreUsuario = primeraLetra + apellido;
        
        if (nombreUsuario.length() > 100) {
            nombreUsuario = nombreUsuario.substring(0, 100);
        }
        
        return nombreUsuario;
    }
    
  
    
    public void validarCedula() {
        String cedula = nuevaPersona.getPeperCedula();
        if (cedula == null || cedula.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCedula",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "La cédula es requerida"));
            cedulaValida = false;
            return;
        }
        
        cedula = cedula.trim();
        if (cedula.length() != 10) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCedula",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "La cédula debe tener 10 dígitos"));
            cedulaValida = false;
            return;
        }
        
        if (!cedula.matches("[0-9]+")) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCedula",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "La cédula debe contener solo números"));
            cedulaValida = false;
            return;
        }
        
        if (!validarCedulaEcuatoriana(cedula)) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCedula",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "Cédula inválida - Verifique los dígitos"));
            cedulaValida = false;
            return;
        }
        
        if (personaFacade.existeCedula(cedula)) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCedula",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "Esta cédula ya está registrada en el sistema"));
            cedulaValida = false;
            return;
        }
        
        cedulaValida = true;
        FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCedula",
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
            "Cédula válida"));
    }
    
    public void validarEmail() {
        String email = nuevaPersona.getPeperEmail();
        if (email == null || email.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperEmail",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "El email es requerido"));
            emailValido = false;
            return;
        }
        
        email = email.trim().toLowerCase();
        
        if (!PATRON_EMAIL.matcher(email).matches()) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperEmail",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "Formato de email inválido"));
            emailValido = false;
            return;
        }
        
        if (email.length() > 30) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperEmail",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "El email no puede exceder 30 caracteres"));
            emailValido = false;
            return;
        }
        
        if (personaFacade.existeEmail(email)) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperEmail",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "Este email ya está registrado en el sistema"));
            emailValido = false;
            return;
        }
        
        emailValido = true;
        FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperEmail",
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
            "Email válido"));
    }
    
    public void validarCelular() {
        String celular = nuevaPersona.getPeperCelular();
        
        if (celular == null || celular.trim().isEmpty()) {
            celularValido = true;
            return;
        }
        
        celular = celular.trim();
        
        if (!PATRON_CELULAR.matcher(celular).matches()) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCelular",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "Celular inválido. Use formato: 09XXXXXXXX"));
            celularValido = false;
            return;
        }
        
        if (personaFacade.existeCelular(celular)) {
            FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCelular",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "Este número de celular ya está registrado"));
            celularValido = false;
            return;
        }
        
        celularValido = true;
        FacesContext.getCurrentInstance().addMessage("formCrearPersona:peperCelular",
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
            "Celular válido"));
    }
    
    public void validarFechaNacimiento() {
        Date fechaNac = nuevaPersona.getPepeperFecNa();
        if (fechaNac != null) {
            Date hoy = new Date();
            if (fechaNac.after(hoy)) {
                FacesContext.getCurrentInstance().addMessage("formCrearPersona:pepeperFecNa",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "La fecha de nacimiento no puede ser futura"));
            }
        }
    }
    
    private boolean validarCedulaEcuatoriana(String cedula) {
        try {
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) {
                return false;
            }
            
            int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
            if (tercerDigito < 0 || tercerDigito > 6) {
                return false;
            }
            
            int total = 0;
            int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
            int verificador = Integer.parseInt(cedula.substring(9, 10));
            
            for (int i = 0; i < 9; i++) {
                int valor = Integer.parseInt(cedula.substring(i, i + 1)) * coeficientes[i];
                if (valor > 9) {
                    valor -= 9;
                }
                total += valor;
            }
            
            int residuo = total % 10;
            int digitoVerificador = (residuo == 0) ? 0 : 10 - residuo;
            
            return digitoVerificador == verificador;
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean validarDatosCompletos() {
        validarCedula();
        validarEmail();
        validarCelular();
        validarFechaNacimiento();
        
        if (!cedulaValida) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "La cédula no es válida o ya está registrada"));
            return false;
        }
        
        if (!emailValido) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "El email no es válido o ya está registrado"));
            return false;
        }
        
        if (!celularValido && nuevaPersona.getPeperCelular() != null 
            && !nuevaPersona.getPeperCelular().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                "El celular no es válido o ya está registrado"));
            return false;
        }
        
        return validarDatosPersona();
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
            PesexSexo sexo = sexoFacade.find(codigo);
            if (sexo != null) {
                return sexo;
            }
            
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
    
    private PeescEstciv buscarEstcivPorCodigo(String codigo) {
        try {
            PeescEstciv estciv = estcivFacade.find(codigo);
            if (estciv != null) {
                return estciv;
            }
            
            List<PeescEstciv> todosEstciv = estcivFacade.findAll();
            for (PeescEstciv e : todosEstciv) {
                if (codigo.equals(e.getPeescId())) {
                    return e;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error al buscar estado civil: " + e.getMessage());
        }
        return null;
    }


    public List<PeescEstciv> getEstadosCiviles() {
        return estcivFacade.findAll();
    }
    
    public List<PesexSexo> getSexos() {
        return sexoFacade.findAll();
    }

    public PeperPerson getNuevaPersona() {
        return nuevaPersona;
    }

    public void setNuevaPersona(PeperPerson nuevaPersona) {
        this.nuevaPersona = nuevaPersona;
    }
    
    public void setIdGenerado(String idGenerado) {
        this.idGenerado = idGenerado;
    }
    
    public boolean isCedulaValida() {
        return cedulaValida;
    }
    
    public void setCedulaValida(boolean cedulaValida) {
        this.cedulaValida = cedulaValida;
    }
    
    public boolean isEmailValido() {
        return emailValido;
    }
    
    public void setEmailValido(boolean emailValido) {
        this.emailValido = emailValido;
    }
    
    public boolean isCelularValido() {
        return celularValido;
    }
    
    public void setCelularValido(boolean celularValido) {
        this.celularValido = celularValido;
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

@FacesConverter(forClass = PeescEstciv.class)
class EstcivConverter implements Converter {
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        PeescEstcivFacade facade = context.getApplication()
            .evaluateExpressionGet(context, "#{estcivFacade}", PeescEstcivFacade.class);
        return facade.find(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof PeescEstciv) {
            return ((PeescEstciv) value).getPeescId();
        }
        return "";
    }
}