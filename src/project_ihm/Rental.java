package project_ihm;

public class Rental {
    private static int nextNumeroEmprunt = 1;

    private int numeroEmprunt;
    private Book book;
    private Etudiant etudiant; // Include a reference to the Etudiant class
    private int duration;

    public Rental() {
        this.numeroEmprunt = nextNumeroEmprunt++;
    }

    public int getNumeroEmprunt() {
        return numeroEmprunt;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
