/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.dhruza.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dominik
 */
@Entity
@Table(name = "Vaccination")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vaccination.findAll", query = "SELECT v FROM Vaccination v")
    , @NamedQuery(name = "Vaccination.findByIDVaccination", query = "SELECT v FROM Vaccination v WHERE v.iDVaccination = :iDVaccination")
    , @NamedQuery(name = "Vaccination.findByVaccinationDate", query = "SELECT v FROM Vaccination v WHERE v.vaccinationDate = :vaccinationDate")
    , @NamedQuery(name = "Vaccination.findByManufacturer", query = "SELECT v FROM Vaccination v WHERE v.manufacturer = :manufacturer")
    , @NamedQuery(name = "Vaccination.findByPersonID", query = "SELECT v FROM Vaccination v WHERE v.person.iDPerson = :iDPerson")})
public class Vaccination implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "IDVaccination")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer iDVaccination;
    @Column(name = "VaccinationDate")
    private LocalDate vaccinationDate;
    @Basic(optional = false)
    @Column(name = "Manufacturer")
    private String manufacturer;
    @Lob
    @Column(name = "ManufacturerPicture")
    private byte[] manufacturerPicture;
    @JoinColumn(name = "PersonID", referencedColumnName = "IDPerson")
    @ManyToOne
    private Person person;

    public Vaccination() {
    }
    
    public Vaccination(Vaccination data) {
        updateDetails(data);
    }

    public Vaccination(Integer iDVaccination) {
        this.iDVaccination = iDVaccination;
    }

    public Vaccination(Integer iDVaccination, String manufacturer, LocalDate date) {
        this.iDVaccination = iDVaccination;
        this.manufacturer = manufacturer;
        this.vaccinationDate = date;
    }

    public Integer getIDVaccination() {
        return iDVaccination;
    }

    public void setIDVaccination(Integer iDVaccination) {
        this.iDVaccination = iDVaccination;
    }

    public LocalDate getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(LocalDate vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public byte[] getManufacturerPicture() {
        return manufacturerPicture;
    }

    public void setManufacturerPicture(byte[] manufacturerPicture) {
        this.manufacturerPicture = manufacturerPicture;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDVaccination != null ? iDVaccination.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vaccination)) {
            return false;
        }
        Vaccination other = (Vaccination) object;
        if ((this.iDVaccination == null && other.iDVaccination != null) || (this.iDVaccination != null && !this.iDVaccination.equals(other.iDVaccination))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hr.dhruza.model.Vaccination[ iDVaccination=" + iDVaccination + " ]";
    }

    public void updateDetails(Vaccination data) {
        this.vaccinationDate = data.getVaccinationDate();
        this.manufacturer = data.getManufacturer();
        this.manufacturerPicture = data.getManufacturerPicture();
    }  
}
