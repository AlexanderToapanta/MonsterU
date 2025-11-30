package ec.edu.monster.controlador;

import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.facades.XeusuUsuarFacade;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
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
    
    private XeusuUsuar nuevoUsuario;
    private String confirmarContrasena;
    private PasswordController passwordController;

    public CrearUsuarioController() {
        nuevoUsuario = new XeusuUsuar();
        passwordController = new PasswordController();
    }

    public void initNuevoUsuario() {
        nuevoUsuario = new XeusuUsuar();
        confirmarContrasena = "";
        nuevoUsuario.setXeusuEstado("ACTIVO");
    }

    public void crearUsuario() {
        try {
            // Debug en consola del servidor
            System.out.println("=== INICIANDO CREACIÓN DE USUARIO ===");
            System.out.println("Datos recibidos:");
            System.out.println("ID: " + nuevoUsuario.getXeusuId());
            System.out.println("Nombre: " + nuevoUsuario.getXeusuNombre());
            System.out.println("Estado: " + nuevoUsuario.getXeusuEstado());

            // Validaciones
            if (!validarDatos()) {
                System.out.println("❌ Validaciones fallaron");
                return;
            }
            System.out.println("✅ Validaciones pasadas");

            // Verificar si el ID ya existe
            if (usuarioFacade.find(nuevoUsuario.getXeusuId()) != null) {
                System.out.println("❌ ID de usuario ya existe: " + nuevoUsuario.getXeusuId());
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de usuario ya existe"));
                return;
            }
            System.out.println("✅ ID de usuario disponible");

            // Encriptar contraseña
            System.out.println("🔐 Encriptando contraseña...");
            String contrasenaEncriptada = passwordController.encriptarClave(nuevoUsuario.getXeusuContra());
            nuevoUsuario.setXeusuContra(contrasenaEncriptada);
            System.out.println("✅ Contraseña encriptada. Longitud: " + contrasenaEncriptada.length());

            // Establecer campos NULL explícitamente
            nuevoUsuario.setPeperId(null);
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
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario creado correctamente"));
            } else {
                System.out.println("❌ USUARIO NO SE GUARDÓ EN BD");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar usuario"));
            }

            // Limpiar formulario
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
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de usuario es requerido"));
            return false;
        }

        if (nuevoUsuario.getXeusuId().length() > 5) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de usuario no puede tener más de 5 caracteres"));
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
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El estado es requerido"));
            return false;
        }

        return true;
    }

    public void generarContrasenaAleatoria() {
        try {
            String contrasenaAleatoria = passwordController.generarContraseñaAleatoria();
            nuevoUsuario.setXeusuContra(contrasenaAleatoria);
            confirmarContrasena = contrasenaAleatoria;
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Contraseña generada", "Se ha generado una contraseña aleatoria"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al generar contraseña: " + e.getMessage()));
        }
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
}