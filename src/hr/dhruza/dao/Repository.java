/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.dhruza.dao;
import hr.dhruza.model.Person;
import hr.dhruza.model.Vaccination;
import java.util.List;

/**
 *
 * @author Dominik
 */
public interface Repository<T> {
    
    int add(T data) throws Exception;
    void update(T data) throws Exception;
    void delete(T data) throws Exception;
    T get(int id) throws Exception;
    List<T> getAll() throws Exception;
    

    default void release() throws Exception {};
}
