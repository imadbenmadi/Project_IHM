package project_ihm;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifyBookController {
    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private Button saveButton;

    private Book bookToModify;

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

            // You can perform additional update logic here if needed

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
}
