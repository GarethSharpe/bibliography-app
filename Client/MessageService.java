import java.util.Scanner;
import java.awt.event.*;
import javax.swing.*;

public class MessageService implements IMessageService { 

	public String makeSubmitMessage(
        String action, 
        String isbn, 
        String title, 
        String author,
        String year, 
        String publisher) {

        String message = action +
            " ISBN " + isbn + 
            " TITLE " + title + 
            " AUTHOR " + author + 
            " YEAR " + year +
            " PUBLISHER " + publisher;

        return message;
    }

    public String makeUpdateMessage(
        String action,
        String selection,
        String isbn, 
        String update) {

        String message = action + 
            " ISBN " + isbn + " " +
            selection + " " +
            update;

        return message;
    }

    public String toBibtex(String response) {

    	if (response.equals("NOTFOUND")) { return "NOTFOUND"; }

    	String title, partOfTitle;
    	String author, partOfAuthor, authorLast;
    	String publisher, partOfPublisher;
    	String isbn, year;

		title = partOfTitle = author = partOfAuthor = publisher = partOfPublisher = "";

        Scanner responseScanner = new Scanner(response);

        if (!responseScanner.hasNext()) { return null; }
		if (!responseScanner.next().equals("ISBN")) { return null; }

		isbn = responseScanner.next();

		if (!responseScanner.next().equals("TITLE")) { return null; }

		title = responseScanner.next();
		partOfTitle = responseScanner.next();
		while (!partOfTitle.equals("AUTHOR")) {
			title = title + " " + partOfTitle;
			partOfTitle = responseScanner.next();
		}

		author = responseScanner.next();
		partOfAuthor = responseScanner.next();
		while (!partOfAuthor.equals("YEAR")) {
			author = author + " " + partOfAuthor;
			partOfAuthor = responseScanner.next();
		}

		year = responseScanner.next();
		
		responseScanner.next();
		publisher = responseScanner.nextLine();
		publisher = publisher.substring(1);

		Scanner authorScanner = new Scanner(author);
		authorLast = authorScanner.next();

		int n = authorLast.length();
		char end = authorLast.charAt(n - 1);
		if (end == ',')
			authorLast = authorLast.substring(0, n - 1);

        String formattedResponse = 
        	"@book {" + authorLast + year + ",\n" +
        	" AUTHOR = {" + author + "},\n" +
        	" TITLE = {" + title + "},\n" +
        	" PUBLISHER = {" + publisher + "},\n" +
        	" YEAR = {" + year + "},\n" +
        	" ISBN = {" + isbn + "}\n}\n";

        responseScanner.close();
        authorScanner.close();

        return formattedResponse;
    }

    public void numberFilter(JTextField field, int length) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                if (field.getText().length() == length ||
                    !Character.isDigit(keyChar))
                    e.consume();
            }
        });
    }

    public void textFilter(JTextField field, int length) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                if (field.getText().length() == length ||
                    Character.isDigit(keyChar))
                    e.consume();
            }
        });
    }

}