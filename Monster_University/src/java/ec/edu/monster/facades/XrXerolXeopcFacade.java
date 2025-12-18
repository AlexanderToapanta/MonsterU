package ec.edu.monster.facades;

import ec.edu.monster.modelo.XrXerolXeopc;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Facade para XrXerolXeopc
 */
@Stateless
public class XrXerolXeopcFacade extends AbstractFacade<XrXerolXeopc> {

    @PersistenceContext(unitName = "Monster_UniversityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public XrXerolXeopcFacade() {
        super(XrXerolXeopc.class);
    }

   public boolean existeAsignacion(String xerolId, String xeopcId) {
    try {
        Long count = (Long) em.createQuery(
            "SELECT COUNT(x) FROM XrXerolXeopc x WHERE x.xrXerolXeopcPK.xerolId = :xerolId AND x.xrXerolXeopcPK.xeopcId = :xeopcId")
            .setParameter("xerolId", xerolId)
            .setParameter("xeopcId", xeopcId)
            .getSingleResult();
        return count > 0;
    } catch (Exception e) {
        return false;
    }
}

/**
 * Encuentra una asignación específica
 */
public XrXerolXeopc encontrarAsignacion(String xerolId, String xeopcId) {
    try {
        return (XrXerolXeopc) em.createQuery(
            "SELECT x FROM XrXerolXeopc x WHERE x.xrXerolXeopcPK.xerolId = :xerolId AND x.xrXerolXeopcPK.xeopcId = :xeopcId")
            .setParameter("xerolId", xerolId)
            .setParameter("xeopcId", xeopcId)
            .getSingleResult();
    } catch (Exception e) {
        return null;
    }
}

/**
 * Obtiene las opciones asignadas a un rol (activas)
 */
public List<XrXerolXeopc> findOpcionesPorRol(String xerolId) {
    try {
        return em.createQuery(
            "SELECT x FROM XrXerolXeopc x WHERE x.xrXerolXeopcPK.xerolId = :xerolId AND x.xropFechaRetiro IS NULL ORDER BY x.xeopcOpcion.xeopcNombre")
            .setParameter("xerolId", xerolId)
            .getResultList();
    } catch (Exception e) {
        return new ArrayList<>();
    }
}

/**
 * Obtiene los roles que tienen una opción asignada (activos)
 */
public List<XrXerolXeopc> findRolesPorOpcion(String xeopcId) {
    try {
        return em.createQuery(
            "SELECT x FROM XrXerolXeopc x WHERE x.xrXerolXeopcPK.xeopcId = :xeopcId AND x.xropFechaRetiro IS NULL ORDER BY x.xerolRol.xerolNombre")
            .setParameter("xeopcId", xeopcId)
            .getResultList();
    } catch (Exception e) {
        return new ArrayList<>();
    }
}

    // Método para eliminar una asignación específica
    public void eliminarAsignacion(String xerolId, String xeopcId) {
        XrXerolXeopc asignacion = encontrarAsignacion(xerolId, xeopcId);
        if (asignacion != null) {
            remove(asignacion);
        }
    }
}