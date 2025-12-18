package ec.edu.monster.facades;

import ec.edu.monster.modelo.XeopcOpcion;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Facade para XeopcOpcion
 */
@Stateless
public class XeopcOpcionFacade extends AbstractFacade<XeopcOpcion> {

    @PersistenceContext(unitName = "Monster_UniversityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public XeopcOpcionFacade() {
        super(XeopcOpcion.class);
    }
    /**
 * Obtiene todas las opciones ordenadas por nombre
 */
public List<XeopcOpcion> findAllOrdenadas() {
    try {
        return em.createQuery(
            "SELECT x FROM XeopcOpcion x ORDER BY x.xeopcNombre")
            .getResultList();
    } catch (Exception e) {
        return new ArrayList<>();
    }
}

/**
 * Busca opciones por nombre (para búsquedas)
 */
public List<XeopcOpcion> buscarPorNombre(String nombre) {
    try {
        return em.createQuery(
            "SELECT x FROM XeopcOpcion x WHERE x.xeopcNombre LIKE :nombre ORDER BY x.xeopcNombre")
            .setParameter("nombre", "%" + nombre + "%")
            .getResultList();
    } catch (Exception e) {
        return new ArrayList<>();
    }
}

/**
 * Verifica si una opción está siendo usada
 */
public boolean estaEnUso(String xeopcId) {
    try {
        Long count = (Long) em.createQuery(
            "SELECT COUNT(x) FROM XrXerolXeopc x WHERE x.xrXerolXeopcPK.xeopcId = :xeopcId")
            .setParameter("xeopcId", xeopcId)
            .getSingleResult();
        return count > 0;
    } catch (Exception e) {
        return false;
    }
}

    // Puedes añadir consultas custom si las necesitas más adelante
}