package library;

import java.util.ArrayList;
import java.util.List;

public class BookLibrary {
    private final List<Book> books = new ArrayList<>();

    public void addBook(Book b) {
        books.add(b);
    }
    public void removeBook(Book b) {
        books.remove(b);
    }
    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books.clear();
        this.books.addAll(books);
    }
}
