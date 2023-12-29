package project_ihm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TextFormatter;

import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EtudiantController {

    @FXML
    public Button LogoutBtn;

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
    private TableColumn<Book, Node> rentButtonColumn; // Updated to Node type

    private List<Book> bookList = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void initialize() {
        initializeBookTable();
    }

    @FXML
    private void handleLogoutButtonAction() {
        showLogin();
        ((Stage) LogoutBtn.getScene().getWindow()).close();
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

    private void initializeBookTable() {
        numeroSerieColumn.setCellValueFactory(new PropertyValueFactory<>("numeroSerie"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        nomAuteurColumn.setCellValueFactory(new PropertyValueFactory<>("nomAuteur"));
        exemplairesDisponiblesColumn.setCellValueFactory(new PropertyValueFactory<>("exemplairesDisponibles"));
        rentButtonColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(createRentButton(cellData.getValue())));

        // Load book data from JSON file
        loadBookDataFromJson();

        // Add book data to the table
        bookTable.getItems().addAll(bookList);
    }

    private Node createRentButton(Book book) {
        if (book.getExemplairesDisponibles() > 0) {
            Button rentButton = new Button("Rent");
            rentButton.setOnAction(event -> handleRentBook(book));
            rentButton.getStyleClass().add("pending-button"); // Set style programmatically
            return rentButton;
        } else {
            Label statusLabel = new Label("Not Available");
            statusLabel.getStyleClass().add("not-available-label");
            return statusLabel;
        }
    }

    private void loadBookDataFromJson() {
        try {
            File jsonFile = new File("Database.json");
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            JsonNode livresNode = rootNode.path("livres");

            Iterator<JsonNode> livresIterator = livresNode.elements();
            while (livresIterator.hasNext()) {
                JsonNode livreNode = livresIterator.next();
                int numeroSerie = livreNode.path("numeroSerie").asInt();
                String titre = livreNode.path("titre").asText();
                String nomAuteur = livreNode.path("nomAuteur").asText();
                int exemplairesDisponibles = livreNode.path("exemplairesDisponibles").asInt();

                Book book = new Book(numeroSerie, titre, nomAuteur, exemplairesDisponibles);
                bookList.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Rental> reqEmpruntsList = new ArrayList<>();
    public List<Rental> getReqEmpruntsList() {
        return reqEmpruntsList;
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
    private void handleRentBook(Book selectedBook) {
        if (selectedBook != null && selectedBook.getExemplairesDisponibles() > 0) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Rent Duration");
            dialog.setHeaderText("Enter rental duration in days:");

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
                        Rental rental = new Rental();
                        rental.setBook(selectedBook);
                        rental.setDuration(duration);

                        // Add the rental request to the global reqEmpruntsList
                        getReqEmpruntsList().add(rental);

                        // Update the status in the table
                        Node statusLabel = createStatusLabel("Pending");

                        // Replace the Rent button with the status label in the RentButton column
                        rentButtonColumn.setCellFactory(column -> new TableCell<>() {
                            @Override
                            protected void updateItem(Node item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(item);
                                }
                            }
                        });

                        // Store data after making changes
                        storeData();

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

    // New method to create a status label
    private Node createStatusLabel(String status) {
        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().add("status-label");
        return statusLabel;
    }
}
