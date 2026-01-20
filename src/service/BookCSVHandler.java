package service;

import library.Book;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookCSVHandler {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String CSV_HEADER = "Title,Author,Genre,PublishingDate,DateAdded,ImagePath,DocumentPath";

    // Set folder in local disk
    private static final String FOLDER_PATH = "C:/BookOrganizer/";
    private static final String DEFAULT_FILE_PATH = FOLDER_PATH + "books.csv";  //LOCATION SA: C:/BookOrganizer/books.csv

    public static void initCSV() throws IOException {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(DEFAULT_FILE_PATH);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(CSV_HEADER);
            }
        }
    }

    public static void saveToCSV(List<Book> books) throws IOException {
        saveToCSV(books, DEFAULT_FILE_PATH);
    }

    public static void saveToCSV(List<Book> books, String filePath) throws IOException {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) folder.mkdirs(); // ensure folder exists

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(CSV_HEADER);

            for (Book book : books) {
                String line = String.format("\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\"",
                        escapeCSV(book.getTitle()),
                        escapeCSV(book.getAuthor()),
                        escapeCSV(book.getGenre()),
                        book.getPublishingDate(),
                        DATE_FORMAT.format(book.getDateAdded()),
                        escapeCSV(book.getImagePath()),
                        escapeCSV(book.getDocumentPath())
                );
                writer.println(line);
            }
        }
    }

    public static List<Book> loadFromCSV() throws IOException {
        return loadFromCSV(DEFAULT_FILE_PATH);
    }


    public static List<Book> loadFromCSV(String filePath) throws IOException {
        List<Book> books = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                try {
                    Book book = parseCSVLine(line);
                    if (book != null) books.add(book);
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line);
                }
            }
        }

        return books;
    }

    private static Book parseCSVLine(String line) throws ParseException {
        List<String> values = parseCSVValues(line);

        if (values.size() < 5) return null;

        String title = values.get(0);
        String author = values.get(1);
        String genre = values.get(2);
        int publishingDate = Integer.parseInt(values.get(3));
        Date dateAdded = DATE_FORMAT.parse(values.get(4));
        String imagePath = values.size() > 5 ? values.get(5) : "";
        String documentPath = values.size() > 6 ? values.get(6) : "";

        return new Book(title, author, dateAdded, genre, publishingDate, imagePath, documentPath);
    }

    private static List<String> parseCSVValues(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());

        return values;
    }

    private static String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
