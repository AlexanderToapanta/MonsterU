/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.facades;

import ec.edu.monster.modelo.MecarrCarrera;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Usuario
 */
@Stateless
public class MecarrCarreraFacade extends AbstractFacade<MecarrCarrera> {

    @PersistenceContext(unitName = "Monster_UniversityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MecarrCarreraFacade() {
        super(MecarrCarrera.class);
    }
    
    @Override
    public List<MecarrCarrera> findAll() {
    return em.createQuery("SELECT m FROM MecarrCarrera m").getResultList();
}

    
}
