import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class BookService implements IBookService<Book> {

	private ConcurrentHashMap<Long, Book> library = new ConcurrentHashMap<Long, Book>();

	public Book set(Book book) {
		return library.putIfAbsent(book.getISBN(), book);
	}

	public Book getByISBN(long isbn) {
		return library.get(isbn);
	}

	public Book[] getByAuthor(String author) {
		int count = 0;
		final int n = library.size();
		final Book[] books = library.values().toArray(new Book[n]);
		ArrayList<Book> foundBooks = new ArrayList<Book>();
		for (int i = 0; i < n; i++) {
			if (books[i].getAuthor().contains(author)) {
				foundBooks.add(books[i]);
				count++;
			}
		}
		return foundBooks.toArray(new Book[count]);
	}

	public Book[] getByTitle(String title) {
		int count = 0;
		final int n = library.size();
		final Book[] books = library.values().toArray(new Book[n]);
		ArrayList<Book> foundBooks = new ArrayList<Book>();
		for (int i = 0; i < n; i++) {
			if (books[i].getTitle().contains(title)) {
				foundBooks.add(books[i]);
				count++;
			}
		}
		return foundBooks.toArray(new Book[count]);
	}

	public Book[] getByPublisher(String publisher) {
		int count = 0;
		final int n = library.size();
		final Book[] books = library.values().toArray(new Book[n]);
		ArrayList<Book> foundBooks = new ArrayList<Book>();
		for (int i = 0; i < n; i++) {
			if (books[i].getPublisher().contains(publisher)) {
				foundBooks.add(books[i]);
				count++;
			}
		}
		return foundBooks.toArray(new Book[count]);
	}

	public Book[] getByYear(int year) {
		int count = 0;
		final int n = library.size();
		final Book[] books = library.values().toArray(new Book[n]);
		ArrayList<Book> foundBooks = new ArrayList<Book>();
		for (int i = 0; i < n; i++) {
			if (books[i].getYear() == year) {
				foundBooks.add(books[i]);
				count++;
			}
		}
		return foundBooks.toArray(new Book[count]);
	}

	public Book[] getAll() {
		final int n = library.size();
		Book[] books = library.values().toArray(new Book[n]);
		if (books.length == 0) {
			Book[] emptyBooks = { null };
			return emptyBooks; 
		}
		return books;
	}

	public Book replaceBook(long isbn, Book book) {
		return library.replace(isbn, book);
	}

	public Book removeBook(long isbn) {
		return library.remove(isbn);
	}

	public void removeByTitle(String title) {
		Book[] books = getByTitle(title);
		int n = books.length;
		for (int i = 0; i < n; i++)
			removeBook(books[i].getISBN());
	}

	public void removeByAuthor(String author) {
		Book[] books = getByAuthor(author);
		int n = books.length;
		for (int i = 0; i < n; i++)
			removeBook(books[i].getISBN());
	}

	public void removeByPublisher(String publisher) {
		Book[] books = getByPublisher(publisher);
		int n = books.length;
		for (int i = 0; i < n; i++)
			removeBook(books[i].getISBN());
	}

	public void removeByYear(int year) {
		Book[] books = getByYear(year);
		int n = books.length;
		for (int i = 0; i < n; i++)
			removeBook(books[i].getISBN());
	}

	public void verify() {
		System.out.println("connection to library successful");
	}

}