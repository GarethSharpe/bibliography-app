import javax.swing.*;

public interface IMessageService {
    public String makeSubmitMessage(
        String action, 
        String isbn, 
        String title, 
        String author,
        String year, 
        String publisher);
    public String makeUpdateMessage(
        String action,
        String selection,
        String isbn, 
        String update);
    public String toBibtex(String response);
    public void numberFilter(JTextField field, int length);
    public void textFilter(JTextField field, int length);
}