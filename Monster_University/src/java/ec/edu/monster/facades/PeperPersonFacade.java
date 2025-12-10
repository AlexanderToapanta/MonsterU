/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.facades;

import ec.edu.monster.modelo.PeperPerson;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author Usuario
 */
@Stateless
public class PeperPersonFacade extends AbstractFacade<PeperPerson> {

    @PersistenceContext(unitName = "Monster_UniversityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PeperPersonFacade() {
        super(PeperPerson.class);
    }
    
    // Método para obtener el último ID de persona
    public Integer obtenerMaximoNumeroId() {
    try {
        System.out.println("=== OBTENIENDO MÁXIMO NÚMERO DE ID ===");
        
        // 1. Obtener todos los IDs
        List<PeperPerson> todasPersonas = findAll();
        System.out.println("Total personas en BD: " + todasPersonas.size());
        
        if (todasPersonas.isEmpty()) {
            System.out.println("✅ No hay personas, máximo = 0");
            return 0;
        }
        
        // 2. Buscar el máximo número
        int maxNumero = 0;
        
        for (PeperPerson persona : todasPersonas) {
            String id = persona.getPeperId();
            System.out.println("Analizando ID: " + id);
            
            if (id != null && id.startsWith("PE")) {
                try {
                    // Extraer los números después de "PE"
                    String numeros = id.substring(2); // Quita "PE"
                    
                    // Si hay ceros a la izquierda, quitarlos
                    while (numeros.startsWith("0") && numeros.length() > 1) {
                        numeros = numeros.substring(1);
                    }
                    
                    int numero = Integer.parseInt(numeros);
                    System.out.println("  -> Número: " + numero);
                    
                    if (numero > maxNumero) {
                        maxNumero = numero;
                        System.out.println("  -> Nuevo máximo: " + maxNumero);
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("  -> ID no tiene formato válido: " + id);
                }
            }
        }
        
        System.out.println("✅ Máximo número encontrado: " + maxNumero);
        return maxNumero;
        
    } catch (Exception e) {
        System.out.println("❌ ERROR en obtenerMaximoNumeroId: " + e.getMessage());
        e.printStackTrace();
        return 0;
    }
}

public boolean existeId(String id) {
    try {
        return find(id) != null;
    } catch (Exception e) {
        return false;
    }
}

// Validar si la cédula ya existe en la base de datos
public boolean existeCedula(String cedula) {
    try {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false;
        }
        
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(p) FROM PeperPerson p WHERE p.peperCedula = :cedula", 
            Long.class
        );
        query.setParameter("cedula", cedula.trim());
        
        Long count = query.getSingleResult();
        System.out.println("🔍 Verificando cédula " + cedula + " - Existencias: " + count);
        
        return count > 0;
        
    } catch (Exception e) {
        System.out.println("❌ ERROR en existeCedula: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Validar si el email ya existe en la base de datos
public boolean existeEmail(String email) {
    try {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(p) FROM PeperPerson p WHERE LOWER(p.peperEmail) = LOWER(:email)", 
            Long.class
        );
        query.setParameter("email", email.trim());
        
        Long count = query.getSingleResult();
        System.out.println("🔍 Verificando email " + email + " - Existencias: " + count);
        
        return count > 0;
        
    } catch (Exception e) {
        System.out.println("❌ ERROR en existeEmail: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Validar si el celular ya existe en la base de datos
public boolean existeCelular(String celular) {
    try {
        if (celular == null || celular.trim().isEmpty()) {
            return false;
        }
        
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(p) FROM PeperPerson p WHERE p.peperCelular = :celular", 
            Long.class
        );
        query.setParameter("celular", celular.trim());
        
        Long count = query.getSingleResult();
        System.out.println("🔍 Verificando celular " + celular + " - Existencias: " + count);
        
        return count > 0;
        
    } catch (Exception e) {
        System.out.println("❌ ERROR en existeCelular: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Método para buscar persona por cédula
public PeperPerson buscarPorCedula(String cedula) {
    try {
        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }
        
        TypedQuery<PeperPerson> query = em.createQuery(
            "SELECT p FROM PeperPerson p WHERE p.peperCedula = :cedula", 
            PeperPerson.class
        );
        query.setParameter("cedula", cedula.trim());
        
        List<PeperPerson> resultados = query.getResultList();
        
        if (resultados.isEmpty()) {
            return null;
        }
        
        return resultados.get(0);
        
    } catch (Exception e) {
        System.out.println("❌ ERROR en buscarPorCedula: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

// Método para buscar persona por email
public PeperPerson buscarPorEmail(String email) {
    try {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        TypedQuery<PeperPerson> query = em.createQuery(
            "SELECT p FROM PeperPerson p WHERE LOWER(p.peperEmail) = LOWER(:email)", 
            PeperPerson.class
        );
        query.setParameter("email", email.trim());
        
        List<PeperPerson> resultados = query.getResultList();
        
        if (resultados.isEmpty()) {
            return null;
        }
        
        return resultados.get(0);
        
    } catch (Exception e) {
        System.out.println("❌ ERROR en buscarPorEmail: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
}