package project_ihm;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class EtudiantControler {

    @FXML
    private TableView<Map<String, Object>> etudiantTableView;

    @FXML
    private TableColumn<Map<String, Object>, String> nomColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> prenomColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> numeroEtudiantColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> filiereColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> niveauColumn;

    private ObservableList<Map<String, Object>> etudiants = FXCollections.observableArrayList();
    private List<Map<String, Object>> allEtudiants;

    @FXML
    private void initialize() {
        // Set up columns
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("nom").toString()));
        prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("prenom").toString()));
        numeroEtudiantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("numeroEtudiant").toString()));


        // Set up table data
        etudiantTableView.setItems(etudiants);

        // Load data from the database
        loadEtudiantsFromDatabase();
        allEtudiants = etudiants; // No need for a new ArrayList here
    }

    private void loadEtudiantsFromDatabase() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("DataBase.json"));
            JsonNode etudiantsNode = rootNode.path("etudiants");

            // Using ObjectReader for flexibility
            ObjectReader objectReader = objectMapper.readerFor(new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> etudiantsData = objectReader.readValue(etudiantsNode);

            etudiants.addAll(etudiantsData);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception according to your application's needs
        }
    }
}
