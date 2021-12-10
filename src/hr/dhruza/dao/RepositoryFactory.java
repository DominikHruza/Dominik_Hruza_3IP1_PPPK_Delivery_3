/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.dhruza.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.security.jca.GetInstance;

/**
 *
 * @author Dominik
 */
public class RepositoryFactory {

    private static final List<Repository> repositorys = new ArrayList<>();
    
    private RepositoryFactory() {
    }
    
    public static <T extends Repository> Repository getRepository(Class<T> clazz) throws Exception {
        Repository instance = findInstance(clazz);
        if(instance != null){
           return instance;
        }
        
        T newInstance = clazz.newInstance();
        repositorys.add(newInstance);
        return newInstance;
    }   
    
    public static void releaseAll(){
        repositorys.forEach(repo -> {
            try {
                repo.release();
            } catch (Exception ex) {
                Logger.getLogger(RepositoryFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private static <T extends Repository> Repository findInstance(Class<T> clazz) {
        return repositorys.stream().filter(repo -> clazz.isInstance(repo))
                .findAny()
                .orElse(null);
    }
}
