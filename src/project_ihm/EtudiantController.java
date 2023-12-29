package project_ihm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
    private TableColumn<Book, Button> rentButtonColumn;

    @FXML
    private TableColumn<Book, String> statusColumn; // Added status column

    private List<Book> bookList = new ArrayList<>();
    private List<Rental> rentRequests = new ArrayList<>();

    @FXML
    private void initialize() {
        initializeBookTable();
    }

    private void initializeBookTable() {
        numeroSerieColumn.setCellValueFactory(new PropertyValueFactory<>("numeroSerie"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        nomAuteurColumn.setCellValueFactory(new PropertyValueFactory<>("nomAuteur"));
        exemplairesDisponiblesColumn.setCellValueFactory(new PropertyValueFactory<>("exemplairesDisponibles"));
        rentButtonColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(createRentButton(cellData.getValue())));

        // Add the following line to include the "status" property
        TableColumn<Book, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        bookTable.getColumns().add(statusColumn);

        // Load book data from JSON file
        loadBookDataFromJson();

        // Add book data to the table
        bookTable.getItems().addAll(bookList);
    }


    private Button createRentButton(Book book) {
        Button rentButton = new Button("Rent");
        rentButton.setOnAction(event -> handleRentBook(book));
        return rentButton;
    }

    private void loadBookDataFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Replace with your actual path to the JSON file
            File jsonFile = new File("Database.json");

            // Read JSON file and extract book data
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            JsonNode livresNode = rootNode.path("livres");

            Iterator<JsonNode> livresIterator = livresNode.elements();
            while (livresIterator.hasNext()) {
                JsonNode livreNode = livresIterator.next();
                int numeroSerie = livreNode.path("numeroSerie").asInt();
                String titre = livreNode.path("titre").asText();
                String nomAuteur = livreNode.path("nomAuteur").asText();
                int exemplairesDisponibles = livreNode.path("exemplairesDisponibles").asInt();

                // Create Book object and add it to the list
                Book book = new Book(numeroSerie, titre, nomAuteur, exemplairesDisponibles);
                bookList.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRentBook(Book selectedBook) {
        if (selectedBook != null && selectedBook.getExemplairesDisponibles() > 0) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Rent Duration");
            dialog.setHeaderText("Enter rental duration in days:");

            // Use TextFormatter to allow only numeric input
            TextField textField = dialog.getEditor();
            TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), 0,
                    change -> {
                        String newText = change.getControlNewText();
                        if (newText.matches("\\d*")) {
                            return change;
                        }
                        return null;
                    });
            textField.setTextFormatter(textFormatter);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(durationStr -> {
                try {
                    int duration = Integer.parseInt(durationStr);
                    if (duration > 0) {
                        selectedBook.decrementExemplairesDisponibles();
                        selectedBook.setStatus("Pending Approval"); // Set the status to pending

                        bookTable.refresh();

                        Rental rental = new Rental();
                        rental.setBook(selectedBook);
                        rental.setDuration(duration);

                        storeRentRequest(rental);
                    } else {
                        System.out.println("Invalid duration. Please enter a positive integer.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            });
        } else {
            System.out.println("Selected book is not available for rental.");
        }
    }

    private void storeRentRequest(Rental rental) {
        rentRequests.add(rental);
    }

    public void handleRentBook(ActionEvent actionEvent) {
    }
}
