package project_ihm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
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
    public List<Rental> reqEmpruntsList = new ArrayList<>();

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

                // Serialize Etudiant details
                String etudiant = rental.getUsername();
                System.out.println(etudiant);
                reqEmpruntNode.put("etudiant", etudiant);

                //reqEmpruntNode.set("etudiant", etudiant);
                /***
                if (etudiant != null) { // Add null check
                    ObjectNode etudiantNode = objectMapper.createObjectNode();
                    etudiantNode.put("etudiant", etudiant);
                    reqEmpruntNode.set("etudiant", etudiantNode);
                } else {
                    System.out.println("Etudiant object is null for rental: " + rental.getNumeroEmprunt());
                }
                 ***/
                // Serialize Book details
                Book book = rental.getBook();
                if (book != null) { // Add null check
                    ObjectNode livreNode = objectMapper.createObjectNode();
                    livreNode.put("numeroSerie", book.getNumeroSerie());
                    livreNode.put("titre", book.getTitre());
                    reqEmpruntNode.set("livre", livreNode);
                } else {
                    System.out.println("Book object is null for rental: " + rental.getNumeroEmprunt());
                }

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
                        String username = ApplicationContext.getEtudiantUsername();
                        Rental rental = new Rental();
                        rental.setBook(selectedBook);
                        rental.setDuration(duration);
                        rental.setUsername(username);
                        System.out.println(username);
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

                        // Remove the selected book from the bookTable
                        bookTable.getItems().remove(selectedBook);

                        // Store data after making changes
                        storeData();
                        showSuccessMessage("You Request to Rent The Book Have Been Sent Successfully");
                    } else {
                        showFaillerMessage("Invalid duration. Please enter a positive integer.");
                    }
                } catch (NumberFormatException e) {
                    showFaillerMessage("Invalid input. Please enter a valid number.");
                }
            });
        } else {
            showFaillerMessage("Selected book is not available for rental.");
        }
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
        successStage.setScene(new Scene(new Group(successLabel), 500, 100));

        // Set the position of the stage (adjust as needed)
        successStage.setX(500);
        successStage.setY(300);

        // Show the success message
        successStage.show();
    }
    private void showFaillerMessage(String message) {
        // Create a label to show the success message
        Label successLabel = new Label(message);
        successLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // Create a new stage for the success message
        Stage successStage = new Stage();
        successStage.initModality(Modality.APPLICATION_MODAL);
        successStage.setTitle("Failler");

        // Set the content of the stage
        successStage.setScene(new Scene(new Group(successLabel), 500, 100));

        // Set the position of the stage (adjust as needed)
        successStage.setX(500);
        successStage.setY(300);

        // Show the success message
        successStage.show();
    }
    // New method to create a status label
    private Node createStatusLabel(String status) {
        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().add("status-label");
        return statusLabel;
    }
}
