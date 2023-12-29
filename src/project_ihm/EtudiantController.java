package project_ihm;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class EtudiantController {

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, Integer> numeroSerieColumn;

    @FXML
    private TableColumn<Book, String> titreColumn;

    @FXML
    private TableColumn<Book, String> nomAuteurColumn;

    @FXML
    private TableColumn<Book, Integer> exemplairesDisponiblesColumn;

    @FXML
    private Button rentBookButton;

    private List<Book> bookList;

    // Initialize method
    @FXML
    private void initialize() {
        initializeBookTable();
    }

    // Method to initialize the book table
    private void initializeBookTable() {
        numeroSerieColumn.setCellValueFactory(new PropertyValueFactory<>("numeroSerie"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        nomAuteurColumn.setCellValueFactory(new PropertyValueFactory<>("nomAuteur"));
        exemplairesDisponiblesColumn.setCellValueFactory(new PropertyValueFactory<>("exemplairesDisponibles"));

        // Load book data from JSON file
        loadBookDataFromJson();

        // Add book data to the table
        bookTable.setItems(FXCollections.observableArrayList(bookList));
    }

    // Method to load book data from JSON file
    private void loadBookDataFromJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("DataBase.json"));
            JsonNode livresNode = rootNode.path("livres");

            // Using ObjectReader for flexibility
            ObjectReader objectReader = objectMapper.readerFor(new TypeReference<List<Book>>() {});
            bookList = objectReader.readValue(livresNode);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception according to your application's needs
            bookList = List.of(); // Return an empty list if there's an issue
        }
    }

    // Method to handle book rental
    @FXML
    private void handleRentBook() {
        // Get the selected book from the table
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook != null && selectedBook.getExemplairesDisponibles() > 0) {
            // Implement logic for book rental here
            // For example, decrement the available copies and update the UI
            int currentAvailable = selectedBook.getExemplairesDisponibles();
            selectedBook.exemplairesDisponiblesProperty().set(currentAvailable - 1);

            // Update the table
            bookTable.refresh();
        } else {
            // Display a message indicating that the book is not available
            System.out.println("Selected book is not available for rental.");
        }
    }
}
