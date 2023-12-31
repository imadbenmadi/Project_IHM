package project_ihm;


public class Etudiant {
    private int numeroEtudiant;
    private String nom;
    private String prenom;

    // Constructors, getters, and setters

    public Etudiant(int numeroEtudiant, String nom, String prenom) {
        this.numeroEtudiant = numeroEtudiant;
        this.nom = nom;
        this.prenom = prenom;
    }

    public int getNumeroEtudiant() {
        return numeroEtudiant;
    }

    public void setNumeroEtudiant(int numeroEtudiant) {
        this.numeroEtudiant = numeroEtudiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getFullName() {
        return this.nom +" "+ this.prenom;
    }

    // You can add more methods or fields as needed
}
