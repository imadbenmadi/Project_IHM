package project_ihm;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Book {
    private final IntegerProperty numeroSerie;
    private final StringProperty titre;
    private final StringProperty nomAuteur;
    private final IntegerProperty exemplairesDisponibles;

    // Constructors
    public Book(int numeroSerie, String titre, String nomAuteur, int exemplairesDisponibles) {
        this.numeroSerie = new SimpleIntegerProperty(numeroSerie);
        this.titre = new SimpleStringProperty(titre);
        this.nomAuteur = new SimpleStringProperty(nomAuteur);
        this.exemplairesDisponibles = new SimpleIntegerProperty(exemplairesDisponibles);
    }

    // Getters for properties
    public int getNumeroSerie() {
        return numeroSerie.get();
    }

    public String getTitre() {
        return titre.get();
    }

    public String getNomAuteur() {
        return nomAuteur.get();
    }

    public int getExemplairesDisponibles() {
        return exemplairesDisponibles.get();
    }

    // Property accessors
    public IntegerProperty numeroSerieProperty() {
        return numeroSerie;
    }

    public StringProperty titreProperty() {
        return titre;
    }

    public StringProperty nomAuteurProperty() {
        return nomAuteur;
    }

    public IntegerProperty exemplairesDisponiblesProperty() {
        return exemplairesDisponibles;
    }
}
