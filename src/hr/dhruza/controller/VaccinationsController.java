/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.dhruza.controller;

import hr.dhruza.dao.PersonRepository;
import hr.dhruza.dao.Repository;
import hr.dhruza.dao.RepositoryFactory;
import hr.dhruza.dao.VaccinationRepository;
import hr.dhruza.model.Person;
import hr.dhruza.model.Vaccination;
import hr.dhruza.utilities.FileUtils;
import hr.dhruza.utilities.ImageUtils;
import hr.dhruza.viewmodel.PersonViewModel;
import hr.dhruza.viewmodel.VaccinationViewModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class VaccinationsController implements Initializable {

    private VaccinationRepository vaccinationRepository;
    
    private final PersonViewModel personViewModel;
  
    private VaccinationViewModel selectedVaccinationViewModel;
    
    private Map<Control, Label> validationFields;
    
    private ObservableList<VaccinationViewModel> vaccinations = FXCollections.observableArrayList();
    
    public VaccinationsController(PersonViewModel personViewModel) {
        this.personViewModel = personViewModel;
    }
   
    @FXML
    private TabPane tpContent;
    @FXML
    private Tab tabList;
    @FXML
    private TableView<VaccinationViewModel> tvVaccinations;
    @FXML
    private TableColumn<VaccinationViewModel, String> tcManufacturer;
    @FXML
    private TableColumn<VaccinationViewModel, String> tcDate;
    @FXML
    private Tab tabEdit;
    @FXML
    private ImageView ivManufacturer;
    @FXML
    private TextField tfManufacturer;
    @FXML
    private Label lbManufacturerError;
    @FXML
    private Label lbDateError;
    @FXML
    private DatePicker dpDate;
    @FXML
    private Label lbIconError;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initRepo();
            initVaccinations();
            initTable();
            initValidation();
            setListeners();
        } catch (Exception ex) {
            Logger.getLogger(VaccinationsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @FXML
    private void delete(ActionEvent event) {
        if (tvVaccinations.getSelectionModel().getSelectedItem() != null) {
            vaccinations.remove(tvVaccinations.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void edit(ActionEvent event) {
        if (tvVaccinations.getSelectionModel().getSelectedItem() != null) {
            bindVaccination(tvVaccinations.getSelectionModel().getSelectedItem());
            tpContent.getSelectionModel().select(tabEdit);            
        }     
    }

    @FXML
    private void commit(ActionEvent event) {
        if (formValid()) {
            selectedVaccinationViewModel.getVaccination().setManufacturer(tfManufacturer.getText().trim());
            selectedVaccinationViewModel.getVaccination().setVaccinationDate(dpDate.getValue());
            selectedVaccinationViewModel.getVaccination().setPerson(personViewModel.getPerson());
            if (selectedVaccinationViewModel.getVaccination().getIDVaccination()== 0) {
                vaccinations.add(selectedVaccinationViewModel);
            } else {
                try {
                    // we cannot listen to the upates
                    vaccinationRepository.update(selectedVaccinationViewModel.getVaccination());
                    tvVaccinations.refresh();
                } catch (Exception ex) {
                    Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            selectedVaccinationViewModel = null;
            tpContent.getSelectionModel().select(tabList);
            resetForm();
        }
    }

    @FXML
    private void uploadImage(ActionEvent event) {
        File file = FileUtils.uploadFileDialog(ivManufacturer.getScene().getWindow(), "jpg", "jpeg", "png");
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            try {
                String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                selectedVaccinationViewModel.getVaccination().setManufacturerPicture(ImageUtils.imageToByteArray(image, ext));
                ivManufacturer.setImage(image);
            } catch (IOException ex) {
                Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initVaccinations() {
        try {
               vaccinationRepository
                    .getAll(personViewModel.getPerson().getIDPerson())
                    .forEach(vax -> vaccinations.add(new VaccinationViewModel((Vaccination)vax)));
        } catch (Exception ex) {
            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTable() {
        tcManufacturer.setCellValueFactory(vax -> vax.getValue().getManufacturerProperty());
        tcDate.setCellValueFactory(vax -> vax.getValue().getDateProperty());
       
        
        tvVaccinations.setItems(vaccinations);
    }
    
    private void initRepo() throws Exception {
        this.vaccinationRepository = (VaccinationRepository)RepositoryFactory.getRepository(VaccinationRepository.class);
    }
    
    private void initValidation() {
        validationFields = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(tfManufacturer, lbManufacturerError),
                new AbstractMap.SimpleImmutableEntry<>(dpDate, lbDateError))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    private void setListeners() {
        tpContent.setOnMouseClicked(event -> {
            if (tpContent.getSelectionModel().getSelectedItem().equals(tabEdit)) {
                bindVaccination(null);
            }
        });
        
        vaccinations.addListener((ListChangeListener.Change<? extends VaccinationViewModel> change) -> {
            if (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(vvm -> {
                        try {
                            vaccinationRepository.delete(vvm.getVaccination());
                        } catch (Exception ex) {
                            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(vvm -> {
                        try {
                            int idVax = vaccinationRepository.add(vvm.getVaccination());
                            vvm.getVaccination().setIDVaccination(idVax);
                        } catch (Exception ex) {
                            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
        });
    }
        
    private void bindVaccination(VaccinationViewModel viewModel) {
        resetForm();
        selectedVaccinationViewModel = viewModel != null ? viewModel : new VaccinationViewModel(null);
        tfManufacturer.setText(selectedVaccinationViewModel.getManufacturerProperty().get());
        
      
        dpDate.setValue((LocalDate) selectedVaccinationViewModel.getDateProperty().get()); 
        ivManufacturer.setImage(selectedVaccinationViewModel.getPictureProperty().get() != null ? new Image(new ByteArrayInputStream(selectedVaccinationViewModel.getPictureProperty().get())) : new Image(new File("src/assets/no_image.png").toURI().toString()));
    }
     
    private boolean formValid() {
        // discouraged but ok!
        final AtomicBoolean ok = new AtomicBoolean(true);
        validationFields.keySet().forEach(field -> {
            
            if(field instanceof DatePicker){
                if((((DatePicker) field).getValue() == null)){
                    validationFields.get(field).setVisible(true);
                    ok.set(false);
                } else {
                    validationFields.get(field).setVisible(false);
                }
            }
            
            if (field instanceof TextField) {
                if(((TextField)field).getText().trim().isEmpty()){
                    validationFields.get(field).setVisible(true);
                    ok.set(false);
                } else {
                    validationFields.get(field).setVisible(false);
                }
            }
        });

        if (selectedVaccinationViewModel.getPictureProperty().get() == null) {
            lbIconError.setVisible(true);
            ok.set(false);
        } else {
            lbIconError.setVisible(false);
        }
        
        return ok.get();
    }

    private void resetForm() {
        validationFields.values().forEach(lb -> lb.setVisible(false));
        lbIconError.setVisible(false);
    }   
}
