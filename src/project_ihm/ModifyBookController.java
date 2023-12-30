package project_ihm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ModifyBookController {
    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private Button saveButton;

    private Book bookToModify;

    private File jsonFile = new File("DataBase.json");
    private ObjectMapper objectMapper = new ObjectMapper();

    // Reference to the DashboardController
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private void initialize() {
        // You can perform any initialization here if needed
    }

    public void setBookToModify(Book book) {
        this.bookToModify = book;
        titleField.setText(book.getTitre());
        authorField.setText(book.getNomAuteur());
    }

    @FXML
    private void handleSaveButton() {
        if (bookToModify != null) {
            // Update the book with the modified values
            bookToModify.setTitre(titleField.getText());
            bookToModify.setNomAuteur(authorField.getText());

            // Update the data in the JSON file
            updateDataInJsonFile();

            // Manually refresh the table in DashboardController by updating the modified book
            dashboardController.updateBookInTable(bookToModify);

            // Close the dialog
            closeDialog();
        }
    }



    @FXML
    private void handleCloseButton() {
        // Close the dialog without saving changes
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void updateDataInJsonFile() {
        // Update the data in the JSON file using Jackson
        try {
            // Read the existing data from the file
            ObjectNode jsonData = (ObjectNode) objectMapper.readTree(jsonFile);

            // Find the book in the array and update its properties
            ArrayNode livres = (ArrayNode) jsonData.get("livres");
            for (JsonNode jsonBook : livres) {
                if (jsonBook.get("numeroSerie").asInt() == bookToModify.getNumeroSerie()) {
                    ((ObjectNode) jsonBook).put("titre", bookToModify.getTitre());
                    ((ObjectNode) jsonBook).put("nomAuteur", bookToModify.getNomAuteur());
                    break;
                }
            }

            // Save the updated data back to the file
            objectMapper.writeValue(jsonFile, jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
