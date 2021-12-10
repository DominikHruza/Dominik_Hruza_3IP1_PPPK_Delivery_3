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

/**
 *
 * @author Dominik
 */
public class VaccinationRepository implements Repository<Vaccination>{

    @Override
    public int add(Vaccination data) throws Exception {
        try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();
            em.getTransaction().begin();

            Person person = em.find(Person.class, data.getPerson().getIDPerson());
            Vaccination vaccination = new Vaccination(data);
            
            vaccination.setPerson(person);
            em.persist(vaccination);

            em.getTransaction().commit();
            return vaccination.getIDVaccination();
        }
    }
    
    @Override
    public void update(Vaccination data) throws Exception {
           try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();
            em.getTransaction().begin();

            em.find(Vaccination.class, data.getIDVaccination()).updateDetails(data);
            em.getTransaction().commit();
        }
    }

    @Override
    public void delete(Vaccination data) throws Exception {
       try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();
            em.getTransaction().begin();

            em.remove(em.contains(data) ? data : em.merge(data));

            em.getTransaction().commit();
        }
    }

    @Override
    public Vaccination get(int id) throws Exception {
        try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();

            return em.find(Vaccination.class, id);
        }
    }

    @Override
    public List<Vaccination> getAll() throws Exception {
         try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();

            return em.createNamedQuery("Vaccination.findAll").getResultList();
        }
    }
    
    public List<Vaccination> getAll(Integer idPerson) throws Exception {
         try (EntityManagerWrapper wrapper = HibernateFactory.getEntityManger()) {
            EntityManager em = wrapper.get();

            return em.createNamedQuery("Vaccination.findByPersonID")
                    .setParameter("iDPerson", idPerson)
                    .getResultList();
        }
    } 
    
    @Override
    public void release() throws Exception {
        HibernateFactory.release();
    }
}
