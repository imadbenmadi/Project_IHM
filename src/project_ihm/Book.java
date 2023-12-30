package project_ihm;

import javafx.beans.property.*;

public class Book {
    private final IntegerProperty numeroSerie;
    private final StringProperty titre;
    private final StringProperty nomAuteur;
    private final IntegerProperty exemplairesDisponibles;
    private final StringProperty status; // New property for book status

    public Book(int numeroSerie, String titre, String nomAuteur, int exemplairesDisponibles) {
        this.numeroSerie = new SimpleIntegerProperty(numeroSerie);
        this.titre = new SimpleStringProperty(titre);
        this.nomAuteur = new SimpleStringProperty(nomAuteur);
        this.exemplairesDisponibles = new SimpleIntegerProperty(exemplairesDisponibles);
        this.status = new SimpleStringProperty("Available"); // Default status is "Available"
    }

    public int getNumeroSerie() {
        return numeroSerie.get();
    }

    public String getTitre() {
        return titre.get();
    }

    public void setTitre(String text) {
        this.titre.set(text);
    }

    public String getNomAuteur() {
        return nomAuteur.get();
    }

    public void setNomAuteur(String text) {
        this.nomAuteur.set(text);
    }

    public int getExemplairesDisponibles() {
        return exemplairesDisponibles.get();
    }

    public void setExemplairesDisponibles(int exemplairesDisponibles) {
        this.exemplairesDisponibles.set(exemplairesDisponibles);
    }

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

    public void decrementExemplairesDisponibles() {
        exemplairesDisponibles.set(exemplairesDisponibles.get() - 1);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }
}