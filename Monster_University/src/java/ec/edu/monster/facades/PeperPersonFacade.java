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
}