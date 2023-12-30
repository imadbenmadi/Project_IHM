package project_ihm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class DashboardController {
    @FXML
    private Button LogoutBtn;

    @FXML
    private TextField addTitleField;

    @FXML
    private TextField addAuthorField;

    @FXML
    private TableView<Book> booksTable;

    private ObjectMapper objectMapper = new ObjectMapper();
    private File jsonFile = new File("DataBase.json");
    private ObjectNode data;

    private ObservableList<Book> booksList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Read data from the JSON file using Jackson
        try {
            data = (ObjectNode) objectMapper.readTree(jsonFile);
            loadBooks();
            setupTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBook() {
        String newTitle = addTitleField.getText();
        String newAuthor = addAuthorField.getText();

        if (!newTitle.isEmpty() && !newAuthor.isEmpty()) {
            // Add the new book to the 'livres' array
            ArrayNode livres = (ArrayNode) data.get("livres");
            ObjectNode newBook = objectMapper.createObjectNode();
            newBook.put("numeroSerie", getNextBookSerialNumber());
            newBook.put("titre", newTitle);
            newBook.put("nomAuteur", newAuthor);
            newBook.put("exemplairesDisponibles", 1);
            livres.add(newBook);

            // Save the changes to the JSON file
            saveDataToJsonFile();

            // Clear the input fields
            addTitleField.clear();
            addAuthorField.clear();

            // Refresh the books table
            loadBooks();
        }
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

    private void loadBooks() {
        ArrayNode livres = (ArrayNode) data.get("livres");

        // Clear the existing books list
        booksList.clear();

        // Load books from JSON and add them to the ObservableList
        for (JsonNode book : livres) {
            int numeroSerie = book.get("numeroSerie").asInt();
            String title = book.get("titre").asText();
            String author = book.get("nomAuteur").asText();
            int exemplairesDisponibles = book.get("exemplairesDisponibles").asInt();
            booksList.add(new Book(numeroSerie, title, author, exemplairesDisponibles));
        }
    }

    private void setupTable() {
        // Set up the columns
        TableColumn<Book, Integer> numeroSerieColumn = new TableColumn<>("Numero Serie");
        numeroSerieColumn.setCellValueFactory(new PropertyValueFactory<>("numeroSerie"));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("nomAuteur"));

        TableColumn<Book, Integer> exemplairesDisponiblesColumn = new TableColumn<>("Exemplaires Disponibles");
        exemplairesDisponiblesColumn.setCellValueFactory(new PropertyValueFactory<>("exemplairesDisponibles"));

        TableColumn<Book, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button modifyButton = new Button("Modify");

            {
                deleteButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleDeleteBook(book.getTitre());
                });

                modifyButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    openModifyBookDialog(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(deleteButton, modifyButton);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }
        });

        // Set the items to the table
        booksTable.setItems(booksList);

        // Add columns to the table
        booksTable.getColumns().addAll(numeroSerieColumn, titleColumn, authorColumn, exemplairesDisponiblesColumn, actionColumn);
    }

    private void handleDeleteBook(String title) {
        ArrayNode livres = (ArrayNode) data.get("livres");

        for (int i = 0; i < livres.size(); i++) {
            JsonNode book = livres.get(i);
            if (book.get("titre").asText().equals(title)) {
                livres.remove(i);
                saveDataToJsonFile();
                loadBooks(); // Refresh the books table
                break;
            }
        }
    }

    private void openModifyBookDialog(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyBook.fxml"));
            Parent root = loader.load();

            ModifyBookController modifyBookController = loader.getController();
            modifyBookController.setBookToModify(book);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modify Book");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the books table after modification
            loadBooks();
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