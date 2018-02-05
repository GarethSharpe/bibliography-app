public interface IBook {
    public long getISBN();
    public String getTitle();
    public String getAuthor();
    public String getPublisher();
    public int getYear();
    public void setISBN(long isbn);
    public void setTitle(String title);
    public void setAuthor(String author);
    public void setPublisher(String publisher);
    public void setYear(int year);
    public boolean equals(Object otherBook);
    public String toString();
}