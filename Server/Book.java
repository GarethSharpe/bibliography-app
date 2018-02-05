public class Book implements IBook<Book> {

	private long isbn;				// primary key
  	private String title;
  	private String author;
  	private String publisher;
  	private int year;
    		    
	public Book(long isbn, String title, String author, int year, String publisher) {
		this.isbn = isbn;   
		this.title = title;
		this.author = author;
		this.year = year;
		this.publisher = publisher;
	}

	public long getISBN() { return this.isbn; }

	public String getTitle() { return this.title; }

	public String getAuthor() { return this.author; }

	public String getPublisher() { return this.publisher; }

	public int getYear() { return this.year; }

	public void setISBN(long isbn) { this.isbn = isbn; }

	public void setTitle(String title) { this.title = title; }

	public void setAuthor(String author) { this.author = author; }

	public void setPublisher(String publisher) { this.publisher = publisher; }

	public void setYear(int year) { this.year = year; }

	@Override
	public boolean equals(Object otherBook) {
		if (this == otherBook) return true;
		if (this == null) return false;
		if (getClass() != otherBook.getClass()) return false;
		Book book = (Book) otherBook;
		if (this.isbn == book.isbn) return true;
		else return false;
	}

	@Override
	public String toString() {
		return "ISBN " + String.valueOf(this.isbn) + 
			" TITLE " + this.title + 
			" AUTHOR " + this.author + 
			" YEAR " + this.year +
			" PUBLISHER " + this.publisher;
	}

}