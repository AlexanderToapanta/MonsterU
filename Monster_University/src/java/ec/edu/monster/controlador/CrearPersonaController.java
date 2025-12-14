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
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
 // Cambia estas constantes con la información de SendGrid
private final String CORREO_REMITENTE = "alexandertoapantaj05@gmail.com"; // ¡Debe ser el correo que verificaste en SendGrid!
// ¡VERIFICA CADA CARÁCTER!
private final String CONTRASENIA_REMITENTE = "";
private final String SERVIDOR_SMTP = "smtp.sendgrid.net";
private final int PUERTO_SMTP = 587; // Puerto 587 para usar TLS/STARTTLS


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
        enviarCorreoCredenciales(
        personaVerificada.getPeperEmail(),      // Correo de la persona
        usuarioCreado.getXeusuNombre(),         // Nombre de usuario generado
        nuevaPersona.getPeperCedula()           // Contraseña (cédula)
    );
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
/**
 * Método para enviar correo con credenciales usando Elastic Email SMTP
 */
private void enviarCorreoCredenciales(String destinatario, String nombreUsuario, String contrasenia) {
    try {
        // CAMBIO 1: Cambiar el mensaje inicial
        System.out.println("=== ENVIANDO CORREO CON SENDGRID (SMTP) ===");
        System.out.println("📧 Destinatario: " + destinatario);
        System.out.println("👤 Usuario: " + nombreUsuario);
        System.out.println("🔐 Contraseña: " + contrasenia);
        
        // Validar que el destinatario no sea null
        if (destinatario == null || destinatario.trim().isEmpty()) {
            System.out.println("❌ ERROR: El destinatario está vacío");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", 
                "No se pudo enviar correo: El email del destinatario está vacío"));
            return;
        }
        
        // Configuración SMTP para SendGrid
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Usa TLS (obligatorio para puerto 587)
        props.put("mail.smtp.host", SERVIDOR_SMTP);  // Debe ser "smtp.sendgrid.net"
        props.put("mail.smtp.port", String.valueOf(PUERTO_SMTP)); // Debe ser 587
        props.put("mail.smtp.ssl.trust", SERVIDOR_SMTP);
        
        // Configuración adicional para mejor rendimiento
        props.put("mail.smtp.connectiontimeout", "5000"); // 5 segundos
        props.put("mail.smtp.timeout", "5000"); // 5 segundos
        props.put("mail.smtp.writetimeout", "5000"); // 5 segundos
        
        // Crear sesión con autenticación
        // La autenticación YA ESTÁ CORRECTA (usuario: "apikey", contraseña: CONTRASENIA_REMITENTE)
        Session session = Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    System.out.println("🔑 Autenticando con SendGrid...");
                    System.out.println("     Usuario: apikey"); 
                    System.out.println("     API Key: " + CONTRASENIA_REMITENTE.substring(0, 4) + "...");
                    
                    return new PasswordAuthentication("apikey", CONTRASENIA_REMITENTE);
                }
            });
        
        // Habilitar depuración para ver errores detallados
        session.setDebug(true);
        
        // Crear mensaje de correo
        Message message = new MimeMessage(session);
        
        // Configurar remitente
        message.setFrom(new InternetAddress(CORREO_REMITENTE, "Monsters University"));
        
        // Configurar destinatario
        message.setRecipients(Message.RecipientType.TO, 
            InternetAddress.parse(destinatario.trim()));
        
        // Asunto del correo
        message.setSubject("Credenciales de Acceso - Monsters University");
        message.setSentDate(new Date());
        
        // Contenido del correo en HTML
        String contenidoHTML = construirContenidoHTML(nombreUsuario, contrasenia);
        
        // Contenido en texto plano (para clientes que no soportan HTML)
        String textoPlano = construirContenidoTextoPlano(nombreUsuario, contrasenia);
        
        // Configurar contenido multipart (HTML + texto plano)
        MimeMultipart multipart = new MimeMultipart("alternative");
        
        // 1. Parte de texto plano
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(textoPlano, "utf-8");
        multipart.addBodyPart(textPart);
        
        // 2. Parte HTML
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(contenidoHTML, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);
        
        // Asignar el contenido al mensaje
        message.setContent(multipart);
        
        // Enviar el correo
        System.out.println("🚀 Enviando correo...");
        Transport.send(message);
        
        System.out.println("✅ CORREO ENVIADO EXITOSAMENTE");
        System.out.println("    📨 De: " + CORREO_REMITENTE);
        System.out.println("    📬 Para: " + destinatario);
        System.out.println("    🕐 Fecha: " + new Date());
        
        // Mostrar mensaje de éxito en la interfaz
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
            "Credenciales enviadas por correo a: " + destinatario));
            
    } catch (MessagingException e) {
        System.out.println("❌ ERROR SMTP: " + e.getMessage());
        e.printStackTrace();
        
        String mensajeError = "Error al enviar correo: ";
        if (e.getMessage().contains("535")) {
            // Mantenemos este mensaje de error ya que es el que estás experimentando
            mensajeError += "Credenciales SMTP incorrectas (Error 535). Verifica API Key o que el usuario sea 'apikey'.";
        } else if (e.getMessage().contains("550") || e.getMessage().contains("554")) {
            mensajeError += "Correo rechazado por el servidor. Verifica el destinatario y que el remitente esté verificado en SendGrid.";
        } else if (e.getMessage().contains("Connection timed out")) {
            mensajeError += "Timeout de conexión. Verifica tu conexión a internet o el bloqueo del puerto 587.";
        } else {
            mensajeError += e.getMessage();
        }
        
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error correo", 
            mensajeError));
            
    } catch (Exception e) {
        System.out.println("❌ ERROR inesperado: " + e.getMessage());
        e.printStackTrace();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
            "Error inesperado al enviar correo: " + e.getMessage()));
    }
}
/**
 * Construye el contenido HTML del correo
 */
private String construirContenidoHTML(String nombreUsuario, String contrasenia) {
    return "<!DOCTYPE html>" +
           "<html lang='es'>" +
           "<head>" +
           "<meta charset='UTF-8'>" +
           "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
           "<title>Credenciales Monsters University</title>" +
           "<style>" +
           "body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }" +
           ".container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 0 20px rgba(0,0,0,0.1); }" +
           ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px 20px; text-align: center; }" +
           ".header h1 { margin: 0; font-size: 28px; }" +
           ".content { padding: 30px; }" +
           ".credentials-box { background: #f8f9fa; border-left: 4px solid #667eea; padding: 20px; margin: 25px 0; border-radius: 0 5px 5px 0; }" +
           ".credential-item { margin: 15px 0; font-size: 16px; }" +
           ".label { font-weight: bold; color: #555; display: inline-block; width: 120px; }" +
           ".value { color: #222; font-family: 'Courier New', monospace; background: #e9ecef; padding: 5px 10px; border-radius: 3px; }" +
           ".instructions { background: #e7f3ff; border: 1px solid #b6d4fe; border-radius: 5px; padding: 20px; margin: 25px 0; }" +
           ".instructions h3 { color: #084298; margin-top: 0; }" +
           ".btn-container { text-align: center; margin: 30px 0; }" +
           ".btn-access { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 14px 32px; text-decoration: none; border-radius: 30px; font-weight: bold; display: inline-block; transition: transform 0.3s; }" +
           ".btn-access:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4); }" +
           ".warning { background: #fff3cd; border: 1px solid #ffc107; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0; text-align: center; font-weight: bold; }" +
           ".footer { background: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 14px; border-top: 1px solid #dee2e6; }" +
           ".logo { max-width: 200px; margin-bottom: 20px; }" +
           "</style>" +
           "</head>" +
           "<body>" +
           "<div class='container'>" +
           "<div class='header'>" +
           "<h1>Monsters University</h1>" +
           "<p>Sistema de Gestión de Matrículas</p>" +
           "</div>" +
           "<div class='content'>" +
           "<h2>¡Bienvenido/a al Sistema!</h2>" +
           "<p>Se han creado tus credenciales para acceder al sistema de gestión:</p>" +
           "<div class='credentials-box'>" +
           "<div class='credential-item'><span class='label'>👤 Usuario:</span> <span class='value'>" + nombreUsuario + "</span></div>" +
           "<div class='credential-item'><span class='label'>🔑 Contraseña:</span> <span class='value'>" + contrasenia + "</span></div>" +
           "</div>" +
           "<div class='instructions'>" +
           "<h3>📋 Instrucciones de Acceso:</h3>" +
           "<ol>" +
           "<li>Accede al sistema a través del siguiente enlace</li>" +
           "<li>Ingresa las credenciales proporcionadas</li>" +
           "<li>Cambia tu contraseña en tu primer inicio de sesión</li>" +
           "</ol>" +
           "</div>" +
           "<div class='btn-container'>" +
           "<a href='http://localhost:8080/Monster_University' class='btn-access'>🔗 Acceder al Sistema</a>" +
           "</div>" +
           "<div class='warning'>" +
           "⚠️ IMPORTANTE: Esta es una contraseña temporal. Por seguridad, cámbiala inmediatamente después de tu primer acceso." +
           "</div>" +
           "</div>" +
           "<div class='footer'>" +
           "<p>© 2023 Monsters University. Todos los derechos reservados.</p>" +
           "<p>Este es un correo automático, por favor no responder.</p>" +
           "<p><small>Si no solicitaste estas credenciales, por favor ignora este mensaje.</small></p>" +
           "</div>" +
           "</div>" +
           "</body>" +
           "</html>";
}

/**
 * Construye el contenido en texto plano del correo
 */
private String construirContenidoTextoPlano(String nombreUsuario, String contrasenia) {
    return "MONSTERS UNIVERSITY\n" +
           "=====================\n\n" +
           "CREDENCIALES DE ACCESO AL SISTEMA\n\n" +
           "¡Bienvenido/a!\n\n" +
           "Se han creado tus credenciales para acceder al sistema de gestión:\n\n" +
           "USUARIO: " + nombreUsuario + "\n" +
           "CONTRASEÑA: " + contrasenia + "\n\n" +
           "INSTRUCCIONES DE ACCESO:\n" +
           "1. Accede al sistema: http://localhost:8080/Monster_University\n" +
           "2. Ingresa las credenciales proporcionadas\n" +
           "3. Cambia tu contraseña en tu primer inicio de sesión\n\n" +
           "IMPORTANTE: Esta es una contraseña temporal. Por seguridad, cámbiala inmediatamente después de tu primer acceso.\n\n" +
           "----------------------------------------\n" +
           "© 2023 Monsters University\n" +
           "Este es un correo automático, no responder.\n" +
           "Si no solicitaste estas credenciales, ignora este mensaje.";
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