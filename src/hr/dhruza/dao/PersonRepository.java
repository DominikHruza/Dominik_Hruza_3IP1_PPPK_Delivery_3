/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.dhruza.dao;

import hr.dhruza.dao.sql.EntityManagerWrapper;
import hr.dhruza.dao.sql.HibernateFactory;
import hr.dhruza.model.Person;
import hr.dhruza.model.Vaccination;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


public class PersonRepository implements Repository<Person> {

    
    @Override
    public int add(Person data) throws Exception {
        try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();
            em.getTransaction().begin();

            Person person = new Person(data);
            em.persist(person);

            em.getTransaction().commit();
            return person.getIDPerson();
        }
    }

    @Override
    public void update(Person person) throws Exception {
        try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();
            em.getTransaction().begin();

            em.find(Person.class, person.getIDPerson()).updateDetails(person);

            em.getTransaction().commit();
        }
    }

    @Override
    public void delete(Person person) throws Exception {
        try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();
            em.getTransaction().begin();
            
            em.remove(em.contains(person) ? person : em.merge(person));

            em.getTransaction().commit();
        }
    }

    @Override
    public Person get(int id) throws Exception {
        try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();

            return em.find(Person.class, id);
        }
    }

    @Override
    public List<Person> getAll() throws Exception {
         try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();

            return em.createNamedQuery("Person.findAll").getResultList();
        }
    }
    

    @Override
    public void release() throws Exception {
        HibernateFactory.release();
    }
}
