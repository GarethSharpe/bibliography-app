public interface IBookService<Book> {
    public Book set(Book book);
    public Book getByISBN(long isbn);
    public Book[] getByAuthor(String author);
    public Book[] getByTitle(String title);
    public Book[] getByPublisher(String publisher);
    public Book[] getByYear(int year);
    public Book[] getAll();
    public Book replaceBook(long isbn, Book book);
    public Book removeBook(long isbn);
    public void removeByTitle(String title);
	public void removeByAuthor(String author);
	public void removeByPublisher(String publisher);
	public void removeByYear(int year);
    public void verify();

}
