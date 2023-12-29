package project_ihm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Project_IHM extends Application {

    private static Project_IHM instance; // Singleton instance

    private List<Rental> reqEmpruntsList = new ArrayList<>();

    public List<Rental> getReqEmpruntsList() {
        return reqEmpruntsList;
    }

    public static Project_IHM getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this; // Set the singleton instance

        try {
            // Load the Login FXML file and show the login window
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loginLoader.load();
            Scene loginScene = new Scene(loginRoot);
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Login");
            primaryStage.show();

            // Get the controller for the Login window
            LoginController loginController = loginLoader.getController();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void storeData() {
        try {
            // Store reqEmpruntsList to ReqEmprunts field in Database.json
            File jsonFile = new File("Database.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            ArrayNode reqEmpruntsNode = (ArrayNode) rootNode.path("ReqEmprunts");

            for (Rental rental : reqEmpruntsList) {
                ObjectNode reqEmpruntNode = objectMapper.createObjectNode();
                reqEmpruntNode.put("numeroEmprunt", rental.getNumeroEmprunt());
                reqEmpruntNode.put("duree", rental.getDuration());

                ObjectNode etudiantNode = objectMapper.createObjectNode();
                etudiantNode.put("numeroEtudiant", rental.getEtudiant().getNumeroEtudiant());
                etudiantNode.put("nom", rental.getEtudiant().getNom());
                etudiantNode.put("prenom", rental.getEtudiant().getPrenom());
                reqEmpruntNode.set("etudiant", etudiantNode);

                ObjectNode livreNode = objectMapper.createObjectNode();
                livreNode.put("numeroSerie", rental.getBook().getNumeroSerie());
                livreNode.put("titre", rental.getBook().getTitre());
                reqEmpruntNode.set("livre", livreNode);

                reqEmpruntsNode.add(reqEmpruntNode);
            }

            // Write the updated JSON back to the file
            objectMapper.writeValue(jsonFile, rootNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
