/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.facades;

import ec.edu.monster.modelo.XeusuUsuar;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Usuario
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
    public XeusuUsuar doLogin(String username, String password) {
    try {
        TypedQuery<XeusuUsuar> query = em.createQuery(
            "SELECT u FROM XeusuUsuar u WHERE u.xeusuNombre = :username AND u.xeusuContra = :password", 
            XeusuUsuar.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        
        XeusuUsuar usuario = query.getSingleResult();
        
       
        FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().put("usuario", usuario);
        
        return usuario;
    } catch (Exception e) {
        return null;
    }
}
    
}
