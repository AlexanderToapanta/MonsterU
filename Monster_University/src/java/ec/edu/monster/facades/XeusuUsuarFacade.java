package ec.edu.monster.facades;

import ec.edu.monster.modelo.XeusuUsuar;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade para XeusuUsuar
 */
@Stateless
public class XeusuUsuarFacade extends AbstractFacade<XeusuUsuar> {

    @PersistenceContext(unitName = "Monster_UniversityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public XeusuUsuarFacade() {
        super(XeusuUsuar.class);
    }

    /**
     * Login seguro: no lanza excepciones si no encuentra usuario.
     * Nota: la encriptación/validación de password debe hacerse en el controller
     * comparando hashes si aplica.
     */
    public XeusuUsuar doLogin(String username, String password) {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.xeusuNombre = :user AND u.xeusuContra = :pass",
                XeusuUsuar.class
            );
            query.setParameter("user", username);
            query.setParameter("pass", password);

            List<XeusuUsuar> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);

        } catch (Exception e) {
            System.out.println("Error en doLogin: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<XeusuUsuar> findAll() {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u ORDER BY u.xeusuId",
                XeusuUsuar.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.out.println("Error en findAll: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Encuentra usuarios que NO tienen rol asignado (xerolId IS NULL)
     */
    public List<XeusuUsuar> findUsuariosSinRol() {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.xerolId IS NULL ORDER BY u.xeusuNombre", 
                XeusuUsuar.class
            );
            List<XeusuUsuar> resultado = query.getResultList();
            System.out.println("findUsuariosSinRol: Encontrados " + resultado.size() + " usuarios sin rol");
            return resultado;
        } catch (Exception e) {
            System.out.println("Error en findUsuariosSinRol: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Encuentra usuarios que tienen un rol específico asignado
     */
    public List<XeusuUsuar> findUsuariosByRolId(String rolId) {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.xerolId.xerolId = :rolId ORDER BY u.xeusuNombre", 
                XeusuUsuar.class
            );
            query.setParameter("rolId", rolId);
            List<XeusuUsuar> resultado = query.getResultList();
            System.out.println("findUsuariosByRolId: Encontrados " + resultado.size() + " usuarios con rol " + rolId);
            return resultado;
        } catch (Exception e) {
            System.out.println("Error en findUsuariosByRolId: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Encuentra usuarios que tienen algún rol asignado (xerolId IS NOT NULL)
     */
    public List<XeusuUsuar> findUsuariosConRol() {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.xerolId IS NOT NULL ORDER BY u.xerolId.xerolNombre, u.xeusuNombre", 
                XeusuUsuar.class
            );
            List<XeusuUsuar> resultado = query.getResultList();
            System.out.println("findUsuariosConRol: Encontrados " + resultado.size() + " usuarios con rol");
            return resultado;
        } catch (Exception e) {
            System.out.println("Error en findUsuariosConRol: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Encuentra usuarios por estado (ACTIVO, INACTIVO, etc.)
     */
    public List<XeusuUsuar> findUsuariosByEstado(String estado) {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.xeusuEstado = :estado ORDER BY u.xeusuNombre", 
                XeusuUsuar.class
            );
            query.setParameter("estado", estado);
            return query.getResultList();
        } catch (Exception e) {
            System.out.println("Error en findUsuariosByEstado: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Encuentra usuarios que tienen una persona asociada
     */
    public List<XeusuUsuar> findUsuariosConPersona() {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.peperId IS NOT NULL ORDER BY u.peperId.peperApellido", 
                XeusuUsuar.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.out.println("Error en findUsuariosConPersona: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Encuentra usuarios que NO tienen persona asociada
     */
    public List<XeusuUsuar> findUsuariosSinPersona() {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.peperId IS NULL ORDER BY u.xeusuNombre", 
                XeusuUsuar.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.out.println("Error en findUsuariosSinPersona: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Encuentra usuario por nombre de usuario (exacto)
     */
    public XeusuUsuar findByNombreUsuario(String nombreUsuario) {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.xeusuNombre = :nombre", 
                XeusuUsuar.class
            );
            query.setParameter("nombre", nombreUsuario);
            List<XeusuUsuar> resultado = query.getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } catch (Exception e) {
            System.out.println("Error en findByNombreUsuario: " + e.getMessage());
            return null;
        }
    }

    /**
     * Encuentra usuarios cuyo nombre contenga el texto proporcionado
     */
    public List<XeusuUsuar> findByNombreContaining(String texto) {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE LOWER(u.xeusuNombre) LIKE LOWER(:texto) ORDER BY u.xeusuNombre", 
                XeusuUsuar.class
            );
            query.setParameter("texto", "%" + texto + "%");
            return query.getResultList();
        } catch (Exception e) {
            System.out.println("Error en findByNombreContaining: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Verifica si existe un usuario con el nombre de usuario especificado
     */
    public boolean existeNombreUsuario(String nombreUsuario) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM XeusuUsuar u WHERE u.xeusuNombre = :nombre", 
                Long.class
            );
            query.setParameter("nombre", nombreUsuario);
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.out.println("Error en existeNombreUsuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cuenta cuántos usuarios hay en total
     */
    public long countUsuarios() {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM XeusuUsuar u", 
                Long.class
            );
            return query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error en countUsuarios: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Cuenta cuántos usuarios tienen un rol específico
     */
    public long countUsuariosByRol(String rolId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM XeusuUsuar u WHERE u.xerolId.xerolId = :rolId", 
                Long.class
            );
            query.setParameter("rolId", rolId);
            return query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error en countUsuariosByRol: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Cuenta cuántos usuarios no tienen rol
     */
    public long countUsuariosSinRol() {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM XeusuUsuar u WHERE u.xerolId IS NULL", 
                Long.class
            );
            return query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error en countUsuariosSinRol: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Método genérico para consultas con criterios dinámicos
     */
    public List<XeusuUsuar> findByCriteria(String whereClause) {
        try {
            String queryStr = "SELECT u FROM XeusuUsuar u";
            if (whereClause != null && !whereClause.trim().isEmpty()) {
                queryStr += " WHERE " + whereClause;
            }
            queryStr += " ORDER BY u.xeusuNombre";
            
            TypedQuery<XeusuUsuar> query = em.createQuery(queryStr, XeusuUsuar.class);
            return query.getResultList();
        } catch (Exception e) {
            System.out.println("Error en findByCriteria: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Busca usuario por ID de persona asociada
     */
    public XeusuUsuar findByPersonaId(String personaId) {
        try {
            TypedQuery<XeusuUsuar> query = em.createQuery(
                "SELECT u FROM XeusuUsuar u WHERE u.peperId.peperId = :personaId", 
                XeusuUsuar.class
            );
            query.setParameter("personaId", personaId);
            List<XeusuUsuar> resultado = query.getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } catch (Exception e) {
            System.out.println("Error en findByPersonaId: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si una persona ya tiene usuario asociado
     */
    public boolean tieneUsuarioAsociado(String personaId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM XeusuUsuar u WHERE u.peperId.peperId = :personaId", 
                Long.class
            );
            query.setParameter("personaId", personaId);
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.out.println("Error en tieneUsuarioAsociado: " + e.getMessage());
            return false;
        }
    }
}