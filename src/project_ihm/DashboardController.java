package project_ihm;
import java.util.Iterator;

import javafx.beans.property.SimpleStringProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.ls.LSOutput;
import project_ihm.Etudiant;
import java.io.File;
import java.io.IOException;

public class DashboardController {
    @FXML
    public Button SearchBtn;
    @FXML
    public TableView rentsTable1;
    @FXML
    private TableView<CurrentRent> rentsTable;
    @FXML
    private Button LogoutBtn;

    @FXML
    public TextField addTitleField;
    @FXML
    public TextField addAuthorField;
    @FXML
    public TextField addExemplairesField;

    @FXML
    private TableView<Book> booksTable;
    @FXML
    private VBox rentsVBox;
    @FXML
    private Label currentRentsLabel;

    private ObjectMapper objectMapper = new ObjectMapper();
    private File jsonFile = new File("DataBase.json");
    private ObjectNode data;
    private ObservableList<Book> booksList = FXCollections.observableArrayList();
    @FXML
    private TableView<Request> requestsTable;
    @FXML
    private Button addBookButton;

    private ObservableList<Request> requestsList = FXCollections.observableArrayList();
    private ObservableList<CurrentRent> currentRentsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Read data from the JSON file using Jackson
        try {
            data = (ObjectNode) objectMapper.readTree(jsonFile);
            loadBooks();
            setupTable();
            loadRequests();
            setupRequestsTable();
            setup_Current_Rents_Table();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadRequests() {
        ArrayNode reqEmprunts = (ArrayNode) data.get("ReqEmprunts");

        ObservableList<Request> updatedRequestsList = FXCollections.observableArrayList();

        for (JsonNode request : reqEmprunts) {
            int numeroEmprunt = request.get("numeroEmprunt").asInt();
            int duree = request.get("duree").asInt();

            JsonNode etudiantNode = request.get("etudiant");
            int numeroEtudiant;
            String nomEtudiant;
            String prenomEtudiant;
            if (etudiantNode != null) {
                numeroEtudiant = etudiantNode.get("numeroEtudiant").asInt();
                nomEtudiant = etudiantNode.get("nom").asText();
                prenomEtudiant = etudiantNode.get("prenom").asText();
            } else {
                // Handle the case where "etudiant" is not present
                numeroEtudiant = 0; // or some default value
                nomEtudiant = "";
                prenomEtudiant = "";
            }

            JsonNode livreNode = request.get("livre");
            int numeroSerieLivre = livreNode.get("numeroSerie").asInt();
            String titreLivre = livreNode.get("titre").asText();

            updatedRequestsList.add(new Request(numeroEmprunt, duree,
                    new Etudiant(numeroEtudiant, nomEtudiant, prenomEtudiant),
                    new Book(numeroSerieLivre, titreLivre, "", 0)));
        }

        requestsList.setAll(updatedRequestsList);
    }

    private void setupRequestsTable() {
        TableColumn<Request, Integer> numeroEmpruntColumn = new TableColumn<>("id");
        numeroEmpruntColumn.setCellValueFactory(new PropertyValueFactory<>("numeroEmprunt"));
        numeroEmpruntColumn.setPrefWidth(50);

        TableColumn<Request, Integer> dureeColumn = new TableColumn<>("Duree");
        dureeColumn.setCellValueFactory(new PropertyValueFactory<>("duree"));
        dureeColumn.setPrefWidth(50);

        TableColumn<Request, String> etudiantColumn = new TableColumn<>("Etudiant");
        etudiantColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEtudiantFullName()));
        etudiantColumn.setPrefWidth(0);

        TableColumn<Request, String> livreColumn = new TableColumn<>("Livre");
        livreColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLivre().getTitre()));
        livreColumn.setPrefWidth(100);

        TableColumn<Request, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");

            {
                acceptButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
                acceptButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleAcceptRequest(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(acceptButton);
                }
            }
        });

        requestsTable.setItems(requestsList);
        requestsTable.getColumns().addAll(numeroEmpruntColumn, dureeColumn, etudiantColumn, livreColumn, actionColumn);
    }
    private void setup_Current_Rents_Table() {
        // Set up columns
        TableColumn<CurrentRent, Integer> rentIdColumn = new TableColumn<>("Rent ID");
        rentIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentId"));

        TableColumn<CurrentRent, Integer> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        TableColumn<CurrentRent, String> studentNameColumn = new TableColumn<>("Student Name");
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<CurrentRent, String> bookTitleColumn = new TableColumn<>("Book Title");
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));


        // Add columns to the table
        rentsTable1.getColumns().addAll(rentIdColumn, durationColumn, studentNameColumn, bookTitleColumn);

        // Populate the table with data
        loadCurrentRents();

        rentsTable1.setEditable(true);
    }

    private void loadCurrentRents() {
        ArrayNode emprunts = (ArrayNode) data.get("emprunts");

        ObservableList<CurrentRent> currentRentsList = FXCollections.observableArrayList();

        for (JsonNode emprunt : emprunts) {
            int rentId = emprunt.get("numeroEmprunt").asInt();
            int duration = emprunt.get("duree").asInt();
            JsonNode etudiantNode = emprunt.get("etudiant");
            String studentName = etudiantNode.get("nom").asText() + " " + etudiantNode.get("prenom").asText();
            JsonNode livreNode = emprunt.get("livre");
            String bookTitle = livreNode.get("titre").asText();

            currentRentsList.add(new CurrentRent(rentId, duration, studentName, bookTitle));
        }

        rentsTable1.setItems(currentRentsList);
    }



    private void handleAcceptRequest(Request request) {
        // Remove the accepted request from the list
        requestsList.remove(request);

        // Update the requests table
        requestsTable.refresh();

        // Perform actions when the admin accepts the request
        // For example, remove from ReqEmprunts and add to emprunts in the database
        try {
            // Load data from Database.json
            File jsonFile = new File("Database.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Remove the accepted request from ReqEmprunts field
            ArrayNode reqEmpruntsNode = (ArrayNode) rootNode.path("ReqEmprunts");
            Iterator<JsonNode> reqEmpruntsIterator = reqEmpruntsNode.iterator();
            while (reqEmpruntsIterator.hasNext()) {
                JsonNode node = reqEmpruntsIterator.next();
                if (node.get("numeroEmprunt").asInt() == request.getNumeroEmprunt()) {
                    reqEmpruntsIterator.remove();
                    break;
                }
            }

            // Add the accepted request to emprunts field
            ArrayNode empruntsNode = (ArrayNode) rootNode.path("emprunts");
            ObjectNode empruntNode = objectMapper.createObjectNode();
            empruntNode.put("numeroEmprunt", request.getNumeroEmprunt());
            empruntNode.put("duree", request.getDuree());

            ObjectNode etudiantNode = objectMapper.createObjectNode();
            etudiantNode.put("numeroEtudiant", request.getEtudiant().getNumeroEtudiant());
            etudiantNode.put("nom", request.getEtudiant().getNom());
            etudiantNode.put("prenom", request.getEtudiant().getPrenom());
            empruntNode.set("etudiant", etudiantNode);

            ObjectNode livreNode = objectMapper.createObjectNode();
            livreNode.put("numeroSerie", request.getLivre().getNumeroSerie());
            livreNode.put("titre", request.getLivre().getTitre());
            empruntNode.set("livre", livreNode);

            empruntsNode.add(empruntNode);

            // Write the updated JSON back to the file
            objectMapper.writeValue(jsonFile, rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void openAddBookWindow() {

        // Create the VBox with the content from AddBookWindow.fxml
        VBox addBookLayout = new VBox();
        Label titleLabel = new Label("Add New Book to the Library");
         addTitleField = new TextField();
         addAuthorField = new TextField();
         addExemplairesField = new TextField();
        Button addBookButton = new Button("Add Book");

        // Set up the layout for the add book window
        addBookLayout.getChildren().addAll(titleLabel, addTitleField, addAuthorField, addExemplairesField, addBookButton);
        addBookLayout.setSpacing(10);
        addBookLayout.setStyle("-fx-border-color: black; -fx-padding: 10px; -fx-border-radius: 10px;");

        Stage addBookStage = new Stage();
        addBookStage.setScene(new Scene(addBookLayout, 300, 250));
        addBookStage.setTitle("Add Book Window");

        // Set up the event handler for the "Add Book" button
        addBookButton.setOnAction(event -> handleAddBook(addBookStage));
        addBookStage.show();
    }
    @FXML
    public void handleAddBook(Stage addBookStage) {
        String newTitle = this.addTitleField.getText();
        String newAuthor = this.addAuthorField.getText();
        int exemplaires = Integer.parseInt(this.addExemplairesField.getText());


        if (!newTitle.isEmpty() && !newAuthor.isEmpty()) {
            // Add the new book to the 'livres' array
            ArrayNode livres = (ArrayNode) data.get("livres");
            ObjectNode newBook = objectMapper.createObjectNode();
            newBook.put("numeroSerie", getNextBookSerialNumber());
            newBook.put("titre", newTitle);
            newBook.put("nomAuteur", newAuthor);
            newBook.put("exemplairesDisponibles", exemplaires); // Use the value from the TextField
            livres.add(newBook);

            // Save the changes to the JSON file
            saveDataToJsonFile();

            // Clear the input fields
            addTitleField.clear();
            addAuthorField.clear();
            addExemplairesField.clear(); // Clear the exemplaires field

            // Refresh the books table
            loadBooks();

            // Close the add book window after adding the book
            addBookStage.close();
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
    public void loadBooks() {
        ArrayNode livres = (ArrayNode) data.get("livres");

        // Create a new ObservableList to hold the updated books
        ObservableList<Book> updatedBooksList = FXCollections.observableArrayList();

        // Load books from JSON and add them to the new ObservableList
        for (JsonNode book : livres) {
            int numeroSerie = book.get("numeroSerie").asInt();
            String title = book.get("titre").asText();
            String author = book.get("nomAuteur").asText();
            int exemplairesDisponibles = book.get("exemplairesDisponibles").asInt();
            updatedBooksList.add(new Book(numeroSerie, title, author, exemplairesDisponibles));
        }

        // Set the new ObservableList to the existing booksList
        booksList.setAll(updatedBooksList);
    }
    private void setupTable() {
        // Set up the columns
        TableColumn<Book, Integer> numeroSerieColumn = new TableColumn<>("Numero Serie");
        numeroSerieColumn.setCellValueFactory(new PropertyValueFactory<>("numeroSerie"));
        numeroSerieColumn.setPrefWidth(100);  // Set the preferred width

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        titleColumn.setPrefWidth(100);  // Set the preferred width

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("nomAuteur"));
        authorColumn.setPrefWidth(150);  // Set the preferred width

        TableColumn<Book, Integer> exemplairesDisponiblesColumn = new TableColumn<>("Exemplaires Disponibles");
        exemplairesDisponiblesColumn.setCellValueFactory(new PropertyValueFactory<>("exemplairesDisponibles"));
        exemplairesDisponiblesColumn.setPrefWidth(100);  // Set the preferred width

        TableColumn<Book, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button modifyButton = new Button("Modify");

            {
                // Set the background color of the Delete button to red
                deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

                // Set the background color of the Modify button to another color (e.g., blue)
                modifyButton.setStyle("-fx-background-color: #6495ED; -fx-text-fill: white;");

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
            modifyBookController.setDashboardController(this); // Pass the DashboardController reference

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

    public void updateBookInTable(Book updatedBook) {
        clearAndInitializeTable();
    }

    private void clearAndInitializeTable() {
        // Clear the entire content of the table
        booksList.clear();
        loadBooks();
        booksList.forEach(System.out::println);

        // Clear existing columns from the table
        booksTable.getColumns().clear();

        // Reinitialize the table
        setupTable();
        showSuccessMessage("Updated Successfully ");
    }
    private void showSuccessMessage(String message) {
        // Create a label to show the success message
        Label successLabel = new Label(message);
        successLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        // Create a new stage for the success message
        Stage successStage = new Stage();
        successStage.initModality(Modality.APPLICATION_MODAL);
        successStage.setTitle("Success");

        // Set the content of the stage
        successStage.setScene(new Scene(new Group(successLabel), 200, 100));

        // Set the position of the stage (adjust as needed)
        successStage.setX(500);
        successStage.setY(300);

        // Show the success message
        successStage.show();
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
