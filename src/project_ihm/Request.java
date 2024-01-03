package project_ihm;

import javafx.beans.property.*;

public class Request {
    private final IntegerProperty numeroEmprunt;
    private final IntegerProperty duree;
    private final StringProperty etudiant;
    private final ObjectProperty<Book> livre;

    public Request(int numeroEmprunt, int duree, String etudiant, Book livre) {
        this.numeroEmprunt = new SimpleIntegerProperty(numeroEmprunt);
        this.duree = new SimpleIntegerProperty(duree);
        this.etudiant = new SimpleStringProperty(etudiant);
        this.livre = new SimpleObjectProperty<>(livre);
    }
    public String getEtudiantFullName() {
        return this.etudiant.get();
    }
    public int getNumeroEmprunt() {
        return numeroEmprunt.get();
    }

    public int getDuree() {
        return duree.get();
    }

    public String getEtudiant() {
        return etudiant.get();
    }

    public Book getLivre() {
        return livre.get();
    }
}
