package ec.edu.monster.facades;

import ec.edu.monster.modelo.XerolRol;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Facade para XerolRol
 */
@Stateless
public class XerolRolFacade extends AbstractFacade<XerolRol> {

    @PersistenceContext(unitName = "Monster_UniversityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public XerolRolFacade() {
        super(XerolRol.class);
    }

    // Puedes añadir consultas custom si las necesitas más adelante
}
