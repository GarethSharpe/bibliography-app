import java.util.Scanner;

public interface IServerThread<Book> {
	public void run();
	public Book parseSubmit(Scanner messageScanner);
	public String parseUpdate(Scanner messageScanner);
	public String parseRemove(Scanner messageScanner);
	public Book[] parseGet(Scanner messageScanner);
}
