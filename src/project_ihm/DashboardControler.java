package project_ihm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class DashboardControler {
    @FXML
    private Button LogoutBtn;

    @FXML
    private TextField searchTextField;

    private ObjectMapper objectMapper = new ObjectMapper();
    private File jsonFile = new File("DataBase.json");
    private ObjectNode data;

    @FXML
    private void initialize() {
        // Read data from the JSON file using Jackson
        try {
            data = (ObjectNode) objectMapper.readTree(jsonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBook() {
        // Implement adding a book functionality
        // For example, adding a new book to the 'livres' array
        ArrayNode livres = (ArrayNode) data.get("livres");
        ObjectNode newBook = objectMapper.createObjectNode();
        newBook.put("numeroSerie", getNextBookSerialNumber());
        newBook.put("titre", "New Book Title");
        newBook.put("nomAuteur", "New Author");
        newBook.put("exemplairesDisponibles", 1);
        livres.add(newBook);

        saveDataToJsonFile();
    }

    @FXML
    private void handleDeleteBook() {
        // Implement deleting a book functionality
        // For example, removing a book from the 'livres' array
        int bookToDeleteSerialNumber = 1; // Replace with the actual book serial number to delete
        ArrayNode livres = (ArrayNode) data.get("livres");

        for (int i = 0; i < livres.size(); i++) {
            JsonNode book = livres.get(i);
            if (book.get("numeroSerie").asInt() == bookToDeleteSerialNumber) {
                livres.remove(i);
                break;
            }
        }

        saveDataToJsonFile();
    }

    @FXML
    private void handleModifyBook() {
        // Implement modifying a book functionality
        // For example, updating the title of a book
        int bookToModifySerialNumber = 1; // Replace with the actual book serial number to modify
        ArrayNode livres = (ArrayNode) data.get("livres");

        for (int i = 0; i < livres.size(); i++) {
            ObjectNode book = (ObjectNode) livres.get(i);
            if (book.get("numeroSerie").asInt() == bookToModifySerialNumber) {
                book.put("titre", "Modified Book Title");
                break;
            }
        }

        saveDataToJsonFile();
    }

    @FXML
    private void handleAcceptRentRequest() {
        // Implement accepting a rent request functionality
        // For example, adding an accepted rent request to the 'emprunts' array
        // Assuming you have a rent request object to add
        ObjectNode rentRequest = objectMapper.createObjectNode();
        // Fill in rent request details
        // ...

        ArrayNode emprunts = (ArrayNode) data.get("emprunts");
        emprunts.add(rentRequest);

        saveDataToJsonFile();
    }

    @FXML
    private void handleSearchBook() {
        String searchTitle = searchTextField.getText();
        // Implement searching for a book by title functionality
        // For example, display the details of the found book in the UI
        // ...

        // You can add more specific search functionality as needed
    }

    private int getNextBookSerialNumber() {
        // Determine the next available book serial number
        ArrayNode livres = (ArrayNode) data.get("livres");
        int maxSerialNumber = 0;

        for (JsonNode book : livres) {
            int currentSerialNumber = book.get("numeroSerie").asInt();
            if (currentSerialNumber > maxSerialNumber) {
                maxSerialNumber = currentSerialNumber;
            }
        }

        return maxSerialNumber + 1;
    }

    private void saveDataToJsonFile() {
        // Save the updated data to the JSON file using Jackson
        try {
            objectMapper.writeValue(jsonFile, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLogin() {
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loginLoader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loginRoot));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogoutButtonAction(ActionEvent event) {
        showLogin();
        ((Stage) LogoutBtn.getScene().getWindow()).close();
    }
}
