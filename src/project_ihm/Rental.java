package project_ihm;

public class Rental {
    private Book book; // Ensure it's using the correct package for Book
    private int duration;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
