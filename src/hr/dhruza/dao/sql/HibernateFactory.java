/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.dhruza.dao.sql;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
/**
 *
 * @author Dominik
 */
public class HibernateFactory {
    private static final String PERSISTENCE_UNIT = "PPPK_Delivery_3PU";
   
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    
    public static EntityManagerWrapper getEntityManger() {
        
        return new EntityManagerWrapper(EMF.createEntityManager());
    }
    
    public static void release() {
        EMF.close();
    }    
}
