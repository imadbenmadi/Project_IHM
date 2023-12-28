package project_ihm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class LoginController {

    @FXML
    private TextField InputUserName;

    @FXML
    private TextField InputPassword;

    @FXML
    private Button LoginButton;

    private Project_IHM mainApp; // Reference to the main application

    @FXML
    private void initialize() {
        // Set up the login logic
        setupLoginLogic();
    }

    public void setMainApp(Project_IHM mainApp) {
        this.mainApp = mainApp;
    }

    private void setupLoginLogic() {
        LoginButton.setOnAction(this::handleClick);
    }

    public void handleClick(ActionEvent event) {
        String username = InputUserName.getText().trim();
        String password = InputPassword.getText().trim();

        // Perform authentication logic
        authenticateUser(username, password);
    }

    private void authenticateUser(String inputUsername, String inputPassword) {
        // Your authentication logic here

        if (inputUsername.equalsIgnoreCase("admin") && inputPassword.equals("admin")) {
            // Admin Dashboard
            openAdminDashboard();
        } else {
            // Check if the username exists in the database and the password matches
            // For simplicity, I'm assuming that etudiant usernames are their passwords (numeroEtudiant)

            // Mock data for demonstration
            List<Map<String, Object>> etudiants = getEtudiantDataFromJson();
            boolean isEtudiant = false;

            for (Map<String, Object> etudiant : etudiants) {
                String username = etudiant.get("nom") + "." + etudiant.get("prenom");
                String pwd = etudiant.get("numeroEtudiant").toString();

                if (inputUsername.equalsIgnoreCase(username) && inputPassword.equals(pwd)) {
                    isEtudiant = true;
                    break;
                }
            }

            if (isEtudiant) {
                // Etudiant Dashboard
                openEtudiantDashboard();
            } else {
                // Incorrect credentials
                setErrorText("Incorrect username or password.");
            }
        }
    }

    private List<Map<String, Object>> getEtudiantDataFromJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("DataBase.json"));
            JsonNode etudiantsNode = rootNode.path("etudiants");

            // Using ObjectReader for flexibility
            ObjectReader objectReader = objectMapper.readerFor(new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> etudiants = objectReader.readValue(etudiantsNode);

            return etudiants;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception according to your application's needs
            return new ArrayList<>(); // Return an empty list if there's an issue
        }
    }

    private void openAdminDashboard() {
        // Open the Admin Dashboard
        mainApp.showDashboard();
        closeLoginWindow();
    }

    private void openEtudiantDashboard() {
        // Open the Etudiant Dashboard
        mainApp.showEtudiant();
        closeLoginWindow();
    }

    private void setErrorText(String errorMessage) {
        // Your implementation to set the error text
    }

    private void closeLoginWindow() {
        // Close the login window
        ((Stage) LoginButton.getScene().getWindow()).close();
    }
}
