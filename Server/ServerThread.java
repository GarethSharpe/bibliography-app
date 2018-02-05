import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ServerThread extends Thread 
						implements IServerThread {

	private Socket clientSocket = null;
	private BookService bookService = null;

	public ServerThread(Socket socket, BookService bookService) {
		super("ServerThread");
		this.clientSocket = socket;
		this.bookService = bookService;
	}

	public void run() { 

		System.out.println("new thread");

		try (
			PrintWriter output = new PrintWriter(this.clientSocket.getOutputStream(), true);
			Scanner input = new Scanner(this.clientSocket.getInputStream());
		) {
			String message, command, status;

			while (input.hasNextLine()) { 

				message = input.nextLine();
				Scanner messageScanner = new Scanner(message);

				if (!messageScanner.hasNext()) { break; }

				command = messageScanner.next();

				switch (command) {

					case "SUBMIT":
						Book book = parseSubmit(messageScanner);
						if (book != null && bookService.set(book) == null)
							output.println("SUCCESS");
						else
							output.println("DUPLICATE");
						break;

					case "UPDATE":
						status = parseUpdate(messageScanner);
						output.println(status);
						break;

					case "REMOVE":
						status = parseRemove(messageScanner);
						output.println(status);
						break;

					case "GET":
						Book[] books = parseGet(messageScanner);
						try {
							int n = books.length;
							String allBooks = "";
							for (int i = 0; i < n; i++)
								allBooks = allBooks + books[i].toString() + "\n";
							n = allBooks.length();
							allBooks = allBooks.substring(0, n - 1);
							if (allBooks == null)
								throw new NullPointerException();
							output.println(allBooks);
						} catch (StringIndexOutOfBoundsException ex) {
							output.println("NOTFOUND");
						} catch (NullPointerException ex) {
							output.println("NOTFOUND");
						}
						break;

					default:
						output.println("UNDEFINED");
						break;
				}
				output.println();
			}
		} catch (IOException e) {
			System.err.println("new thread could not be created");
			System.exit(-1);
		}
	}

	public Book parseSubmit(Scanner messageScanner) {

		long isbn; int year;
		String title, partOfTitle, author, partOfAuthor, publisher, partOfPublisher;
		title = partOfTitle = author = partOfAuthor = publisher = partOfPublisher = "";

		if (!messageScanner.hasNext()) { return null; }
		if (!messageScanner.next().equals("ISBN")) { return null; }

		isbn = messageScanner.nextLong();

		if (!messageScanner.next().equals("TITLE")) { return null; }

		title = messageScanner.next();
		partOfTitle = messageScanner.next();
		while (!partOfTitle.equals("AUTHOR")) {
			title = title + " " + partOfTitle;
			partOfTitle = messageScanner.next();
		}

		author = messageScanner.next();
		partOfAuthor = messageScanner.next();
		while (!partOfAuthor.equals("YEAR")) {
			author = author + " " + partOfAuthor;
			partOfAuthor = messageScanner.next();
		}

		year = messageScanner.nextInt();
		
		messageScanner.next();
		publisher = messageScanner.nextLine();
		publisher = publisher.substring(1);

		Book book = new Book(isbn, title, author, year, publisher);

		return book;
	}

	public String parseUpdate(Scanner messageScanner) { 

		if (!messageScanner.next().equals("ISBN")) { return "UNDEFINED"; }

		long isbn = messageScanner.nextLong();
		Book book = bookService.getByISBN(isbn);

		if (book == null) { return "NOTFOUND"; }

		String command = null;

		while (messageScanner.hasNext()) {

			command = messageScanner.next();

			switch (command) {

				case "AUTHOR":
					book.setAuthor(messageScanner.nextLine().substring(1));
					break;

				case "TITLE":
					book.setTitle(messageScanner.nextLine().substring(1));
					break;

				case "PUBLISHER":	
					book.setPublisher(messageScanner.nextLine().substring(1));
					break;

				case "YEAR":
					String yearString = messageScanner.next();
					try {  
		    			int year = Integer.parseInt(yearString); 
		    			book.setYear(year);
		  			} catch(NumberFormatException ex) {  
		    			return "INVALID";  
		  			}
					break;

				case "ISBN":
					return "RESTRICTED";

				default:
					return "UNDEFINED";
			}
		}

		bookService.replaceBook(isbn, book);

		return "SUCCESS";
	}

	public String parseRemove(Scanner messageScanner) {

		String command = messageScanner.next();

		switch (command) {

			case "ISBN":
				long isbn = messageScanner.nextLong();
				bookService.removeBook(isbn);
				break;

			case "AUTHOR":
				String author = messageScanner.nextLine().substring(1);
				bookService.removeByAuthor(author);
				break;

			case "TITLE":
				String title = messageScanner.nextLine().substring(1);
				bookService.removeByTitle(title);
				break;

			case "PUBLISHER":
				String publisher = messageScanner.nextLine().substring(1);
				bookService.removeByPublisher(publisher);
				break; 

			case "YEAR":
				String yearString = messageScanner.next();
				try {  
	    			int year = Integer.parseInt(yearString); 
	    			bookService.removeByYear(year);
	  			} catch(NumberFormatException ex) {  
	    			return "INVALID";  
	  			}
				break;

			default:
				return "INVALID";
		}

		return "SUCCESS"; 
	}

	public Book[] parseGet(Scanner messageScanner) {

		String command = messageScanner.next();

		switch (command) {

			case "ALL":
				return bookService.getAll();

			case "ISBN":
				long isbn = messageScanner.nextLong();
				Book book = bookService.getByISBN(isbn);
				Book[] books = { book };
				return books;

			case "AUTHOR":
				String author = messageScanner.nextLine().substring(1);;
				return bookService.getByAuthor(author);

			case "TITLE":
				String title = messageScanner.nextLine().substring(1);;
				return bookService.getByTitle(title);

			case "PUBLISHER":
				String publisher = messageScanner.nextLine().substring(1);;
				return bookService.getByPublisher(publisher);

			case "YEAR":
				String yearString = messageScanner.next();
				try {  
	    			int year = Integer.parseInt(yearString); 
	    			return bookService.getByYear(year);
	  			} catch(NumberFormatException ex) {  
	    			Book[] notFound = { null };
					return notFound;  
	  			}

			default:
				Book[] notFound = { null };
				return notFound;
		}
	}

}
