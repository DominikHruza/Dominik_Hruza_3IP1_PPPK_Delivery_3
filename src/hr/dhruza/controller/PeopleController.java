/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.dhruza.controller;

import hr.dhruza.utilities.FileUtils;
import hr.dhruza.dao.PersonRepository;
import hr.dhruza.dao.Repository;
import hr.dhruza.dao.RepositoryFactory;
import hr.dhruza.model.Person;
import hr.dhruza.utilities.ImageUtils;
import hr.dhruza.utilities.ValidationUtils;
import hr.dhruza.viewmodel.PersonViewModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class PeopleController implements Initializable {

    private PersonRepository peopleRepository;
    
    private Map<TextField, Label> validationFields;
    
    private ObservableList<PersonViewModel> people = FXCollections.observableArrayList();
    
    private PersonViewModel selectedPersonViewModel;

    public PeopleController() {
        try {
            this.peopleRepository = (PersonRepository) RepositoryFactory.getRepository(PersonRepository.class);
        } catch (Exception ex) {
            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @FXML
    private TabPane tpContent;
    @FXML
    private Tab tabList;
    @FXML
    private TableView<PersonViewModel> tvPeople;
    @FXML
    private TableColumn<PersonViewModel, String> tcFirstName;
    @FXML
    private TableColumn<PersonViewModel, String> tcLastName;
    @FXML
    private TableColumn<PersonViewModel, Integer> tcAge;
    @FXML
    private TableColumn<PersonViewModel, String> tcEmail;
    @FXML
    private Tab tabEdit;
    @FXML
    private ImageView ivPerson;
    @FXML
    private TextField tfFirstName, tfLastName, tfAge, tfEmail;
    @FXML
    private Label lbFirstNameError, lbLastNameError, lbAgeError, lbEmailError, lbIconError;
    @FXML
    private Button btnVaccinations;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initRepo();
            initValidation();
            initPeople();
            initTable();
            addIntegerMask(tfAge);
            setListeners();
        } catch (Exception ex) {
            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @FXML
    private void edit() {
        if (tvPeople.getSelectionModel().getSelectedItem() != null) {
            bindPerson(tvPeople.getSelectionModel().getSelectedItem());
            tpContent.getSelectionModel().select(tabEdit);            
        }     
    }

    @FXML
    private void delete(ActionEvent event) {
        if (tvPeople.getSelectionModel().getSelectedItem() != null) {
            people.remove(tvPeople.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void uploadImage(ActionEvent event) {
        File file = FileUtils.uploadFileDialog(tfAge.getScene().getWindow(), "jpg", "jpeg", "png");
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            try {
                String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                selectedPersonViewModel.getPerson().setPicture(ImageUtils.imageToByteArray(image, ext));
                ivPerson.setImage(image);
            } catch (IOException ex) {
                Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void commit(ActionEvent event) {
         if (formValid()) {
            selectedPersonViewModel.getPerson().setFirstName(tfFirstName.getText().trim());
            selectedPersonViewModel.getPerson().setLastName(tfLastName.getText().trim());
            selectedPersonViewModel.getPerson().setAge(Integer.valueOf(tfAge.getText().trim()));
            selectedPersonViewModel.getPerson().setEmail(tfEmail.getText().trim());
            if (selectedPersonViewModel.getPerson().getIDPerson() == 0) {
                people.add(selectedPersonViewModel);
            } else {
                try {
                    // we cannot listen to the upates
                    peopleRepository.update(selectedPersonViewModel.getPerson());
                    tvPeople.refresh();
                } catch (Exception ex) {
                    Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            selectedPersonViewModel = null;
            tpContent.getSelectionModel().select(tabList);
            resetForm();
        }
    }
    
    
    @FXML
    void showVax(ActionEvent event) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/dhruza/view/Vaccinations.fxml"));
        loader.setController(new VaccinationsController(selectedPersonViewModel));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setTitle("Vaccinations");
        stage.setScene(scene);
        stage.show();
    }
    
    private void initValidation() {
         validationFields = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(tfFirstName, lbFirstNameError),
                new AbstractMap.SimpleImmutableEntry<>(tfLastName, lbLastNameError),
                new AbstractMap.SimpleImmutableEntry<>(tfAge, lbAgeError),
                new AbstractMap.SimpleImmutableEntry<>(tfEmail, lbEmailError))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void initPeople() {
        try {
            peopleRepository
                    .getAll()        
                    .forEach(person -> people.add(new PersonViewModel((Person) person)));
        } catch (Exception ex) {
            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  

    private void initTable() {
        tcFirstName.setCellValueFactory(person -> person.getValue().getFirstNameProperty());
        tcLastName.setCellValueFactory(person -> person.getValue().getLastNameProperty());
        tcAge.setCellValueFactory(person -> person.getValue().getAgeProperty().asObject());
        tcEmail.setCellValueFactory(person -> person.getValue().getEmailProperty());
        
        tvPeople.setItems(people);
    }

    private void addIntegerMask(TextField tf) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("\\d*")) {
                return change;
            }
            return null;
        };
        // first we must convert integer to string, and then we apply filter
        tf.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
    }

    private void setListeners() {
        tpContent.setOnMouseClicked(event -> {
            if (tpContent.getSelectionModel().getSelectedItem().equals(tabEdit)) {
                bindPerson(null);
            }
        });
        
        people.addListener((ListChangeListener.Change<? extends PersonViewModel> change) -> {
            if (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(pvm -> {
                        try {
                            peopleRepository.delete(pvm.getPerson());
                        } catch (Exception ex) {
                            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(pvm -> {
                        try {
                            int idPerson = peopleRepository.add(pvm.getPerson());
                            pvm.getPerson().setIDPerson(idPerson);
                        } catch (Exception ex) {
                            Logger.getLogger(PeopleController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
        });
    }   
    
    private void bindPerson(PersonViewModel viewModel) {
        resetForm();
        btnVaccinations.setVisible(viewModel != null);
        selectedPersonViewModel = viewModel != null ? viewModel : new PersonViewModel(null);
        tfFirstName.setText(selectedPersonViewModel.getFirstNameProperty().get());
        tfLastName.setText(selectedPersonViewModel.getLastNameProperty().get());
        tfAge.setText(String.valueOf(selectedPersonViewModel.getAgeProperty().get()));
        tfEmail.setText(selectedPersonViewModel.getEmailProperty().get());
        ivPerson.setImage(selectedPersonViewModel.getPictureProperty().get() != null ? new Image(new ByteArrayInputStream(selectedPersonViewModel.getPictureProperty().get())) : new Image(new File("src/assets/no_image.png").toURI().toString()));
    }
    
    private void resetForm() {
        validationFields.values().forEach(lb -> lb.setVisible(false));
        lbIconError.setVisible(false);
    }
    
    private boolean formValid() {
        // discouraged but ok!
        final AtomicBoolean ok = new AtomicBoolean(true);
        validationFields.keySet().forEach(tf -> {
            if (tf.getText().trim().isEmpty() || tf.getId().contains("Email") && !ValidationUtils.isValidEmail(tf.getText().trim())) {
                validationFields.get(tf).setVisible(true);
                ok.set(false);
            } else {
                validationFields.get(tf).setVisible(false);
            }
        });

        if (selectedPersonViewModel.getPictureProperty().get() == null) {
            lbIconError.setVisible(true);
            ok.set(false);
        } else {
            lbIconError.setVisible(false);
        }
        return ok.get();
    }

    private void initRepo() throws Exception {
        this.peopleRepository = (PersonRepository)RepositoryFactory.getRepository(PersonRepository.class);
    }
}
