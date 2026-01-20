package library;

import service.BookValidator;

import java.util.Date;

public class Book {
    private String title;
    private String author;
    private Date dateAdded;
    private String genre;
    private int publishingDate;
    private String imagePath;
    private String documentPath;

    public Book(String title, String author, Date dateAdded, String genre, int publishingDate,
                String imagePath, String documentPath) {

        BookValidator.validate(title, author, genre, publishingDate);
        this.title = title.trim();
        this.author = author.trim();
        this.dateAdded = dateAdded;
        this.genre = genre.trim();
        this.publishingDate = publishingDate;
        this.imagePath = imagePath;
        this.documentPath = documentPath;
    }

    // Getters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Date getDateAdded() { return dateAdded; }
    public String getGenre() { return genre; }
    public int getPublishingDate() { return publishingDate; }
    public String getImagePath() { return imagePath; }
    public String getDocumentPath() { return documentPath; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setDateAdded(Date dateAdded) { this.dateAdded = dateAdded; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPublishingDate(int publishingDate) { this.publishingDate = publishingDate; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    @Override
    public String toString() {
        return title + " by " + author;
    }
}
