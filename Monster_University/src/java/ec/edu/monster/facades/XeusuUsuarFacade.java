package ec.edu.monster.facades;

import ec.edu.monster.modelo.XeusuUsuar;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
            return null;
        }
    }

    /**
     * Si necesitas métodos para consultas más complejas (usuarios por rol, no asignados, etc.)
     * los podemos añadir aquí. En la implementación actual manejamos asignaciones desde el controller.
     */
}
