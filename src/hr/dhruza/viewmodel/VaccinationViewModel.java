/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.dhruza.viewmodel;

import hr.dhruza.model.Vaccination;
import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Dominik
 */
public class VaccinationViewModel {
    
    private final Vaccination vaccination;
    
    public VaccinationViewModel(Vaccination vaccination) {
        if (vaccination == null) {
            vaccination = new Vaccination(0, "", null);
        }
        this.vaccination = vaccination;
    }

    public Vaccination getVaccination() {
        return vaccination;
    }
    
    public StringProperty getManufacturerProperty() {
        return new SimpleStringProperty(vaccination.getManufacturer());
    }
    public ObjectProperty getDateProperty() {   
        return new SimpleObjectProperty(vaccination.getVaccinationDate());
    }
  
    public IntegerProperty getIDProperty() {
        return new SimpleIntegerProperty(vaccination.getIDVaccination());
    }
   
    public ObjectProperty<byte[]> getPictureProperty() {
        return new SimpleObjectProperty<>(vaccination.getManufacturerPicture());
    }
}
