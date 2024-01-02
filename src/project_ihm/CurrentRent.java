package project_ihm;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class CurrentRent {
    private final SimpleIntegerProperty rentId;
    private final SimpleIntegerProperty duration;
    private final SimpleStringProperty studentName;
    private final SimpleStringProperty bookTitle;

    public CurrentRent(int rentId, int duration, String studentName, String bookTitle) {
        this.rentId = new SimpleIntegerProperty(rentId);
        this.duration = new SimpleIntegerProperty(duration);
        this.studentName = new SimpleStringProperty(studentName);
        this.bookTitle = new SimpleStringProperty(bookTitle);
    }

    public int getRentId() {
        return rentId.get();
    }

    public int getDuration() {
        return duration.get();
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getBookTitle() {
        return bookTitle.get();
    }
}
