package project_ihm;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Request {
    private final IntegerProperty numeroEmprunt;
    private final IntegerProperty duree;
    private final ObjectProperty<Etudiant> etudiant;
    private final ObjectProperty<Book> livre;

    public Request(int numeroEmprunt, int duree, Etudiant etudiant, Book livre) {
        this.numeroEmprunt = new SimpleIntegerProperty(numeroEmprunt);
        this.duree = new SimpleIntegerProperty(duree);
        this.etudiant = new SimpleObjectProperty<>(etudiant);
        this.livre = new SimpleObjectProperty<>(livre);
    }

    public int getNumeroEmprunt() {
        return numeroEmprunt.get();
    }

    public int getDuree() {
        return duree.get();
    }

    public Etudiant getEtudiant() {
        return etudiant.get();
    }

    public Book getLivre() {
        return livre.get();
    }
}
