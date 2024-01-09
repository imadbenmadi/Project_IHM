package project_ihm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;

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
    private Label errorLabel;

    @FXML
    private TextField InputUserName;

    @FXML
    private TextField InputPassword;

    @FXML
    private Button LoginButton;


    @FXML
    private void initialize() {
        setupLoginLogic();
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
        if (inputUsername.equalsIgnoreCase("admin") && inputPassword.equals("admin")) {
            openAdminDashboard();
        } else {
            List<Map<String, Object>> etudiants = getEtudiantDataFromJson();
            boolean isEtudiant = false;

            for (Map<String, Object> etudiant : etudiants) {
                String username = etudiant.get("nom") + "." + etudiant.get("prenom");
                String pwd = etudiant.get("numeroEtudiant").toString();

                if (inputUsername.equalsIgnoreCase(username) && inputPassword.equals(pwd)) {
                    isEtudiant = true;
                    ApplicationContext.setEtudiantContext(etudiant);
                    break;
                }
            }

            if (isEtudiant) {
                openEtudiantDashboard();
            } else {
                setErrorText("Incorrect username or password.");
            }
        }
    }

    private List<Map<String, Object>> getEtudiantDataFromJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("DataBase.json"));
            JsonNode etudiantsNode = rootNode.path("etudiants");
            ObjectReader objectReader = objectMapper.readerFor(new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> etudiants = objectReader.readValue(etudiantsNode);

            return etudiants;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void showDashboard() {
        try {
            FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent dashboardRoot = dashboardLoader.load();
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(new Scene(dashboardRoot));
            dashboardStage.setTitle("Dashboard");
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to show the Etudiant window
    public void showEtudiant() {
        try {
            FXMLLoader etudiantLoader = new FXMLLoader(getClass().getResource("Etudiant.fxml"));
            Parent etudiantRoot = etudiantLoader.load();
            Stage etudiantStage = new Stage();
            etudiantStage.setScene(new Scene(etudiantRoot));
            etudiantStage.setTitle("Etudiant");
            etudiantStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openAdminDashboard() {
        // Open the Admin Dashboard
        showDashboard();
        closeLoginWindow();
    }

    private void openEtudiantDashboard() {
        // Open the Etudiant Dashboard
        showEtudiant();
        closeLoginWindow();
    }

    private void setErrorText(String errorMessage) {
        if (errorLabel != null) {
            errorLabel.setText(errorMessage);
        }
    }

    private void closeLoginWindow() {
        // Close the login window
        ((Stage) LoginButton.getScene().getWindow()).close();
    }
}
